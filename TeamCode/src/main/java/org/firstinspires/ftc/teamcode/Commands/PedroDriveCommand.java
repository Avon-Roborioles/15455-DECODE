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
                Gamepads.gamepad1().getGamepad().invoke().left_stick_y,
                Gamepads.gamepad1().getGamepad().invoke().left_stick_x,
                Gamepads.gamepad1().getGamepad().invoke().right_stick_x,
                true
        );
    }
    public boolean isDone(){
        return false;
    }

}
