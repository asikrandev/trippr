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

function getmlt(from, text, excluded) {
    var data = {
        "query" : {
            "more_like_this" : {
                "like_text" : text,
                "min_term_freq" : 1,
                "min_doc_freq" : 1
            }
        }
    };

    $.post( "http://54.191.28.250:9200/trippr/_search", JSON.stringify(data), function(data) {extractData(data, excluded)}, "json").done(function(){sendMessage(from, result)});
}

function extractData(data, excluded) {
    var city = '';
    i = 0;
    while (i<10 && city == '') {
        name = data.hits.hits[i]._source.name;
        if (excluded.indexOf(name) != -1) {
            i++;
        } else {
            city = name;
            break;
        }
    }
    result = city;
}

function onConnected() {
	acisionSDK.messaging.setCallbacks({
		onMessage: function(msg) {
			receivedMessage(msg);
		}
	});
}

function receivedMessage(msg){
    var msgjson = JSON.parse(msg.content);

    var text = msgjson.text;
    var excluded = msgjson.excluded;

    getmlt(msg.from, text, excluded);
    setTimeout(function(){
        console.log('Result is: ' + result);
        console.log(msg.content);
        //sendMessage(msg.from, result);
    }, 700);
    result = '';
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
