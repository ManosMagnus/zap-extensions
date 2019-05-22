// * This Community Script will analyze incoming websocket messages for base64 strings

// * Regex Test: https://regex101.com/r/OOElRY/3
// ** Forked by: https://regex101.com/library/dS0sM8

// Author: Manos Kirtas (manolis.kirt@gmail.com)

// Passive scan rules should not send messages
// Right click the script in the Scripts tree and select "enable"  or "disable"

OPCODE_TEXT = 0x1;
RISK_INFO 	= 0;
CONFIDENCE_MEDIUM = 2;

PRINT_RESULTS = false;

var base64Regex = /((?:[A-Za-z0-9+\/]{4}\n?)*(?:[A-Za-z0-9+\/]{2}==|[A-Za-z0-9+\/]{3}=))/gmi;

base64Decoder = java.util.Base64.getDecoder();
JavaString = Java.type("java.lang.String");

function scan(helper,msg) {

    if(msg.opcode != OPCODE_TEXT || msg.isOutgoing){
        return;
    }
    var message = String(msg.getReadablePayload()).valueOf();
    if( (matches = message.match(base64Regex)) != null ){
        var counter = 0;

        matches.forEach(function(evidence){

            decodedEvidence = new JavaString(base64Decoder.decode(evidence));
            if(PRINT_RESULTS){
                print("Message: " + message); 
                print("Evidence: " + evidence);
                print("Decoded Evidence: " + decodedEvidence);
            }

            helper.newAlert()
                .setRiskConfidence(RISK_INFO, CONFIDENCE_MEDIUM)
                .setName("Base64 Disclosure in WebSocket message (script)")
                .setDescription("A Base64-encoded string has been founded in the websocket incoming message. Base64-encoded data may contain sensitive" +
                                "information such as usernames, passwords or cookies which should be further inspected. Decode evidence: "
                                + decodedEvidence + ".")
                .setParam(counter++)
                .setSolution("Base64-encoding should not be used to store or send sensitive information.")
                .setEvidence(evidence)
                .raise();

        });
    }
}
