package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueShootPose;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackSpike3Start;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackSpike3End;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackSpike2End;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackSpike2Start;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueLeavePose;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Commands.BetterParallelRaceGroup;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.PoseTrackerComponent;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.UtilityCommands.ShootCommand;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@Autonomous
public class BlueBackAuto extends NextFTCOpMode {
    public BlueBackAuto(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE,LimelightSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower),
                PoseTrackerComponent.INSTANCE,
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.BLUE)
        );
    }
    public void onInit(){

    }

    public void onStartButtonPressed(){
        DrumSubsystem.INSTANCE.useObelisk();
        PedroComponent.follower().setPose(blueBackStart);
        DrumSubsystem.INSTANCE.readyAuto();
        Path startTurnToShoot = new Path(
                new BezierLine(
                        blueBackStart,
                        blueShootPose
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(blueBackStart.getHeading(), blueShootPose.getHeading());
        Path backToIntake3 = new Path(
                new BezierLine(
                        blueShootPose,
                        blueBackSpike3Start
                )
        );
        backToIntake3.setLinearHeadingInterpolation(blueShootPose.getHeading(), blueBackSpike3Start.getHeading());
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);
        Path intake3 = new Path(
                new BezierLine(

                        blueBackSpike3Start,
                        blueBackSpike3End
                )
        );
        intake3.setLinearHeadingInterpolation(blueBackSpike3Start.getHeading(), blueBackSpike3End.getHeading());
        FollowPath backToIntake3Command = new FollowPath(backToIntake3);
        FollowPath intake3Command = new FollowPath(intake3);

        Path intake3ToShoot = new Path(
                new BezierLine(
                        blueBackSpike3End,
                        blueShootPose
                )
        );
        intake3ToShoot.setLinearHeadingInterpolation(blueBackSpike3End.getHeading(), blueShootPose.getHeading());

        Path shootToIntakeHPZone = new Path(
                new BezierLine(
                        blueShootPose,
                        blueBackSpike2Start
                )
        );
        shootToIntakeHPZone.setLinearHeadingInterpolation(blueShootPose.getHeading(), blueBackSpike2Start.getHeading());

        Path intakeHP = new Path(
                new BezierLine(
                        blueBackSpike2Start,
                        blueBackSpike2End
                )
        );
        intakeHP.setLinearHeadingInterpolation(blueBackSpike2Start.getHeading(), blueBackSpike2End.getHeading());

        Path intakeHpToShoot = new Path(
                new BezierLine(
                        blueBackSpike2End,
                        blueShootPose
                )
        );
        intakeHpToShoot.setLinearHeadingInterpolation(blueBackSpike2End.getHeading(), blueShootPose.getHeading());
        Path toCenter = new Path(
                new BezierLine(
                        blueShootPose,
                        blueLeavePose
                )
        );
        toCenter.setLinearHeadingInterpolation(blueShootPose.getHeading(), blueLeavePose.getHeading());


        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
        Command autoRoutine = new SequentialGroup(
                new ParallelGroup(
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject
                        ),
                        new SequentialGroup(
                                new BetterParallelRaceGroup(
                                        LimelightSubsystem.INSTANCE.detectObelisk,
                                        new Delay(2)
                                ),
                                startTurnToShootCommand
                        ),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos
                ),
                ShootCommand.getShootCommand(),

                new BetterParallelRaceGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBallsWithPauseNoStop,
                        new SequentialGroup(
                                backToIntake3Command,
                                new ParallelGroup(
                                        new SequentialGroup(
                                                new Delay(.3),
                                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.5))
                                        ),
                                        intake3Command
                                ),
                                new Delay(3)
                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new ParallelGroup(
                        new FollowPath(intake3ToShoot),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        new SequentialGroup(
                                new Delay(.5),
                                DrumSubsystem.INSTANCE.stopIntakeWheels,
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject
                        )
                ),
                ShootCommand.getShootCommand(),


                new BetterParallelRaceGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBallsWithPauseNoStop,

                        new SequentialGroup(
                                new FollowPath(shootToIntakeHPZone),
                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.5)),
                                new ParallelGroup(
                                        new SequentialGroup(
                                                new Delay(.3),
                                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.5))
                                        ),
                                        new FollowPath(intakeHP)
                                ),
                                new Delay(3)

                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new ParallelGroup(
                        new FollowPath(intakeHpToShoot),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        new SequentialGroup(
                                new Delay(.5),
                                DrumSubsystem.INSTANCE.stopIntakeWheels,
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject
                        )
                ),
                ShootCommand.getShootCommand(),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new FollowPath(toCenter)

        );
        autoRoutine.schedule();

    }

    @Override
    public void onStop(){
        PedroComponent.follower().setMaxPower(1);
    }
}