package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;

import static dev.nextftc.extensions.pedro.PedroComponent.follower;

@TeleOp(group = "Test")
@Configurable
public class HeadingPController extends NextFTCOpMode {
    public static double kP = -.01;
    public static double kI=.000001;
    PedroDriverControlled driverControlled;
    Limelight3A limelight3A;
    private double integral =0;
    public HeadingPController(){
        addComponents(
                new SubsystemComponent(DriveSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                new PedroComponent(Constants::createFollower),
                new TelemetryComponent(),
                BindingsComponent.INSTANCE
        );
    }
    public void onStartButtonPressed(){
        Command runCommand = new SequentialGroup(
                new ParallelDeadlineGroup(
                        DriveSubsystem.INSTANCE.targetDrive,
                        LimelightSubsystem.INSTANCE.aprilTagAim
                ),
                new LambdaCommand()
                        .setStart(()->PedroComponent.follower().holdPoint(follower().getPose()))
                        .setIsDone(Gamepads.gamepad1().a())
                        .setStop((Boolean b)->follower().breakFollowing())
        ).requires(LimelightSubsystem.INSTANCE,DriveSubsystem.INSTANCE);

        runCommand.schedule();
        BindingManager.update();
    }

    @Override
    public void onUpdate(){

        BindingManager.update();
    }
}
