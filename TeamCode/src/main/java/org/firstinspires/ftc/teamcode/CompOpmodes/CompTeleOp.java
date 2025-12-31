package org.firstinspires.ftc.teamcode.CompOpmodes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.Commands.PatternSetCommand;
import org.firstinspires.ftc.teamcode.Commands.PedroDriveCommand;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;



public abstract class CompTeleOp extends NextFTCOpMode {


    PedroDriverControlled fieldCentric;
    PedroDriveCommand robotCentric;
    Follower follower;

    public CompTeleOp(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE, LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE
        );
    }


    @Override
    public void onStartButtonPressed(){


        BindingManager.update();
        follower=PedroComponent.follower();
        follower.setPose(new Pose());
        Gamepads.gamepad2().dpadUp().whenBecomesTrue(new PatternSetCommand());
        Gamepads.gamepad1().rightTrigger().atLeast(.7).whenBecomesTrue(DrumSubsystem.INSTANCE.intakeThreeBalls);
        Gamepads.gamepad1().leftTrigger().atLeast(.7).whenBecomesTrue(
                new SequentialGroup(
                        new ParallelGroup(
                                DrumSubsystem.INSTANCE.servoEject,
                                LauncherSubsystem.INSTANCE.runToCalculatedPos
                        ),
                        new Delay(.5),
                        //new InstantCommand(()->new TelemetryItem(()->"Running Finished")),
                        DrumSubsystem.INSTANCE.shootPattern,
                        new Delay(2),
                        new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.schedule())


                )
        );
        Gamepads.gamepad1().rightBumper().whenBecomesTrue(DrumSubsystem.INSTANCE.intakeThreeBallsWithPause);
        Gamepads.gamepad1().x().whenBecomesTrue(DrumSubsystem.INSTANCE.stopIntakeWheels);
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(DrumSubsystem.INSTANCE.zero);
        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(new SequentialGroup(
                LauncherSubsystem.INSTANCE.runToCalculatedPos,

                DrumSubsystem.INSTANCE.rapidOuttake
        ));
        fieldCentric= new PedroDriverControlled(

                Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
                Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
                Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
                false
        );
        fieldCentric.requires(DriveSubsystem.INSTANCE);
        robotCentric=new PedroDriveCommand(
                follower,
                Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
                Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
                Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
                true
        );
        robotCentric.requires(DriveSubsystem.INSTANCE);

        //fieldCentric.schedule();
        Gamepads.gamepad1().rightStickButton().whenBecomesTrue(robotCentric);
        Gamepads.gamepad1().rightStickButton().whenBecomesFalse(fieldCentric);
               hasStarted=false;
    }

    boolean hasStarted = false;

    @Override
    public void onUpdate(){
        if (!hasStarted){
            hasStarted=true;
            fieldCentric.schedule();
        }

//        follower().setTeleOpDrive(
//                gamepad1.left_stick_y,
//                gamepad1.left_stick_x,
//                -.25*gamepad1.right_stick_x,
//                true
//        );
//        if (true||!CommandManager.INSTANCE.hasCommandsUsing(DriveSubsystem.INSTANCE)){
//            DriveSubsystem.INSTANCE.normalDrive.schedule();
//        }
        BindingManager.update();

    }

}
