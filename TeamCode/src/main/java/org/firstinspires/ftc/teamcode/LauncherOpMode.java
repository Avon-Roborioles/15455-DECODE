package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

@TeleOp
public class LauncherOpMode extends NextFTCOpMode {


    public LauncherOpMode(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DrumSubsystem.INSTANCE),
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onUpdate(){
        Gamepads.gamepad1().dpadUp().whenBecomesTrue(LauncherSubsystem.INSTANCE::increaseRPMby100);
        Gamepads.gamepad1().dpadDown().whenBecomesTrue(LauncherSubsystem.INSTANCE::decreaseRPMby100);
    }
}
