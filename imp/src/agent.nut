#require "Parse.class.nut:1.0.1"

local hipchat_authToken = "YOUR_HIPCHAT_AUTH";
local hipchat_prefix = "https://api.hipchat.com/v2/room/672604/notification?auth_token=";
local parse_appId = "PARSE_APP_ID";
local parse_apiKey = "PARSE_API_KEY";
local table_id = "PARSE_TABLE_ID";

local isLogging = true;
local timer = null;
local time_limit = 300;
local low_battery_message = "@Doctor, I'm dying!";
local game_started_message = "Table Occupied.";
local empty_table_message = "No one is playing.";
local goal_stack = [];
parse <- Parse(parse_appId, parse_apiKey);

/**
 *  
 * Device listeners
 * 
 **/

device.on("player1_scored", function(voltage) {
    checkVoltage(voltage);
    playerScored(1);
    setTimer();
}); 

device.on("player2_scored", function(voltage) {
    checkVoltage(voltage);
    playerScored(2);
    setTimer();
});

device.on("redact_goal", function(voltage) {
    checkVoltage(voltage);
    redactGoal();
});

device.on("reset_game", function(voltage) {
    checkVoltage(voltage);
    resetGame();
});


function playerScored(playerNum) {
    if (isLogging) {
        server.log("player " + playerNum + " scored");
    }
    goal_stack.push(playerNum);
    parse.runCloudFunction("playerScored", {"tableId": table_id, "playerNumber" : playerNum}, function(err, response) {
        server.log(serialize(response));
    });
}

function redactGoal() {
    if (isLogging) {
        server.log("redact_goal");
    }
    
    if (goal_stack.len() > 0) {
        local lastPlayerScoredNum = goal_stack.pop();
        parse.runCloudFunction("playerGoalDisallowed", {"tableId": table_id, "playerNumber" : lastPlayerScoredNum}, function(err, response) {
            server.log(serialize(response));
        });
    }
}

function resetGame() {
    if (isLogging) {
        server.log("reset_game");
    }
    goal_stack.clear();
    parse.runCloudFunction("resetGame", {"tableId": table_id}, function(err, response) {
        server.log(serialize(response));
    });
}

/**
 * 
 * helper functions
 * 
 **/

function checkVoltage(voltage) {
    if (voltage <= 3.4) {
        sendMessageToHipchat(low_battery_message, "red");
    }
}

function setTimer() {
    if (timer != null) {
        imp.cancelwakeup(timer);
    }
    timer = imp.wakeup(time_limit, function() {
        local final_score = goal_keeper.getScore();
        //sendMessageToHipchat("Final Score: " + goal_keeper.player_1_name + " " + final_score + " " + goal_keeper.player_2_name);
        
        goal_keeper.endGame();
        goal_keeper.reset();
        parse.runCloudFunction("unlockTable", {"tableId": table_id}, function(err, response) {
            server.log(serialize(response));
        });
        sendMessageToHipchat(empty_table_message);
    });
}

function sendMessageToHipchat(string, color = "green") {
    local headers = { 
        "Content-Type" : "application/json"
    };
    local body = http.jsonencode({"color": color, "message_format": "text", "message": string});
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


function serialize(data, offset = "") {
    local nextoff = offset + " ";
    switch (typeof data) {
        case "string":
            return "\"" + data + "\"";
        case "array":
            local inner = [];
            foreach (it in data) {
                inner.append(serialize(it));
            }
            return "[\n" + nextoff + join(",\n"+nextoff, inner) + "\n" + offset + "]";
        case "table":
            local inner = [];
            foreach (key, val in data) {
                local sval = serialize(val, nextoff);
                inner.append(key+" = "+sval);
            }
            return "{\n" + nextoff + join("\n"+nextoff, inner) + "\n" + offset + "}";
        case "instance":
            if (data instanceof Serializable) {
                return data.serialize(nextoff);
            } else {
                return "instance of " + data.getclass();
            }
        default:
            return data;
    }
}

function join(glue, pieces) {
    local joined = null;
    foreach (it in pieces) {
        if (joined == null) {
            joined = it.tostring();
        } else {
            joined += glue + it.tostring();
        }
    }
    return joined;
}

resetGame();