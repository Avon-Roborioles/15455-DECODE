package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;

import static org.firstinspires.ftc.teamcode.RobotConfig.DrumConstants.*;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Compartment;
import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.Enums.DrumMode;
import org.firstinspires.ftc.teamcode.Enums.Pattern;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Enums.CompartmentColor;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.teamcode.UtilityCommands.IfElse;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.commands.utility.NullCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

@Configurable
public class DrumSubsystem implements Subsystem {
    public static final DrumSubsystem INSTANCE = new DrumSubsystem();
    private MotorEx drumMotor= new MotorEx(drumName);
    private MotorEx intakeMotor=new MotorEx(intakeName);

    private int loopsSinceSensorUpdate=0;


    private Compartment pink = new Compartment(pinkOuttake, pinkIntake,"pink/1");
    private Compartment red = new Compartment(redOuttake, redIntake,"red/2");
    private Compartment black = new Compartment(blackOuttake, blackIntake,"black/3");
    private ArrayList<Compartment> compartments = new ArrayList<>();


    private static double maxPower = 1;


    private double curPos=0;
    private double updatePos = 0;
    private double startPos=0;

    private DrumMode drumMode = DrumMode.DISCRETE_OUTTAKE;
    private ArrayList<Compartment> targetCompartments = new ArrayList<>();


    private ArtifactSensor colorSensor;
    private boolean artifactSensorEnabled = false;

    private boolean hasBeenJammed = false;
    private double lastJamTime =0;
    private double unJamDirection =0;

    double power;

    private boolean useObelisk = true;

    private Pattern pattern = new Pattern.PatternBuilder()
            .first(ArtifactColor.GREEN)
            .second(ArtifactColor.PURPLE)
            .third(ArtifactColor.PURPLE)
            .build();

    private Pattern obeliskPattern = new Pattern.PatternBuilder()
            .first(ArtifactColor.GREEN)
            .second(ArtifactColor.PURPLE)
            .third(ArtifactColor.PURPLE)
            .build();
    private Pattern defaultPattern = new Pattern.PatternBuilder()
            .first(ArtifactColor.PURPLE)
            .second(ArtifactColor.GREEN)
            .third(ArtifactColor.PURPLE)
            .build();
    private Pattern nextPattern = null;


    private double smoothEjectDirection=1;
    public static double kp=0.003;
    public static double kI=0.000000000008;
    public static double kD = 0.0003;

    public ServoEx servo= new ServoEx(servoName);
    private PIDCoefficients coefficients = new PIDCoefficients(kp,kI,kD);



    private ControlSystem controlSystem2;


    public double elapsedTime = 0;
    public double numTunes = 0;
    public double avgTime =0;
    public ElapsedTime timer = new ElapsedTime();
    public void updateTuneTimes(){
        elapsedTime+=timer.time(TimeUnit.MILLISECONDS);
        numTunes++;
        avgTime=elapsedTime/numTunes;
    }
    public void resetTuneTimes(){
        elapsedTime=0;
        numTunes=0;
        avgTime=0;
    }
    public Command tuneDrum = new LambdaCommand()
            .setStart(()->{timer.reset();timer.startTime();plusOneRev();})
            .setIsDone(()->controlSystem2.isWithinTolerance(kineticStateTolerance))
            .setStop((Boolean b)->{updateTuneTimes();});


    public Command servoFreeRotate = new SetPosition(servo,servoIntakePos).requires(servo);
    public Command servoEject = new ParallelGroup(
            new SetPosition(servo,servoEjectPos),
            new Delay(0.2)
    ).requires(servo);
    private double ejectDelay = 0.15;



    public Command rapidOuttake = new SequentialGroup(
            new LambdaCommand()
                    .setStart(()->setToTargetPattern(pattern))
                    .setIsDone(this::isWithinTolerance),

            servoEject,
            new Delay(1.5),
            new InstantCommand(()->new TelemetryItem(()->"Finished Running to rapid setup")),
            new LambdaCommand()
                    .setStart(()->setToEjectRapid())
                    .setIsDone(()->controlSystem2.isWithinTolerance(kineticStateTolerance))
    );



