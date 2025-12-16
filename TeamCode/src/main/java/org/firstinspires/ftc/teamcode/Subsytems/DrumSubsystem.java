package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.Compartment;
import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.Enums.DrumMode;
import org.firstinspires.ftc.teamcode.Enums.Pattern;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Enums.CompartmentColor;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.UtilityCommands.IfElse;

import java.util.ArrayList;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
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
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;
import dev.nextftc.hardware.powerable.SetPower;

@Configurable
public class DrumSubsystem implements Subsystem {
    public static final DrumSubsystem INSTANCE = new DrumSubsystem();
    private MotorEx drumMotor= new MotorEx("drumMotor");
    private MotorEx intakeMotor=new MotorEx("intakeMotor");
    private final double ticksPerRev=1211;

    private Compartment pink = new Compartment(0, 607,"pink/1");
    private Compartment red = new Compartment(ticksPerRev / 3, 1015,"red/2");
    private Compartment black = new Compartment(ticksPerRev / 3 * 2, 203,"black/3");
    private ArrayList<Compartment> compartments = new ArrayList<>();
    private double servoEjectPos=.2539;
    private double servoIntakePos = 0;

    private static double maxPower = 1;


    private double curPos=0;
    private double updatePos = 0;

    private DrumMode drumMode = DrumMode.DISCRETE_OUTTAKE;
    private ArrayList<Compartment> targetCompartments = new ArrayList<>();




    private ArtifactSensor colorSensor;
    double power;
    public KineticState tolerance = new KineticState(10,0);

    private Pattern pattern = new Pattern(ArtifactColor.PURPLE,ArtifactColor.GREEN,ArtifactColor.PURPLE);
    private double smoothEjectDirection=1;
    public static double kp=0.004;
    public static double kI=0.000000000008;
    public static double kD = 0.0004;

    public ServoEx servo= new ServoEx("ejectServo");
    private PIDCoefficients coefficients = new PIDCoefficients(kp,kI,kD);



    private ControlSystem controlSystem2;




    public Command servoFreeRotate = new SetPosition(servo,servoIntakePos);
    public Command servoEject = new SetPosition(servo,servoEjectPos);
    private double ejectDelay = 3;


    public Command rapidOuttake = new SequentialGroup(
            new LambdaCommand()
                    .setStart(()->setToTargetPattern(new Pattern(ArtifactColor.PURPLE,ArtifactColor.GREEN,ArtifactColor.PURPLE)))
                    .setIsDone(()->controlSystem2.isWithinTolerance(tolerance)),

            servoEject,
            new Delay(1.5),
            new InstantCommand(()->new TelemetryItem(()->"Finished Running to rapid setup")),
            new LambdaCommand()
                    .setStart(()->setToEjectRapid())
                    .setIsDone(()->controlSystem2.isWithinTolerance(tolerance))
    );



    public Command shootPurple= new IfElse(
            ()->!isValid(false,true),
            new NullCommand(5,6),
            new SequentialGroup(
                    servoEject,
                    new LambdaCommand()
                            .setStart((()->this.setToOuttakeColor(ArtifactColor.PURPLE)))
                                .setIsDone(()->controlSystem2.isWithinTolerance(tolerance)),

                    //new Delay(ejectDelay),
//                        stopServo,
                    new InstantCommand(this::setEjectCompartmentToNothing)

            )
    );
    public Command shootGreen=new IfElse(
            ()->!isValid(true,false),
            new NullCommand(5,6),
            new SequentialGroup(
                    servoEject,
                    new LambdaCommand()
                            .setStart((()->this.setToOuttakeColor(ArtifactColor.GREEN)))
                            .setIsDone(()->controlSystem2.isWithinTolerance(tolerance)),
//                        setPower,
                    //new Delay(ejectDelay),
//                        stopServo,
                    new InstantCommand(this::setEjectCompartmentToNothing)

            )
                    );
    public Command shootAny=new IfElse(
            ()->!isValid(true,true),
            new NullCommand(5,6),
            new SequentialGroup(
                    new LambdaCommand()
                            .setStart((this::setToOuttakeAny))
                            .setIsDone(()->controlSystem2.isWithinTolerance(tolerance)),
//                        setPower,
                    new Delay(ejectDelay),
//                        stopServo,
                    new InstantCommand(this::setEjectCompartmentToNothing)

            )
    );;

