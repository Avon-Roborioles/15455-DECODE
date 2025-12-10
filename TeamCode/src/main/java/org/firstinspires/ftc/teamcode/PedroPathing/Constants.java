package org.firstinspires.ftc.teamcode.PedroPathing;

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
            .mass(7.9)
            .forwardZeroPowerAcceleration(-33.6250884677704)
            .lateralZeroPowerAcceleration(-63.52709172579524)
            .translationalPIDFCoefficients(new PIDFCoefficients(.6,.0001,.055,0))
            .headingPIDFCoefficients(new PIDFCoefficients(5,6,.2,.01))
            .centripetalScaling(.0009)
            ;

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1)
            ;


    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("frontRight")
            .leftFrontMotorName("frontLeft")
            .leftRearMotorName("backLeft")
            .rightRearMotorName("backRight")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity( 77.03174837367742)
            .yVelocity(62.43715877983514)


            ;

    public static PinpointConstants pinpointConstants = new PinpointConstants()
            .strafePodX(-3.25)
            .forwardPodY(-5.575)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .hardwareMapName("pinpoint")
            ;



    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(pinpointConstants)
                .build();

    }
}
