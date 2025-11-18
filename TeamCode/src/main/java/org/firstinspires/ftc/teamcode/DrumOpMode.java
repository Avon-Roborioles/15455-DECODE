package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class DrumOpMode extends NextFTCOpMode {

    public DrumOpMode(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE),
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onStartButtonPressed(){




        Gamepads.gamepad1().leftBumper().whenBecomesTrue(DrumSubsystem.INSTANCE.shootPurple);
        Gamepads.gamepad1().rightBumper().whenBecomesTrue(DrumSubsystem.INSTANCE.shootGreen);
        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(DrumSubsystem.INSTANCE.testCommand);
        Gamepads.gamepad1().dpadRight().whenBecomesTrue(DrumSubsystem.INSTANCE.intakeOneBall);
        //Gamepads.gamepad1().dpadUp().whenBecomesTrue(DrumSubsystem.INSTANCE.shootPattern);


        Gamepads.gamepad2().a().whenBecomesTrue(DrumSubsystem.INSTANCE::readColorAndReturnValidity);
        Gamepads.gamepad2().b().whenBecomesTrue(DrumSubsystem.INSTANCE::setIntakeMode);
        Gamepads.gamepad2().x().whenBecomesTrue(DrumSubsystem.INSTANCE::setShootMode);

//        Gamepads.gamepad2().dpadLeft().whenBecomesTrue(DrumSubsystem.INSTANCE.turnToPink);
//        Gamepads.gamepad2().dpadRight().whenBecomesTrue(DrumSubsystem.INSTANCE.turnToRed);
//        Gamepads.gamepad2().dpadUp().whenBecomesTrue(DrumSubsystem.INSTANCE.turnToBlack);
    }

    @Override
    public void onUpdate(){
        TelemetryManager.getInstance().print(telemetry);
    }
    @Override
    public void onStop(){
        CommandManager.INSTANCE.cancelAll();
        TelemetryManager.getInstance().reset();
    }
}
