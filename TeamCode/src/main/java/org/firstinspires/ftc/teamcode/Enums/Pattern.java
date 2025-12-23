package org.firstinspires.ftc.teamcode.Enums;

public class Pattern {

    private ArtifactColor[] first;
    private ArtifactColor[] second;
    private ArtifactColor[] third;

    public Pattern(ArtifactColor first,ArtifactColor second,ArtifactColor third){
        this.first=new ArtifactColor[]{first};
        this.second=new ArtifactColor[]{second};
        this.third=new ArtifactColor[] {third};
    }

    public ArtifactColor[] first(){
        return first;
    }
    public ArtifactColor[] second(){
        return second;
    }
    public ArtifactColor[] third(){
        return third;
    }

}
