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
        public static double xVelocity = 72.99;
        public static double yVelocity = 55.24;

/// Follower constants:

        public static double mass = 12.35;
        public static double forwardZPwrAcc = -42.44; ///forwardZeroPowerAcceleration
        public static double lateralZPwrAcc = -61.21; ///lateralZeroPowerAcceleration
        public static double centripetalScaling = 0.0009;
        public static PIDFCoefficients translationalPID = new PIDFCoefficients(0.6,.0001,0.055,0); ///translationalPIDFCoefficients
        public static PIDFCoefficients headingPID = new PIDFCoefficients(2,.5,0.1,0.01); ///headingPIDFCoefficients
        public static PathConstraints constraints = new PathConstraints(0.99, 100, 1, 1);
        public static PIDFCoefficients secondaryHeadingPID = new PIDFCoefficients(5.5,5.2,.08,.01);
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

        public static double distanceThreshold = 4.5+.8;


    }
    @Configurable
    public static class DrumConstants{
        public static String drumName = "drumMotor";
        public static String intakeName = "intakeMotor";

        public static double stallCurrentThreshold = 8.5;//amps
        public static double unJamPower = 1;
        public static double unJamTimeMs = 20;
        public static final double ticksPerRev = 8192;
        public static final double motorTPR = 1211.6;
        public static double kP =.0009;
        public static double kI=0.0000000000008;
        public static double kD=.000062;
        public static double staticKS = .0;
        public static double kineticKS = 0;

        public static String servoName ="ejectServo";
        public static double servoEjectPos = .2994;
        public static double servoIntakePos=.0239;

        public static KineticState kineticStateTolerance = new KineticState(200,1000);

        public static double pinkOuttake = 0;
        public static double pinkIntake = 4119;
        public static double redOuttake = 2750;
        public static double redIntake = 6836;
        public static double blackOuttake = 5470;
        public static double blackIntake = 1413;





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
        public static Pose center= new Pose(72,72, Math.toRadians(90));
        public static Pose redGoal = new Pose(140,140);
        public static Pose redAimPose = new Pose(135,135);
        public static Pose blueGoal = new Pose (5,144-5);
        public static Pose blueAimPose = new Pose(7.5,144-7.5);
        public static Pose redBasePose = new Pose(37.77,33.96,Math.toRadians(90));
        public static Pose blueBasePose = new Pose(144-37.77,33.96,Math.toRadians(90));
        public static Pose redHPZoneReset = new Pose(9.5,10.8,Math.toRadians(-90));
        public static Pose blueHPZoneReset = new Pose(144-7.5,12.8,Math.toRadians(-90));

    }
    public static class GlobalConstants {
        public static Pose startPose = FieldConstants.center;
    }
    @Configurable
    public static class PoseConstants {
        public static Pose redBackStart =          new Pose(77,9, Math.toRadians(-90));
        public static Pose redBackShootPose =      new Pose(87,19,Math.toRadians(248.5));
        public static Pose redBackSpike3Start =    new Pose(95,33,Math.toRadians(0));
        public static Pose redBackSpike3End =      new Pose(130,33,Math.toRadians(0));
        public static Pose redBackSpike2Start =    new Pose(95,57,Math.toRadians(0));
        public static Pose redBackSpike2End =      new Pose(130,57, redBackSpike2Start.getHeading());
        public static Pose redHPZoneIntakePose1 = new Pose(128,20.97,Math.toRadians(-29.88));
        public static Pose redHPZoneIntakePose2 = new Pose(128,12,Math.toRadians(-29));
        public static Pose redHPZoneIntakePose3 = new Pose(127,7,Math.toRadians(0));
        public static Pose redHPZoneIntakePose4 = new Pose(125.5,12,Math.toRadians(30.6));
        public static Pose redHPZoneIntakePose5 = new Pose(121,12,Math.toRadians(0));
        public static Pose redHPZoneIntakePose6 = new Pose(131,7.5,Math.toRadians(0));
        public static Pose redHPZoneIntakePose7 = new Pose();

        public static Pose redLeavePose =          new Pose(83,40,Math.toRadians(-90));


        public static double headingInverter(double original){
            return -(original-Math.PI/2)+Math.PI/2;
        }

        public static Pose blueBackStart =         new Pose(144-79,9, headingInverter(Math.toRadians(-90)));
        public static Pose blueShootPose =         new Pose(144-87,19,headingInverter(Math.toRadians(248.5)));
        public static Pose blueBackSpike3Start =   new Pose(144-95,36,headingInverter(Math.toRadians(0)));
        public static Pose blueBackSpike3End =     new Pose(144-130.5,36,headingInverter(Math.toRadians(0)));
        public static Pose blueBackSpike2Start =   new Pose(144-95,60,headingInverter(Math.toRadians(0)));
        public static Pose blueBackSpike2End =     new Pose(144-130.5,60,headingInverter(redBackSpike2Start.getHeading()));
        public static Pose blueLeavePose =         new Pose(144-83,40,headingInverter(Math.toRadians(-90)));


    }


}
