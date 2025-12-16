package org.firstinspires.ftc.teamcode.Telemetry;

import dev.nextftc.core.components.Component;
import dev.nextftc.ftc.ActiveOpMode;

public class TelemetryComponent implements Component {

    @Override
    public void postUpdate(){
        TelemetryManager.getInstance().print(ActiveOpMode.telemetry());
    }
    @Override
    public void postStop(){
        TelemetryManager.getInstance().reset();
    }
}
