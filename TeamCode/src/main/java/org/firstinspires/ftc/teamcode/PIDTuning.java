package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robocol.Command;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.MotorEx;
import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp
@Disabled
public class PIDTuning extends NextFTCOpMode {

    Limelight3A limelight;


    public PIDTuning(){
        addComponents(
                new PedroComponent(Constants::createFollower),
                BulkReadComponent.INSTANCE
        );
        TelemetryItem data = new TelemetryItem(()->"Using tag:"+usingTag);
    }

    @Override
    public void onInit(){
        follower().startTeleopDrive();
        limelight = hardwareMap.get(Limelight3A.class,"limeLight");
        limelight.start();
        limelight.pipelineSwitch(0);
    }

    boolean usingTag=false;
    double kP = -.0001;

    @Override
    public void onUpdate(){

        LLResult result = limelight.getLatestResult();
        usingTag = result.isValid();
        if (usingTag) {
            follower().setTeleOpDrive(
                    gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    result.getTx()*kP,
                    false

                    );
        } else {
            follower().setTeleOpDrive(
                    gamepad1.left_stick_y,
                    gamepad1.left_stick_x,
                    gamepad1.right_stick_x,
                    false
                    );
        }

        TelemetryManager.getInstance().print(telemetry);
    }





    }

