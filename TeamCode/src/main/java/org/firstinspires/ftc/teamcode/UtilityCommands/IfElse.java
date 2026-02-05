package org.firstinspires.ftc.teamcode.UtilityCommands;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.jetbrains.annotations.NonBlocking;

import java.util.function.BooleanSupplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.conditionals.IfElseCommand;

public class IfElse extends Command {

    private BooleanSupplier condition;
    private Command c1;
    private Command c2;
    private Command curCommand;
    private String name="ajkdljsklfs";


    public IfElse(BooleanSupplier condition, @NonNull Command command1,@NonNull Command c2){
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
        curCommand.start();
        name= curCommand.name();;
    }
    @Override
    public void update(){
        try {
            curCommand.update();
        } catch (Exception e){
            new TelemetryItem(()->name+" was null");
        }
    }
    @Override
    public void stop(boolean stop){
        try {
            curCommand.stop(stop);
        }catch(Exception e){

        }
    }
    @Override
    public boolean isDone(){
        try {
        return curCommand.isDone();
    }catch(Exception e){
        return false;
    }
    }
}
