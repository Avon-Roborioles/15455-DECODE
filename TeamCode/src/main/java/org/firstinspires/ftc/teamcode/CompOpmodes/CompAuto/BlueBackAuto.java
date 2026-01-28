package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackPose2;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackPose2;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackPose3;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackPose4;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakeEnd;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakeStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueLeavePose;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.PoseTrackerComponent;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.ParallelRaceGroup;
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
                        blueBackPose2
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(blueBackStart.getHeading(),blueBackPose2.getHeading());
        Path backToIntake3 = new Path(
                new BezierLine(
                        blueBackPose2,
                        blueBackPose3
                )
        );
        backToIntake3.setLinearHeadingInterpolation(blueBackPose2.getHeading(), blueBackPose3.getHeading());
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);
        Path intake3 = new Path(
                new BezierLine(

                        blueBackPose3,
                        blueBackPose4
                )
        );
        intake3.setLinearHeadingInterpolation(blueBackPose3.getHeading(),blueBackPose4.getHeading());
        FollowPath backToIntake3Command = new FollowPath(backToIntake3);
        FollowPath intake3Command = new FollowPath(intake3);

        Path intake3ToShoot = new Path(
                new BezierLine(
                        blueBackPose4,
                        blueBackPose2
                )
        );
        intake3ToShoot.setLinearHeadingInterpolation(blueBackPose4.getHeading(), blueBackPose2.getHeading());

        Path shootToIntakeHPZone = new Path(
                new BezierLine(
                        blueBackPose2,
                        blueHPZoneIntakeStart
                )
        );
        shootToIntakeHPZone.setLinearHeadingInterpolation(blueBackPose2.getHeading(), blueHPZoneIntakeStart.getHeading());

        Path intakeHP = new Path(
                new BezierLine(
                        blueHPZoneIntakeStart,
                        blueHPZoneIntakeEnd
                )
        );
        intakeHP.setLinearHeadingInterpolation(blueHPZoneIntakeStart.getHeading(), blueHPZoneIntakeEnd.getHeading());

        Path intakeHpToShoot = new Path(
                new BezierLine(
                        blueHPZoneIntakeEnd,
                        blueBackPose2
                )
        );
        intakeHpToShoot.setLinearHeadingInterpolation(blueHPZoneIntakeEnd.getHeading(), blueBackPose2.getHeading());
        Path toCenter = new Path(
                new BezierLine(
                        blueBackPose2,
                        blueLeavePose
                )
        );
        toCenter.setLinearHeadingInterpolation(blueBackPose2.getHeading(), blueLeavePose.getHeading());


        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
        Command autoRoutine = new SequentialGroup(

                new ParallelGroup(
                        DrumSubsystem.INSTANCE.secureBalls,
                        new SequentialGroup(
                                LimelightSubsystem.INSTANCE.detectObelisk,
                                startTurnToShootCommand
                        ),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos
                ),

                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,

                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),

                new ParallelRaceGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBallsWithPause,
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
                                LauncherSubsystem.INSTANCE.runToCalculatedPos
                        ),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),


                new ParallelRaceGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBallsWithPause,

                        new SequentialGroup(
                                new FollowPath(shootToIntakeHPZone),
                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.45)),
                                new FollowPath(intakeHP),
                                new Delay(3)

                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new ParallelRaceGroup(
                        new FollowPath(intakeHpToShoot),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos
                ),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(LauncherSubsystem.INSTANCE::stop),
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