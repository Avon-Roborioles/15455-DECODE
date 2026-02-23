package org.firstinspires.ftc.teamcode.UtilityOpModes;

import static org.firstinspires.ftc.teamcode.RobotConfig.TeleOpConstants.debugOperationLayer;
import static org.firstinspires.ftc.teamcode.RobotConfig.TeleOpConstants.normalOperationLayer;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Commands.PatternSetCommand;
import org.firstinspires.ftc.teamcode.Commands.PedroDriveCommand;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.PoseTrackerComponent;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp
public  class TestOpMode extends NextFTCOpMode {


    public Servo servo;

    public TestOpMode(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE, LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.RED)
        );
    }




    @Override
    public void onStartButtonPressed(){
        servo =hardwareMap.get(Servo.class,"driverLight");
        //BindingManager.update();
        Follower follower=PedroComponent.follower();
        follower.setPose(RobotConfig.FieldConstants.center);
        Gamepads.gamepad2().dpadUp().inLayer(normalOperationLayer).whenBecomesTrue(new PatternSetCommand());
        Gamepads.gamepad1().rightTrigger().atLeast(.7).inLayer(normalOperationLayer).whenBecomesTrue(DrumSubsystem.INSTANCE.intakeThreeBalls);

        Gamepads.gamepad1().rightBumper().inLayer(normalOperationLayer).whenBecomesTrue(DrumSubsystem.INSTANCE.intakeThreeBallsWithPause);

        Gamepads.gamepad1().b().inLayer(normalOperationLayer).whenBecomesTrue(DrumSubsystem.INSTANCE.spitOutIntakeWheels);
        Gamepads.gamepad1().b().inLayer(normalOperationLayer).whenBecomesFalse(DrumSubsystem.INSTANCE.stopIntakeWheels);


        Command normalDrive= new PedroDriveCommand(
                Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
                Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
                Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
                false,
                Math.toRadians(0)
        ).requires(DriveSubsystem.INSTANCE);

        Command robotCentric = new PedroDriveCommand(
                Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
                Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
                Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
                true,
                Math.toRadians(0)
        ).requires(DriveSubsystem.INSTANCE);

        Command realNormalDrive = new SequentialGroup(
                new Delay(.015),
                normalDrive
        );
        Command realRobotCentric = new SequentialGroup(
                new Delay(.015),
                robotCentric
        );

        DrumSubsystem.INSTANCE.stopUseObelisk();

        Gamepads.gamepad1().leftStickButton().whenBecomesTrue(realRobotCentric);
        Gamepads.gamepad1().leftStickButton().whenBecomesFalse(realNormalDrive);
        DriveSubsystem.INSTANCE.setDefaultCommand(normalDrive);

        Gamepads.gamepad2().dpadDown().inLayer(normalOperationLayer).whenBecomesTrue(
                new InstantCommand(
                        ()->{
                            Pose baseZone = RobotConfig.FieldConstants.redBasePose;
                            if (AllianceComponent.getColor().equals(AllianceColor.BLUE)) baseZone = RobotConfig.FieldConstants.blueBasePose;
                            Path path = new Path(
                                    new BezierLine(
                                            PedroComponent.follower().getPose(),
                                            baseZone
                                    )
                            );
                            path.setLinearHeadingInterpolation(PedroComponent.follower().getHeading(), baseZone.getHeading());
                            new FollowPath(path).schedule();
                        }
                )
        );
        Gamepads.gamepad1().leftBumper().whenBecomesTrue(DriveSubsystem.INSTANCE.targetDrive);

        Command aprilTagTracking = new SequentialGroup(
                LauncherSubsystem.INSTANCE.runToLowRPM,
                new SequentialGroup(
                        DrumSubsystem.INSTANCE.servoEject,
                        new Delay(.5),
                        DrumSubsystem.INSTANCE.shootPattern,
                        new Delay(.5),
                        new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.schedule())
                ),
                new InstantCommand(follower::breakFollowing),
                new InstantCommand(()->servo.setPosition(.388))

        ).requires(DriveSubsystem.INSTANCE).setInterruptible(true).named("April Tag Alignment");
        Gamepads.gamepad1().leftTrigger().atLeast(.7).inLayer(normalOperationLayer).whenBecomesTrue(aprilTagTracking);

        BindingManager.setLayer(RobotConfig.TeleOpConstants.normalOperationLayer);

        Gamepads.gamepad2().rightBumper()
                .inLayer(normalOperationLayer).whenBecomesTrue(
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.manual,
                                new InstantCommand(()->BindingManager.setLayer(debugOperationLayer)),
                                LauncherSubsystem.INSTANCE.stop
                        )
                );
        Gamepads.gamepad1().a().whenBecomesTrue(DrumSubsystem.INSTANCE::resetSensor);

        //DEBUG COMMANDS

        Gamepads.gamepad2().b().inLayer(debugOperationLayer).whenBecomesTrue(DrumSubsystem.INSTANCE.rotateIntakeWheels);
        Gamepads.gamepad2().b().inLayer(debugOperationLayer).whenBecomesFalse(DrumSubsystem.INSTANCE.stopIntakeWheels);

        Gamepads.gamepad2().rightBumper().inLayer(debugOperationLayer).whenBecomesTrue(LauncherSubsystem.INSTANCE::increaseRPMby50);
        Gamepads.gamepad2().leftBumper().inLayer(debugOperationLayer).whenBecomesTrue(LauncherSubsystem.INSTANCE::decreaseRPMby50);
        Gamepads.gamepad2().y().inLayer(debugOperationLayer).whenBecomesTrue(LauncherSubsystem.INSTANCE::calculateVelocity);
        Gamepads.gamepad2().rightStickButton().inLayer(debugOperationLayer).whenBecomesTrue(LauncherSubsystem.INSTANCE::stop);
        Gamepads.gamepad2().rightBumper()
                .inLayer(debugOperationLayer).whenBecomesTrue(
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.manual,
                                new InstantCommand(()->BindingManager.setLayer(normalOperationLayer))
                        )
                );
        Gamepads.gamepad2().rightTrigger().atLeast(.7).inLayer(debugOperationLayer).whenBecomesTrue(DrumSubsystem.INSTANCE.servoEject);
        Gamepads.gamepad2().leftTrigger().atLeast(.7).inLayer(debugOperationLayer).whenBecomesTrue(DrumSubsystem.INSTANCE.servoFreeRotate);
        Gamepads.gamepad2().a().inLayer(debugOperationLayer).whenBecomesTrue(
                ()->{
                    DrumSubsystem.INSTANCE.resetZero();
                    DrumSubsystem.INSTANCE.resetCompartments();
                }
        );
        new TelemetryItem(()->"Current Layer: " +BindingManager.getLayer());
        new TelemetryItem(()->"G2 Right Bumper"+Gamepads.gamepad2().rightBumper().get());
    }



    @Override
    public void onUpdate(){
        BindingManager.update();

    }
    @Override
    public void onStop(){
        CommandManager.INSTANCE.cancelAll();
    }

}
