package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;

public class Compartment {

    private ArtifactColor color;
    private double outtakeCoords;
    private double intakeCoords;

    private String string;
    private double ticksPerRev=1012;
    public Compartment(double outtakeCoords,double intakeCoords,String string){
        color = ArtifactColor.NOTHING;
        this.outtakeCoords =outtakeCoords;
        this.intakeCoords=intakeCoords;
        this.string=string;
    }

    public void setColor(ArtifactColor color){
        this.color=color;
    }
    public double getOuttakeCoords(){
        return outtakeCoords;
    }
    public double getIntakeCoords(){
        return intakeCoords;
    }
    public ArtifactColor color(){
        return color;
    }
    public String toString(){
        return string;
    }

}
