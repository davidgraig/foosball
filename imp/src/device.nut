function goal() {
    if (goal_pin.read() == 0) {
        local voltage = getVoltage();
        agent.send("goal_scored", voltage);
    }
    imp.sleep(0.5);
}

function getVoltage() {
    local voltage = hardware.voltage();
    local reading = voltage_pin.read();
    return (reading / 6553.5) * voltage;
}

voltage_pin <- hardware.pin9;
voltage_pin.configure(ANALOG_IN);

goal_pin <- hardware.pin1;
goal_pin.configure(DIGITAL_IN_PULLUP, goal);

imp.setpowersave(true);
