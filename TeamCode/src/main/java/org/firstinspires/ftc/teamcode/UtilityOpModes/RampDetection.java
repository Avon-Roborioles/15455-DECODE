package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp
public class RampDetection extends NextFTCOpMode {

    public RampDetection(){
        addComponents(
                new TelemetryComponent(),
                new SubsystemComponent(LimelightSubsystem.INSTANCE),
                new LoopTimeComponent()
        );
    }

    public void onInit(){
        new TelemetryItem(LimelightSubsystem.INSTANCE::lookAtRamp);
    }
}
