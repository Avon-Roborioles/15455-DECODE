package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.Enums.AllianceColor;

import dev.nextftc.core.components.Component;

public class AllianceComponent implements Component {

    private static AllianceComponent INSTANCE = null;
    private final AllianceColor color;
    private AllianceComponent(AllianceColor color){
        this.color=color;
    }

    public static AllianceComponent getINSTANCE(AllianceColor color) {
        if (INSTANCE==null){
            INSTANCE=new AllianceComponent(color);
        }
        return INSTANCE;
    }



}
