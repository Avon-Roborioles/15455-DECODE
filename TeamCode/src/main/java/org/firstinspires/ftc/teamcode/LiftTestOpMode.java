package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class LiftTestOpMode extends OpMode {
    Servo servo1;
    Servo servo2;
    Servo servo3;
    Servo servo4;


    public void init(){
        servo1=hardwareMap.get(Servo.class,"frServo");
        servo2 = hardwareMap.get(Servo.class,"flServo");
        servo3=hardwareMap.get(Servo.class,"brServo");
        servo4=hardwareMap.get(Servo.class,"blServo");
        servo1.setPosition(0);
        servo2.setPosition(0);
        servo3.setPosition(0);
        servo4.setPosition(0);
    }
    public void setPos(double pos){
        servo1.setPosition(pos);
        servo2.setPosition(pos);
        servo3.setPosition(pos);
        servo4.setPosition(pos);
    }
    public void loop(){
        double multiplier = .001;
        setPos(servo1.getPosition()+gamepad1.right_stick_y*multiplier);
    }
}
