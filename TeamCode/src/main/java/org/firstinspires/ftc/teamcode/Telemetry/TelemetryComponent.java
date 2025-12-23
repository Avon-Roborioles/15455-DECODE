package org.firstinspires.ftc.teamcode.Telemetry;

import com.bylazar.telemetry.PanelsTelemetry;

import dev.nextftc.core.components.Component;
import dev.nextftc.ftc.ActiveOpMode;

public class TelemetryComponent implements Component {
    com.bylazar.telemetry.TelemetryManager telemetryManager = PanelsTelemetry.INSTANCE.getTelemetry();
    @Override
    public void postUpdate(){
        TelemetryManager.getInstance().print(telemetryManager);
        TelemetryManager.getInstance().print(ActiveOpMode.telemetry());
    }
    @Override
    public void postStop(){
        TelemetryManager.getInstance().reset();
    }
}
