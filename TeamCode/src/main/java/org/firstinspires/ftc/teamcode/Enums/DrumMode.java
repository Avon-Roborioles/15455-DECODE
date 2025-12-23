package org.firstinspires.ftc.teamcode.Enums;

import org.firstinspires.ftc.robotcontroller.external.samples.SampleRevBlinkinLedDriver;
import org.firstinspires.ftc.teamcode.UtilityOpModes.DrumTuner;

public enum DrumMode {
    INTAKE("Intake"),
    DISCRETE_OUTTAKE("Discrete Outtake"),
    RAPID_OUTTAKING("Rapid Outtaking"),
    RAPID_OUTTAKE_SETUP("Rapid Outtake Setup"),
    ZEROING("Zeroing"),
    SECURE("Secure");
    private String name;
    DrumMode(String name){
        this.name=name;
    }

    public String toString(){
        return name;
    }
}
