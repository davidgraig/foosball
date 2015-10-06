local hipchat_authToken = "AUTH_TOKEN_HERE";
local hipchat_prefix = "https://api.hipchat.com/v2/room/672604/notification?auth_token=";
local isLogging = false;
local timer = null;
local game_started = false;
local time_limit = 120;
local low_battery_message = "I'm dying!";
local game_started_message = "Game started.";
local game_over_message = "No one is playing!";

device.on("goal_scored" function(voltage) {
    if (isLogging) {
        server.log("goal_scored() called, voltage: " + voltage);    
    }
    
    if (voltage <= 3.4) {
        sendMessageToHipChat(low_battery_message, "red");
    }
    
    if (game_started == false) {
        game_started = true;    
        sendMessageToHipChat(game_started_message);
    }
    
    if (timer != null) {
        imp.cancelwakeup(timer);
    }
    
    timer = imp.wakeup(time_limit, function() {
        sendMessageToHipChat(game_over_message);
        game_started = false;
    });
});


function sendMessageToHipChat(string, color = "green") {
    local headers = { 
        "Content-Type" : "application/json"
    };
    local body = http.jsonencode({"color": color, "message_format": "html", "message": string});
    local url = hipchat_prefix + hipchat_authToken;
    httpPostWrapper(url, headers, body);
}

function httpPostWrapper (url, headers, string, log = false) {
    local request = http.post(url, headers, string);
    local response = request.sendsync();
    if (isLogging) {
        server.log("response: " + http.jsonencode(response));
    }
    return response;
}
