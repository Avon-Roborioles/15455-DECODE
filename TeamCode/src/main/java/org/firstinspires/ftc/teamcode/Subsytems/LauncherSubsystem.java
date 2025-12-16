package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.functionalInterfaces.Configurator;
import dev.nextftc.hardware.controllable.RunToPosition;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable

public class LauncherSubsystem implements Subsystem {

    public static final LauncherSubsystem INSTANCE = new LauncherSubsystem();
    private final double ticksPerRev = 537.6;

    private MotorEx launchMotor = new MotorEx("launchMotor");


    private ControlSystem normalControlSystem = ControlSystem.builder()
            .velPid(.02)
            //.basicFF(0,.0001)
            //.angular(AngleType.REVOLUTIONS,feedback->feedback.velPid(1))
            .build();
    private ControlSystem controlSystem = ControlSystem.builder()
            .velPid(.02)
            .basicFF(0,.005)
            //.angular(AngleType.REVOLUTIONS,feedback->feedback.velPid(1))
            .build();

    private static double rpm;
    public static double distanceCm= 100;
    public Command runToCalculatedPos= new LambdaCommand()
            .setUpdate(this::calculateVelocity)
            .setIsDone(()->controlSystem.isWithinTolerance(new KineticState(50,0,0)));
    public Command stop= new LambdaCommand()
            .setUpdate(this::stop)
            .setIsDone(()->controlSystem.isWithinTolerance(new KineticState(50,0,0)));

    @Override
    public void initialize(){
        launchMotor.setDirection(-1);
        rpm =0;
        launchMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        new TelemetryData("Position",()->launchMotor.getCurrentPosition());
        new TelemetryData("Velocity",()->-launchMotor.getVelocity());
        new TelemetryData("Target Velocity",()->rpm);
        new TelemetryData("Calculated Distance Cm",()->distanceCm);
    }

    public void increaseRPMby100(){
        rpm=1870;
    }

    public void decreaseRPMby100(){
        rpm-=10;

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
    }
    private void stop(){
        rpm = 0;
    }
    @Override
    public void periodic(){
        controlSystem.setGoal(new KineticState(0,rpm));
        normalControlSystem.setGoal(new KineticState(0,rpm));
        double maxPower = .9;
        double power;
        if (normalControlSystem.isWithinTolerance(new KineticState(0,250))) {
            power =controlSystem.calculate(new KineticState(0, -launchMotor.getVelocity()));
        } else {
            power = normalControlSystem.calculate(new KineticState(0,-launchMotor.getVelocity()));
        }
        if (power>0&&power>maxPower){
            power = maxPower;
        } else if (power<0&&power<maxPower){
            power = -maxPower;
        }
        launchMotor.setPower(power);

    }
}
