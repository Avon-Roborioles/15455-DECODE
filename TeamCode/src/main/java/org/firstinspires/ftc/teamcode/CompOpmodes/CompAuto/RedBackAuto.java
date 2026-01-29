package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.*;
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
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
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
public class RedBackAuto extends NextFTCOpMode {
    public RedBackAuto(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE,LimelightSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower),
                PoseTrackerComponent.INSTANCE,
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.RED)
        );
    }
    public void onInit(){

    }

    public void onStartButtonPressed(){
        DrumSubsystem.INSTANCE.useObelisk();
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
        Path toCenter = new Path(
                new BezierLine(
                        redBackPose2,
                        redLeavePose
                )
        );
        toCenter.setLinearHeadingInterpolation(redBackPose2.getHeading(), redLeavePose.getHeading());


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
                        LauncherSubsystem.INSTANCE.runToCalculatedPos
                ),                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(LauncherSubsystem.INSTANCE::stop),
                new FollowPath(toCenter)

        );
        autoRoutine.schedule();

    }

    @Override
    public void onStop(){
        PedroComponent.follower().setMaxPower(1);
    }
}