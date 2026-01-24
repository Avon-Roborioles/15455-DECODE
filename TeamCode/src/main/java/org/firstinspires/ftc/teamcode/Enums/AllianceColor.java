package org.firstinspires.ftc.teamcode.Enums;

public enum AllianceColor {
    RED("red"),
    BLUE("blue");
    private String color;

    AllianceColor(String color){
        this.color=color;
    }


    public boolean equals(AllianceColor other){
        return this.color.equals(other.color);
    }
}
