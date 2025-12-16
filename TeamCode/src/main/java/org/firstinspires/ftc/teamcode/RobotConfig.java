package org.firstinspires.ftc.teamcode;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;

import dev.nextftc.control.KineticState;
import dev.nextftc.hardware.impl.CRServoEx;

public class RobotConfig {
    public static class DriveConstants{

/// Mecanum Drive constants:
        public static String frontLeftWheel = "front_left";
        public static String frontRightWheel = "front_right";
        public static String backLeftWheel = "rear_left";
        public static String backRightWheel = "rear_right";
        public static double xVelocity = 58.18826629;
        public static double yVelocity = 48.0511835;

/// Follower constants:

        public static double mass = 7.9;
        public static double forwardZPwrAcc = -41.54307; ///forwardZeroPowerAcceleration
        public static double lateralZPwrAcc = -55; ///lateralZeroPowerAcceleration
        public static double centripetalScaling = 0.0009;
        public static PIDFCoefficients translationalPID = new PIDFCoefficients(0.6,.0001,0.055,0); ///translationalPIDFCoefficients
        public static PIDFCoefficients headingPID = new PIDFCoefficients(5,6,0.2,0.01); ///headingPIDFCoefficients
        public static PathConstraints constraints = new PathConstraints(0.99, 100, 1, 1);

/// Pinpoint constants:
        public static double strafePodX = -4;
        public static double strafePodY = 4.75;
        public static String hMapName = "pinpoint";


    }

    public static class SensorConstants{
/// Sensor constants:
        public static String sensorName = "colorSensor";
        public static String distanceSensor = "distanceSensor";
        public static double[] normal = {0.244,0.424,0.346};
        public static double[] purpleTemplate = {0.232,0.344,0.427};
        public static double[] greenTemplate = {0.14,0.49,0.37};


    }

    public static class DrumConstants{
        public static String drumName = "drumMotor";
        public static String intakeName = "intakeMotor";
        public static final int ticksPerRev = 867;
        public static double kp=.0055;
        public static double kI=150./Math.pow(10,13);
        public static String servoName ="ejectServo";
        public static KineticState kineticStateTolerance = new KineticState(20,0);

    }
    public static class LauncherConstant{

        public static String motorName = "launchMotor";
        public static double ticksPerRev = 537.6;
        public static double gravity = -7614.432;

    }

    public static class FieldConstants {
        public static Pose redGoal = new Pose(51,-53);
    }
}
