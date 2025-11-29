package org.firstinspires.ftc.teamcode.Commands;

import dev.nextftc.core.commands.Command;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;

public class PedroDriveCommand extends Command {


    public void start(){
        PedroComponent.follower().startTeleopDrive();
    }
    public void update(){
        PedroComponent.follower().setTeleOpDrive(
                Gamepads.gamepad1().leftStickY().get(),
                Gamepads.gamepad1().leftStickX().get(),
                Gamepads.gamepad1().rightStickX().get(),
                true
        );
    }
    public boolean isDone(){
        return false;
    }

}
