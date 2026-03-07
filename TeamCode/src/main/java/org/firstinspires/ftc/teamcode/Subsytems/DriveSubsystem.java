package org.firstinspires.ftc.teamcode.Subsytems;


import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.MathFunctions;


import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

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
import dev.nextftc.ftc.Gamepads;

@Configurable
public class DriveSubsystem implements Subsystem {


    public static DriveSubsystem INSTANCE = new DriveSubsystem();
    public static double kP = .8;
    public static double kI=0;
    public static double kD = 0;

    public static double aKP = 1;

    public static double kStatic = .12;
    public static double kKinetic = .07;
    public static double headingThreshold = .01;
    public static double usePID = 1;
    public PIDCoefficients coefficients = new PIDCoefficients(kP,kI,kD);
    public PIDCoefficients aprilTagCoefficients = new PIDCoefficients(aKP,kI,kD);
    public double integral = 0;


    private Command defaultCommand = new NullCommand();
    ControlSystem lockOnConrolSystem = new ControlSystemBuilder()
            .angular(AngleType.RADIANS,
                    feedback -> feedback.posPid(coefficients)
            )
            .build();
    ControlSystem aprilTagControlSystem = new ControlSystemBuilder()
            .angular(AngleType.RADIANS,
                    feedback -> feedback.posPid(aprilTagCoefficients)
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
                                    Gamepads.gamepad1().leftStickY().negate(),
                                    Gamepads.gamepad1().leftStickX().negate(),
                                    this::getOdometryHeadingPower,
                                    false
                            )
                    )

            ).requires(this);
            aprilTagTargetDrive=new SequentialGroup(
                    new InstantCommand(lockOnConrolSystem::reset),
                    LimelightSubsystem.INSTANCE.aprilTagAim,
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
        } else {
            targetDrive= new SequentialGroup(
                    new InstantCommand(lockOnConrolSystem::reset),
                    new ParallelDeadlineGroup(
                            new LambdaCommand()
                                    .setIsDone(()->Gamepads.gamepad1().square().get()),
                            new PedroDriverControlled(
                                    Gamepads.gamepad1().leftStickY(),
                                    Gamepads.gamepad1().leftStickX(),
                                    this::getOdometryHeadingPower,
                                    false
                            )
                    )
//                    ,
//                    new BetterParallelRaceGroup(
//                    new LazyTurnTo(this::getGoalHeadingRad),
//                    new WaitUntil(()->Gamepads.gamepad1().y().get())
//                    )
            ).requires(this);
            aprilTagTargetDrive=new SequentialGroup(
                    new InstantCommand(lockOnConrolSystem::reset),
                    LimelightSubsystem.INSTANCE.aprilTagAim,
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
        }

    }

    public Command targetDrive = new SequentialGroup(
            new InstantCommand(lockOnConrolSystem::reset),
            new ParallelDeadlineGroup(
                    new LambdaCommand()
                            .setIsDone(()->Gamepads.gamepad1().square().get()),
                    new PedroDriverControlled(
                            Gamepads.gamepad1().leftStickY().negate(),
                            Gamepads.gamepad1().leftStickX().negate(),
                            this::getOdometryHeadingPower,
                            false
                    )
            )
    ).requires(this);

    public Command aprilTagTargetDrive = new SequentialGroup(
            new InstantCommand(lockOnConrolSystem::reset),
            LimelightSubsystem.INSTANCE.aprilTagAim,
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


    public double getOdometryHeadingPower(){
        double requiredAngle = getGoalHeadingRad();
        lockOnConrolSystem.setGoal(new KineticState(requiredAngle));
        double currentAngle = follower().getPose().getHeading();

        double kStaticToUse = kStatic;
        if (follower().getPoseTracker().getAngularVelocity()>headingThreshold) kStaticToUse=kKinetic;

        double error =MathFunctions.normalizeAngle(requiredAngle)-MathFunctions.normalizeAngle(currentAngle);
        double power = usePID*lockOnConrolSystem.calculate(new KineticState(currentAngle))+ kStaticToUse *Math.signum(sigmoid(error));
//        TelemetryManager.getInstance().addTempTelemetry("Lock on power: "+power);
        TelemetryManager.getInstance().addTempTelemetry("Goal Error: "+error);
        if (error==0){
            return 0;
        }
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
    public double getAprilTagHeadingPower(){
        double kStaticToUse = kStatic;
        if (follower().getPoseTracker().getAngularVelocity()>headingThreshold) kStaticToUse=kKinetic;

        double heading;
        try {
            double result = -LimelightSubsystem.INSTANCE.getAprilTagOffset();
            heading = aprilTagControlSystem.calculate(new KineticState(result))+kStaticToUse*Math.signum(result);
        } catch (Exception e) {
            heading = -Gamepads.gamepad1().getGamepad().invoke().right_stick_x;
        }
        return  heading;
    }

    @Override
    public void periodic(){
        coefficients.kP=kP;
        coefficients.kD=kD;
        coefficients.kI=kI;
        aprilTagCoefficients.kP =aKP;
    }
    public void setDefaultCommand(Command command){
        defaultCommand=command;


    }
    @Override
    public Command getDefaultCommand(){
        return  defaultCommand;
    }

}
