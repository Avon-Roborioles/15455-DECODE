package org.firstinspires.ftc.teamcode.Enums;

import java.util.ArrayList;
import java.util.Collections;

public class Pattern {

    private ArrayList<ArtifactColor> first;
    private ArrayList<ArtifactColor> second;
    private ArrayList<ArtifactColor> third;

    public Pattern(ArrayList<ArtifactColor> first,ArrayList<ArtifactColor> second,ArrayList<ArtifactColor> third){
        this.first=first;
        this.second=second;
        this.third=third;
    }

    public ArrayList<ArtifactColor> first(){
        return first;
    }
    public ArrayList<ArtifactColor> second(){
        return second;
    }
    public ArrayList<ArtifactColor> third(){
        return third;
    }

    public String toString(){

        String toRet="";
        toRet+="First Artifact: ";

        for (ArtifactColor color:first){
            toRet+= color.toString()+", ";
        }
        toRet+="\nSecond Artifact: ";
        for (ArtifactColor color:second){
            toRet+= color.toString()+", ";
        }
        toRet+="\nThird Artifact: ";
        for (ArtifactColor color:third){
            toRet+= color.toString()+", ";
        }
        toRet+="\n";
        return toRet;
    }


    public static class PatternBuilder {
        public PatternBuilder(){
            first=new ArrayList<ArtifactColor>();

            second=new ArrayList<ArtifactColor>();
            third=new ArrayList<ArtifactColor>();
        }
        ArrayList<ArtifactColor> first,second, third;
        public PatternBuilder first(ArtifactColor... artifactColors){
            Collections.addAll(first, artifactColors);
            return this;
        }

        public PatternBuilder clearFirst(){
            first=new ArrayList<ArtifactColor>();
            return this;
        }
        public PatternBuilder second(ArtifactColor... artifactColors){
            Collections.addAll(second, artifactColors);
            return this;
        }
        public PatternBuilder clearSecond(){
            second=new ArrayList<ArtifactColor>();
            return this;
        }
        public PatternBuilder third(ArtifactColor... artifactColors){
            Collections.addAll(third, artifactColors);
            return this;
        }
        public PatternBuilder clearThird(){
            third=new ArrayList<ArtifactColor>();
            return this;
        }
        public Pattern build(){
            return new Pattern(first,second,third);
        }


    }

}