    public boolean isZeroing = false;
    public double zeroingPower =.2;

//    public Command turnToRed = new LambdaCommand()
//            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.RED))))
//            .setIsDone(()->controlSystem.isWithinTolerance(tole   rance));
//
//    public Command turnToPink= new LambdaCommand()
//            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.PINK))))
//            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));
//
//    public Command turnToBlack= new LambdaCommand()
//            .setStart(()->controlSystem.setGoal(new KineticState(turnToCompartment(CompartmentColor.BLACK))))
//            .setIsDone(()->controlSystem.isWithinTolerance(tolerance));



    public Command turnToIntake=new LambdaCommand()
            .setStart(this::setToIntake)
            .setIsDone(()->controlSystem2.isWithinTolerance(tolerance));

    public Command rotateIntakeWheels= new SetPower(intakeMotor,1);
    public Command stopIntakeWheels =  new SetPower(intakeMotor,0);
    private Command spitOutIntakeWheels = new SetPower(intakeMotor,-1);

    //public Command shootPattern = shootPattern();;

    private Command intakeOneWithoutStop=new SequentialGroup(
            servoFreeRotate,
            turnToIntake,
            //new InstantCommand(()->new TelemetryItem(()->"turned to intake")),
            new ParallelDeadlineGroup(
                    new LambdaCommand()
                                .setIsDone(()->readColorAndReturnValidity())
                        //new ForcedParallelCommand(new InstantCommand(()-> new TelemetryItem(()->"Valid artifact")))
                    ,
                    rotateIntakeWheels
            ),

            stopIntakeWheels
//                ,
//                new ParallelDeadlineGroup(
//                        new Delay(.1),
//                        spitOutIntakeWheels
//                )
    );
    public Command intakeOneBall=new SequentialGroup(
            intakeOneWithoutStop,
            stopIntakeWheels
    ).requires(this);


    public Command intakeThreeBalls =new SequentialGroup(
            intakeOneWithoutStop,
            intakeOneWithoutStop,
            intakeOneWithoutStop,
            new ParallelDeadlineGroup(
                    new Delay(.01),
                    spitOutIntakeWheels
            ),
            stopIntakeWheels
    ).requires(this);;



    public TouchSensor magneticSensor;

    public Command zero = new LambdaCommand()
            .setStart(this::startZeroing)
            .setIsDone(this::isZeroingFinished)
            .setStop(this::onZeroingStopped)
            .setInterruptible(false);
    public Command plusOneRev = new LambdaCommand()
            .setStart(this::plusOneRev)
            .setIsDone(()->controlSystem2.isWithinTolerance(tolerance));

    {
        compartments.add(pink);
        compartments.add(red);
        compartments.add(black);
        targetCompartments.add(pink);
        //drumMotor.reverse();
    }
    @Override
    public void initialize(){
        //intakeMotor=new MotorEx("intakeMotor");
        //controlSystem.setGoal(new KineticState(0));
        magneticSensor=ActiveOpMode.hardwareMap().get(TouchSensor.class,"magSensor");

            controlSystem2 = ControlSystem.builder()
                    .posPid(coefficients)
                    .build();

        //drum motor


        {
            drumMotor.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            //drumMotor= new MotorEx("drumMotor");
            drumMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



            curPos=0;
            updatePos = 0;
        }
        colorSensor = new ArtifactSensor(ActiveOpMode.hardwareMap());


        //telemetry
        {
            new TelemetryData("Drum Motor Position", drumMotor::getCurrentPosition);
            new TelemetryData("Drum Target",()->updatePos);
            //new TelemetryItem(()->"Is intake mode: "+intakeMode);
            //new TelemetryItem(this::getCurCompartmentString);
            new TelemetryData("Drum Power",()->drumMotor.getPower());
            new TelemetryData("Drum Control System 2 Target", ()->controlSystem2.getGoal().getPosition());

            new TelemetryItem(()->pink.toString()+" color: "+pink.color().toString());
            new TelemetryItem(()->red.toString()+" color: "+red.color().toString());
            new TelemetryItem(()->black.toString()+" color: "+black.color().toString());
        }

    }



