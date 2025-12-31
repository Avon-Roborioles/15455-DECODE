package org.firstinspires.ftc.teamcode.CompOpmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;

@TeleOp(group = "Comp")
public class BlueCompTeleOp extends CompTeleOp {
    public BlueCompTeleOp(){
        super();
        addComponents(AllianceComponent.getINSTANCE(AllianceColor.BLUE));

    }
}
