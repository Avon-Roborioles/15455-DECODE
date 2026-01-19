package org.firstinspires.ftc.teamcode.SubsystemOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.utility.NullCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp(group = "Subsystem")
public class DrumOpMode extends NextFTCOpMode {

    public DrumOpMode(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE
        );
    }

    @Override
    public void onStartButtonPressed(){
//        Gamepads.gamepad1().leftBumper().whenBecomesTrue(DrumSubsystem.INSTANCE.shootPurple);
//        Gamepads.gamepad1().rightBumper().whenBecomesTrue(DrumSubsystem.INSTANCE.shootGreen);
//        Gamepads.gamepad1().dpadRight().whenBecomesTrue(DrumSubsystem.INSTANCE.intakeOneBall);
//        Gamepads.gamepad1().rightBumper().whenBecomesTrue(DrumSubsystem.INSTANCE.secureBalls);
        //Gamepads.gamepad1().dpadUp().whenBecomesTrue(DrumSubsystem.INSTANCE.shootPattern);
        //Gamepads.gamepad1().a().whenBecomesTrue(new NullCommand());//

        //Gamepads.gamepad2().a().whenBecomesTrue(DrumSubsystem.INSTANCE::readColorAndReturnValidity);
    }
}