    public Command shootPurple= new IfElse(
            ()->!isValid(false,true),
            new NullCommand(5,6),
            new SequentialGroup(

                    servoEject,
                    new LambdaCommand()
                            .setStart((()->this.setToOuttakeColor(ArtifactColor.PURPLE)))
                            .setIsDone(this::isWithinTolerance),


                    new Delay(ejectDelay),
                    new InstantCommand(this::setEjectCompartmentToNothing)

            )
    );//.requires(servo,drumMotor);
    public Command shootGreen=new IfElse(
            ()->!isValid(true,false),
            new NullCommand(5,6),
            new SequentialGroup(
                    servoEject,
                    new LambdaCommand()
                            .setStart((()->this.setToOuttakeColor(ArtifactColor.GREEN)))
                            .setIsDone(this::isWithinTolerance),
                    new Delay(ejectDelay),
                    new InstantCommand(this::setEjectCompartmentToNothing)
            )
    );//.requires(servo,drumMotor);;
    public Command shootAny=new IfElse(
            ()->!isValid(true,true),
            new NullCommand(5,6),
            new SequentialGroup(
                    new LambdaCommand()
                            .setStart((this::setToOuttakeAny))
                            .setIsDone(this::isWithinTolerance),
                    //new Delay(ejectDelay),
                    new InstantCommand(this::setEjectCompartmentToNothing)
            )
    );//.requires(servo,drumMotor);

    public Command shootWeakPurple = new IfElse(
            ()->isValid(false,true),
            shootPurple,
            shootAny
    );
    public Command shootWeakGreen = new IfElse(
            ()->isValid(true,false),
            shootGreen,
            shootAny
    );

    public Command shootFirstPattern = new IfElse(
            ()->isNthBallGreen(1),
            shootWeakGreen,
            shootWeakPurple
    );
    public Command shootSecondPattern = new IfElse(
            ()->isNthBallGreen(2),
            shootWeakGreen,
            shootWeakPurple
    );
    public Command shootThirdPattern = new IfElse(
            ()->isNthBallGreen(3),
            shootWeakGreen,
            shootWeakPurple
    );

    public Command shootPattern = new SequentialGroup(
            new InstantCommand(this::preparePattern),
            //new InstantCommand(()->new TelemetryItem(()->"Shooting First Pattern")),
            shootFirstPattern,
            //new InstantCommand(()->new TelemetryItem(()->"Shooting Second Pattern")),
            shootSecondPattern,
            //new InstantCommand(()->new TelemetryItem(()->"Shooting Third Pattern")),
            shootThirdPattern,
            new InstantCommand(this::resetNextPattern)

    );


    public boolean isZeroing = false;
    public double zeroingPower =.2;

    public Command turnToIntake=
            new LambdaCommand()
            .setStart(this::setToIntake)
            .setIsDone(()->controlSystem2.isWithinTolerance(kineticStateTolerance));

    public Command rotateIntakeWheels= new LambdaCommand()
            .setStart(()->intakeMotor.getMotor().setPower(1));
    public Command stopIntakeWheels =  new LambdaCommand()
            .setStart(()->intakeMotor.getMotor().setPower(0));
    public Command spitOutIntakeWheels = new LambdaCommand()
            .setStart(()->intakeMotor.getMotor().setPower(-1));


    private Command intakeOneWithoutStop=new IfElse(
            this::canIntake,
            new SequentialGroup(
                    servoFreeRotate,
                    new InstantCommand(this::enableArtifactSensor),
                    turnToIntake,
                    rotateIntakeWheels,
                    new LambdaCommand()
                            .setIsDone(this::readColorAndReturnValidity)
                            .setStop((Boolean b)->{if (b) intakeMotor.getMotor().setPower(0);disableArtifactSensor();}),
                    new InstantCommand(this::disableArtifactSensor)
            ),
            new NullCommand()
    );

    public Command intakeOneBall=new SequentialGroup(
            intakeOneWithoutStop,
            stopIntakeWheels
    ).requires(this);

    public Command secureBalls = new LambdaCommand()
            .setStart(this::setToSecure)
            .setIsDone(()->controlSystem2.isWithinTolerance(kineticStateTolerance));

    public Command intakeThreeBalls =new SequentialGroup(
            intakeOneWithoutStop,
            intakeOneWithoutStop,
            intakeOneWithoutStop,
            new ParallelDeadlineGroup(
                    new Delay(.01),
                    spitOutIntakeWheels
            ),
            stopIntakeWheels,
            secureBalls
    ).requires(this);
    public Command intakeThreeBallsWithPause = new SequentialGroup(
            intakeOneBall,
            intakeOneBall,
            intakeOneBall,
            new ParallelDeadlineGroup(
                    new Delay(.01),
                    spitOutIntakeWheels
            ),

            stopIntakeWheels,
            secureBalls
    ).requires(this);

    public Command manual = new InstantCommand(()->{drumMode=DrumMode.MANUAL;});


    public TouchSensor magneticSensor;

