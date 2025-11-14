package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.ArtifactSensor;
import org.firstinspires.ftc.teamcode.Compartment;
import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.Enums.Pattern;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Enums.CompartmentColor;
import org.firstinspires.ftc.teamcode.UtilityCommands.ShootCommandFactory;

import java.util.ArrayList;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.CRServoEx;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;

@Configurable
public class DrumSubsystem implements Subsystem {
    public static final DrumSubsystem INSTANCE = new DrumSubsystem();
    private MotorEx drumMotor= new MotorEx("drumMotor");
    private MotorEx intakeMotor=new MotorEx("intakeMotor");
    private final double ticksPerRev=867;

    private double curPos=0;
    private double updatePos = 0;
    private Compartment pink,red,black;
    boolean intakeMode=false;
    private ArtifactSensor colorSensor;
    double power;
    public KineticState tolerance = new KineticState(10,2);

    private Pattern pattern = new Pattern(ArtifactColor.PURPLE,ArtifactColor.GREEN,ArtifactColor.PURPLE);

    private ArrayList<Compartment> compartments;
    public static double kp=.006;
    public static double kI=1./Math.pow(10,13);

    public CRServoEx servo= new CRServoEx("ejectServo");;
    public ControlSystem controlSystem= ControlSystem.builder()
            .posPid(kp,kI,0)
            .build();


