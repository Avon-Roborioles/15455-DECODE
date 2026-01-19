package org.firstinspires.ftc.teamcode.Subsytems;


import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.bylazar.configurables.annotations.Configurable;


import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.builder.ControlSystemBuilder;
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
    public static double kP = 0.005;
    public static double kI=0.00000000003;
    public static double kD = 0.0001;
    public PIDCoefficients coefficients = new PIDCoefficients(kP,kI,kD);
    public double integral = 0;
    public double lastAprilTagReadms=0;

    private Command defaultCommand = new NullCommand();
    ControlSystem lockOnConrolSystem = new ControlSystemBuilder()
            .posPid(coefficients)
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
        double heading;

        try {

            double result = LimelightSubsystem.INSTANCE.getAprilTagOffset();

            heading =lockOnConrolSystem.calculate(new KineticState(result));
            TelemetryManager.getInstance().addTempTelemetry("No Exception");
        } catch (Exception e) {
            lockOnConrolSystem.reset();
            heading = -Gamepads.gamepad1().getGamepad().invoke().right_stick_x;
            TelemetryManager.getInstance().addTempTelemetry("Exception");
        }
        return  heading;
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
