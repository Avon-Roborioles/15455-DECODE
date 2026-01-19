package org.firstinspires.ftc.teamcode.SubsystemOpModes;

import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp(group = "Subsystem")
public class MecanumDrive extends NextFTCOpMode {


    public MecanumDrive(){
        addComponents(
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                //new LoopTimeComponent(),
                BulkReadComponent.INSTANCE
        );



    }
    @Override
    public void onStartButtonPressed(){
        PedroComponent.follower().setPose(new Pose());
        PedroComponent.follower().startTeleopDrive();

//        new PedroDriverControlled(
//                Gamepads.gamepad1().leftStickY(),
//                Gamepads.gamepad1().leftStickX(),
//                Gamepads.gamepad1().rightStickX(),
//                false
//        ).schedule();
        new TelemetryData("LSY: ",()->(double)gamepad1.left_stick_y);
        new TelemetryData("LSX: ",()->(double)gamepad1.left_stick_x);
        new TelemetryData("RSX: ",()->(double)gamepad1.right_stick_x);
    }

    public void onUpdate(){
        follower().setTeleOpDrive(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                true
        );
    }
}