    private void setToIntake(){
        targetCompartments.clear();
        for (Compartment compartment:compartments){
            if (compartment.color().equals(ArtifactColor.NOTHING)){
                targetCompartments.add(compartment);
            }
        }
        drumMode = DrumMode.INTAKE;
        updateTarget();
    }
    private void setToOuttakeColor(ArtifactColor color){
        targetCompartments.clear();
        for (Compartment compartment:compartments){
            if (compartment.color().equals(color)){
                targetCompartments.add(compartment);
            }
        }
        drumMode=DrumMode.DISCRETE_OUTTAKE;
        updateTarget();;
    }
    private void setToOuttakeAny(){
        targetCompartments.clear();
        for (Compartment compartment:compartments){
            if (!compartment.color().equals(ArtifactColor.NOTHING)){
                targetCompartments.add(compartment);
            }
        }
        drumMode=DrumMode.DISCRETE_OUTTAKE;
        updateTarget();
    }
    private void setToTargetPattern(Pattern pattern){
        this.pattern=pattern;
        drumMode=DrumMode.RAPID_OUTTAKE_SETUP;
        updateTarget();

    }
    private void setToEjectRapid(){
        drumMode=DrumMode.RAPID_OUTTAKING;
        updateTarget();
    }
    private void plusOneRev(){
        controlSystem2.setGoal(new KineticState(controlSystem2.getGoal().getPosition()+ticksPerRev));
    }

    private void startZeroing(){
        isZeroing=true;
        drumMode=DrumMode.ZEROING;
        updateTarget();
    }
    private boolean isZeroingFinished(){
        return magneticSensor.isPressed();
    }
    private void onZeroingStopped(Boolean b){
        isZeroing=false;
        drumMotor.zero();
        updatePos=0;
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
        double posTolerance = tolerance.getPosition();
        if (Math.abs(pink.getIntakeCoords()-normalizedCoords)%ticksPerRev<posTolerance){
            return pink;
        } else if (Math.abs(red.getIntakeCoords()-normalizedCoords)%ticksPerRev<posTolerance){
            return red;
        } else if (Math.abs(black.getIntakeCoords()-normalizedCoords)%ticksPerRev<posTolerance){
            return black;
        } else {
            return  null;
        }

    }


    public boolean isValid(boolean green, boolean purple){
                boolean toRet =false;
        for (Compartment compartment:compartments){
            if (green &&compartment.color().equals(ArtifactColor.GREEN)){
                toRet=true;
            } else if (purple&&compartment.color().equals(ArtifactColor.PURPLE)){
                toRet= true;
            }

        }
        return toRet;
    }

    public boolean readColorAndReturnValidity(){
        ArtifactColor curcolor=colorSensor.read();

        Compartment compartment= getIntakeCurCompartment();

        if (compartment!=null) {
            new TelemetryItem(()->"Set "+compartment.toString()+"to: "+curcolor.toString());
            compartment.setColor(curcolor);
            return curcolor != ArtifactColor.NOTHING;
        } else {

            //new TelemetryItem(()->"Compartment not identified");
            return false;
        }

    }
    public void setEjectCompartmentToNothing(){
        Compartment compartment = this.getEjectCompartment(curPos);
        if (compartment!=null){
            compartment.setColor(ArtifactColor.NOTHING);
        } else { new TelemetryItem(()->"Eject Compartment is invalid");}
    }
    
