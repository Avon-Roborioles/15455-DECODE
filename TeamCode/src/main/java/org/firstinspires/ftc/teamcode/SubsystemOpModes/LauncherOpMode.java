package org.firstinspires.ftc.teamcode.SubsystemOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto.BlueBackAuto;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.bindings.BindingManager;
import dev.nextftc.bindings.Button;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp(group= "Subsystem")
public class LauncherOpMode extends NextFTCOpMode {


    public LauncherOpMode(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE),
                BindingsComponent.INSTANCE,
                new PedroComponent(Constants::createFollower),
                new TelemetryComponent(),
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.RED)
        );
    }

    public double totalSpeedUpTime = 0;
    public double numSpeedUps = 0;
    public double lastSpeedUpStartTime = 0;


    @Override
    public void onStartButtonPressed(){
        PedroComponent.follower().setPose(RobotConfig.FieldConstants.center);
        Gamepads.gamepad1().dpadUp().whenBecomesFalse(LauncherSubsystem.INSTANCE.runToCalculatedPos);
        Gamepads.gamepad1().dpadDown().whenBecomesFalse(LauncherSubsystem.INSTANCE::stop);

        Button button=new Button(LauncherSubsystem.INSTANCE::isUpToSpeed);
        button.whenBecomesTrue(()->{
            numSpeedUps=numSpeedUps+1;
            totalSpeedUpTime+=System.currentTimeMillis()-lastSpeedUpStartTime;
        });
        button.whenBecomesFalse(()->lastSpeedUpStartTime=System.currentTimeMillis());

        Gamepads.gamepad1().b().whenBecomesTrue(()->{
            totalSpeedUpTime=0;
            numSpeedUps=0;
            lastSpeedUpStartTime=0;
        });

        new TelemetryData("Avg Speed Up Time",()->{
            if (numSpeedUps==0){
                return 10.;
            }
            return totalSpeedUpTime/numSpeedUps;
        });

        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose().toString());

    }

    public void onUpdate(){
        BindingManager.update();
    }



}
