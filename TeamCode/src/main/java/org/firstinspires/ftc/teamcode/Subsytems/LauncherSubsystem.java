package org.firstinspires.ftc.teamcode.Subsytems;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.functionalInterfaces.Configurator;
import dev.nextftc.hardware.impl.MotorEx;

public class LauncherSubsystem implements Subsystem {

    public static final LauncherSubsystem INSTANCE = new LauncherSubsystem();
    private final double ticksPerRev = 537.6;

    private MotorEx launchMotor = new MotorEx("launchMotor");
    private ControlSystem controlSystem = ControlSystem.builder()
            .velPid(1)
            .angular(AngleType.REVOLUTIONS,feedback->feedback.velPid(1))
            .build();
    private double rpm;

    @Override
    public void initialize(){
        rpm =0;
        new TelemetryData("Position",()->launchMotor.getCurrentPosition());
    }

    public void increaseRPMby100(){
        rpm+=100;
    }

    public void decreaseRPMby100(){
        rpm-=100;
    }

    @Override
    public void periodic(){
        controlSystem.setGoal(new KineticState(0,rpm));
        launchMotor.setPower(controlSystem.calculate(new KineticState(0,launchMotor.getVelocity()/ticksPerRev)));
    }
}
