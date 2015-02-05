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

var result = '';

function getmlt(text) {
    var data = {
          "query" : {
                  "more_like_this" : {
                            "like_text" : text,
                            "min_term_freq" : 1,
                            "min_doc_freq" : 1
                  }
          }
    };

    $.post( "http://ec2-54-191-28-250.us-west-2.compute.amazonaws.com:9200/trippr/_search", JSON.stringify(data), extractData, "json");
}

function extractData(data) {
    console.log(data);
    result = data.hits.hits[0]._source.name;
}

function onConnected() {
	acisionSDK.messaging.setCallbacks({
		onMessage: function(msg) {
			receivedMessage(msg);
		}
	});
}

function receivedMessage(msg){
    result = '';
    getmlt(msg.content);
    setTimeout(function(){
        console.log(result);
        console.log(msg.content);
	    sendMessage(msg.from, "Messaged received from " + msg.from + ":\n\t" + msg.content + ' VETE PA=> ' + result);
    }, 300);
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
