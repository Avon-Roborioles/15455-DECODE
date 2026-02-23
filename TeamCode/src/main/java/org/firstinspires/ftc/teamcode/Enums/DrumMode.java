package org.firstinspires.ftc.teamcode.Enums;

public enum DrumMode {
    INTAKE("Intake"),
    DISCRETE_OUTTAKE("Discrete Outtake"),
    RAPID_OUTTAKING("Rapid Outtaking"),
    RAPID_OUTTAKE_SETUP("Rapid Outtake Setup"),
    ZEROING("Zeroing"),
    SECURE("Secure"),
    STANDBY("Standby"),
    MANUAL("Manual"),
    TUNING("Tuning");

    private String name;
    DrumMode(String name){
        this.name=name;
    }

    public String toString(){
        return name;
    }
}
