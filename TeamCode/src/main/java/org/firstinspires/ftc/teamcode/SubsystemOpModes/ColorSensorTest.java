package org.firstinspires.ftc.teamcode.SubsystemOpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsytems.ArtifactSensor;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

@TeleOp(group ="Subsystem")
public class ColorSensorTest extends OpMode {
    ArtifactSensor sensor;

    @Override
    public void init() {
        sensor=new ArtifactSensor(hardwareMap);
    }

    @Override
    public void loop() {


        telemetry.addData("Artifact",sensor.read());
        sensor.updateSensorReads();
        TelemetryManager.getInstance().print(telemetry);
        TelemetryManager.getInstance().resetTempTelemetry();
    }
}
