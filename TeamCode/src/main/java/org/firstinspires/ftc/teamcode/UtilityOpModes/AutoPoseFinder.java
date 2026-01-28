package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp(group = "Utility")
public class AutoPoseFinder extends NextFTCOpMode {
    public AutoPoseFinder(){
        addComponents(
                new PedroComponent(Constants::createFollower),
                new TelemetryComponent()
        );

    }
    @Override
    public void onInit(){
        follower().setPose(RobotConfig.FieldConstants.center);
        new TelemetryItem(()->"Pose"+follower().getPose());

    }

}
