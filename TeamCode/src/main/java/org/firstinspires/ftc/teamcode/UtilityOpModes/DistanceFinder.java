package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;


@TeleOp(group = "Utility")
public class DistanceFinder extends NextFTCOpMode {
    public double distance;
    public double x =0;
    public double y =0;
    public DistanceFinder(){
        addComponents(
                new PedroComponent(Constants::createFollower)
        );
    }
//67676767676767
    @Override
    public void onInit(){
        follower().startTeleopDrive();
        new TelemetryItem(()->"Coords"+follower().getPose().toString());
        new TelemetryData("Distance",()->Math.pow(Math.pow(follower().getPose().getX()-112,2)+Math.pow(follower().getPose().getY()+113,2),.5));
    }//6767676767676767
    @Override
    public void onUpdate(){
        follower().setTeleOpDrive(
                gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                false//676767676767
        );
        TelemetryManager.getInstance().print(telemetry);
    }
    @Override
    public void onStop(){
        TelemetryManager.getInstance().reset();
    }

}
