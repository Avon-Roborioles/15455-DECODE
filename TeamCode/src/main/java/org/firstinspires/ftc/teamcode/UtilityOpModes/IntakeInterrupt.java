package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;

import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp
public class IntakeInterrupt extends NextFTCOpMode {
    public IntakeInterrupt(){
        addComponents(
                new LoopTimeComponent(),
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DrumSubsystem.INSTANCE),
                AllianceComponent.getINSTANCE(AllianceColor.BLUE)
        );
    }

    public void onInit(){
        Gamepads.gamepad1().rightTrigger().atLeast(.7).whenBecomesTrue(DrumSubsystem.INSTANCE.intakeThreeBallsWithPause);
        Gamepads.gamepad1().a().whenBecomesTrue(new InstantCommand(()->{}).addRequirements(DrumSubsystem.INSTANCE));
        new SequentialGroup(
                LauncherSubsystem.INSTANCE.runToLowRPM,
                new SequentialGroup(
                        DrumSubsystem.INSTANCE.servoEject,
                        new Delay(.5),
                        DrumSubsystem.INSTANCE.shootPattern,
                        new Delay(.5),
                        new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.schedule())
                )

        ).requires(DrumSubsystem.INSTANCE,LauncherSubsystem.INSTANCE);
    }
}
