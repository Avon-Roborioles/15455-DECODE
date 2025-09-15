package org.firstinspires.ftc.teamcode.CompOpmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;


public abstract class RobotOpMode extends OpMode {

    protected Robot robot;
    @Override
    public void init(){
        robot = new Robot(hardwareMap);
        createLogic();
    }

    public abstract void createLogic();
    @Override
    public void loop(){
        TelemetryManager.getInstance().print(telemetry);
    }
}
