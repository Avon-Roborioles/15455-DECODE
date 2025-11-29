package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.ArtifactSensor;
import org.firstinspires.ftc.teamcode.Compartment;
import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.Enums.Pattern;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Enums.CompartmentColor;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.UtilityCommands.IfElse;

import java.util.ArrayList;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.commands.utility.NullCommand;
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
    public KineticState tolerance = new KineticState(20,0);

    private Pattern pattern = new Pattern(ArtifactColor.PURPLE,ArtifactColor.GREEN,ArtifactColor.PURPLE);

    private ArrayList<Compartment> compartments;
    public static double kp=.0025;
    public static double kI=150./Math.pow(10,13);

    public CRServoEx servo= new CRServoEx("ejectServo");;
    public ControlSystem controlSystem= ControlSystem.builder()
            .posPid(kp,kI)
            .build();
    private ControlSystem controlSystem2;

    public Command turnToIntake ;
    public Command shootPurple;
    public Command shootGreen ;
    public Command shootAny ;

    public boolean isZeroing = false;
    public double zeroingPower =.2;

//    public Command turnToRed = new LambdaCommand()
//            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.RED))))
//            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));
//
//    public Command turnToPink= new LambdaCommand()
//            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.PINK))))
//            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));
//
//    public Command turnToBlack= new LambdaCommand()
//            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.BLACK))))
//            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));





    private Command rotateIntakeWheels= new SetPower(intakeMotor,1);
    private Command stopIntakeWheels =  new SetPower(intakeMotor,0);
    private Command spitOutIntakeWheels = new SetPower(intakeMotor,-1);

    //public Command shootPattern = shootPattern();;

    private Command intakeOneWithoutStop;
    public Command intakeOneBall ;


    public Command intakeThreeBalls ;
    public Command testCommand;



    public TouchSensor magneticSensor;

    public Command zero = new LambdaCommand()
            .setStart(this::startZeroing)
            .setIsDone(this::isZeroingFinished)
            .setStop(this::onZeroingStopped)
            .setInterruptible(false);

    private void startZeroing(){
        isZeroing=true;
    }
    private boolean isZeroingFinished(){
        return magneticSensor.isPressed();
    }
    private void onZeroingStopped(Boolean b){
        isZeroing=false;
        drumMotor.zero();
        updatePos=0;
    }

    @Override
    public void initialize(){
        //intakeMotor=new MotorEx("intakeMotor");
        //controlSystem.setGoal(new KineticState(0));
        magneticSensor=ActiveOpMode.hardwareMap().get(TouchSensor.class,"magSensor");
        controlSystem2=ControlSystem.builder()
                .posPid(kp,kI)
                .build();
        testCommand=new LambdaCommand()
                .setStart(()->controlSystem2.setGoal(new KineticState())).requires(this);

        turnToIntake=new LambdaCommand()
                .setStart(()->controlSystem2.setGoal(new KineticState(calculateIntakePos())))
                .setIsDone(()->controlSystem2.isWithinTolerance(tolerance));
        intakeOneWithoutStop=new SequentialGroup(
                turnToIntake,
                new ParallelDeadlineGroup(
                        new LambdaCommand()
                                .setIsDone(()->readColorAndReturnValidity()),
                        rotateIntakeWheels
                )
//                ,
//                new ParallelDeadlineGroup(
//                        new Delay(.1),
//                        spitOutIntakeWheels
//                )
        );
        intakeOneBall=new SequentialGroup(
                intakeOneWithoutStop,
                stopIntakeWheels
        ).requires(this);
        intakeThreeBalls=new SequentialGroup(
                intakeOneWithoutStop,
                intakeOneWithoutStop,
                intakeOneWithoutStop,
                stopIntakeWheels
        ).requires(this);






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
        servo.setPower(0);
        //telemetry
        {
            new TelemetryData("Drum Motor Position", drumMotor::getCurrentPosition);
            new TelemetryData("Drum Target",()->updatePos);
            //new TelemetryItem(()->"Is intake mode: "+intakeMode);
            new TelemetryItem(this::getCurCompartmentString);
            new TelemetryData("Drum Power",()->power);
            new TelemetryData("Drum Control System 2 Target", ()->controlSystem2.getGoal().getPosition());
        }
        servo.getServo();
        Command setPower= new SetPower(servo,-1);
        Command stopServo = new SetPower(servo,0);
        Command purplelambda = new LambdaCommand()
                .setUpdate(()-> controlSystem2.setGoal(
                        new KineticState(calculateColorShootPos(ArtifactColor.PURPLE))
                ))
                .setIsDone(()->controlSystem2.isWithinTolerance(tolerance));
        Command greenLambda = new LambdaCommand()
                .setUpdate(()-> controlSystem2.setGoal(
                        new KineticState(calculateColorShootPos(ArtifactColor.GREEN))
                ))
                .setIsDone(()->controlSystem2.isWithinTolerance(tolerance));
        Command anyLambda = new LambdaCommand()
                .setUpdate(()-> controlSystem2.setGoal(
                        new KineticState(calculateAllShootPos())
                ))
                .setIsDone(()->controlSystem2.isWithinTolerance(tolerance));

        shootPurple = new IfElse(
                ()->!isValid(false,true),
                new NullCommand(5,6),
                new SequentialGroup(
                        purplelambda,
//                        setPower,
                        new Delay(3),
//                        stopServo,
                        new InstantCommand(this::setEjectCompartmentToNothing)

                )
        ).requires(this);
        shootGreen=new IfElse(
                ()->!isValid(true,false),
                new NullCommand(5,6),
                new SequentialGroup(
                        greenLambda,
//                        setPower,
                        new Delay(3),
//                        stopServo,
                        new InstantCommand(this::setEjectCompartmentToNothing)

                )
        ).requires(this);
        shootAny=new IfElse(
                ()->!isValid(true,true),
                new SequentialGroup(
                        new InstantCommand( ()->new TelemetryItem(()->"Any If else is NOT valid"))
                ),
                new SequentialGroup(
                        new InstantCommand( ()->new TelemetryItem(()->"Any If else IS valid")),
                        anyLambda,
//                        setPower,
                        new Delay(3),
//                        stopServo,
                        new InstantCommand(this::setEjectCompartmentToNothing)

                )
        ).requires(this);
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
        double finalNormalizedCoords = normalizedCoords;
        //TelemetryManager.getInstance().addTempTelemetry("Normalized coords"+ finalNormalizedCoords);
        double posTolerance = tolerance.getPosition()+10;
        if (Math.abs(pink.getIntakeCoords()-normalizedCoords)<posTolerance){
            return pink;
        } else if (Math.abs(red.getIntakeCoords()-normalizedCoords)<posTolerance){
            return red;
        } else if (Math.abs(black.getIntakeCoords()-normalizedCoords)<posTolerance){
            return black;
        } else {
            return  null;
        }

    }


    public boolean isValid(boolean green, boolean purple){
        double pos=1000000;
        if (green&&purple){
            pos = calculateAllShootPos();
        } else if (green){
            pos = calculateColorShootPos(ArtifactColor.GREEN);
        } else if (purple){
            pos = calculateColorShootPos(ArtifactColor.PURPLE);
        }
        Compartment compartment = getEjectCompartment(pos);
        if (compartment==null||compartment.color()==ArtifactColor.NOTHING){
            return false;
        }
        return true;
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
                targets.add(compartment.getOuttakeCoords());
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
                //new TelemetryItem(()->"Match found"+compartment.getOuttakeCoords());
            }
        }
        double closestTarget = findClosestTarget(targets);
        updatePos = closestTarget;
        String updateString ="Color Outtake Pos: "+updatePos;
        //new TelemetryItem( ()->updateString);
        return closestTarget;
    }
