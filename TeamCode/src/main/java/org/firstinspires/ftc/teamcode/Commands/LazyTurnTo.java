package org.firstinspires.ftc.teamcode.Commands;

import java.util.function.DoubleSupplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.units.Angle;
import dev.nextftc.extensions.pedro.TurnTo;

public class LazyTurnTo extends Command {

    private TurnTo turnTo;
    private DoubleSupplier angleSupp;

    public LazyTurnTo(DoubleSupplier angleRad){
        angleSupp=angleRad;
    }


    @Override
    public void start(){
        turnTo=new TurnTo(Angle.fromRad(angleSupp.getAsDouble()));
        turnTo.start();
    }


    @Override
    public void update(){
        turnTo.update();
    }

    @Override
    public void stop(boolean b){
        turnTo.stop(b);
    }



    @Override
    public boolean isDone(){
        return turnTo.isDone();
    }
}
