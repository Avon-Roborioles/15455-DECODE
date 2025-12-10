package org.firstinspires.ftc.teamcode.Subsytems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;

public class LimelightSubsystem implements Subsystem {
    public static LimelightSubsystem INSTANCE = new LimelightSubsystem();

    private final int targetPipeline = 0;
    private final int obeliskPipeline = 1;
    private Limelight3A limelight;
    public void initialize(){
        limelight= ActiveOpMode.hardwareMap().get(Limelight3A.class,"limelight");
        limelight.pipelineSwitch(targetPipeline);
        limelight.start();
    }
    public double getAprilTagOffset(){
        limelight.pipelineSwitch(targetPipeline);
        LLResult result = limelight.getLatestResult();
        if (result!=null&&result.isValid()){
            return result.getTx();
        }
        return 0;
    }


}
