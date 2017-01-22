WebSocketClient = {
	stompClient : null,
	debugEnabled : false,
	websocketServer : '',// by default, it's on the same server as the clients. change this for it to be elsewhere
	/** Callback function after websocket connects */
	handleConnect : function() {
		console.log("Connected. Override this and manipulate your dom however you want")
	},
	/** Callback function if websocket fails to connect */
	handleConnectionFailure : function() {
		console.log("Connection Failure. Override this and manipulate your dom however you want")
	},
	/** Callback function after websocket disconnects */
	handleDisconnect : function() {
		console.log("Disconnected. Override this and manipulate your dom however you want")
	},
	connect : function() {
		WebSocketClient.connectToWebsocketServer("");
	},
	connectToWebsocketServer : function(domainAndContext) {
		var socketAddress = '/websocket/broker';
		if (domainAndContext) {
			socketAddress = domainAndContext + socketAddress;
		}
		//console.log("Connecting to socket at " + socketAddress);
		var socket = new SockJS(socketAddress);
		socket.onclose = WebSocketClient.handleDisconnect;
		this.stompClient = Stomp.over(socket);
		this.stompClient.debug = this.debugEnabled;
		this.stompClient.subscriptionMap = {};
		this.stompClient.connect({}, WebSocketClient.handleConnect, WebSocketClient.onConnectFailure);
	},
	onConnectFailure : function() {
		WebSocketClient.handleConnectionFailure();
	},
	subscribe : function(subscription) {
		if (!this.stompClient.connected) {
			return;
		}
		this.stompClient.subscriptionMap[subscription] = this.stompClient.subscribe(subscription, function(message) {
			WebSocketClient.handleMessage(subscription, JSON.parse(message.body));
		});
		//console.log("Subscribed to " + subscription);
	},
	unsubscribe : function(subscription) {
		if (!this.stompClient.connected) {
			return;
		}
		if (!WebSocketClient.stompClient.subscriptionMap[subscription]) {
			return;
		}
		WebSocketClient.stompClient.subscriptionMap[subscription].unsubscribe();
		//console.log("Unsubscribed from " + subscription);
	},
	unsubscribeFromAll : function() {
		if (!WebSocketClient.stompClient || !WebSocketClient.stompClient.subscriptionMap) {
			return;
		}

		for ( var subscription in WebSocketClient.stompClient.subscriptionMap) {
			if (WebSocketClient.stompClient.subscriptionMap.hasOwnProperty(subscription)) {
				WebSocketClient.unsubscribe(subscription);
				//console.log("Unsubscribed from " + subscription);
			}
		}
	},
	unsubscribeFromAll : function() {
		// loop through all subscriptions, unsubscribe
		for ( var key in WebSocketClient.stompClient.subscriptionMap) {
			if (WebSocketClient.stompClient.subscriptionMap.hasOwnProperty(key)) {
				WebSocketClient.stompClient.subscriptionMap[key].unsubscribe();
			}
		}
		WebSocketClient.stompClient.subscriptionMap = {};
	},
	handleMessage : function(subscription, object) {
		console.log('Message received from subscription ' + subscription);
		console.log(object);
	},
	sendMessage : function(destination, object) {
		destination = "/inbound/" + destination;
		//console.log("Sending message to " + destination);
		this.stompClient.send(destination, {}, JSON.stringify(object));
	},
	broadcast : function(destination, object) {
		if (!this.stompClient.connected) {
			return;
		}
		//console.log("broadcasting message to " + destination);
		this.stompClient.send(destination, {}, JSON.stringify(object));
	},
	disconnect : function() {
		if (this.stompClient != null) {
			this.stompClient.disconnect();
		}
		this.handleDisconnect();

	}
}