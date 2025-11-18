package org.firstinspires.ftc.teamcode.UtilityCommands;

import org.firstinspires.ftc.teamcode.Compartment;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;

import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.hardware.powerable.SetPower;

public class ShootCommandFactory {


    public static Command getShootAndEject( boolean green, boolean purple){

        double pos=1000000;
        DrumSubsystem drumSubsystem = DrumSubsystem.INSTANCE;
        if (green&&purple){
            pos = drumSubsystem.calculateAllShootPos();
        } else if (green){
            pos = drumSubsystem.calculateColorShootPos(ArtifactColor.GREEN);
        } else if (purple){
            pos = drumSubsystem.calculateColorShootPos(ArtifactColor.PURPLE);
        }
        Compartment compartment = drumSubsystem.getEjectCompartment(pos);
        if (compartment==null||compartment.color()==ArtifactColor.NOTHING){
            return new InstantCommand(()->{});
        }
        KineticState state = new KineticState(pos);
        Command shootCommand = new LambdaCommand()
                .setStart(()-> drumSubsystem.controlSystem.setGoal(state))
                .setIsDone(()-> drumSubsystem.controlSystem.isWithinTolerance(drumSubsystem.tolerance));
        Command eject = new SetPower(drumSubsystem.servo,1).endAfter(.310);
        Command sequential = new SequentialGroup(
                shootCommand,
                eject,
                new SetPower(drumSubsystem.servo,0)
        );
        return sequential;
    }
}
