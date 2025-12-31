package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;

import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;

@Autonomous
@Configurable
public class IntakeSpeedTest extends NextFTCOpMode {

    public static double maxPower=.2;

    public IntakeSpeedTest(){
        addComponents(
                new SubsystemComponent(DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower)
        );
    }


    public void onStartButtonPressed(){
        DrumSubsystem.INSTANCE.setZero(0);
        Path forward = new Path(
                new BezierLine(
                        new Pose(0,0),
                        new Pose(20,0)
                )
        );
        PedroComponent.follower().setMaxPower(maxPower);
        FollowPath forwardComand = new FollowPath(forward);
        DrumSubsystem.INSTANCE.intakeThreeBalls.schedule();
        forwardComand.schedule();
    }
}
