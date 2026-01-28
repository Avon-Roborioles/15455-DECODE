package org.firstinspires.ftc.teamcode.Subsytems;


import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;


import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.builder.ControlSystemBuilder;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.commands.utility.NullCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.extensions.pedro.TurnTo;
import dev.nextftc.ftc.Gamepads;

@Configurable
public class DriveSubsystem implements Subsystem {


    public static DriveSubsystem INSTANCE = new DriveSubsystem();
    public static double kP = 1;
    public static double kI=0.000000005;
    public static double kD = 0.0001;
    public PIDCoefficients coefficients = new PIDCoefficients(kP,kI,kD);
    public double integral = 0;
    public double lastAprilTagReadms=0;

    private Command defaultCommand = new NullCommand();
    ControlSystem lockOnConrolSystem = new ControlSystemBuilder()

            .angular(AngleType.RADIANS,
                    feedback -> feedback.posPid(coefficients)
            )
            .build();


    @Override
    public void initialize(){


    }
    public Command normalDrive= new PedroDriverControlled(
            Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
            Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
            Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
            false
    ).requires(this).setInterruptible(true).named("Field Centric");
    public Command robotCentric = new PedroDriverControlled(
            Gamepads.gamepad1().leftStickY().negate().deadZone(.1),
            Gamepads.gamepad1().leftStickX().negate().deadZone(.1),
            Gamepads.gamepad1().rightStickX().negate().deadZone(.1).map((Double input)->{return input/3;}),
            true
    ).requires(this).setInterruptible(true).named("Robot Centric");
    public Command targetDrive = new SequentialGroup(
            new InstantCommand(lockOnConrolSystem::reset),
            new ParallelDeadlineGroup(
                    new LambdaCommand()
                            .setIsDone(()->Gamepads.gamepad1().square().get()),
                    new PedroDriverControlled(
                            Gamepads.gamepad1().leftStickY().negate(),
                            Gamepads.gamepad1().leftStickX().negate(),
                            this::getAprilTagHeadingPower,
                            false
                    )
            )
    ).requires(this);
    public void resetLastAprilTagReadms(){
        lastAprilTagReadms=0;
    }

    public double getAprilTagHeadingPower(){


        Pose difference = follower().getPose().copy().minus(RobotConfig.FieldConstants.redGoal);
        if (AllianceComponent.getColor().equals(AllianceColor.BLUE)){
            difference = follower().getPose().copy().minus(RobotConfig.FieldConstants.blueGoal);
        }

        double requiredAngle = Math.atan2(difference.getY(),difference.getX());
        lockOnConrolSystem.setGoal(new KineticState(requiredAngle));
        double currentAngle = follower().getPose().getHeading();
        double power = lockOnConrolSystem.calculate(new KineticState(currentAngle));
        TelemetryManager.getInstance().addTempTelemetry("Lock on power: "+power);
        TelemetryManager.getInstance().addTempTelemetry("Goal Error: "+(requiredAngle-currentAngle));
        return power;
    }

    @Override
    public void periodic(){
        coefficients.kP=kP;
        coefficients.kD=kD;
        coefficients.kI=kI;
    }
    public void setDefaultCommand(Command command){
        defaultCommand=command;


    }
    @Override
    public Command getDefaultCommand(){
        return  defaultCommand;
    }

}
