package org.firstinspires.ftc.teamcode.PedroPathing;

import static org.firstinspires.ftc.teamcode.RobotConfig.DriveConstants.*;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(mass)
            .forwardZeroPowerAcceleration(forwardZPwrAcc)
            .lateralZeroPowerAcceleration(lateralZPwrAcc)
            .translationalPIDFCoefficients(translationalPID)
            .headingPIDFCoefficients(headingPID)
            .secondaryHeadingPIDFCoefficients(secondaryHeadingPID)
            .useSecondaryDrivePIDF(true)
            .useSecondaryHeadingPIDF(false)
            .centripetalScaling(centripetalScaling)
            ;

    public static PathConstraints pathConstraints = constraints;


    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName(frontRightWheel)
            .leftFrontMotorName(frontLeftWheel)
            .leftRearMotorName(backLeftWheel)
            .rightRearMotorName(backRightWheel)
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity( xVelocity)
            .yVelocity(yVelocity)
            .useBrakeModeInTeleOp(true)


            ;

    public static PinpointConstants pinpointConstants = new PinpointConstants()
            .strafePodX(strafePodX)
            .forwardPodY(strafePodY)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .hardwareMapName(pinpointName)

            ;



    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(pinpointConstants)
                .build();

    }
}
