package org.firstinspires.ftc.teamcode.Subsytems;


import static org.firstinspires.ftc.teamcode.RobotConfig.LauncherConstant.*;
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedforward.BasicFeedforwardParameters;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.controllable.MotorGroup;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.VoltageCompensatingMotor;

@Configurable
public class LauncherSubsystem implements Subsystem {

    public static final LauncherSubsystem INSTANCE = new LauncherSubsystem();
    private MotorEx cHubMotor  =new MotorEx(cHubLaunchName);
    private MotorEx eHubMotor = new MotorEx(eHubLaunchName);

    private MotorGroup launchGroup = new MotorGroup(
            cHubMotor,
            eHubMotor
    );
    private double lastTickTime = 0;
    private double deltaCharge =0;

    public Servo servo;

    public static double vkP=.003;
    public static double vkI=0;
    public static double vkD=0;

    public static double kV = .00055;
    public static double kA =.00;

    public static double shootRPM = 1600;


    PIDCoefficients coefficients=new PIDCoefficients(.02);
    BasicFeedforwardParameters basicFeedforwardParameters=new BasicFeedforwardParameters(kV,kA);

    private ControlSystem normalControlSystem = ControlSystem.builder()
            .velPid(coefficients)
            .basicFF(basicFeedforwardParameters)
            //.angular(AngleType.REVOLUTIONS,feedback->feedback.velPid(1))
            .build();


    private  double rpm;
    public static double distanceCm= 100;
    public static double realPower=0 ;
    public Command runToCalculatedPos= new LambdaCommand()
            .setUpdate(this::calculateVelocity)
            .setIsDone(()-> Math.abs(rpm-cHubMotor.getVelocity())<40)
            .setStop((Boolean b)->{if (b){rpm=0;}});

    public Command runBackToCalculatedPos = new LambdaCommand()
            .setUpdate(this::calculateVelocity)
            .setIsDone(()-> Math.abs(rpm-cHubMotor.getVelocity())<100)
            .setStop((Boolean b)->{if (b){rpm=0;}});
    public Command stop= new LambdaCommand()
            .setUpdate(this::stop)
            .setIsDone(()->true);


    @Override
    public void initialize(){
        deltaCharge=0;
        rpm =0;
        servo = ActiveOpMode.hardwareMap().get(Servo.class,"driverLight");

        eHubMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        cHubMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        cHubMotor.setDirection(-1);
        eHubMotor.setDirection(1);
        new TelemetryData("Launcher Position",()-> cHubMotor.getCurrentPosition());
        new TelemetryData("Launcher Velocity",()->cHubMotor.getVelocity());
        new TelemetryData("Launcher Target Velocity",()->rpm);
        new TelemetryData("Calculated Distance Cm",()->distanceCm);
        new TelemetryData("Launcher Power",()-> launchGroup.getPower());

    }

    public void increaseRPMby50(){
        rpm+=50;
    }

    public void decreaseRPMby50(){
        rpm-=50;

    }
    public void calculateVelocity(){
        Pose pedroPose = PedroComponent.follower().getPose();

        Pose goal = RobotConfig.FieldConstants.redGoal;
        if (AllianceComponent.getColor().equals(AllianceColor.BLUE)){
            goal=RobotConfig.FieldConstants.blueGoal;
        }
        double distanceInch = goal.distanceFrom(
                pedroPose.copy().linearCombination(
                        new Pose(
                                Math.cos(pedroPose.getHeading()),
                                Math.sin(pedroPose.getHeading())
                        ),
                        1,
                        -7
                )
        );
        double distance = distanceInch*2.54;
        distanceCm=distance;
        double radians = Math.toRadians(37);
        //rpm = distance*Math.sqrt(gravity/ ( 2*Math.pow(Math.cos(radians),2)*(87-distance*Math.tan(radians)) ) );
         // 500
//        rpm = shootRPM;
        rpm =0.000151386*Math.pow(distance,3)-0.121004*Math.pow(distance,2)+29.927*Math.pow(distance,1)-3629;
        normalControlSystem.setGoal(new KineticState(0,rpm));

    }
    public void stop(){
        rpm = 0;
    }
    @Override
    public void periodic(){
        normalControlSystem.setGoal(new KineticState(launchGroup.getCurrentPosition(),rpm));
        double maxPower = 1;
        double power;
        //calculateVelocity();
        power = normalControlSystem.calculate(cHubMotor.getState());

        if (rpm == 0){
            power = 0;
        }
//        if (power>0&&power>maxPower){
//            power = maxPower;
//        } else if (power<0&&power<maxPower){
//            power = -maxPower;
//        }

        if (realPower!=0)power = realPower;
        if (ActiveOpMode.opModeIsActive()) {
            launchGroup.setPower(power);
        }
        coefficients.kP=vkP;
        coefficients.kI=vkI;
        coefficients.kD=vkD;
        basicFeedforwardParameters.kV=kV;
        basicFeedforwardParameters.kA=kA;
        if (lastTickTime ==0){
            lastTickTime = System.currentTimeMillis();
        }
        //deltaCharge+=(System.currentTimeMillis()-lastTickTime)/1000.* (cHubMotor.getMotor().getCurrent(CurrentUnit.AMPS)+eHubMotor.getMotor().getCurrent(CurrentUnit.AMPS));
        lastTickTime=System.currentTimeMillis();
        if (normalControlSystem.isWithinTolerance(new KineticState(0,40))){
            servo.setPosition(.5);
            TelemetryManager.getInstance().addTempTelemetry("Finished Speed");
        } else {
            servo.setPosition(.63);
            TelemetryManager.getInstance().addTempTelemetry("NOT FINISHED");

        }
    }
}
