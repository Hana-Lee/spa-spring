/*
 * app.data.js
 * Data module
 */

/*jslint         browser : true, continue : true,
 devel  : true, indent  : 2,    maxerr   : 50,
 newcap : true, nomen   : true, plusplus : true,
 regexp : true, sloppy  : true, vars     : false,
 white  : true
 */
/*global $, io, app */

app.data = (function() {
	'use strict';
	var stateMap = {
		sio : null
	}, makeSio, getSio, initModule;

	makeSio = function() {
		var socket = io.connect('http://localhost:3000/chat');

		return {
			emit : function(event_name, data) {
				socket.emit(event_name, data);
				console.log('event_name : ' + event_name);
				console.log('data : ' + data);
			},
			on : function(event_name, callback) {
				socket.on(event_name, function() {
					callback(arguments);
					console.log('event_name : ' + event_name);
					console.log('arguments : ' + arguments);
				});
			}
		};
	};

	getSio = function() {
		if (!stateMap.sio) {
			stateMap.sio = makeSio();
		}
		return stateMap.sio;
	};

	initModule = function() {
	};

	return {
		getSio : getSio,
		initModule : initModule
	};
}());
