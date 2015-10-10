local hipchat_authToken = "AUTH_TOKEN_HERE";
local hipchat_prefix = "https://api.hipchat.com/v2/room/672604/notification?auth_token=";
local isLogging = true;
local timer = null;
local time_limit = 300;
local low_battery_message = "@Doctor, I'm dying!";
local game_started_message = "Game started.";
local empty_table_message = "No one is playing!";
local goal_disallowed_message = "Goal disallowed.";
local game_reset_message = "Game Reset.";

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
            if (last_goal == player_1_name && player_1_score > 0) {
                player_1_score--;
            } else if (last_goal == player_2_name && player_2_score > 0) {
               player_2_score--;
            }
        }
    }
    
    function getScore() {
        return "[" + player_1_score + " - " + player_2_score + "]";
    }
}



local goal_keeper = GoalKeeper();


device.on("wake_up" function(voltage) {
    if (isLogging) {
        server.log("wake_up. voltage: " + voltage);
    }
    checkVoltage(voltage);
    if (!goal_keeper.isGameInProgress()) {
        goal_keeper.startGame();
        sendMessageToHipchat(game_started_message);
    }
    setTimer();
}); 

device.on("player1_scored", function(voltage) {
    if (isLogging) {
        server.log("player1_scored");
    }
    if (goal_keeper.isGameInProgress()) {
        goal_keeper.player1Scored();
        
        sendMessageToHipchat(goal_keeper.player_1_name + " scores! " + goal_keeper.getScore());
    }
    
}); 

device.on("player2_scored", function(voltage) {
    if (isLogging) {
        server.log("player2_scored");
    }
    if (goal_keeper.isGameInProgress()) {
        goal_keeper.player2Scored();
        sendMessageToHipchat(".                      " + goal_keeper.getScore() + " " + goal_keeper.player_2_name + " scores!");
    }
});

device.on("redact_goal", function(voltage) {
    if (isLogging) {
        server.log("redact_goal");
    }
    if (goal_keeper.isGameInProgress()) {
        goal_keeper.disallowLastGoal();
        sendMessageToHipchat(goal_disallowed_message + " Score: " + goal_keeper.getScore(), "red");    
    }
});

device.on("reset_game", function(voltage) {
    if (isLogging) {
        server.log("reset_game");
    }
    if (goal_keeper.isGameInProgress()) {
        local final_score = goal_keeper.getScore();
        sendMessageToHipchat("Final Score: " + goal_keeper.player_1_name + " " + final_score + " " + goal_keeper.player_2_name);
        goal_keeper.endGame();
        goal_keeper.reset();
        sendMessageToHipchat(game_reset_message, "yellow");
    }
});



function checkVoltage(voltage) {
    if (voltage <= 3.4) {
        sendMessageToHipChat(low_battery_message, "red");
    }
}

function setTimer() {
    if (timer != null) {
        imp.cancelwakeup(timer);
    }
    timer = imp.wakeup(time_limit, function() {
        local final_score = goal_keeper.getScore();
        sendMessageToHipchat("Final Score: " + goal_keeper.player_1_name + " " + final_score + " " + goal_keeper.player_2_name);
        
        goal_keeper.endGame();
        goal_keeper.reset();
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