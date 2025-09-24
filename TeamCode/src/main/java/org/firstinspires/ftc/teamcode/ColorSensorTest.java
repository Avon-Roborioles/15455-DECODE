package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Light;
import com.qualcomm.robotcore.hardware.LightBlinker;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@TeleOp
public class ColorSensorTest extends OpMode {

    ColorSensor sensor;
    double sum;
    double nRed,nGreen, nBlue;
    String color = "purple";
    String distance = "in threshold";
    boolean strongDetection= false;
    @Override
    public void init() {

        sensor = hardwareMap.get(ColorSensor.class, "colorsensor");
        double red = sensor.red();
        double green = sensor.green();
        double blue = sensor.blue();
        sum = red+green+blue;
        nRed = red/sum;
        nGreen = green/sum;
        nBlue=blue/sum;
        new TelemetryData("Red",()-> sensor.red()*1.0);
        new TelemetryData("Green",()-> sensor.green()*1.0);
        new TelemetryData("Blue",()-> sensor.blue()*1.0);
        new TelemetryData("nRed",()->nRed);
        new TelemetryData("nGreen",()->nGreen);
        new TelemetryData("nBlue",()->nBlue);
        new TelemetryData("sum",()->sum);
        new TelemetryItem(()->"Color: "+color);
        new TelemetryItem(()->"Distance: "+distance);
        new TelemetryItem(()->"Strong Detection: "+strongDetection);
    }

    @Override
    public void loop() {

        double red = sensor.red();
        double green = sensor.green();
        double blue = sensor.blue();
        sum = red+green+blue;
        nRed = red/sum;
        nGreen = green/sum;
        nBlue=blue/sum;
        strongDetection = false;
        if (nGreen>.43){
            strongDetection=nGreen>.47;
            color = "green";
        } else if (nBlue+nRed>.58){
            strongDetection = (nBlue+nRed)>.6;
            color = "purple";
        } else {
            color = "undetermined";
        }
        if (sum<300){
            distance = "too far";
        } else {
            distance= "close enough";
        }
        if (strongDetection){
            distance= "close enough";

        }

        TelemetryManager.getInstance().print(telemetry);

    }
}