//    private Command shootPattern(){
//        Command command1=shootPurple;
//        Command command2=shootPurple;
//        Command command3=shootPurple;
//        if (pattern.first()==ArtifactColor.GREEN){
//            command1=shootGreen;
//        }
//        if (pattern.second()==ArtifactColor.GREEN){
//            command2 = shootGreen;
//        }
//        if (pattern.third()==ArtifactColor.GREEN){
//            command3 = shootGreen;
//        }
//        Command toRet = new SequentialGroup(
//                command1,
//                command2,
//                command3
//        );
//        return toRet;
//    }

    public boolean readColorAndReturnValidity(){
        ArtifactColor curcolor=colorSensor.read();

        Compartment compartment= getIntakeCurCompartment();

        if (compartment!=null) {
            //new TelemetryItem(()->"Set "+getCurCompartmentString()+"to: "+curcolor.toString());
            compartment.setColor(curcolor);

        } else; //new TelemetryItem(()->"Compartment not identified");
        return curcolor != ArtifactColor.NOTHING;
    }
    public void setEjectCompartmentToNothing(){
        Compartment compartment = this.getEjectCompartment(curPos);
        if (compartment!=null){
            compartment.setColor(ArtifactColor.NOTHING);
        } else { new TelemetryItem(()->"Eject Compartment is invalid");}
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

        double normalizedCoords = pos%ticksPerRev;
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
        String name;
        if (toRet!=null){
            name = toRet.toString();
        } else {
            name = "invalid";
        }
        double finalNormalizedCoords = normalizedCoords;
        new TelemetryItem(()->"Eject Pos: "+ finalNormalizedCoords +"Eject Compartment: "+name);
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
            //new TelemetryData("targets size",()->targets.size()*1.);
            double closestDistance =2000000000 ;
            for (int i=0;i<targets.size();i++){
                double target = modifyTarget(targets.get(i));
                double distance = curPos-target;
                if (Math.abs(distance)<closestDistance){
                    closestTarget=target;
                    double finalClosestTarget = closestTarget;
                    //new TelemetryData("New Closest Target",()-> finalClosestTarget);
                    closestDistance=distance;
                }
            }
        } else {
            //new TelemetryItem(()->"Targets is empty");
            closestTarget=updatePos;
        }
        double finalClosestTarget1 = closestTarget;
        //new TelemetryData("New Closest Target",()-> finalClosestTarget1);
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

        double finalModifiedTarget = modifiedTarget;
        //new TelemetryData("Modified Target",()-> finalModifiedTarget);
        return modifiedTarget;
    }
    public void foo(){}
    @Override
    public void periodic(){
        curPos = drumMotor.getCurrentPosition();
        updatePos=controlSystem2.getGoal().getPosition();
//        controlSystem.setGoal(
//                new KineticState(Math.round(updatePos))
//        );
        //controlSystem.setLastMeasurement(new KineticState(drumMotor.getCurrentPosition()));
        controlSystem2.setLastMeasurement(drumMotor.getState());
        //controlSystem2.setGoal(new KineticState(updatePos));

        power= controlSystem2.calculate(drumMotor.getState());
        //power= kp*(updatePos-curPos);
        if (!isZeroing) {
            drumMotor.setPower(power);
        }else {
            drumMotor.setPower(zeroingPower);
        }
    }
}