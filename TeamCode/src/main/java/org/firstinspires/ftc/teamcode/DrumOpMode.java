package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.UtilityOpModes.CompartmentColor;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class DrumOpMode extends NextFTCOpMode {
    DrumPrototype drumPrototype;
    public DrumOpMode(){
        drumPrototype=new DrumPrototype();
        addComponents(
                new SubsystemComponent(drumPrototype)
        );
    }


    @Override
    public void onStartButtonPressed(){
//        drumPrototype.turnByOne();
    }
    boolean lastAon = false;
    boolean lastBon = false;
    boolean lastLefton = false;
    boolean lastRighton = false;
    boolean lastUpon = false;
    boolean lastXon = false;
    @Override
    public void onUpdate(){
        BindingManager.update();
        if (!lastAon&& gamepad1.a){
            drumPrototype.readColor();
        }

        if (!lastBon&& gamepad1.b){
            drumPrototype.setIntakeMode();
        }
        if (!lastXon&& gamepad1.x){
            drumPrototype.setShootMode();
        }
        if (!lastLefton&& gamepad1.dpad_left){
            drumPrototype.turnToCompartment(CompartmentColor.PINK);
        }
        if (!lastUpon&& gamepad1.dpad_up){
            drumPrototype.turnToCompartment(CompartmentColor.BLACK);
        }
        if (!lastRighton&& gamepad1.dpad_right){
            drumPrototype.turnToCompartment(CompartmentColor.RED);
        }

        lastRighton = gamepad1.dpad_right;
        lastUpon = gamepad1.dpad_up;
        lastLefton = gamepad1.dpad_left;
        lastBon=gamepad1.b;
        lastAon= gamepad1.a;
        lastXon = gamepad1.x;

        TelemetryManager.getInstance().print(telemetry);

    }
    @Override
    public void onStop(){
        CommandManager.INSTANCE.cancelAll();
        TelemetryManager.getInstance().reset();
    }
}
