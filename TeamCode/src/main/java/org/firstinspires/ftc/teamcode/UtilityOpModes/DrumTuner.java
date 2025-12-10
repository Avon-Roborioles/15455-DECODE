package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp(group = "Tuning")
public class DrumTuner extends NextFTCOpMode {

    public DrumTuner(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE),
                BindingsComponent.INSTANCE
        );
    }

    public void onInit(){
        Gamepads.gamepad1().a().whenBecomesTrue(DrumSubsystem.INSTANCE.plusOneRev);
    }
    public void onUpdate(){
        TelemetryManager.getInstance().print(telemetry);

    }
    public void onStop(){
        TelemetryManager.getInstance().reset();
    }
}
