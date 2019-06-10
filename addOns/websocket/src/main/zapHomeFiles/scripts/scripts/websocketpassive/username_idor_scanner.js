// This script passively scans the incoming WebSocket messages for usernames.
// Finds usernames which are hashed with one of the following encoding methods {MD2, MD5
// SHA1, SHA256, SHA384, SHA512}. The usernames has to be defined in any context.

// Based on: org.zaproxy.zap.extension.pscanrulesBeta.UsernameIdorScanner

// Author: Manos Kirtas (manolis.kirt@gmail.com)

var Model = Java.type("org.parosproxy.paros.model.Model");
var Control = Java.type("org.parosproxy.paros.control.Control");
var ExtensionUserManagement = Java.type("org.zaproxy.zap.extension.users.ExtensionUserManagement");
var DigestUtils = Java.type("org.apache.commons.codec.digest.DigestUtils");

OPCODE_TEXT = 0x1;
RISK_INFO 	= 0;
CONFIDENCE_HIGH = 3;

usersList = null;
extUserManagment = null;

function scan(helper,msg) {

    if(msg.opcode != OPCODE_TEXT || msg.isOutgoing || (usersList = getUsers()) == null){
        return;
    }
    var message = String(msg.getReadablePayload()).valueOf();

    usersList.forEach(function(user){

        var username = user.getName();
        var usernameHashes = getHashes(username);

        Object.keys(usernameHashes).forEach(function(hashType){
            var hash = new RegExp(usernameHashes[hashType]);
            if((matches = message.match(usernameHashes[hashType]))!= null) {
                matches.forEach(function(evidence){
                    helper.newAlert()
                        .setRiskConfidence(RISK_INFO, CONFIDENCE_HIGH)
                        .setName("Username Hash Found in WebSocket message (script)")
                        .setDescription(getDiscription(user, hashType))
                        .setSolution("Use per user or session indirect object references (create a temporary mapping at time of use)."
                                     + " Or, ensure that each use of a direct object reference is tied to an authorization check to ensure the"
                                     + " user is authorized for the requested object.")
                        .setReference("https://www.owasp.org/index.php/Top_10_2013-A4-Insecure_Direct_Object_References\n"
                                      + "https://www.owasp.org/index.php/Testing_for_Insecure_Direct_Object_References_(OTG-AUTHZ-004)")
                        .setEvidence(evidence)
                        .setCweIdm(284) // CWE-284: Improper Access Control
                        .setWascId(02) // WASC-02: Insufficient Authorization
                        .raise();
                });
            }
        });
    });


}

function getDiscription(user, hashType){
    var username = user.getName();
    var context = Model.getSingleton().getSession().getContext(parseInt(user.getContextId())).getName();
    return "A " + hashType + " hash of {" + username + " / context: " + context + "} was found in incoming WebSocket message."
        +" This may indicate that the application is subject to an Insecure Direct Object"
        +" Reference (IDOR) vulnerability. Manual testing will be required to see if this"
        +" discovery can be abused.";
}

function getHashes(username){
    usernameHashes = {};
    usernameHashes['MD2'] = DigestUtils.md2Hex(username);
    usernameHashes['MD5'] = DigestUtils.md5Hex(username);
    usernameHashes['SHA1'] = DigestUtils.sha1Hex(username);
    usernameHashes['SHA256'] = DigestUtils.sha256Hex(username);
    usernameHashes['SHA384'] = DigestUtils.sha384Hex(username);
    usernameHashes['SHA512'] = DigestUtils.sha512Hex(username);

    return usernameHashes;
}

function getUsers(){
    if(usersList == null){
        if(( extUserManagment = getExtensionUserManagment()) != null){
            usersList = [];
            var contexts = Model.getSingleton().getSession().getContexts();
            var context;

            for(i = 0; i < contexts.size(); i++){
                context = contexts.get(i);
                contextUsers = extUserManagment.getContextUserAuthManager(context.getIndex()).getUsers();
                if(contextUsers.size() > 0){
                    for(j = 0; j < contextUsers.size(); j++ ){
                        usersList.push(contextUsers.get(j));
                    }
                }
            }
        }
    }
    return usersList;
}

function getExtensionUserManagment(){
    if(extUserManagment == null){
        extUserManagment = Control.getSingleton()
            .getExtensionLoader()
            .getExtension(ExtensionUserManagement.class);
    }
    return extUserManagment;
}
