package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.*;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

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
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@Autonomous
public class RedBackAuto extends NextFTCOpMode {
    public RedBackAuto(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE,LimelightSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower),
                new PoseTrackerComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE
        );
    }
    public void onInit(){

    }

    public void onStartButtonPressed(){
        PedroComponent.follower().setPose(redBackStart);
        DrumSubsystem.INSTANCE.readyAuto();
        Path startTurnToShoot = new Path(
                new BezierLine(
                        redBackStart,
                        redBackPose2
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(redBackStart.getHeading(),redBackPose2.getHeading());
        Path backToIntake3 = new Path(
                new BezierLine(
                        redBackPose2,
                        redBackPose3
                )
        );
        backToIntake3.setLinearHeadingInterpolation(redBackPose2.getHeading(), redBackPose3.getHeading());
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);
        Path intake3 = new Path(
                new BezierLine(

                        redBackPose3,
                        redBackPose4
                )
        );
        intake3.setLinearHeadingInterpolation(0,0);
        FollowPath backToIntake3Command = new FollowPath(backToIntake3);
        FollowPath intake3Command = new FollowPath(intake3);

        Path intake3ToShoot = new Path(
                new BezierLine(
                        redBackPose4,
                        redBackPose2
                )
        );
        intake3ToShoot.setLinearHeadingInterpolation(redBackPose4.getHeading(), redBackPose2.getHeading());

        Path shootToIntakeHPZone = new Path(
                new BezierLine(
                        redBackPose2,
                        redHPZoneIntakeStart
                )
        );
        shootToIntakeHPZone.setLinearHeadingInterpolation(redBackPose2.getHeading(), redHPZoneIntakeStart.getHeading());

        Path intakeHP = new Path(
                new BezierLine(
                        redHPZoneIntakeStart,
                        redHPZoneIntakeEnd
                )
        );
        intakeHP.setLinearHeadingInterpolation(redHPZoneIntakeStart.getHeading(), redHPZoneIntakeEnd.getHeading());

        Path intakeHpToShoot = new Path(
                new BezierLine(
                        redHPZoneIntakeEnd,
                        redBackPose2
                )
        );
        intakeHpToShoot.setLinearHeadingInterpolation(redHPZoneIntakeEnd.getHeading(), redBackPose2.getHeading());


        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
        Command autoRoutine = new SequentialGroup(
                new ParallelGroup(
                        new SequentialGroup(
                                LimelightSubsystem.INSTANCE.detectObelisk,
                                startTurnToShootCommand
                        ),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos
                ),

                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                DrumSubsystem.INSTANCE.shootFirstPattern,
                LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,

                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),

                new ParallelGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBallsWithPause,

                        new SequentialGroup(
                                backToIntake3Command,
                                //new Delay(500),
                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.75)),
                                intake3Command

                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new FollowPath(intake3ToShoot),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),


                new ParallelGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBallsWithPause,

                        new SequentialGroup(
                                new FollowPath(shootToIntakeHPZone),
                                //new Delay(500),
                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.5)),
                                new FollowPath(intakeHP)

                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new FollowPath(intakeHpToShoot),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern

        );
        autoRoutine.schedule();

    }
}