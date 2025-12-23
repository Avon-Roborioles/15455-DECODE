package org.firstinspires.ftc.teamcode.SubsystemOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp(group= "Subsystem")
public class LauncherOpMode extends NextFTCOpMode {


    public LauncherOpMode(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE),
                BindingsComponent.INSTANCE,
                new PedroComponent(Constants::createFollower),
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE
        );
    }

    public void onStartButtonPressed(){
        Gamepads.gamepad1().dpadUp().whenBecomesFalse(LauncherSubsystem.INSTANCE.runToCalculatedPos);
        Gamepads.gamepad1().dpadDown().whenBecomesFalse(LauncherSubsystem.INSTANCE::decreaseRPMby100);
    }



}
