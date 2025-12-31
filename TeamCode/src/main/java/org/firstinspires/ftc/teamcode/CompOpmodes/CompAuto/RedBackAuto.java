package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathBuilder;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Commands.PedroDriveCommand;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.PoseTrackerComponent;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
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
        //PedroComponent.follower().setPose(new Pose(-69.667,-.525,Math.toRadians(-177.57)));
        DrumSubsystem.INSTANCE.readyAuto();
        Path startTurnToShoot = new Path(
                new BezierLine(
                        new Pose(-69.667,-.525),
                        new Pose(-67.24,-2.7)
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(Math.toRadians(-177),Math.toRadians(165.8));
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);

        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
        Command autoRoutine = new SequentialGroup(
                LimelightSubsystem.INSTANCE.detectObelisk,
                startTurnToShootCommand,
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootPattern
        );
        autoRoutine.schedule();

    }
}
