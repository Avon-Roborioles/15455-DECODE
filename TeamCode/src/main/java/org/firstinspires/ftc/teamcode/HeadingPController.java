package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.hardware.driving.DriverControlledCommand;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp
public class HeadingPController extends NextFTCOpMode {
    PedroDriverControlled driverControlled;
    Limelight3A limelight3A;
    public HeadingPController(){
        addComponents(
                new PedroComponent(Constants::createFollower)
        );
    }
    @Override
    public void onInit(){
        limelight3A = hardwareMap.get(Limelight3A.class,"limeLight");
        limelight3A.start();
        limelight3A.pipelineSwitch(0);
        limelight3A.start();
        follower().startTeleopDrive();
    }
    @Override
    public void onUpdate(){
        double kP=-.01;
        double heading = -gamepad1.right_stick_x;
        LLResult result = limelight3A.getLatestResult();
        if (Math.abs(result.getTx())!=0||result.isValid()){
            heading = kP*result.getTx();
            telemetry.addData("Limelight sees",result.getTx());
        } else {
            telemetry.addLine("Nothing found");
        }

        follower().setTeleOpDrive(-gamepad1.left_stick_y, -gamepad1.left_stick_x, heading,false);
        telemetry.update();
    }
}
