package org.firstinspires.ftc.teamcode;

import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.GamepadEx;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class TestTeleop extends NextFTCOpMode {
    public TestTeleop(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE, LauncherSubsystem.INSTANCE),
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onStartButtonPressed(){
        Gamepads.gamepad1().rightTrigger().atLeast(.7).whenBecomesTrue(DrumSubsystem.INSTANCE.intakeThreeBalls);
        Gamepads.gamepad1().leftTrigger().atLeast(.7).whenBecomesTrue(
                new SequentialGroup(
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        new InstantCommand(()->new TelemetryItem(()->"Running Finished")),
                        DrumSubsystem.INSTANCE.shootAny,
                        DrumSubsystem.INSTANCE.shootAny,
                        DrumSubsystem.INSTANCE.shootAny
                )
        );
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(DrumSubsystem.INSTANCE.zero);
//        new PedroDriverControlled(
//                Gamepads.gamepad1().leftStickY(),
//                Gamepads.gamepad1().leftStickX(),
//                Gamepads.gamepad1().rightStickX(),
//                false
//        ).schedule();
        PedroComponent.follower().startTeleopDrive();
    }
    @Override
    public void onUpdate(){

//        PedroComponent.follower().setTeleOpDrive(
//                Gamepads.gamepad1().leftStickY().get(),
//                Gamepads.gamepad1().leftStickX().get(),
//                Gamepads.gamepad1().rightStickX().get(),
//                false
//        );
        follower().setTeleOpDrive(
                gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                true
        );
        TelemetryManager.getInstance().print(telemetry);
    }
    @Override
    public void onStop(){
        TelemetryManager.getInstance().reset();
    }
}
