local player1Scored = false;

function scored() {
    if (scored_pin.read() == 0) {
        if (player1Scored) {
            agentSend("player1_scored");
        } else {
            agentSend("player2_scored");
        }
        player1Scored = false;
    }
}

function player1Score() {
    player1Scored = true;
}

function redactGoal() {
    if (redact_goal_pin.read() == 0) {
        agentSend("redact_goal");
    }
}

function resetGame() {
    if (reset_game_pin.read() == 0) {
        agentSend("reset_game");
    }
}


function agentSend(string) {
    local voltage = getVoltage();
    agent.send(string, voltage);
    imp.sleep(1.00);
}

function getVoltage() {
    local voltage = hardware.voltage();
    local reading = voltage_pin.read();
    return (reading / 6553.5) * voltage;
}

scored_pin <- hardware.pin1;
player1_pin <- hardware.pin2;
redact_goal_pin <- hardware.pin7;
reset_game_pin <- hardware.pin8;
voltage_pin <- hardware.pin9;

scored_pin.configure(DIGITAL_IN_PULLUP, scored);
player1_pin.configure(DIGITAL_IN_PULLUP, player1Score);
player2_pin.configure(DIGITAL_IN_PULLUP, player2Score);
redact_goal_pin.configure(DIGITAL_IN_PULLUP, redactGoal);
reset_game_pin.configure(DIGITAL_IN_PULLUP, resetGame);
voltage_pin.configure(ANALOG_IN);
imp.setpowersave(true);