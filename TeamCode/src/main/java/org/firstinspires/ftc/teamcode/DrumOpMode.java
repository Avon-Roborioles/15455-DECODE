package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.UtilityOpModes.CompartmentColor;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class DrumOpMode extends NextFTCOpMode {
    DrumPrototype drumPrototype=DrumPrototype.INSTANCE;
    public DrumOpMode(){
        addComponents(
                new SubsystemComponent(DrumPrototype.INSTANCE),
                BindingsComponent.INSTANCE
        );
    }


    @Override
    public void onStartButtonPressed(){
//        drumPrototype.turnByOne();
        Gamepads.gamepad1().a().whenBecomesTrue(DrumPrototype.INSTANCE::readColor);
        Gamepads.gamepad1().b().whenBecomesTrue(DrumPrototype.INSTANCE::setIntakeMode);
        Gamepads.gamepad1().x().whenBecomesTrue(DrumPrototype.INSTANCE::setShootMode);
        Gamepads.gamepad1().dpadLeft().whenBecomesTrue(()->drumPrototype.turnToCompartment(CompartmentColor.PINK));
        Gamepads.gamepad1().dpadRight().whenBecomesTrue(()->drumPrototype.turnToCompartment(CompartmentColor.RED));
        Gamepads.gamepad1().dpadUp().whenBecomesTrue(()->drumPrototype.turnToCompartment(CompartmentColor.BLACK));
        Gamepads.gamepad1().leftBumper().whenBecomesTrue(()->drumPrototype.shootColor(ArtifactColor.PURPLE));
        Gamepads.gamepad1().rightBumper().whenBecomesTrue(()->drumPrototype.shootColor(ArtifactColor.GREEN));
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(drumPrototype::shoot);
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
