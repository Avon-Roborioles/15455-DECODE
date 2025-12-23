package org.firstinspires.ftc.teamcode.Commands;

import com.pedropathing.follower.Follower;

import java.util.function.Supplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;

public class PedroDriveCommand extends Command {

    Supplier<Double> forward;
    Supplier<Double> strafe;
    Supplier<Double> turn;
    boolean isRobotCentric;
    Follower follower;


    public PedroDriveCommand(Follower follower,Supplier<Double> forward, Supplier<Double>strafe, Supplier<Double>turn, boolean isRobotCentric){
        this.follower=follower;
        this.forward=forward;
        this.strafe=strafe;
        this.turn=turn;
        this.isRobotCentric=isRobotCentric;
    }

    public void start(){
        follower.startTeleopDrive();
        follower.update();
    }
    public void update(){
        follower.update();
        follower.setTeleOpDrive(
                forward.get(),
                strafe.get(),
                turn.get(),
                isRobotCentric
        );
    }
    public boolean isDone(){
        return false;
    }

}
