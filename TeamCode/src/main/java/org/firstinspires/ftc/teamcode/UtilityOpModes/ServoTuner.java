package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(group = "Tuning")
public class ServoTuner extends OpMode {

    Servo servo;
    
    @Override
    public void init(){
        servo = hardwareMap.get(Servo.class,"ejectServo");
        servo.setPosition(1);
    }

    @Override
    public void loop(){
        servo.setPosition(servo.getPosition()+gamepad1.right_stick_y*.01);
        telemetry.addData("Servo pos",servo.getPosition());
        telemetry.update();
    }
}
