package org.firstinspires.ftc.teamcode.Subsytems;


import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;


import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Commands.BetterParallelRaceGroup;
import org.firstinspires.ftc.teamcode.Commands.LazyLockOn;
import org.firstinspires.ftc.teamcode.Commands.LazyTurnTo;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.builder.ControlSystemBuilder;
import dev.nextftc.control.feedback.AngleType;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.WaitUntil;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.commands.utility.NullCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.core.units.Angle;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.extensions.pedro.TurnTo;
import dev.nextftc.ftc.Gamepads;
import kotlin.Lazy;

@Configurable
public class DriveSubsystem implements Subsystem {


    public static DriveSubsystem INSTANCE = new DriveSubsystem();
    public static double kP = .8;
    public static double kI=0;
    public static double kD = 0;
    public static double kS = .12;
    public static double usePID = 1;
    public PIDCoefficients coefficients = new PIDCoefficients(kP,kI,kD);
    public double integral = 0;


    private Command defaultCommand = new NullCommand();
    ControlSystem lockOnConrolSystem = new ControlSystemBuilder()

            .angular(AngleType.RADIANS,
                    feedback -> feedback.posPid(coefficients)
            )
            .build();


    @Override
    public void initialize(){
        new TelemetryItem(()->"Pose"+PedroComponent.follower().getPose());
        new TelemetryData("Nice heading",this::getGoalHeadingRad);
        if (AllianceComponent.getColor().equals(AllianceColor.BLUE)){
            targetDrive=new SequentialGroup(
                    new InstantCommand(lockOnConrolSystem::reset),
                    new ParallelDeadlineGroup(
                            new LambdaCommand()
                                    .setIsDone(()->Gamepads.gamepad1().square().get()),
                            new PedroDriverControlled(
                                    Gamepads.gamepad1().leftStickY(),
                                    Gamepads.gamepad1().leftStickX(),
                                    this::getAprilTagHeadingPower,
                                    false
                            )
                    )

            ).requires(this);
        } else {
            targetDrive= new SequentialGroup(
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
//                    ,
//                    new BetterParallelRaceGroup(
//                    new LazyTurnTo(this::getGoalHeadingRad),
//                    new WaitUntil(()->Gamepads.gamepad1().y().get())
//                    )
            ).requires(this);
        }

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

    public double getAprilTagHeadingPower(){



        double requiredAngle = getGoalHeadingRad();
        lockOnConrolSystem.setGoal(new KineticState(requiredAngle));
        double currentAngle = follower().getPose().getHeading();
        double error =MathFunctions.normalizeAngle(requiredAngle)-MathFunctions.normalizeAngle(currentAngle);
        double power = usePID*lockOnConrolSystem.calculate(new KineticState(currentAngle))+kS*Math.signum(sigmoid(error));
//        TelemetryManager.getInstance().addTempTelemetry("Lock on power: "+power);
        TelemetryManager.getInstance().addTempTelemetry("Goal Error: "+error);
        return power;
    }
    public double sigmoid(double input){
        return input;
    }

    public double getGoalHeadingRad(){
        Pose difference = follower().getPose().copy().minus(RobotConfig.FieldConstants.redAimPose);
        if (AllianceComponent.getColor().equals(AllianceColor.BLUE)){
            difference = follower().getPose().copy().minus(RobotConfig.FieldConstants.blueAimPose);
        }

        double requiredAngle = Math.atan2(difference.getY(),difference.getX());

        return requiredAngle;
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
