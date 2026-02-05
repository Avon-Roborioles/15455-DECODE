package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
public class LaserDistanceSensorTest extends OpMode {

    public DigitalChannel digitalChannel;
    public void init(){
        digitalChannel=hardwareMap.get(DigitalChannel.class,"laserSensor");
    }


    public void loop(){
        telemetry.addData("Distance Sensor",digitalChannel.getState());
        telemetry.update();
    }
}
