package org.firstinspires.ftc.teamcode.Subsytems;


import com.bylazar.configurables.annotations.Configurable;


import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;

@Configurable
public class DriveSubsystem implements Subsystem {


    public static DriveSubsystem INSTANCE = new DriveSubsystem();
    public static double kP = -.01;
    public static double kI=.000001;
    public double integral = 0;
    public double lastAprilTagReadms=1000;
    @Override
    public void initialize(){
        normalDrive.addRequirements(this);

    }
    public Command normalDrive= new PedroDriverControlled(
            Gamepads.gamepad1().leftStickY(),
            Gamepads.gamepad1().leftStickY(),
            Gamepads.gamepad1().rightStickX(),
            true
    );
    public Command targetDrive = new ParallelDeadlineGroup(
            new LambdaCommand()
                    .setIsDone(()->Gamepads.gamepad1().square().get()),
            new PedroDriverControlled(
                    Gamepads.gamepad1().leftStickY().negate(),
                    Gamepads.gamepad1().leftStickX().negate(),
                    this::getAprilTagHeadingPower,
                    false
            )
    ).requires(this);

    public double getAprilTagHeadingPower(){
        BindingManager.update();
        double heading;
        double curTime = System.currentTimeMillis();
        try {
            double result = LimelightSubsystem.INSTANCE.getAprilTagOffset();


            integral += result * kI * (curTime - lastAprilTagReadms);
            heading = kP * result + integral;


        } catch (Exception e) {

            integral=0;

            heading = -Gamepads.gamepad1().getGamepad().invoke().right_stick_x;

        }
        lastAprilTagReadms = curTime;
        return  heading;



    }

    @Override
    public void periodic(){

    }

}
