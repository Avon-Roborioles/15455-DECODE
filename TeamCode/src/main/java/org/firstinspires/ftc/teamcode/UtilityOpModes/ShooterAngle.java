package org.firstinspires.ftc.teamcode.UtilityOpModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.GamepadEx;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.hardware.impl.MotorEx;


import static dev.nextftc.extensions.pedro.PedroComponent.follower;


@TeleOp(group = "Test")
public class ShooterAngle extends NextFTCOpMode {
    ShooterAngleSub shooterAngle = ShooterAngleSub.INSTANCE;
    private Limelight3A limelight3A;
    Command path1Commmand;
    private MotorEx motorEx;
    private ControlSystem controlSystem;
    private GamepadEx gamepad1;

    private double distance;
    private double velocity;
    private double angle;


    public ShooterAngle(){
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterAngleSub.INSTANCE), BindingsComponent.INSTANCE
        );






    }
    @Override
    public void onInit(){
        follower().setPose(new Pose(0,0,3*Math.PI/2));
        limelight3A = hardwareMap.get(Limelight3A.class, "limeLight");
        limelight3A.start();
        limelight3A.pipelineSwitch(0);
        limelight3A.start();







    }
    @Override
    public void onStartButtonPressed(){
        Gamepads.gamepad1().a().whenBecomesTrue(ShooterAngleSub.INSTANCE::setVelo);
    }



    @Override
    public void onUpdate(){

        LLResult result = limelight3A.getLatestResult();
        if(result != null && result.isValid()){
                Pose3D botPose = result.getBotpose_MT2();
                distance = getDistance(result.getTa());


                double x = botPose.getPosition().x;
                double y = botPose.getPosition().y;
                telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
                telemetry.addData("Distance ", distance);
                telemetry.addData("ty", result.getTy());
                telemetry.update();

            }
            else{
                telemetry.addData("Nothing found",result.isValid());
            }
        telemetry.addData("Nothing found",result.isValid());
            telemetry.update();

    }

    private double getDistance(double ta){
        double scale = 38665.95;
        double distance = (scale/ta);
        return distance;
    }
    
}
