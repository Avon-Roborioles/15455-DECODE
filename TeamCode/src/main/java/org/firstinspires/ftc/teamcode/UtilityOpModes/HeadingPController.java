package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.NextFTCOpMode;

import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp(group = "Test")
@Configurable
public class HeadingPController extends NextFTCOpMode {
    public static double kP = -.01;
    public static double kI=.000001;
    PedroDriverControlled driverControlled;
    Limelight3A limelight3A;
    private double integral =0;
    public HeadingPController(){
        addComponents(
                new PedroComponent(Constants::createFollower),
                new TelemetryComponent()
        );
    }
    @Override
    public void onInit(){
        limelight3A = hardwareMap.get(Limelight3A.class,"limeLight");
        limelight3A.start();
        limelight3A.pipelineSwitch(0);
        limelight3A.start();
        follower().startTeleopDrive();
        new TelemetryData("Integral",()->integral);
    }
    @Override
    public void onUpdate(){

        double heading = -gamepad1.right_stick_x;
        LLResult result = limelight3A.getLatestResult();
        if (Math.abs(result.getTx())!=0||result.isValid()){

            TelemetryManager.getInstance().addTempTelemetry("Limelight sees: "+result.getTx());
            integral+=result.getTx()*kI;
            heading = kP*result.getTx()+integral;
        } else {
            TelemetryManager.getInstance().addTempTelemetry("Nothing found");
            integral =0;
        }

        follower().setTeleOpDrive(-gamepad1.left_stick_y, -gamepad1.left_stick_x, heading,false);
    }
}
