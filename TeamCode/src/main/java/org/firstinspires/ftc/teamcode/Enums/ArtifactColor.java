package org.firstinspires.ftc.teamcode.Enums;

public enum ArtifactColor {
    GREEN("green"),
    PURPLE("purple"),
    NOTHING("nothing");
    private String color;
    ArtifactColor(String color){
        this.color=color;
    }

    public boolean equals(ArtifactColor other){
        if (other.color.equals(this.color)){
            return true;
        } else return false;
    }

    @Override
    public String toString(){
        return color;
    }
}
