package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class LauncherOpMode extends NextFTCOpMode {


    public LauncherOpMode(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE),
                BindingsComponent.INSTANCE
        );
    }

    public void onStartButtonPressed(){
        Gamepads.gamepad1().dpadUp().whenBecomesFalse(LauncherSubsystem.INSTANCE::calculateVelocity);
        Gamepads.gamepad1().dpadDown().whenBecomesFalse(LauncherSubsystem.INSTANCE::decreaseRPMby100);
    }

    @Override
    public void onUpdate(){

        TelemetryManager.getInstance().print(telemetry);
    }
    @Override
    public void onStop(){
        TelemetryManager.getInstance().reset();
    }
}
