package org.firstinspires.ftc.teamcode.CompOpmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Commands.PedroDriveCommand;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.ftc.Gamepads;

@TeleOp(group = "Comp")
public class BlueCompTeleOp extends CompTeleOp {
    public BlueCompTeleOp(){
        super();
        addComponents(AllianceComponent.getINSTANCE(AllianceColor.BLUE));

    }
    @Override
    public Command getFieldCentricDrive() {
        return new PedroDriveCommand(
                Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
                Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
                Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
                false,
                Math.toRadians(180)
        ).requires(DriveSubsystem.INSTANCE);
    }

    @Override
    public Command getRobotCentricDrive() {
        return new PedroDriveCommand(
                Gamepads.gamepad1().leftStickY().negate().deadZone(.1).map((Double input)->{return input/2;}),
                Gamepads.gamepad1().leftStickX().negate().deadZone(.1).map((Double input)->{return input/2;}),
                Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
                true,
                Math.toRadians(0)
        ).requires(DriveSubsystem.INSTANCE);
    }
}
