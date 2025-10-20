package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.UtilityOpModes.CompartmentColor;

import java.util.ArrayList;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;

public class DrumPrototype implements Subsystem {
    private MotorEx motorEx;
    private double ticksPerRev=1012;
    ControlSystem controlSystem;
    private double curPos=0;
    private double updatePos = 0;
    private Compartment pink,red,black;
    private Compartment curCompartment;
    boolean intakeMode=false;
    private ArtifactSensor colorSensor;

    private ArrayList<Compartment> compartments;

    @Override
    public void initialize(){
        //motor
        {
            motorEx = new MotorEx("backRight");


            controlSystem = ControlSystem.builder()
                    .posPid(.0006, 0.000000000015, .0000000000)
                    .build();
            motorEx.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorEx.setPower(0);
            motorEx.reverse();
            motorEx.zero();
            updatePos = 0;
        }
        colorSensor = new ArtifactSensor(ActiveOpMode.hardwareMap());
        //compartments
        {
            pink = new Compartment(0, 498);
            red = new Compartment(ticksPerRev / 3, 837);
            black = new Compartment(ticksPerRev / 3 * 2, 164);
            compartments = new ArrayList<>();
            compartments.add(red);
            compartments.add(pink);
            compartments.add(black);
        }
        //telemetry
        {
            new TelemetryData("Motor Position",motorEx::getCurrentPosition);
            new TelemetryData("Target",()->updatePos);
            new TelemetryItem(()->"Is intake mode: "+intakeMode);
            new TelemetryItem(this::getCurCompartment);


        }
    }
    public String getCurCompartment(){
        if (curCompartment==null){
            return "invalid";
        } else {
            if (curCompartment==pink){
                return "pink";
            }else if (curCompartment==black){
                return "black";
            }
        }
        return "red";
    }

    public void turnByOne(){
        updatePos+=ticksPerRev/3;
    }
    public void turnByMinusOne(){
        updatePos-=ticksPerRev/3;
    }

    public void turnToIntake(){
        ArrayList<Double> targets = new ArrayList<>();
        for (Compartment compartment:compartments){
            if (compartment.color().equals(ArtifactColor.NOTHING)){
                targets.add(compartment.getIntakeCoords());
            }
        }
        updatePos = findClosestTarget(targets);
    }
    public void shoot(){
        ArrayList<Double> targets = new ArrayList<>();
        for (Compartment compartment:compartments){
            ArtifactColor artifactColor= compartment.color();
            if (artifactColor.equals(ArtifactColor.PURPLE)||artifactColor.equals(ArtifactColor.GREEN)){
                targets.add(compartment.getIntakeCoords());
            }
        }
        updatePos = findClosestTarget(targets);
    }
    public void shootColor(ArtifactColor color){
        ArrayList<Double> targets = new ArrayList<>();
        for (Compartment compartment:compartments){
            if (compartment.color().equals(color)){
                targets.add(compartment.getIntakeCoords());
            }
        }
        updatePos = findClosestTarget(targets);
    }

    public void readColor(){
        ArtifactColor color=colorSensor.read();


        if (curCompartment!=null) {
            curCompartment.setColor(color);
        }
    }

    public void updateCurCompartment(){
        double normalizedCoords = updatePos%ticksPerRev;
        if (Math.abs(pink.getIntakeCoords()-normalizedCoords)<30){
            curCompartment= pink;
        } else if (Math.abs(red.getIntakeCoords()-normalizedCoords)<30){
            curCompartment=red;
        } else if (Math.abs(black.getIntakeCoords()-normalizedCoords)<30){
            curCompartment = black;
        } else {
            curCompartment = null;
        }
    }


    public void turnToCompartment(CompartmentColor color){
        double pos=0;
        if (color.equals(CompartmentColor.PINK)){
            if (intakeMode){
                pos = pink.getIntakeCoords();
            } else {
                pos = pink.getOuttakeCoords();
            }
        } else if (color.equals(CompartmentColor.BLACK)){
            if (intakeMode){
                pos = black.getIntakeCoords();
            } else {
                pos = black.getOuttakeCoords();
            }
        } else if (color.equals(CompartmentColor.RED)) {
            if (intakeMode){
                pos = red.getIntakeCoords();
            } else {
                pos = red.getOuttakeCoords();
            }
        }
        updatePos = modifyTarget(pos);
    }
    private double findClosestTarget(ArrayList<Double> targets){
        double closestTarget = 0;
        if (!targets.isEmpty()) {
            closestTarget = 0;
            double closestDistance =2000000000 ;
            for (int i=0;i<targets.size();i++){
                double target = modifyTarget(targets.get(i));
                double distance = curPos-target;
                if (Math.abs(distance)<closestDistance){
                    closestTarget=target;
                    closestDistance=distance;
                }
            }
        } else {
            closestTarget=updatePos;
        }
        return closestTarget;
    }
    //input a target and this outputs another number that is the same rotation but is the closest to the current position
    private double modifyTarget(double target){
        double multiplier = -1;
        if (target<curPos){
            multiplier=1;
        }
        double modifiedTarget = target;
        double turn = multiplier*ticksPerRev;
        while(Math.abs(modifiedTarget-curPos)>ticksPerRev/2){
            modifiedTarget+=turn;
        }


        return modifiedTarget;
    }

    public void setIntakeMode(){
        intakeMode=true;
    }
    public void setShootMode(){
        intakeMode=false;
    }

    @Override
    public void periodic(){
        curPos = motorEx.getCurrentPosition();
        controlSystem.setGoal(
                new KineticState(Math.round(updatePos))
        );
        motorEx.setPower(
                controlSystem.calculate(
                        new KineticState(motorEx.getCurrentPosition(),motorEx.getVelocity())
                )
        );
        updateCurCompartment();



    }





}