    private double turnToCompartment(CompartmentColor color){
        double pos=0;
        boolean intakeMode=false;
        if (drumMode.equals(DrumMode.INTAKE)){
            intakeMode=true;
        }
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
        if (Math.abs(pink.getOuttakeCoords()-normalizedCoords)<30||Math.abs(pink.getOuttakeCoords()-normalizedCoords)>850){
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



    private double findClosestTarget(ArrayList<Double> targets){
        double closestTarget = 0;
        if (!targets.isEmpty()) {
            //new TelemetryData("targets size",()->targets.size()*1.);
            double closestDistance =2000000000 ;
            for (int i=0;i<targets.size();i++){
                double target = modifyTarget(targets.get(i));
                double distance = Math.abs(curPos-target);
                if (distance<closestDistance){
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
    //input a target and this outputs another number that is the same physical position but is the closest angular tick to the current position
    private double modifyTarget(double target){
        double multiplier = -1;
        if (target<curPos){
            multiplier=1;
        }
        double modifiedTarget = target;
        double turn = multiplier*ticksPerRev;
        while(Math.abs(modifiedTarget-curPos)>(ticksPerRev/2.)){
            modifiedTarget+=turn;
        }

        double finalModifiedTarget = modifiedTarget;
        TelemetryManager.getInstance().addTempTelemetry("Modified Target: "+ finalModifiedTarget);
        return modifiedTarget;
    }

    public void updateTarget(){
        ArrayList<Double> doubleTargets = new ArrayList<>();

        if (drumMode.equals(DrumMode.INTAKE)||drumMode.equals(DrumMode.DISCRETE_OUTTAKE)) {
            maxPower=1;
            for (Compartment compartment:targetCompartments){
                if (drumMode.equals(DrumMode.INTAKE)){
                    doubleTargets.add(compartment.getIntakeCoords());
                } else {
                    doubleTargets.add(compartment.getOuttakeCoords());
                }
            }
            if (doubleTargets.isEmpty()){
                doubleTargets.add(curPos);
            }


            double closestTarget =findClosestTarget(doubleTargets);
            TelemetryManager.getInstance().addTempTelemetry("Double target: "+closestTarget);
            Compartment targetCompartment=pink;
            for (Compartment compartment:compartments){
                if (drumMode.equals(DrumMode.INTAKE)){
                    if (modifyTarget(compartment.getIntakeCoords())==closestTarget){
                        targetCompartment=compartment;
                    }
                } else {
                    if (modifyTarget(compartment.getOuttakeCoords())==closestTarget){
                        targetCompartment=compartment;
                    }
                }
            }
            controlSystem2.setGoal(new KineticState(closestTarget));

            TelemetryManager.getInstance().addTempTelemetry("COMPARTMENT TARGET: "+targetCompartment.toString());

        } else if (drumMode.equals(DrumMode.RAPID_OUTTAKING)){
            controlSystem2.setGoal(new KineticState(curPos+ticksPerRev*-1*smoothEjectDirection));

            maxPower=.4;
        } else if (drumMode.equals(DrumMode.RAPID_OUTTAKE_SETUP)){


            for (Compartment compartment:compartments) {
                if (compartment.color().equals(pattern.second())){
                    int listIndex = compartments.indexOf(compartment);
                    int indexOfLeftBall = listIndex-1;
                    int indexOfRightBall = listIndex+1;
                    if (indexOfLeftBall<0){
                        indexOfLeftBall+=3;
                    }
                    if (indexOfRightBall>2){
                        indexOfRightBall-=3;
                    }
                    if (compartments.get(indexOfLeftBall).color().equals(pattern.first())){
                        doubleTargets.add(compartment.getIntakeCoords());
                    } else {

                    }
                }
                if (doubleTargets.isEmpty()){
                    doubleTargets.add(curPos);
                }


                double closestTarget =findClosestTarget(doubleTargets);
                TelemetryManager.getInstance().addTempTelemetry("Double target: "+closestTarget);
                Compartment targetCompartment=pink;
                for (Compartment compartment1:compartments){

                    if (modifyTarget(compartment1.getIntakeCoords())==closestTarget){
                        targetCompartment=compartment;
                    }

                }
                int listIndex = compartments.indexOf(targetCompartment);
                int indexOfLeftBall = listIndex-1;
                int indexOfRightBall = listIndex+1;
                if (indexOfLeftBall<0){
                    indexOfLeftBall+=3;
                }
                if (indexOfRightBall>2){
                    indexOfRightBall-=3;
                }
                if (compartments.get(indexOfLeftBall).color().equals(pattern.first())){
                    smoothEjectDirection=1;
                } else {
                    smoothEjectDirection=-1;
                }
                controlSystem2.setGoal(new KineticState(closestTarget));


            }
        }else if (drumMode.equals(DrumMode.ZEROING)){
            power = zeroingPower;
            //drumMotor.setPower(zeroingPower);
        }
    }
    @Override
    public void periodic(){
        curPos = drumMotor.getCurrentPosition();
        updatePos=controlSystem2.getGoal().getPosition();

        if (drumMode.equals(DrumMode.INTAKE)||drumMode.equals(DrumMode.DISCRETE_OUTTAKE)||drumMode.equals(DrumMode.RAPID_OUTTAKE_SETUP)||drumMode.equals(DrumMode.RAPID_OUTTAKING)){
            power= controlSystem2.calculate(drumMotor.getState());
            TelemetryManager.getInstance().addTempTelemetry("Before filter power: "+power);
            if (power>0&&power>maxPower){
                power = maxPower;
            } else if (power<0&&power<-maxPower){
                power=-maxPower;
            }
            drumMotor.setPower(power);
        }

        controlSystem2.setLastMeasurement(drumMotor.getState());

        TelemetryManager.getInstance().addTempTelemetry("Control system is finished: "+controlSystem2.isWithinTolerance(tolerance));

        coefficients.kI=kI;
        coefficients.kP=kp;
        coefficients.kD=kD;
    }
}