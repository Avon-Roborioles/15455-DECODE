package org.firstinspires.ftc.teamcode;

import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp(group = "Comp")
public class CompTeleOp extends NextFTCOpMode {
    public CompTeleOp(){
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
                        DrumSubsystem.INSTANCE.servoEject,
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        new InstantCommand(()->new TelemetryItem(()->"Running Finished")),
<<<<<<< Updated upstream
<<<<<<< Updated upstream
<<<<<<< Updated upstream:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/TestTeleop.java
                        // need to add a shootAll, which rotates the drum a full 360 without stopping at each slot
                        DrumSubsystem.INSTANCE.shootAny,
                        DrumSubsystem.INSTANCE.shootAny,
                        DrumSubsystem.INSTANCE.shootAny
=======
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
                        DrumSubsystem.INSTANCE.shootPurple,
                        DrumSubsystem.INSTANCE.shootGreen,
                        DrumSubsystem.INSTANCE.shootPurple,
                        LauncherSubsystem.INSTANCE.stop

<<<<<<< Updated upstream
<<<<<<< Updated upstream
>>>>>>> Stashed changes:TeamCode/src/main/java/org/firstinspires/ftc/teamcode/CompTeleOp.java
=======
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
                )
        );
        Gamepads.gamepad1().x().whenBecomesTrue(DrumSubsystem.INSTANCE.stopIntakeWheels);
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(DrumSubsystem.INSTANCE.zero);
        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(new SequentialGroup(
                LauncherSubsystem.INSTANCE.runToCalculatedPos,

                DrumSubsystem.INSTANCE.rapidOuttake
        ));
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


        follower().setTeleOpDrive(
                gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                -.25*gamepad1.right_stick_x,
                true
        );
        TelemetryManager.getInstance().print(telemetry);
//        if (true||!CommandManager.INSTANCE.hasCommandsUsing(DriveSubsystem.INSTANCE)){
//            DriveSubsystem.INSTANCE.normalDrive.schedule();
//        }
        BindingManager.update();

    }
    @Override
    public void onStop(){
        TelemetryManager.getInstance().reset();
    }
}
