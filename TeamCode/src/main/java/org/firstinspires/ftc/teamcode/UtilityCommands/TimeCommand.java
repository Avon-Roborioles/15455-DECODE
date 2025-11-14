package org.firstinspires.ftc.teamcode.UtilityCommands;

import dev.nextftc.core.commands.Command;

public class TimeCommand extends Command {
    private double startTime = 0;

    private double totTime;
    public TimeCommand(double timeMs){
        totTime=timeMs;
        startTime=System.currentTimeMillis();
    }
    @Override
    public void start(){
        startTime = System.currentTimeMillis();
    }


    public boolean isDone(){
        return System.currentTimeMillis()-startTime>=totTime;
    }
}
