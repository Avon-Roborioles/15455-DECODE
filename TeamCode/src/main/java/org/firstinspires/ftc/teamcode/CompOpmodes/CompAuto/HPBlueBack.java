package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;


import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.*;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackSpike3Start;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Commands.BetterParallelRaceGroup;
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
public class HPBlueBack extends NextFTCOpMode {
    public HPBlueBack(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower),
                PoseTrackerComponent.INSTANCE,
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.BLUE)
        );
    }
    public static PathConstraints constraints=new PathConstraints(0.99, 100, 1, .1);

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
        intake3.setLinearHeadingInterpolation(blueBackSpike3Start.getHeading(),blueBackSpike3End.getHeading());
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
                        blueHPZoneIntakePose6,
                        blueShootPose
                )
        );
        intakeHpToShoot.setLinearHeadingInterpolation(blueHPZoneIntakePose6.getHeading(), RobotConfig.PoseConstants.headingInverter(Math.toRadians(248.5)));
        Path toCenter = new Path(
                new BezierLine(
                        blueShootPose,
                        blueLeavePose
                )
        );
        toCenter.setLinearHeadingInterpolation(blueShootPose.getHeading(), blueLeavePose.getHeading());
        Path path1 = new Path(
                new BezierLine(
                        blueShootPose,
                        blueHPZoneIntakePose1
                ),
                constraints
        );
        path1.setLinearHeadingInterpolation(blueShootPose.getHeading(),blueHPZoneIntakePose1.getHeading());

        Path path2 = new Path(
                new BezierLine(
                        blueHPZoneIntakePose1,
                        blueHPZoneIntakePose2
                )
        );
        path2.setLinearHeadingInterpolation(blueHPZoneIntakePose1.getHeading(), blueHPZoneIntakePose2.getHeading());
        Path path3 = new Path(
                new BezierLine(
                        blueHPZoneIntakePose2,
                        blueHPZoneIntakePose3
                )
        );
        path3.setLinearHeadingInterpolation(blueHPZoneIntakePose2.getHeading(), blueHPZoneIntakePose3.getHeading());
        Path path4 = new Path(
                new BezierLine(
                        blueHPZoneIntakePose3,
                        blueHPZoneIntakePose4
                )
        );
        path4.setLinearHeadingInterpolation(blueHPZoneIntakePose3.getHeading(), blueHPZoneIntakePose4.getHeading());
        Path path5 = new Path(
                new BezierLine(
                        blueHPZoneIntakePose4,
                        blueHPZoneIntakePose5
                )
        );
        path5.setLinearHeadingInterpolation(blueHPZoneIntakePose4.getHeading(), blueHPZoneIntakePose5.getHeading());
        Path path6 = new Path(
                new BezierLine(
                        blueHPZoneIntakePose5,
                        blueHPZoneIntakePose6
                )
        );
        path6.setLinearHeadingInterpolation(blueHPZoneIntakePose5.getHeading(), blueHPZoneIntakePose6.getHeading());
        Path path7 = new Path(
                new BezierLine(
                        blueHPZoneIntakePose6,
                        blueHPZoneIntakePose7
                )
        );
        path7.setLinearHeadingInterpolation(blueHPZoneIntakePose6.getHeading(), blueHPZoneIntakePose7.getHeading());


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
                                DrumSubsystem.INSTANCE.intakeThreeBallsWithPauseNoStop
                        ),
                        new Delay(7)
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
