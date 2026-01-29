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

        public static double distanceThreshold = 4.5+.8;


    }
    @Configurable
    public static class DrumConstants{
        public static String drumName = "drumMotor";
        public static String intakeName = "intakeMotor";

        public static double stallCurrentThreshold = 8.5;//amps
        public static double unJamPower = 1;
        public static double unJamTimeMs = 20;
        public static final double ticksPerRev = 1211.6;
        public static double kp=.0055;
        public static double kI=150./Math.pow(10,13);

        public static String servoName ="ejectServo";
        public static double servoEjectPos = .2994;
        public static double servoIntakePos=.0239;

        public static KineticState kineticStateTolerance = new KineticState(25,200);

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
        public static Pose center= new Pose(72,72, Math.toRadians(90));
        public static Pose redGoal = new Pose(72+67.5,72+67.5);
        public static Pose blueGoal = new Pose (72-67.5,72+67.5);
        public static Pose redBasePose = new Pose(37.77,33.96,Math.toRadians(90));
        public static Pose blueBasePose = new Pose(144-37.77,33.96,Math.toRadians(90));

    }
    public static class GlobalConstants {
        public static Pose startPose = FieldConstants.center;
    }

    public static class PoseConstants {
        public static Pose redBackStart = new Pose(79,9, Math.toRadians(-90));
        public static Pose redBackPose2 = new Pose(87,19,Math.toRadians(248.5));
        public static Pose redBackPose3 = new Pose(98,33,Math.toRadians(0));
        public static Pose redBackPose4 = new Pose(130,33,Math.toRadians(0));
        public static Pose redHPZoneIntakeStart = new Pose(98,57,Math.toRadians(0));
        public static Pose redHPZoneIntakeEnd =   new Pose(130,57,redHPZoneIntakeStart.getHeading());
        public static Pose redLeavePose =         new Pose(83,40,Math.toRadians(-90));

        public static Pose mirrorRedToBlue(Pose original){
            return new Pose(
                    144-original.getX(),
                    original.getY(),
                    -(original.getHeading()-Math.PI/2)+Math.PI/2
            );
        }
        public static double headingInverter(double original){
            return -(original-Math.PI/2)+Math.PI/2;
        }

        public static Pose blueBackStart =         new Pose(144-79,9, headingInverter(Math.toRadians(-90)));
        public static Pose blueBackPose2 =         new Pose(144-87,19,headingInverter(Math.toRadians(248.5)));
        public static Pose blueBackPose3 =         new Pose(144-95,33,headingInverter(Math.toRadians(0)));
        public static Pose blueBackPose4 =         new Pose(144-130.5,33,headingInverter(Math.toRadians(0)));
        public static Pose blueHPZoneIntakeStart = new Pose(144-95,60,headingInverter(Math.toRadians(0)));
        public static Pose blueHPZoneIntakeEnd =   new Pose(144-130.5,60,headingInverter(redHPZoneIntakeStart.getHeading()));
        public static Pose blueLeavePose =         new Pose(144-83,40,headingInverter(Math.toRadians(-90)));


    }


}
