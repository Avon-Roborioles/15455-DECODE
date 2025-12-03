package org.firstinspires.ftc.teamcode.Subsytems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
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

    private double rpm;
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

    }

    public void increaseRPMby100(){
        rpm=1870;
    }

    public void decreaseRPMby100(){
        rpm-=10;

    }
    public void calculateVelocity(){
        double distance = LauncherSubsystem.distanceCm;
        double radians = Math.toRadians(37);
        rpm = distance*Math.sqrt(-7614.432/ ( 2*Math.pow(Math.cos(radians),2)*(87-distance*Math.tan(radians)) ) );
        rpm = 1000; // 500
    }
    private void stop(){
        rpm = 0;
    }
    @Override
    public void periodic(){
        controlSystem.setGoal(new KineticState(0,rpm));
        normalControlSystem.setGoal(new KineticState(0,rpm));
        if (normalControlSystem.isWithinTolerance(new KineticState(0,250))) {
            launchMotor.setPower(controlSystem.calculate(new KineticState(0, -launchMotor.getVelocity())));
        } else {
            //
            launchMotor.setPower(normalControlSystem.calculate(new KineticState(0,-launchMotor.getVelocity())));
        }
    }
}
