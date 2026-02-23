package org.firstinspires.ftc.teamcode.UtilityOpModes;


import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.*;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto.RedBackAuto;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.PoseTrackerComponent;
import org.firstinspires.ftc.teamcode.RobotConfig;
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
public class RedHPZoneTest extends NextFTCOpMode {
    public static PathConstraints constraints=new PathConstraints(0.99, 100, 1, .1);

    public RedHPZoneTest(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE, LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new PedroComponent(Constants::createFollower),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                PoseTrackerComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.RED)
        );
    }


    public void onStartButtonPressed(){
        PedroComponent.follower().setPose(redBackStart);
        Path path1 = new Path(
                new BezierLine(
                        redBackStart,
                        redHPZoneIntakePose1
                ),
                constraints
        );
        path1.setLinearHeadingInterpolation(redBackStart.getHeading(),redHPZoneIntakePose1.getHeading());

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