    public Command zero = new LambdaCommand()
            .setStart(this::startZeroing)
            .setIsDone(this::isZeroingFinished)
            .setStop(this::onZeroingStopped)
            .setInterruptible(false);
    public Command plusOneRev = new LambdaCommand()
            .setStart(this::plusOneRev)
            .setIsDone(()->controlSystem2.isWithinTolerance(kineticStateTolerance));

    {
        compartments.add(pink);
        compartments.add(red);
        compartments.add(black);

        targetCompartments.add(pink);

    }
    boolean drumHasBeenReset = false;
    @Override
    public void initialize(){
        hasBeenJammed=false;
        lastJamTime=0;
        magneticSensor=ActiveOpMode.hardwareMap().get(TouchSensor.class,"magSensor");
        controlSystem2 = ControlSystem.builder()
                .posPid(coefficients)
                .build();
        controlSystem2.setGoal(new KineticState(getCurPos()));

        //drum motor
        {
            drumMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            curPos=getCurPos();
            updatePos = getCurPos();
        }
        colorSensor = new ArtifactSensor(ActiveOpMode.hardwareMap());

        //telemetry
        {
            new TelemetryData("Drum Motor Position", drumMotor::getCurrentPosition);
            new TelemetryData("Drum Motor Velocity",drumMotor::getVelocity);
            new TelemetryData("Drum Motor Adjusted Pos",this::getCurPos);
            new TelemetryData("Drum Target",()->updatePos);
            new TelemetryData("Drum Power",()->drumMotor.getPower());
            new TelemetryData("Drum Acceleration",()->drumMotor.getState().getAcceleration());
            new TelemetryItem(()->"Drum Mode"+drumMode);
            new TelemetryData("Drum Current",()->drumMotor.getMotor().getCurrent(CurrentUnit.AMPS));
            new TelemetryItem(()->"Next Pattern: "+this.getNextPatternString());
            new TelemetryData("Error",()->controlSystem2.getGoal().getPosition()-getCurPos());
        }
    }

    public void readyAuto(){
        //setZero(607);
        pink.setColor(ArtifactColor.GREEN);
        red.setColor(ArtifactColor.PURPLE);
        black.setColor(ArtifactColor.PURPLE);
    }

    //says current rotation the drum is at corresponds to the position inputted
    public void setZero(double pos){
        startPos=pos;
        drumMotor.getMotor().setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        controlSystem2.reset();
        controlSystem2.setGoal(new KineticState(startPos));
    }

