package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.robot.Robot;

import dev.nextftc.core.components.Component;
import dev.nextftc.extensions.pedro.PedroComponent;

public class PoseTrackerComponent implements Component {
    public static PoseTrackerComponent INSTANCE = new PoseTrackerComponent();
    private PoseTrackerComponent(){

    }

    @Override
    public void postInit(){
        PedroComponent.follower().setPose(RobotConfig.GlobalConstants.startPose);
    }


    @Override
    public void preStop(){
        RobotConfig.GlobalConstants.startPose=PedroComponent.follower().getPose();
    }
}
