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
    double offset;


    public PedroDriveCommand(Supplier<Double> forward, Supplier<Double>strafe, Supplier<Double>turn, boolean isRobotCentric, double radiansOffset){

        this.forward=forward;
        this.strafe=strafe;
        this.turn=turn;
        this.isRobotCentric=isRobotCentric;
        offset=radiansOffset;
    }

    public void start(){
        follower.startTeleopDrive();
    }
    public void update(){
        follower.setTeleOpDrive(
                forward.get(),
                strafe.get(),
                turn.get(),
                isRobotCentric,
                offset
        );
    }
    public boolean isDone(){
        return false;
    }

}
