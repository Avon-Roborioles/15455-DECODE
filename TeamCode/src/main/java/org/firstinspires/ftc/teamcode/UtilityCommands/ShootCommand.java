package org.firstinspires.ftc.teamcode.UtilityCommands;


import org.firstinspires.ftc.teamcode.Commands.BetterParallelRaceGroup;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.delays.WaitUntil;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;

public class ShootCommand {

    public static Command getShootCommand(){
        double waitTime = 2;
        return new SequentialGroup(
                DrumSubsystem.INSTANCE.servoEject,


                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                //new InstantCommand(()->new TelemetryItem(()->"Shooting First Pattern")),
                DrumSubsystem.INSTANCE.shootFirstPattern,
                new BetterParallelRaceGroup(
                        new WaitUntil(LauncherSubsystem.INSTANCE::hasShot),
                        new WaitUntil(DrumSubsystem.INSTANCE::isEmpty),
                        new Delay(waitTime)
                ),

                LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                //new InstantCommand(()->new TelemetryItem(()->"Shooting Second Pattern")),
                DrumSubsystem.INSTANCE.shootSecondPattern,
                new BetterParallelRaceGroup(
                        new WaitUntil(LauncherSubsystem.INSTANCE::hasShot),
                        new WaitUntil(DrumSubsystem.INSTANCE::isEmpty),
                        new Delay(waitTime)
                ),
                LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                //new InstantCommand(()->new TelemetryItem(()->"Shooting Third Pattern")),

                DrumSubsystem.INSTANCE.shootThirdPattern,
                new BetterParallelRaceGroup(
                        new WaitUntil(LauncherSubsystem.INSTANCE::hasShot),
                        new WaitUntil(DrumSubsystem.INSTANCE::isEmpty),
                        new Delay(waitTime)
                ),
                new Delay(.5),
                new InstantCommand(DrumSubsystem.INSTANCE::resetNextPattern),
                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.schedule())
        );
    }
}
