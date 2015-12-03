/*
 * app.shell.js
 * Shell module for SPA
 */

/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, app */

app.shell = (function() {
	'use strict';
	// ---------------- BEGIN MODULE SCOPE VARIABLES --------------
	var configMap = {
		anchor_schema_map : {
			chat : {
				opened : true,
				closed : true
			}
		},
		resize_interval : 200,
		main_html : String() + '<div class="app-shell-head">'
				+ '<div class="app-shell-head-logo">' + '<h1>app</h1>'
				+ '<p>javascript end to end</p>' + '</div>'
				+ '<div class="app-shell-head-acct"></div>' + '</div>'
				+ '<div class="app-shell-main">'
				+ '<div class="app-shell-main-nav"></div>'
				+ '<div class="app-shell-main-content"></div>' + '</div>'
				+ '<div class="app-shell-foot"></div>'
				+ '<div class="app-shell-modal"></div>'
	}, stateMap = {
		$container : undefined,
		anchor_map : {},
		resize_idto : undefined
	}, jqueryMap = {},

	copyAnchorMap, setJqueryMap, changeAnchorPart, onResize, onHashchange, onTapAcct, onLogin, onLogout, setChatAnchor, initModule;
	// ----------------- END MODULE SCOPE VARIABLES ---------------

	// ------------------- BEGIN UTILITY METHODS ------------------
	// Returns copy of stored anchor map; minimizes overhead
	copyAnchorMap = function() {
		return $.extend(true, {}, stateMap.anchor_map);
	};
	// -------------------- END UTILITY METHODS -------------------

	// --------------------- BEGIN DOM METHODS --------------------
	// Begin DOM method /setJqueryMap/
	setJqueryMap = function() {
		var $container = stateMap.$container;

		jqueryMap = {
			$container : $container,
			$acct : $container.find('.app-shell-head-acct'),
			$nav : $container.find('.app-shell-main-nav')
		};
	};
	// End DOM method /setJqueryMap/

	// Begin DOM method /changeAnchorPart/
	// Purpose : Changes part of the URI anchor component
	// Arguments :
	// * arg_map - The map describing what part of the URI anchor
	// we want changed.
	// Returns :
	// * true - the Anchor portion of the URI was updated
	// * false - the Anchor portion of the URI could not be updated
	// Actions :
	// The current anchor rep stored in stateMap.anchor_map.
	// See uriAnchor for a discussion of encoding.
	// This method
	// * Creates a copy of this map using copyAnchorMap().
	// * Modifies the key-values using arg_map.
	// * Manages the distinction between independent
	// and dependent values in the encoding.
	// * Attempts to change the URI using uriAnchor.
	// * Returns true on success, and false on failure.
	//
	changeAnchorPart = function(arg_map) {
		var anchor_map_revise = copyAnchorMap(), bool_return = true, key_name, key_name_dep;

		// Begin merge changes into anchor map
		KEYVAL: for (key_name in arg_map) {
			if (arg_map.hasOwnProperty(key_name)) {

				// skip dependent keys during iteration
				if (key_name.indexOf('_') === 0) {
					continue KEYVAL;
				}

				// update independent key value
				anchor_map_revise[key_name] = arg_map[key_name];

				// update matching dependent key
				key_name_dep = '_' + key_name;
				if (arg_map[key_name_dep]) {
					anchor_map_revise[key_name_dep] = arg_map[key_name_dep];
				} else {
					delete anchor_map_revise[key_name_dep];
					delete anchor_map_revise['_s' + key_name_dep];
				}
			}
		}
		// End merge changes into anchor map

		// Begin attempt to update URI; revert if not successful
		try {
			app.util_b.uriAnchor.setAnchor(anchor_map_revise);
		} catch (error) {
			// replace URI with existing state
			app.util_b.uriAnchor.setAnchor(stateMap.anchor_map, null, true);
			bool_return = false;
		}
		// End attempt to update URI...

		return bool_return;
	};
	// End DOM method /changeAnchorPart/
	// --------------------- END DOM METHODS ----------------------

	// ------------------- BEGIN EVENT HANDLERS -------------------
	// Begin Event handler /onHashchange/
	// Purpose : Handles the hashchange event
	// Arguments :
	// * event - jQuery event object.
	// Settings : none
	// Returns : false
	// Actions :
	// * Parses the URI anchor component
	// * Compares proposed application state with current
	// * Adjust the application only where proposed state
	// differs from existing and is allowed by anchor schema
	//
	onHashchange = function(event) {
		var _s_chat_previous, _s_chat_proposed, s_chat_proposed, anchor_map_proposed, is_ok = true, anchor_map_previous = copyAnchorMap();

		// attempt to parse anchor
		try {
			anchor_map_proposed = app.util_b.uriAnchor.makeAnchorMap();
		} catch (error) {
			app.util_b.uriAnchor.setAnchor(anchor_map_previous, null, true);
			return false;
		}
		stateMap.anchor_map = anchor_map_proposed;

		// convenience vars
		_s_chat_previous = anchor_map_previous._s_chat;
		_s_chat_proposed = anchor_map_proposed._s_chat;

		// Begin adjust chat component if changed
		if (!anchor_map_previous || _s_chat_previous !== _s_chat_proposed) {
			s_chat_proposed = anchor_map_proposed.chat;
			switch (s_chat_proposed) {
			case 'opened':
				is_ok = app.chat.setSliderPosition('opened');
				break;
			case 'closed':
				is_ok = app.chat.setSliderPosition('closed');
				break;
			default:
				app.chat.setSliderPosition('closed');
				delete anchor_map_proposed.chat;
				app.util_b.uriAnchor.setAnchor(anchor_map_proposed, null, true);
			}
		}
		// End adjust chat component if changed

		// Begin revert anchor if slider change denied
		if (!is_ok) {
			if (anchor_map_previous) {
				app.util_b.uriAnchor.setAnchor(anchor_map_previous, null, true);
				stateMap.anchor_map = anchor_map_previous;
			} else {
				delete anchor_map_proposed.chat;
				app.util_b.uriAnchor.setAnchor(anchor_map_proposed, null, true);
			}
		}
		// End revert anchor if slider change denied

		return false;
	};
	// End Event handler /onHashchange/

	// Begin Event handler /onResize/
	onResize = function() {
		if (stateMap.resize_idto) {
			return true;
		}

		app.chat.handleResize();
		stateMap.resize_idto = setTimeout(function() {
			stateMap.resize_idto = undefined;
		}, configMap.resize_interval);

		return true;
	};
	// End Event handler /onResize/

	onTapAcct = function(event) {
		var acct_text, user_name, user = app.model.people.get_user();
		if (user.get_is_anon()) {
			user_name = prompt('Please sign-in');
			app.model.people.login(user_name);
			jqueryMap.$acct.text('... processing ...');
		} else {
			app.model.people.logout();
		}
		return false;
	};

	onLogin = function(event, login_user) {
		jqueryMap.$acct.text(login_user.name);
	};

	onLogout = function(event, logout_user) {
		jqueryMap.$acct.text('Please sign-in');
	};
	// -------------------- END EVENT HANDLERS --------------------

	// ---------------------- BEGIN CALLBACKS ---------------------
	// Begin callback method /setChatAnchor/
	// Example : setChatAnchor( 'closed' );
	// Purpose : Change the chat component of the anchor
	// Arguments:
	// * position_type - may be 'closed' or 'opened'
	// Action :
	// Changes the URI anchor parameter 'chat' to the requested
	// value if possible.
	// Returns :
	// * true - requested anchor part was updated
	// * false - requested anchor part was not updated
	// Throws : none
	//
	setChatAnchor = function(position_type) {
		return changeAnchorPart({
			chat : position_type
		});
	};
	// End callback method /setChatAnchor/
	// ----------------------- END CALLBACKS ----------------------

	// ------------------- BEGIN PUBLIC METHODS -------------------
	// Begin Public method /initModule/
	// Example : app.shell.initModule( $('#app_div_id') );
	// Purpose :
	// Directs the Shell to offer its capability to the user
	// Arguments :
	// * $container (example: $('#app_div_id')).
	// A jQuery collection that should represent
	// a single DOM container
	// Action :
	// Populates $container with the shell of the UI
	// and then configures and initializes feature modules.
	// The Shell is also responsible for browser-wide issues
	// such as URI anchor and cookie management
	// Returns : none
	// Throws : none
	//
	initModule = function($container) {
		// load HTML and map jQuery collections
		stateMap.$container = $container;
		$container.html(configMap.main_html);
		setJqueryMap();

		// configure uriAnchor to use our schema
		app.util_b.uriAnchor.configModule({
			schema_map : configMap.anchor_schema_map
		});

		// configure and initialize feature modules
		app.chat.configModule({
			set_chat_anchor : setChatAnchor,
			chat_model : app.model.chat,
			people_model : app.model.people
		});
		app.chat.initModule(jqueryMap.$container);

		// Handle URI anchor change events.
		// This is done /after/ all feature modules are configured
		// and initialized, otherwise they will not be ready to handle
		// the trigger event, which is used to ensure the anchor
		// is considered on-load
		//
		$(window).bind('resize', onResize).bind('hashchange', onHashchange)
				.trigger('hashchange');

		app.util_b.gevent.subscribe($container, 'app-login', onLogin);
		app.util_b.gevent.subscribe($container, 'app-logout', onLogout);

		jqueryMap.$acct.text('Please sign-in').bind('utap', onTapAcct);
	};
	// End PUBLIC method /initModule/

	return {
		initModule : initModule
	};
	// ------------------- END PUBLIC METHODS ---------------------
}());
