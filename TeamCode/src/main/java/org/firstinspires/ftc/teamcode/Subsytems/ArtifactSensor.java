package org.firstinspires.ftc.teamcode.Subsytems;

import static org.firstinspires.ftc.teamcode.RobotConfig.SensorConstants.*;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.RobotConfig;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import dev.nextftc.ftc.ActiveOpMode;


public class ArtifactSensor  {

    RevColorSensorV3 sensor;
//    Rev2mDistanceSensor dSensor;

    double sum;
    double nRed=0,nGreen=0, nBlue=0;
    double normalDifference;
    double purpleDifference;
    double greenDifference;
    DigitalChannel digitalChannel;
    public boolean isEnabled=true;


    private double red=0;
    private double blue =0;
    private double green=0;
    private double distance =0;


    public ArtifactSensor(HardwareMap hMap){
        sensor=hMap.get(RevColorSensorV3.class,colorSensorName);
        //dSensor = hMap.get(Rev2mDistanceSensor.class, distanceSensorName);
        sensor.status();
        digitalChannel=hMap.get(DigitalChannel.class,"laserSensor");


        new TelemetryData("Red",()->1.*nRed);
        new TelemetryData("Green",()->1.*nGreen);
        new TelemetryData("Blue",()->1.*nBlue);
        new TelemetryItem(()->"Color: "+this.read().toString());
        new TelemetryData("Inches Away",()->distance);
        //new TelemetryData("Csensor Distance",()->sensor.getDistance(DistanceUnit.INCH));
        new TelemetryItem(()->"CSensor Status"+sensor.status());
//        new TelemetryData("Normal Difference",()->normalDifference);
//        new TelemetryData("Purple Difference",()->purpleDifference);
//        new TelemetryData("Green Difference",()->greenDifference);

    }


    private double vectorDifference(double[] v1,double[]v2){
        double sum=0;
        for (int i =0;i<3;i++){
            sum+= (v1[i]-v2[i])*(v1[i]-v2[i]);
        }

        return sum;
    }
    public void updateSensorReads(){
        if (isEnabled) {
            //distance= dSensor.getDistance(DistanceUnit.INCH);
            double d2 = sensor.getDistance(DistanceUnit.INCH);
            TelemetryManager.getInstance().addTempTelemetry("Getting Distance");
            if (digitalChannel.getState()) {


                red = sensor.red();
                green = sensor.green();
                blue = sensor.blue();

                TelemetryManager.getInstance().addTempTelemetry("Getting Color");

            } else if (d2 < 2.5) {
                distance = d2;
                red = sensor.red();
                green = sensor.green();
                blue = sensor.blue();

            }
        } else {
            red = normal[0];
            blue = normal[2];
            green = normal[1];
        }



    }
    public void enable(){
        isEnabled=true;
    }

    public void disable(){
        isEnabled=false;
    }

    public ArtifactColor read() {
        ArtifactColor color;
        if (!isEnabled){
            return ArtifactColor.NOTHING;
        }




        sum = red + green + blue;
        nRed = red / sum;
        nGreen = green / sum;
        nBlue = blue / sum;
        double[] colors = new double[]{nRed,nGreen,nBlue};

       normalDifference = vectorDifference(normal,colors);
       purpleDifference = vectorDifference(colors,purpleTemplate);
       greenDifference = vectorDifference(colors, greenTemplate);

        if (normalDifference<purpleDifference&&normalDifference<greenDifference){
            color = ArtifactColor.NOTHING;
        } else if (purpleDifference<normalDifference&&purpleDifference<greenDifference){
            color = ArtifactColor.PURPLE;
        } else if (greenDifference<purpleDifference&&greenDifference<normalDifference) {
            color = ArtifactColor.GREEN;
        }  else {
            color = ArtifactColor.NOTHING;
        }
//        if (nGreen > .46) {
//            color = ArtifactColor.GREEN;
//        } else if (nBlue + nRed > .58) {
//            color = ArtifactColor.GREEN;
//        } else {
//            color = ArtifactColor.NOTHING;
//        }
        if (distance> RobotConfig.SensorConstants.distanceThreshold){
            color=ArtifactColor.NOTHING;
        }
        return  color;
    }

    public void reset(){
        

        sensor= ActiveOpMode.hardwareMap().get(RevColorSensorV3.class,colorSensorName);
        //dSensor = ActiveOpMode.hardwareMap().get(Rev2mDistanceSensor.class, distanceSensorName);
    }

}
