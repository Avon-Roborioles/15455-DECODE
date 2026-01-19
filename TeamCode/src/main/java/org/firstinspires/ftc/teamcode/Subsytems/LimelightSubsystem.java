package org.firstinspires.ftc.teamcode.Subsytems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryManager;

import java.util.List;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;

public class LimelightSubsystem implements Subsystem {
    public static LimelightSubsystem INSTANCE = new LimelightSubsystem();

    private final int targetPipeline = 0;
    private final int obeliskPipeline = 1;
    private int obeliskAprilTag=0;
    private Limelight3A limelight;

    public Command aprilTagAim = new InstantCommand(
            ()->{
                limelight.start();
                limelight.pipelineSwitch(targetPipeline);
            }
    );


    public Command detectObelisk = new LambdaCommand()
            .setStart(this::startDetectObelisk)
            .setUpdate(this::detectObelisk)
            .setIsDone(()->obeliskAprilTag!=0)
            .setStop(this::endDetectObelisk);
    public void initialize(){
        limelight= ActiveOpMode.hardwareMap().get(Limelight3A.class,"limeLight");
        limelight.pipelineSwitch(targetPipeline);
        //limelight.start();
    }
    public double getAprilTagOffset ()throws Exception{
        limelight.pipelineSwitch(targetPipeline);
        LLResult result = limelight.getLatestResult();
        if (result!=null&&result.isValid()){
            return result.getTx();
        }
        if (result==null){
            TelemetryManager.getInstance().addTempTelemetry("Null");
        } else if (!result.isValid()){
            TelemetryManager.getInstance().addTempTelemetry("Not Valid");
        }
        throw new Exception();
    }

    public void startDetectObelisk(){
        limelight.start();
        limelight.pipelineSwitch(obeliskPipeline);
        obeliskAprilTag=0;
    }

    public void detectObelisk(){
        LLResult result = limelight.getLatestResult();

        List<LLResultTypes.FiducialResult> list =result.getFiducialResults();

        for (LLResultTypes.FiducialResult apriltag:list){
            obeliskAprilTag=apriltag.getFiducialId();
        }
        if (obeliskPipeline!= result.getPipelineIndex()){
            obeliskAprilTag=0;
        }
    }
    public void endDetectObelisk(Boolean b){
        limelight.stop();
        DrumSubsystem.INSTANCE.setObeliskPattern(obeliskAprilTag);
        new TelemetryData("ID",()->obeliskAprilTag*1.);
    }


}
