package org.firstinspires.ftc.teamcode.Tuning;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@TeleOp(group = "Tuning")
public class DrumTuner extends NextFTCOpMode {
    private LoopTimeComponent loopTimeComponent=new LoopTimeComponent();
    public DrumTuner(){
        addComponents(
                new SubsystemComponent(DrumSubsystem.INSTANCE),
                BindingsComponent.INSTANCE,
                new TelemetryComponent(),
                loopTimeComponent,
                BulkReadComponent.INSTANCE
        );
    }
    private ElapsedTime timer = new ElapsedTime();
    //private GraphManager graphManager= PanelsWidget.INSTANCE.getManager();

    public void onInit(){

        Gamepads.gamepad1().a().whenBecomesTrue(DrumSubsystem.INSTANCE.tuneDrum);
        Gamepads.gamepad1().b().whenBecomesTrue(DrumSubsystem.INSTANCE::resetTuneTimes);
        Gamepads.gamepad1().x().whenBecomesTrue(DrumSubsystem.INSTANCE::setTuning);
        Gamepads.gamepad1().y().whenBecomesTrue(DrumSubsystem.INSTANCE::setNormal);
        timer.startTime();
        new TelemetryData("Average Rotation Time ms",()->DrumSubsystem.INSTANCE.avgTime);

    }
    public void onUpdate(){
//        graphManager.addData("Drum Pos",DrumSubsystem.INSTANCE.getCurPos());
//        graphManager.update();
    }
}
