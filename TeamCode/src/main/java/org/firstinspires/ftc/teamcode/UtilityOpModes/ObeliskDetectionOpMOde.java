package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.List;

@TeleOp
public class ObeliskDetectionOpMOde extends OpMode {

    private Limelight3A limelight3A;


    @Override
    public void init(){
        limelight3A= hardwareMap.get(Limelight3A.class,"limeLight");
        limelight3A.pipelineSwitch(1);
        limelight3A.start();
    }
    @Override
    public void loop(){
        LLResult result = limelight3A.getLatestResult();

        List<LLResultTypes.FiducialResult> list =result.getFiducialResults();
        boolean detected = false;
        for (LLResultTypes.FiducialResult apriltag:list){
            telemetry.addData("Id",apriltag.getFiducialId());
            detected=true;
        }

        if (!detected){
            telemetry.addLine("No Detections");
        }


        telemetry.update();
    }
}
