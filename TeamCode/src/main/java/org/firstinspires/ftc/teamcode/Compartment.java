package org.firstinspires.ftc.teamcode;

public class Compartment {

    private ArtifactColor color;
    private double outtakeCoords;
    private double intakeCoords;

    private double ticksPerRev=1012;
    public Compartment(double outtakeCoords,double intakeCoords){
        color = ArtifactColor.NOTHING;
        this.outtakeCoords =outtakeCoords;
        this.intakeCoords=intakeCoords;
    }
    public void setGreen(){
        color=ArtifactColor.GREEN;
    }
    public void setPurple(){
        color = ArtifactColor.PURPLE;
    }
    public void setNothing(){
        color = ArtifactColor.NOTHING;
    }
    public void setColor(ArtifactColor color){
        this.color=color;
    }
    public void turnClockwise(){
        outtakeCoords +=ticksPerRev/3;
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
}