    public Command turnToIntake = new LambdaCommand()
            .setStart(()->controlSystem.setGoal(new KineticState(calculateIntakePos())))
            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));
    public Command shootPurple = ShootCommandFactory.getShootAndEject(this,false,true);
    public Command shootGreen = ShootCommandFactory.getShootAndEject(this,true,false);
    public Command shootAny = ShootCommandFactory.getShootAndEject(this,true,true);

    public Command turnToRed = new LambdaCommand()
            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.RED))))
            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));

    public Command turnToPink= new LambdaCommand()
            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.PINK))))
            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));

    public Command turnToBlack= new LambdaCommand()
            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.BLACK))))
            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));

    private Command rotateIntakeWheels= new SetPower(intakeMotor,1);
    private Command stopIntakeWheels =  new SetPower(intakeMotor,0);

    public Command shootPattern = shootPattern();

    private Command intakeOneWithoutStop= new SequentialGroup(
            turnToIntake,
            new ParallelDeadlineGroup(
                    new LambdaCommand()
                            .setIsDone(()->colorSensor.read()!=ArtifactColor.NOTHING)
                            .setStop((Boolean b)->this.readColor()),
                    rotateIntakeWheels
            )
    );
    public Command intakeOneBall = new SequentialGroup(
            intakeOneWithoutStop,
            stopIntakeWheels
    );


    public Command intakeThreeBalls = new SequentialGroup(
            intakeOneWithoutStop,
            intakeOneWithoutStop,
            intakeOneWithoutStop,
            new SetPower(intakeMotor,1).endAfter(2),
            stopIntakeWheels
    );

    @Override
    public void initialize(){
        //intakeMotor=new MotorEx("intakeMotor");
        controlSystem.setGoal(new KineticState(0));

        //drum motor

        {
            //drumMotor= new MotorEx("drumMotor");
            //drumMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            drumMotor.reverse();
            drumMotor.zero();
            curPos=0;
            updatePos = 0;
        }
        colorSensor = new ArtifactSensor(ActiveOpMode.hardwareMap());
        //compartments
        {
            pink = new Compartment(0, 433);
            red = new Compartment(ticksPerRev / 3, 722);
            black = new Compartment(ticksPerRev / 3 * 2, 151);
            compartments = new ArrayList<>();
            compartments.add(red);
            compartments.add(pink);
            compartments.add(black);
        }
        //telemetry
        {
            new TelemetryData("Motor Position", drumMotor::getCurrentPosition);
            new TelemetryData("Target",()->updatePos);
            new TelemetryItem(()->"Is intake mode: "+intakeMode);
            new TelemetryItem(this::getCurCompartmentString);
            new TelemetryData("Power",()->power);
            new TelemetryData("KP",()->kp);

        }

    }
    public String getCurCompartmentString(){
        Compartment compartment = getIntakeCurCompartment();
        if (compartment==null){
            return "invalid";
        } else {
            if (compartment==pink){
                return "pink";
            }else if (compartment==black){
                return "black";
            }
        }
        return "red";
    }
    public Compartment getIntakeCurCompartment(){
        double normalizedCoords = updatePos%ticksPerRev;
        if (normalizedCoords<0) normalizedCoords+=ticksPerRev;
        if (Math.abs(pink.getIntakeCoords()-normalizedCoords)<30){
            return pink;
        } else if (Math.abs(red.getIntakeCoords()-normalizedCoords)<30){
            return red;
        } else if (Math.abs(black.getIntakeCoords()-normalizedCoords)<30){
            return black;
        } else {
            return  null;
        }

    }

    public double calculateIntakePos(){
        ArrayList<Double> targets = new ArrayList<>();
        for (Compartment compartment:compartments){
            if (compartment.color().equals(ArtifactColor.NOTHING)){
                targets.add(compartment.getIntakeCoords());
            }
        }
        updatePos = findClosestTarget(targets);
        return updatePos;
    }
    public double calculateAllShootPos(){
        ArrayList<Double> targets = new ArrayList<>();
        for (Compartment compartment:compartments){
            ArtifactColor artifactColor= compartment.color();
            if (artifactColor.equals(ArtifactColor.PURPLE)||artifactColor.equals(ArtifactColor.GREEN)){
                targets.add(compartment.getIntakeCoords());
            }
        }
        updatePos = findClosestTarget(targets);
        return updatePos;
    }
    public double calculateColorShootPos(ArtifactColor color){
        ArrayList<Double> targets = new ArrayList<>();
        for (Compartment compartment:compartments){
            if (compartment.color().equals(color)){
                targets.add(compartment.getOuttakeCoords());
            }
        }
        updatePos = findClosestTarget(targets);
        return updatePos;
    }
    private Command shootPattern(){
        Command command1=shootPurple;
        Command command2=shootPurple;
        Command command3=shootPurple;
        if (pattern.first()==ArtifactColor.GREEN){
            command1=shootGreen;
        }
        if (pattern.second()==ArtifactColor.GREEN){
            command2 = shootGreen;
        }
        if (pattern.third()==ArtifactColor.GREEN){
            command3 = shootGreen;
        }
        Command toRet = new SequentialGroup(
                command1,
                command2,
                command3
        );
        return toRet;
    }

    public void readColor(){
        ArtifactColor curcolor=colorSensor.read();
        Compartment compartment= getIntakeCurCompartment();

        if (compartment!=null) {
            compartment.setColor(curcolor);
        }
    }


    public void eject(){
        if (this.getEjectCompartment(curPos) != null) {
            if(this.getEjectCompartment(curPos)!=null&&this.getEjectCompartment(curPos).color()!=ArtifactColor.NOTHING) {
                new SequentialGroup(
                        new LambdaCommand()
                                .setStart(()->{servo.setPower(-1);eject();})
                                .thenWait(.310),
                        new LambdaCommand()
                                .setStart(()->servo.setPower(0))
                ).schedule();
            }
        }

    }

    private double turnToCompartment(CompartmentColor color){
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
        return updatePos;
    }

    public Compartment getEjectCompartment(double pos){
        double normalizedCoords = updatePos%ticksPerRev;
        Compartment toRet;
        if (normalizedCoords<0) normalizedCoords+=ticksPerRev;
        if (Math.abs(pink.getOuttakeCoords()-normalizedCoords)<30){
            toRet= pink;
        } else if (Math.abs(red.getOuttakeCoords()-normalizedCoords)<30){
            toRet=red;
        } else if (Math.abs(black.getOuttakeCoords()-normalizedCoords)<30){
            toRet = black;
        } else {
            toRet = null;
        }
        return toRet;
    }

    public void setIntakeMode(){
        intakeMode=true;
    }
    public void setShootMode(){
        intakeMode=false;
    }

    private double findClosestTarget(ArrayList<Double> targets){
        double closestTarget = 0;
        if (!targets.isEmpty()) {

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
    //input a target and this outputs another number that is the same physical position but is the closest to the current position
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
    public void foo(){}
    @Override
    public void periodic(){
        curPos = drumMotor.getCurrentPosition();
        updatePos=controlSystem.getGoal().getPosition();
//        controlSystem.setGoal(
//                new KineticState(Math.round(updatePos))
//        );
        controlSystem.setLastMeasurement(new KineticState(drumMotor.getCurrentPosition()));

        power= kp*(updatePos-curPos);
        drumMotor.setPower(power);

    }
}