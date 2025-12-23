package org.firstinspires.ftc.teamcode.CompAuto;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathBuilder;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Commands.PedroDriveCommand;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;

@Autonomous
public class RedBackAuto extends NextFTCOpMode {
    public RedBackAuto(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower)
        );
    }
    public void onInit(){
        PedroComponent.follower().setPose(new Pose(-69.667,-.525,Math.toRadians(-177.57)));
        Path startTurnToShoot = new Path(
                new BezierLine(
                        new Pose(-69.667,-.525),
                        new Pose(-67.24,-2.7)
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(Math.toRadians(-177),Math.toRadians(165.8));
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);
        startTurnToShootCommand.schedule();
        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
    }

    public void onStartButtonPressed(){


    }
}
