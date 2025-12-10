package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

import dev.nextftc.core.commands.Command;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp(group = "Utility")
public class AutoRedPoseFinder extends NextFTCOpMode {
    public AutoRedPoseFinder(){
        addComponents(
                new PedroComponent(Constants::createFollower)
        );

    }
    @Override
    public void onInit(){
        follower().setPose(new Pose(0,0,3*Math.PI/2));

    }
    @Override
    public void onUpdate() {
        telemetry.addData("Pose",follower().getPose());
        telemetry.update();
    }
}
