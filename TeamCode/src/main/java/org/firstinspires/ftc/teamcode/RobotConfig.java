package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathConstraints;

import org.firstinspires.ftc.teamcode.Enums.ArtifactColor;
import org.firstinspires.ftc.teamcode.Enums.Pattern;

import dev.nextftc.control.KineticState;

@Configurable
public class RobotConfig {
    public static class DriveConstants{

/// Mecanum Drive constants:
        public static String frontLeftWheel = "frontLeft";
        public static String frontRightWheel = "frontRight";
        public static String backLeftWheel = "backLeft";
        public static String backRightWheel = "backRight";
        public static double xVelocity = 75.64;
        public static double yVelocity = 58.5;

/// Follower constants:

        public static double mass = 12.35;
        public static double forwardZPwrAcc = -42.44; ///forwardZeroPowerAcceleration
        public static double lateralZPwrAcc = -61.21; ///lateralZeroPowerAcceleration
        public static double centripetalScaling = 0.0009;
        public static PIDFCoefficients translationalPID = new PIDFCoefficients(0.6,.0001,0.055,0); ///translationalPIDFCoefficients
        public static PIDFCoefficients headingPID = new PIDFCoefficients(5,7,0.2,0.01); ///headingPIDFCoefficients
        public static PathConstraints constraints = new PathConstraints(0.99, 100, 1, 1);

/// Pinpoint constants:
        public static double strafePodX = -3.25;
        public static double strafePodY = -5.575;
        public static String pinpointName = "pinpoint";


    }

    public static class SensorConstants{
/// Sensor constants:
        public static String colorSensorName = "colorSensor";
        public static String distanceSensorName = "distanceSensor";
        public static double[] normal = {0.244,0.424,0.346};
        public static double[] purpleTemplate = {0.232,0.344,0.427};
        public static double[] greenTemplate = {0.14,0.49,0.37};

        public static double distanceThreshold = 2.5+.8;


    }
    @Configurable
    public static class DrumConstants{
        public static String drumName = "drumMotor";
        public static String intakeName = "intakeMotor";

        public static double stallCurrentThreshold = 8.5;//amps
        public static double unJamPower = .2;
        public static double unJamTimeMs = 150;
        public static final double ticksPerRev = 1211.6;
        public static double kp=.0055;
        public static double kI=150./Math.pow(10,13);

        public static String servoName ="ejectServo";
        public static double servoEjectPos = .2994;
        public static double servoIntakePos=.0239;

        public static KineticState kineticStateTolerance = new KineticState(25,50);

        public static double pinkOuttake = 0;
        public static double pinkIntake = 607;
        public static double redOuttake = ticksPerRev/3;
        public static double redIntake = 1015;
        public static double blackOuttake = ticksPerRev/3*2;
        public static double blackIntake = 203;





        //ppg id = 23
        public static Pattern ppgPattern = new Pattern.PatternBuilder()
                .first(ArtifactColor.PURPLE)
                .second(ArtifactColor.PURPLE)
                .third(ArtifactColor.GREEN)
                .build();

        //pgp id = 22
        public static Pattern pgpPattern = new Pattern.PatternBuilder()
                .first(ArtifactColor.PURPLE)
                .second(ArtifactColor.GREEN)
                .third(ArtifactColor.PURPLE)
                .build();


        //gpp id = 21
        public static Pattern gppPattern = new Pattern.PatternBuilder()
                .first(ArtifactColor.GREEN)
                .second(ArtifactColor.PURPLE)
                .third(ArtifactColor.PURPLE)
                .build();



    }
    public static class LauncherConstant{

        public static String cHubLaunchName = "cHubLaunchMotor";
        public static String eHubLaunchName = "eHubLaunchMotor";
        public static double gravity = -7614.432;

    }
    public static class TeleOpConstants {
        public static String normalOperationLayer = "Normal Operation";
        public static String debugOperationLayer = "Debug Operation";
    }

    public static class FieldConstants {
        public static Pose redGoal = new Pose(70.5,70.5);
    }
    public static class GlobalConstants {
        public static Pose startPose = new Pose(-69.667,-.525,Math.toRadians(-177.57));
    }
}
