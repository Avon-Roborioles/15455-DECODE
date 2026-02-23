package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackShootPose;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackSpike2End;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackSpike2Start;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackSpike3End;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackSpike3Start;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose1;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose2;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose3;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose4;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose5;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose6;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose7;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redLeavePose;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathConstraints;
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
public class HPRedBack extends NextFTCOpMode {
    public HPRedBack(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower),
                PoseTrackerComponent.INSTANCE,
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.RED)
        );
    }
    public static PathConstraints constraints=new PathConstraints(0.99, 100, 1, .1);

    public void onStartButtonPressed(){

        DrumSubsystem.INSTANCE.useObelisk();
        PedroComponent.follower().setPose(redBackStart);
        DrumSubsystem.INSTANCE.readyAuto();
        Path startTurnToShoot = new Path(
                new BezierLine(
                        redBackStart,
                        redBackShootPose
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(redBackStart.getHeading(), redBackShootPose.getHeading());
        Path backToIntake3 = new Path(
                new BezierLine(
                        redBackShootPose,
                        redBackSpike3Start
                )
        );
        backToIntake3.setLinearHeadingInterpolation(redBackShootPose.getHeading(), redBackSpike3Start.getHeading());
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);
        Path intake3 = new Path(
                new BezierLine(

                        redBackSpike3Start,
                        redBackSpike3End
                )
        );
        intake3.setLinearHeadingInterpolation(0,0);
        FollowPath backToIntake3Command = new FollowPath(backToIntake3);
        FollowPath intake3Command = new FollowPath(intake3);

        Path intake3ToShoot = new Path(
                new BezierLine(
                        redBackSpike3End,
                        redBackShootPose
                )
        );
        intake3ToShoot.setLinearHeadingInterpolation(redBackSpike3End.getHeading(), redBackShootPose.getHeading());

        Path shootToIntakeHPZone = new Path(
                new BezierLine(
                        redBackShootPose,
                        redBackSpike2Start
                )
        );
        shootToIntakeHPZone.setLinearHeadingInterpolation(redBackShootPose.getHeading(), redBackSpike2Start.getHeading());

        Path intakeHP = new Path(
                new BezierLine(
                        redBackSpike2Start,
                        redBackSpike2End
                )
        );
        intakeHP.setLinearHeadingInterpolation(redBackSpike2Start.getHeading(), redBackSpike2End.getHeading());

        Path intakeHpToShoot = new Path(
                new BezierLine(
                        redHPZoneIntakePose7,
                        redBackShootPose
                )
        );
        intakeHpToShoot.setLinearHeadingInterpolation(redHPZoneIntakePose7.getHeading(), redBackShootPose.getHeading());
        Path toCenter = new Path(
                new BezierLine(
                        redBackShootPose,
                        redLeavePose
                )
        );
        toCenter.setLinearHeadingInterpolation(redBackShootPose.getHeading(), redLeavePose.getHeading());
        Path path1 = new Path(
                new BezierLine(
                        redBackShootPose,
                        redHPZoneIntakePose1
                ),
                constraints
        );
        path1.setLinearHeadingInterpolation(redBackShootPose.getHeading(),redHPZoneIntakePose1.getHeading());

        Path path2 = new Path(
                new BezierLine(
                        redHPZoneIntakePose1,
                        redHPZoneIntakePose2
                )
        );
        path2.setLinearHeadingInterpolation(redHPZoneIntakePose1.getHeading(), redHPZoneIntakePose2.getHeading());
        Path path3 = new Path(
                new BezierLine(
                        redHPZoneIntakePose2,
                        redHPZoneIntakePose3
                )
        );
        path3.setLinearHeadingInterpolation(redHPZoneIntakePose2.getHeading(), redHPZoneIntakePose3.getHeading());
        Path path4 = new Path(
                new BezierLine(
                        redHPZoneIntakePose3,
                        redHPZoneIntakePose4
                )
        );
        path4.setLinearHeadingInterpolation(redHPZoneIntakePose3.getHeading(), redHPZoneIntakePose4.getHeading());
        Path path5 = new Path(
                new BezierLine(
                        redHPZoneIntakePose4,
                        redHPZoneIntakePose5
                )
        );
        path5.setLinearHeadingInterpolation(redHPZoneIntakePose4.getHeading(), redHPZoneIntakePose5.getHeading());
        Path path6 = new Path(
                new BezierLine(
                        redHPZoneIntakePose5,
                        redHPZoneIntakePose6
                )
        );
        path6.setLinearHeadingInterpolation(redHPZoneIntakePose5.getHeading(), redHPZoneIntakePose6.getHeading());
        Path path7 = new Path(
                new BezierLine(
                        redHPZoneIntakePose6,
                        redHPZoneIntakePose7
                )
        );
        path7.setLinearHeadingInterpolation(redHPZoneIntakePose6.getHeading(), redHPZoneIntakePose7.getHeading());


        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
        Command autoRoutine = new SequentialGroup(

                new ParallelGroup(
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject,
                                new Delay(.5)
                        ),
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

                new BetterParallelRaceGroup(
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
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        DrumSubsystem.INSTANCE.servoEject
                ),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),
                new BetterParallelRaceGroup(

                        new ParallelGroup(
                                new SequentialGroup(
                                        new InstantCommand(()->PedroComponent.follower().setMaxPower(.75)),
                                        new FollowPath(path1),

                                        new FollowPath(path2),
                                        new FollowPath(path3),
                                        new FollowPath(path4),
                                        new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                                        new FollowPath(path5),

                                        new FollowPath(path6)
                                ),
                                DrumSubsystem.INSTANCE.intakeThreeBallsWithPause
                        ),
                        new Delay(10)
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new ParallelGroup(
                        new FollowPath(intakeHpToShoot),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        DrumSubsystem.INSTANCE.servoEject
                ),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
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
}
