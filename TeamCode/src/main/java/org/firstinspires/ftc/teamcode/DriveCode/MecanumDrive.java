package org.firstinspires.ftc.teamcode.DriveCode;

import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.PerpetualCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.hardware.driving.DriverControlledCommand;
import dev.nextftc.hardware.driving.FieldCentric;
import dev.nextftc.hardware.driving.HolonomicDrivePowers;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.driving.RobotCentric;
import dev.nextftc.hardware.impl.Direction;
import dev.nextftc.hardware.impl.IMUEx;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp
public class MecanumDrive extends NextFTCOpMode {


    public MecanumDrive(){
        addComponents(
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE
        );



    }
    @Override
    public void onStartButtonPressed(){
        PedroComponent.follower().setPose(new Pose());
        PedroComponent.follower().startTeleopDrive();

        new PedroDriverControlled(
                Gamepads.gamepad1().leftStickY(),
                Gamepads.gamepad1().leftStickX(),
                Gamepads.gamepad1().rightStickX(),
                false
        ).schedule();
    }

    public void onUpdate(){
        follower().setTeleOpDrive(
                gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                true
        );
        telemetry.addData("LSY:",gamepad1.left_stick_y);
        telemetry.addData("LSX:",gamepad1.left_stick_x);
        telemetry.addData("RSX:",gamepad1.right_stick_x);
        telemetry.update();
    }
}
