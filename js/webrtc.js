login();

function login() {
	var config = {
		persistent: true,
		username: "jegasmlm_gmail_com_1",
		password: "puJcClGvA"
	};
	// Log in to the Acision SDK
	acisionSDK = new AcisionSDK("wvatXmaKZcmM", {
		onConnected: onConnected,
		onAuthFailure: function() {
			console.warn("Invalid username or password!");
		}
	}, config);
}

function onConnected() {
	acisionSDK.messaging.setCallbacks({
		onMessage: function(msg) {
			receivedMessage(msg);
		}
	});
}

function receivedMessage(msg){
	sendMessage(msg.from, "Messaged received from " + msg.from + ":\n\t" + msg.content);
}

function sendMessage(user, text){
	acisionSDK.messaging.sendToDestination(user, text, {}, {
		onAcknowledged : function() {
			console.log("Application got acknowledgement of message being sent");
		},
		onError : function(code, message) {
			console.log("Application failed to send message");
		}
	});
}