    public void enableArtifactSensor(){
        artifactSensorEnabled=true;
    }
    public void disableArtifactSensor(){
        artifactSensorEnabled=false;
    }
    public void resetSensor(){
        colorSensor.reset();
    }
    private  void setToIntake(){
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
    private void setToSecure(){
        targetCompartments.clear();
        drumMode=DrumMode.SECURE;
        updateTarget();
    }
    private void plusOneRev(){
        controlSystem2.setGoal(new KineticState(controlSystem2.getGoal().getPosition()+ticksPerRev/3));
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
        double posTolerance = kineticStateTolerance.getPosition();
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

    public String getNextPatternString(){
        if (nextPattern==null){
            return "Null Pattern";
        } else {
            return nextPattern.toString();
        }
    }
    public boolean isNthBallGreen(int patternNumber){
        boolean isGreen = false;
        ArrayList<ArtifactColor> colors;
        if (patternNumber==1){
            colors=nextPattern.first();
        } else if (patternNumber==2){
            colors=nextPattern.second();
        } else {
            colors=nextPattern.third();
        }

        boolean isPurple=false;

        for (ArtifactColor color : colors){
            if (color.equals(ArtifactColor.GREEN)){
                isGreen=true;
            } else if (color.equals(ArtifactColor.PURPLE)){
                isPurple=true;
            }

        }

        if (false&&isPurple&&isGreen){
            int numPurples=0;
            int numGreens=0;
            for (Compartment compartment:compartments){


                if (compartment.color().equals(ArtifactColor.PURPLE)) numPurples++;
                if (compartment.color().equals(ArtifactColor.GREEN)) numGreens++;



            }

            //parsing third item
            boolean thirdPurple=false;
            boolean thirdGreen=false;
            for (ArtifactColor color : pattern.third()){
                if (color.equals(ArtifactColor.GREEN)){
                    thirdGreen=true;
                } else if (color.equals(ArtifactColor.PURPLE)){
                    thirdPurple=true;
                }

            }

            int numPurplesDemanded=0;
            int numGreensDemanded=0;

            if (!(thirdGreen&&thirdPurple)){
                if (thirdGreen) numGreensDemanded++;
                else if (thirdPurple) {
                    numPurplesDemanded++;
                }
            }

            if (!(patternNumber==3)){
                boolean secondPurple=false;
                boolean secondGreen=false;
                for (ArtifactColor color : pattern.second()){
                    if (color.equals(ArtifactColor.GREEN)){
                        secondGreen=true;
                    } else if (color.equals(ArtifactColor.PURPLE)){
                        secondPurple=true;
                    }

                }

                if (!(secondGreen&&secondPurple)){
                    if (secondGreen) numGreensDemanded++;
                    else if (secondPurple) {
                        numPurplesDemanded++;
                    }
                }

                if (!(patternNumber==2)){
                    boolean firstPurple=false;
                    boolean firstGreen=false;
                    for (ArtifactColor color : pattern.first()){
                        if (color.equals(ArtifactColor.GREEN)){
                            firstGreen=true;
                        } else if (color.equals(ArtifactColor.PURPLE)){
                            firstPurple=true;
                        }

                    }

                    if (!(firstGreen&&firstPurple)){
                        if (firstGreen) numGreensDemanded++;
                        else if (firstPurple) {
                            numPurplesDemanded++;
                        }
                    }
                }

            }


            if (!((numPurplesDemanded==numPurples)&&(numGreens==numGreensDemanded))){
                if (numPurples<numPurplesDemanded){
                    isGreen=true;
                }
                if (numGreens<numGreensDemanded){
                    isGreen=false;
                }
            }
        }

        return isGreen;
    }

    public void setNextPattern(Pattern pattern){
        nextPattern=pattern;
    }
    public void preparePattern(){
        if (nextPattern==null){
            nextPattern=defaultPattern;
            if (useObelisk){
                nextPattern=obeliskPattern;
            }
        }
    }

    public void resetNextPattern(){
        nextPattern=null;
    }
    public void setObeliskPattern(int tagID){
        if (tagID==21){
            obeliskPattern= RobotConfig.DrumConstants.gppPattern;
        } else if (tagID==22){
            obeliskPattern= RobotConfig.DrumConstants.pgpPattern;
        } else if (tagID==23){
            obeliskPattern=RobotConfig.DrumConstants.ppgPattern;
        }
    }

    public boolean isValid(ArtifactColor... colors){
        boolean isPurple = false;
        boolean isGreen = false;

        for (ArtifactColor color:colors){
            if (color.equals(ArtifactColor.PURPLE)){
                isPurple=true;
            } else if (color.equals(ArtifactColor.GREEN)){
                isGreen=true;
            }
        }
        return isValid(isGreen,isPurple);
    }

    public boolean isWithinTolerance(){
        return controlSystem2.isWithinTolerance(kineticStateTolerance);
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
    public boolean canIntake(){
        boolean toRet = false;
        for (Compartment compartment:compartments){
            if (compartment.color().equals(ArtifactColor.NOTHING)){
                toRet=true;
            }
        }
        return toRet;
    }

    public boolean readColorAndReturnValidity(){
        ArtifactColor curcolor=colorSensor.read();

        Compartment compartment= getIntakeCurCompartment();
        if (!artifactSensorEnabled){
            return false;
        }
        if (compartment!=null) {
            //new TelemetryItem(()->"Set "+compartment.toString()+"to: "+curcolor.toString());
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
        double toleranceInt = kineticStateTolerance.getPosition();
        if (Math.abs(pink.getOuttakeCoords()-normalizedCoords)<toleranceInt||Math.abs(pink.getOuttakeCoords()-normalizedCoords)>ticksPerRev-toleranceInt){
            toRet= pink;
        } else if (Math.abs(red.getOuttakeCoords()-normalizedCoords)<toleranceInt){
            toRet=red;
        } else if (Math.abs(black.getOuttakeCoords()-normalizedCoords)<toleranceInt){
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
        //new TelemetryItem(()->"Eject Pos: "+ finalNormalizedCoords +"Eject Compartment: "+name);
        return toRet;
    }

    public double getCurPos(){
        return drumMotor.getCurrentPosition()+startPos;
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
    public void resetZero(){
        setZero(0);
    }
    public void resetCompartments(){
        for (Compartment compartment: compartments){
            compartment.setColor(ArtifactColor.NOTHING);
        }
    }
    public void updateTarget(){
        ArrayList<Double> doubleTargets = new ArrayList<>();

        if (drumMode.equals(DrumMode.INTAKE)||drumMode.equals(DrumMode.DISCRETE_OUTTAKE)) {
            maxPower=1;
            if (drumMode.equals(DrumMode.INTAKE)){
                if (targetCompartments.isEmpty()){
                    targetCompartments.add(red);
                    targetCompartments.add(pink);
                    targetCompartments.add(black);
                }
            }
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
            //TelemetryManager.getInstance().addTempTelemetry("Double target: "+closestTarget);
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

        } else if (drumMode.equals(DrumMode.ZEROING)){
            power = zeroingPower;
            //drumMotor.setPower(zeroingPower);
        } else if (drumMode.equals(DrumMode.SECURE)){
            for (Compartment compartment:compartments){
                    doubleTargets.add(compartment.getIntakeCoords()+ticksPerRev/12);
                    doubleTargets.add(compartment.getOuttakeCoords()+ticksPerRev/12);
            }
            if (doubleTargets.isEmpty()){
                //doubleTargets.add(curPos+ticksPerRev/12);
            }
            double closestTarget =findClosestTarget(doubleTargets);
            controlSystem2.setGoal(new KineticState(closestTarget));
        }else if (drumMode.equals(DrumMode.STANDBY)){
            controlSystem2.setGoal(new KineticState(getCurPos()));
        }else if (drumMode.equals(DrumMode.MANUAL)){
            controlSystem2.setGoal(new KineticState(getCurPos()));
        }
    }
    public static int useUnJamming = 1;
    public double lastVelocity=0;
    public boolean isJammed(){
        return useUnJamming == 1
                && Math.abs(drumMotor.getVelocity()) < 30
                && Math.abs(drumMotor.getPower()) > .3
                && Math.abs(drumMotor.getVelocity()) < Math.abs(previousVelocity)
                && !(drumMotor.getPower() * drumMotor.getVelocity() < 0);
    }
    public double previousVelocity = 0;

    public void useObelisk(){
        useObelisk=true;
    }
    public void stopUseObelisk(){
        useObelisk=false;
    }

    @Override
    public void periodic(){
        if (Gamepads.gamepad1().rightStickButton().get()){
            drumMotor.setPower(0);
        } else {
            curPos = getCurPos();
            updatePos = controlSystem2.getGoal().getPosition();
            if (hasBeenJammed) {
                if (!(System.currentTimeMillis() - lastJamTime < unJamTimeMs)) {
                    hasBeenJammed = false;
                }
            }
            if (isJammed()) {
                lastVelocity = drumMotor.getVelocity();
                hasBeenJammed = true;
                unJamDirection = -1 * Math.signum(drumMotor.getPower());
                lastJamTime = System.currentTimeMillis();
                drumMotor.setPower(unJamDirection * unJamPower);

            } else if (hasBeenJammed) {

            } else if (drumMode.equals(DrumMode.SECURE)
                    || drumMode.equals(DrumMode.INTAKE)
                    || drumMode.equals(DrumMode.DISCRETE_OUTTAKE)
            ) {
                power = controlSystem2.calculate(new KineticState(getCurPos(), drumMotor.getState().getVelocity(), drumMotor.getState().getAcceleration()));
                if (power > 0 && power > maxPower) {
                    power = maxPower;
                } else if (power < 0 && power < -maxPower) {
                    power = -maxPower;
                }
                drumMotor.setPower(power);
            } else if (drumMode.equals(DrumMode.RAPID_OUTTAKING)) {
                double oldPower = power;
                if (getEjectCompartment(curPos) == null) {
                    power = .3;
                } else {
                    power = .1;
                }
                if (controlSystem2.isWithinTolerance(kineticStateTolerance)) power = 0;
                if (Math.abs(oldPower - power) > .01) drumMotor.setPower(power);
            } else if (drumMode.equals(DrumMode.STANDBY)) {
                drumMotor.setPower(0);
            } else if (drumMode.equals(DrumMode.MANUAL)) {
                controlSystem2.setGoal(new KineticState(controlSystem2.getGoal().getPosition() + 10 * Gamepads.gamepad2().getGamepad().invoke().right_stick_y));
                drumMotor.setPower(controlSystem2.calculate(new KineticState(getCurPos(), drumMotor.getVelocity())));
            }
        }
        loopsSinceSensorUpdate++;
        if (loopsSinceSensorUpdate==3){
            if (artifactSensorEnabled) {
                colorSensor.updateSensorReads();
            }
            loopsSinceSensorUpdate=0;
        }
        controlSystem2.setLastMeasurement(new KineticState(getCurPos(),drumMotor.getState().getVelocity(),drumMotor.getState().getAcceleration()));

        coefficients.kI=kI;
        coefficients.kP=kp;
        coefficients.kD=kD;

        previousVelocity=drumMotor.getVelocity();
    }
}