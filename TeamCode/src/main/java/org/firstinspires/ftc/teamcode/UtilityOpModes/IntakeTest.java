package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;

import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class IntakeTest extends NextFTCOpMode {
    public IntakeTest(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE),
                new TelemetryComponent()
        );
    }
    public void onInit(){
        DrumSubsystem.INSTANCE.rotateIntakeWheels.schedule();
    }
}
