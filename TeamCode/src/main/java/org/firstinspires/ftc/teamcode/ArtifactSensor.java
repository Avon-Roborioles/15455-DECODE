package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;

public class ArtifactSensor {

    ColorSensor sensor;
    DistanceSensor dSensor;


    public ArtifactSensor(HardwareMap hMap){
        sensor=hMap.get(ColorSensor.class,"colorsensor");
        dSensor = hMap.get(DistanceSensor.class,"distanceSensor");

        new TelemetryData("Red",()->1.*sensor.red());
        new TelemetryData("Green",()->1.*sensor.green());
        new TelemetryData("Blue",()->1.*sensor.blue());
        new TelemetryData("Inches Away",()->dSensor.getDistance(DistanceUnit.INCH));


    }

    public ArtifactColor read() {
        ArtifactColor color;

        double sum;
        double nRed,nGreen, nBlue;
        double red = sensor.red();
        double green = sensor.green();
        double blue = sensor.blue();
        sum = red + green + blue;
        nRed = red / sum;
        nGreen = green / sum;
        nBlue = blue / sum;
        boolean strongDetection = false;
        if (nGreen > .46) {
            strongDetection = nGreen > .47;
            color = ArtifactColor.GREEN;
        } else if (nBlue + nRed > .58
        ) {
            strongDetection = (nBlue + nRed) > .6;
            color = ArtifactColor.GREEN;
        } else {
            color = ArtifactColor.NOTHING;
        }
        if (sum < 300 && !strongDetection) {
            color = ArtifactColor.NOTHING;


        }
        if (dSensor.getDistance(DistanceUnit.INCH)>2.5){
            color=ArtifactColor.NOTHING;
        }
        return  color;
    }
}
