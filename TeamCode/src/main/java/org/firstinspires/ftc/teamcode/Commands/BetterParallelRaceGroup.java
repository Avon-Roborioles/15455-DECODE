package org.firstinspires.ftc.teamcode.Commands;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;

public class BetterParallelRaceGroup extends ParallelGroup {


    public BetterParallelRaceGroup(Command... commands){
        super(commands);
    }
    double originalSize;
    @Override
    public void start(){
        originalSize= getChildren().size();
        super.start();
    }

    @Override
    public boolean isDone(){
        return originalSize> getChildren().size();
    }
}
