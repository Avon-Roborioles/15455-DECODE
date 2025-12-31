package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
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
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class LauncherSubsystem implements Subsystem {

    public static final LauncherSubsystem INSTANCE = new LauncherSubsystem();
    private final double ticksPerRev = 537.6;

    private MotorEx launchMotor = new MotorEx("launchMotor");


    public static double vkP=.02;
    public static double vkI=0;
    public static double vkD=0;

    public static double kV = .001;
    public static double kA =.005;
    public static double rpmMultiplier=.01;


    PIDCoefficients coefficients=new PIDCoefficients(.02);
    BasicFeedforwardParameters basicFeedforwardParameters=new BasicFeedforwardParameters(.001,.005);

    private ControlSystem normalControlSystem = ControlSystem.builder()
            .velPid(coefficients)
            .basicFF(basicFeedforwardParameters)
            //.angular(AngleType.REVOLUTIONS,feedback->feedback.velPid(1))
            .build();
    private ControlSystem controlSystem = ControlSystem.builder()
            .velPid(coefficients)

            //.angular(AngleType.REVOLUTIONS,feedback->feedback.velPid(1))
            .build();

    private static double rpm;
    public static double distanceCm= 100;
    public static double realPower=1;
    public Command runToCalculatedPos= new LambdaCommand()
            .setUpdate(this::calculateVelocity)
            .setIsDone(()->controlSystem.isWithinTolerance(new KineticState(50,0,0)));
    public Command stop= new LambdaCommand()
            .setUpdate(this::stop)
            .setIsDone(()->controlSystem.isWithinTolerance(new KineticState(50,0,0)));


    @Override
    public void initialize(){

        rpm =0;
        launchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launchMotor.setDirection(-1);
        new TelemetryData("Position",()->launchMotor.getCurrentPosition());
        new TelemetryData("Velocity",()->-launchMotor.getVelocity());
        new TelemetryData("Velocity (Deg)",()->-launchMotor.getMotor().getVelocity(AngleUnit.DEGREES));
        new TelemetryData("Velocity (Deg ->RPM)",()->-launchMotor.getMotor().getVelocity(AngleUnit.DEGREES)/360*60);
        new TelemetryData("Target Velocity",()->rpm);
        //new TelemetryData("Current RPM",()-> launchMotor.getMotor().getVelocity(AngleUnit.RADIANS)*2*Math.PI);
        new TelemetryData("Calculated Distance Cm",()->distanceCm);
        new TelemetryData("Power",()->launchMotor.getPower());
        //new TelemetryData("Version",()->1.);
    }

    public void increaseRPMby100(){
        rpm+=100;
    }

    public void decreaseRPMby100(){
        rpm-=100;

    }
    public void calculateVelocity(){
        Pose pedroPose = PedroComponent.follower().getPose();
        double distanceInch = RobotConfig.FieldConstants.redGoal.distanceFrom(
                pedroPose.copy().linearCombination(
                        new Pose(
                                Math.cos(pedroPose.getHeading()),
                                Math.sin(pedroPose.getHeading())
                        ),
                        1,
                        -9.5
                )
        );
        double distance = distanceInch*2.54;
        distanceCm=distance;
        double radians = Math.toRadians(37);
        //rpm = distance*Math.sqrt(-7614.432/ ( 2*Math.pow(Math.cos(radians),2)*(87-distance*Math.tan(radians)) ) );
         // 500
        rpm = 1500;
        controlSystem.setGoal(new KineticState(0,rpm));

    }
    private void stop(){
        rpm = 0;
    }
    @Override
    public void periodic(){
        controlSystem.setGoal(new KineticState(0,rpm));
        normalControlSystem.setGoal(new KineticState(0,rpm));
        double maxPower = 1;
        double power;

        if (normalControlSystem.isWithinTolerance(new KineticState(0,250))) {
            power =controlSystem.calculate(new KineticState(0, -launchMotor.getVelocity()));
            TelemetryManager.getInstance().addTempTelemetry("In normal control system");
        } else {
            power = normalControlSystem.calculate(new KineticState(0,-launchMotor.getVelocity()));
            TelemetryManager.getInstance().addTempTelemetry("In speed up control system");
        }
        if (rpm == 0){
            power = 0;
        }
        if (power>0&&power>maxPower){
            power = maxPower;
        } else if (power<0&&power<maxPower){
            power = -maxPower;
        }
        power = power + rpm*rpmMultiplier;
        power = realPower;
        launchMotor.setPower(power);
        coefficients.kP=vkP;
        coefficients.kI=vkI;
        coefficients.kD=vkD;
        basicFeedforwardParameters.kV=kV;
        basicFeedforwardParameters.kA=kA;
    }
}
