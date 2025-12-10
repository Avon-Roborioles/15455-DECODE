package org.firstinspires.ftc.teamcode.Subsytems;

import org.firstinspires.ftc.teamcode.Commands.PedroDriveCommand;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;

public class DriveSubsystem implements Subsystem {


    public static DriveSubsystem INSTANCE = new DriveSubsystem();
    public double kP = -.01;
    @Override
    public void initialize(){
        normalDrive.addRequirements(this);

    }
    public Command normalDrive= new PedroDriverControlled(
            Gamepads.gamepad1().leftStickY(),
            Gamepads.gamepad1().leftStickY(),
            Gamepads.gamepad1().rightStickX(),
            true
    );
    public Command targetDrive = new ParallelDeadlineGroup(
            new LambdaCommand()
                    .setIsDone(()->Gamepads.gamepad1().square().get()),
            new PedroDriverControlled(
                    Gamepads.gamepad1().leftStickY(),
                    Gamepads.gamepad1().leftStickY(),
                    ()->kP*LimelightSubsystem.INSTANCE.getAprilTagOffset(),
                    true
            )
    ).requires(this);

    @Override
    public void periodic(){

    }

}
