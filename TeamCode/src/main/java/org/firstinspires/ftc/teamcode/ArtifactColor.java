package org.firstinspires.ftc.teamcode;

public enum ArtifactColor {
    GREEN("green"),
    PURPLE("purple"),
    NOTHING("nothing");
    private String color;
    ArtifactColor(String color){
        this.color=color;
    }

    @Override
    public String toString(){
        return color;
    }
}
