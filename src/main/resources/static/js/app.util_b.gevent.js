/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global jQuery */

app.util_b.gevent = (function() {
	'use strict';
	// ---------------- BEGIN MODULE SCOPE VARIABLES --------------
	var subscribeEvent, publishEvent, unsubscribeEvent, executeEvent, addEvent, createEvent, customEventMap = {}, customSubMap = {};
	// ----------------- END MODULE SCOPE VARIABLES ---------------

	// ------------------- BEGIN UTILITY METHODS ------------------
	// BEGIN utility method /createEvent/
	createEvent = function(event_name) {
		var event, params = {
			bubbles : false,
			cancelable : false,
			detail : undefined
		};
		event = new CustomEvent(event_name, params);

		return event;
	};
	// END utility method /createEvent/

	// BEGIN utility method /addEvent/
	addEvent = function(collection, event_name, fn) {
		createEvent(event_name);
		customEventMap[event_name] = fn;

		collection.addEventListener(event_name, fn, false);
	};
	// END utility method /addEvent/

	// BEGIN utility method /executeEvent/
	executeEvent = function(event_name, data_list) {
		var event = createEvent(event_name);
		customEventMap[event_name].apply(this, data_list);
	};
	// END utility method /executeEvent/
	// -------------------- END UTILITY METHODS -------------------

	// ------------------- BEGIN PUBLIC METHODS -------------------
	// BEGIN public method /publishEvent/
	publishEvent = function(event_name, data) {
		var data_list;

		if (!customSubMap[event_name]) {
			return false;
		}

		if (data) {
			data_list = Array.isArray(data) ? data : [ data ];
			executeEvent(event_name, data_list);
			return true;
		}

		executeEvent(event_name, null);
		return true;
	};
	// END public method /publishEvent/

	// BEGIN public method /subscribeEvent/
	subscribeEvent = function(collection, event_name, fn) {
		if (collection instanceof HTMLElement) {
			addEvent(collection, event_name, fn);
		} else if (collection instanceof HTMLCollection) {
			for (var i = 0; i < collection.length; i++) {
				addEvent(collection[i], event_name, fn);
			}
		}

		customSubMap[event_name] = collection;
	};
	// END public method /subscribeEvent/

	// BEGIN public method /unsubscribeEvent/
	unsubscribeEvent = function(collection, event_name) {
		if (!customSubMap[event_name]) {
			return false;
		}

		customSubMap[event_name] = customSubMap[event_name].not(collection);

		if (customSubMap[event_name].length === 0) {
			delete customSubMap[event_name];
		}

		if (customEventMap[event_name].length === 0) {
			delete customEventMap[event_name]
		}

		return true;
	};
	// END public method /unsubscribeEvent/
	// ------------------- END PUBLIC METHODS ---------------------

	// return public methods
	return {
		publish : publishEvent,
		subscribe : subscribeEvent,
		unsubscribe : unsubscribeEvent,
		customEventMap : customEventMap,
		customSubMap : customSubMap
	};
}());
