package org.firstinspires.ftc.teamcode.Commands;

import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.Enums.Pattern;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;

import java.util.function.Function;
import java.util.function.Supplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.ftc.ActiveOpMode;

public class PatternSetCommand extends Command {

    private Pattern.PatternBuilder patternBuilder = new Pattern.PatternBuilder();
    private Function<ArtifactColor,Pattern.PatternBuilder> method;
    private Supplier<Pattern.PatternBuilder> clearMethod;
    private int methodNumber;

    private boolean lastX = false;
    private boolean lastY=false;
    private boolean lastRightBumper = false;
    private boolean lastLeftDpad = false;
    private boolean lastRightDpad = false;




    public void start(){
        patternBuilder=new Pattern.PatternBuilder();
        method=patternBuilder::first;
        clearMethod=patternBuilder::clearFirst;
        methodNumber=1;
    }

    public void update(){
        boolean aY = !lastY&&ActiveOpMode.gamepad2().y;
        boolean aX = !lastX&&ActiveOpMode.gamepad2().x;
        boolean aRB = !lastRightBumper&&ActiveOpMode.gamepad2().right_bumper;
        boolean aDR = !lastRightDpad&&ActiveOpMode.gamepad2().dpad_right;
        boolean aDL = !lastLeftDpad&&ActiveOpMode.gamepad2().dpad_left;

        lastY=ActiveOpMode.gamepad2().y;
        lastX=ActiveOpMode.gamepad2().x;
        lastLeftDpad=ActiveOpMode.gamepad2().dpad_left;
        lastRightBumper=ActiveOpMode.gamepad2().right_bumper;
        lastRightDpad=ActiveOpMode.gamepad2().dpad_right;
        if (aY){
            method.apply(ArtifactColor.PURPLE);
        }
        if (aX){
            method.apply(ArtifactColor.GREEN);
        }
        if (aRB){
            clearMethod.get();
        }

        if (aDR){
            if (methodNumber==1){

                method=patternBuilder::second;
                clearMethod=patternBuilder::clearSecond;
            } else if (methodNumber==2) {
                method=patternBuilder::third;
                clearMethod=patternBuilder::clearThird;
            }
            methodNumber++;
        }
        if (aDL){
            if (methodNumber==3){
                methodNumber=2;
                method=patternBuilder::second;
                clearMethod=patternBuilder::clearSecond;
            } else if (methodNumber==2) {
                methodNumber=1;
                method=patternBuilder::first;
                clearMethod=patternBuilder::clearFirst;
            }
        }
    }


    public void stop(boolean b){
        DrumSubsystem.INSTANCE.setNextPattern(patternBuilder.build());
    }


    public boolean isDone(){
        return methodNumber==4;
    }
}
