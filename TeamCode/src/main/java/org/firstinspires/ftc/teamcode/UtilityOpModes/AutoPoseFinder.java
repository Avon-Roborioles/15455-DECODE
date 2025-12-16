package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp(group = "Utility")
public class AutoPoseFinder extends NextFTCOpMode {
    public AutoPoseFinder(){
        addComponents(
                new PedroComponent(Constants::createFollower)
        );

    }
    @Override
    public void onInit(){
        follower().setPose(new Pose(0,0,0));

    }
    @Override
    public void onUpdate() {
        telemetry.addData("Pose",follower().getPose());
        telemetry.update();
    }
}
