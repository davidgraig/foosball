#require "Parse.class.nut:1.0.1"

local hipchat_authToken = "YOUR_AUTH_TOKEN_HERE";
local hipchat_prefix = "https://api.hipchat.com/v2/room/672604/notification?auth_token=";
local parse_appId = "YOUR_PARSE_APP_ID";
local parse_apiKey = "YOUR_PARSE_API_KEY";
local isLogging = true;
local timer = null;
local time_limit = 300;
local low_battery_message = "@Doctor, I'm dying!";
local game_started_message = "Game started.";
local empty_table_message = "No one is playing!";
local goal_disallowed_message = "Goal disallowed.";
local game_reset_message = "Game Reset.";
local table_id = "LF6M221BkF";

parse <- Parse(parse_appId, parse_apiKey);

class GoalKeeper {
    goal_stack = [];
    player_1_score = 0;
    player_2_score = 0;
    player_1_name = "Black";
    player_2_name = "Yellow";
    game_started = false;

    function startGame() {
        game_started = true;
    }
    
    function endGame() {
        game_started = false;
    }

    function isGameInProgress() {
        return game_started;
    }

    function reset() {
        goal_stack = [];
        player_1_score = 0;
        player_2_score = 0;
    }

    function player1Scored() {
        player_1_score++;
        goal_stack.push(player_1_name);
    }

    function player2Scored() {
        goal_stack.push(player_2_name);
        player_2_score++;
    } 
    
    function disallowLastGoal() {
        if (goal_stack.len() > 0) {
            local last_goal = goal_stack.pop();
            server.log("Last Goal Scored: " + last_goal);
            if (last_goal == player_1_name && player_1_score > 0) {
                player_1_score--;
                return 1;
            } else if (last_goal == player_2_name && player_2_score > 0) {
               player_2_score--;
               return 2;
            }
        }
    }
    
    function getScore() {
        return "[" + player_1_score + " - " + player_2_score + "]";
    }
}
local goal_keeper = GoalKeeper();





/**
 *  
 * Device listeners
 * 
 **/
 
device.on("wake_up" function(voltage) {
    if (isLogging) {
        server.log("wake_up. voltage: " + voltage);
        server.log("Score: " + goal_keeper.getScore())
    }
    checkVoltage(voltage);
    if (!goal_keeper.isGameInProgress()) {
        goal_keeper.startGame();
        parse.runCloudFunction("resetGame", {"tableId": table_id}, function(err, response) {
            server.log(serialize(response));
        });
        sendMessageToHipchat(game_reset_message);
    }
    setTimer();
}); 

device.on("player1_scored", function(voltage) {
    if (isLogging) {
        server.log("player1_scored");
    }
    if (goal_keeper.isGameInProgress()) {
        goal_keeper.player1Scored();
        parse.runCloudFunction("playerScored", {"tableId": table_id, "playerNumber" : 1}, function(err, response) {
            server.log(serialize(response));
        });
        //sendMessageToHipchat(goal_keeper.player_1_name + " scores! " + goal_keeper.getScore());
    }
    
}); 

device.on("player2_scored", function(voltage) {
    if (isLogging) {
        server.log("player2_scored");
    }
    if (goal_keeper.isGameInProgress()) {
        goal_keeper.player2Scored();
        parse.runCloudFunction("playerScored", {"tableId": table_id, "playerNumber" : 2}, function(err, response) {
            server.log(serialize(response));
        });
        //sendMessageToHipchat(".                      " + goal_keeper.getScore() + " " + goal_keeper.player_2_name + " scores!");
    }
});

device.on("redact_goal", function(voltage) {
    if (isLogging) {
        server.log("redact_goal");
    }
    if (goal_keeper.isGameInProgress()) {
        local lastPlayerScoredNum = goal_keeper.disallowLastGoal();
        server.log("Player number to redact: " + lastPlayerScoredNum);
        //sendMessageToHipchat(goal_disallowed_message + " Score: " + goal_keeper.getScore(), "red"); 
        parse.runCloudFunction("playerGoalDisallowed", {"tableId": table_id, "playerNumber" : lastPlayerScoredNum}, function(err, response) {
            server.log(serialize(response));
        });
    }
});

device.on("reset_game", function(voltage) {
    if (isLogging) {
        server.log("reset_game");
    }
    if (goal_keeper.isGameInProgress()) {
        local final_score = goal_keeper.getScore();
        //sendMessageToHipchat("Final Score: " + goal_keeper.player_1_name + " " + final_score + " " + goal_keeper.player_2_name);
        goal_keeper.endGame();
        goal_keeper.reset();
        parse.runCloudFunction("resetGame", {"tableId": table_id}, function(err, response) {
            server.log(serialize(response));
        });
        sendMessageToHipchat(game_reset_message, "yellow");
    }
});






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
