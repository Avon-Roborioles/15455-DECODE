package org.firstinspires.ftc.teamcode.UtilityCommands;

import java.util.function.BooleanSupplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.conditionals.IfElseCommand;

public class IfElse extends Command {

    private BooleanSupplier condition;
    private Command c1;
    private Command c2;
    private Command curCommand;

    public IfElse(BooleanSupplier condition,Command command1, Command c2){
        this.condition=condition;
        this.c1=command1;
        this.c2=c2;

    }

    @Override
    public void start(){
        if (condition.getAsBoolean()){
            curCommand=c1;
        } else {
            curCommand=c2;
        }
    }
    @Override
    public void update(){
        curCommand.update();
    }
    @Override
    public void stop(boolean stop){
        curCommand.stop(stop);
    }
    @Override
    public boolean isDone(){
        return curCommand.isDone();
    }
}
