package org.firstinspires.ftc.teamcode.UtilityOpModes;


import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueBackStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose1;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose2;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose3;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose4;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose5;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose6;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueHPZoneIntakePose7;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.blueShootPose;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redBackStart;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose1;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose2;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose3;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose4;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose5;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose6;
import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.redHPZoneIntakePose7;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathConstraints;
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

import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@Configurable
@Autonomous
public class BlueHPZoneTest extends NextFTCOpMode {
    public static PathConstraints constraints=new PathConstraints(0.99, 100, 1, .1);

    public BlueHPZoneTest(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE, LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                PoseTrackerComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.BLUE)
        );
    }


    public void onStartButtonPressed(){
        PedroComponent.follower().setPose(blueBackStart);
        Path path1 = new Path(
                new BezierLine(
                        blueShootPose,
                        blueHPZoneIntakePose1
                ),
                constraints
        );
        path1.setLinearHeadingInterpolation(blueBackStart.getHeading(),blueHPZoneIntakePose1.getHeading());

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
        new ParallelGroup(
                new SequentialGroup(
                        DrumSubsystem.INSTANCE.clearCompartments,
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
        ).schedule();
    }



}
