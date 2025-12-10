package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class ShooterAngleSub implements Subsystem {
    public static final ShooterAngleSub INSTANCE = new ShooterAngleSub();
    private MotorEx motorEx;
    private ControlSystem controlSystem;

    private double kP = 0.005;
    private double kI;
    private double kD;
    private double curPose;
    private double updatePose;

    public ShooterAngleSub(){
        motorEx = new MotorEx("shooterMotor");
        controlSystem = ControlSystem.builder().velPid(kP,kI,0).build();
        motorEx.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorEx.setCurrentPosition(curPose);
        motorEx.setPower(0);
        motorEx.reverse();
        motorEx.zero();
        curPose =0;
        updatePose =0;

    }
    public void setVelo(){
        motorEx.setPower(1);

    }
}
