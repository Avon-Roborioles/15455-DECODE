package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.hardware.driving.FieldCentric;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.Direction;
import dev.nextftc.hardware.impl.IMUEx;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp
public class MecanumDrive extends NextFTCOpMode {
    private MotorEx lf,rf,lr,rr;
    private IMUEx imu;
    private MecanumDriverControlled driverControlled;

    public MecanumDrive(){
        lf = new MotorEx("front_left").brakeMode().reversed();
        rf = new MotorEx("front_right").brakeMode();
        lr = new MotorEx("rear_right").brakeMode().reversed();
        rr = new MotorEx("rear_left").brakeMode();



        imu = new IMUEx("imu",Direction.UP, Direction.FORWARD).zeroed();





    }
    @Override
    public void onStartButtonPressed(){
        driverControlled = new MecanumDriverControlled(
                rf,
                lf,
                rr,
                lr,
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX(),
                new FieldCentric(imu)


        );
        driverControlled.schedule();
    }
}
