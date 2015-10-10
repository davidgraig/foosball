function wakeUp() {
    if (wake_pin.read() == 0) {
        agentSend("wake_up");
    }
}

function player1Score() {
    if (player1_pin.read() == 0) {
        agentSend("player1_scored");
    }
}


function player2Score() {
    if (player2_pin.read() == 0) {
        agentSend("player2_scored");
    }
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
    imp.sleep(0.75);
}

function getVoltage() {
    local voltage = hardware.voltage();
    local reading = voltage_pin.read();
    return (reading / 6553.5) * voltage;
}

wake_pin <- hardware.pin1;
player1_pin <- hardware.pin2;
player2_pin <- hardware.pin5;
redact_goal_pin <- hardware.pin7;
reset_game_pin <- hardware.pin8;
voltage_pin <- hardware.pin9;

wake_pin.configure(DIGITAL_IN_PULLUP, wakeUp);
player1_pin.configure(DIGITAL_IN_PULLUP, player1Score);
player2_pin.configure(DIGITAL_IN_PULLUP, player2Score);
redact_goal_pin.configure(DIGITAL_IN_PULLUP, redactGoal);
reset_game_pin.configure(DIGITAL_IN_PULLUP, resetGame);
voltage_pin.configure(ANALOG_IN);
imp.setpowersave(true);