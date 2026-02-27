package org.firstinspires.ftc.teamcode.CompOpmodes.CompAuto;

import static org.firstinspires.ftc.teamcode.RobotConfig.PoseConstants.*;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AllianceComponent;
import org.firstinspires.ftc.teamcode.Commands.BetterParallelRaceGroup;
import org.firstinspires.ftc.teamcode.Enums.AllianceColor;
import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import org.firstinspires.ftc.teamcode.PoseTrackerComponent;
import org.firstinspires.ftc.teamcode.Subsytems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.DrumSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LauncherSubsystem;
import org.firstinspires.ftc.teamcode.Subsytems.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryComponent;
import org.firstinspires.ftc.teamcode.Telemetry.TelemetryItem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.ftc.components.LoopTimeComponent;

@Autonomous
public class RedBackAuto extends NextFTCOpMode {
    public RedBackAuto(){
        addComponents(
                new SubsystemComponent(LauncherSubsystem.INSTANCE, DriveSubsystem.INSTANCE, DrumSubsystem.INSTANCE,LimelightSubsystem.INSTANCE),
                new TelemetryComponent(),
                new PedroComponent(Constants::createFollower),
                PoseTrackerComponent.INSTANCE,
                new LoopTimeComponent(),
                BulkReadComponent.INSTANCE,
                AllianceComponent.getINSTANCE(AllianceColor.RED)
        );
    }
    public void onInit(){

    }

    public void onStartButtonPressed(){

        DrumSubsystem.INSTANCE.useObelisk();
        PedroComponent.follower().setPose(redBackStart);
        DrumSubsystem.INSTANCE.readyAuto();
        Path startTurnToShoot = new Path(
                new BezierLine(
                        redBackStart,
                        redBackShootPose
                )
        );
        startTurnToShoot.setLinearHeadingInterpolation(redBackStart.getHeading(), redBackShootPose.getHeading());
        Path backToIntake3 = new Path(
                new BezierLine(
                        redBackShootPose,
                        redBackSpike3Start
                )
        );
        backToIntake3.setLinearHeadingInterpolation(redBackShootPose.getHeading(), redBackSpike3Start.getHeading());
        FollowPath startTurnToShootCommand = new FollowPath(startTurnToShoot);
        Path intake3 = new Path(
                new BezierLine(

                        redBackSpike3Start,
                        redBackSpike3End
                )
        );
        intake3.setLinearHeadingInterpolation(0,0);
        FollowPath backToIntake3Command = new FollowPath(backToIntake3);
        FollowPath intake3Command = new FollowPath(intake3);

        Path intake3ToShoot = new Path(
                new BezierLine(
                        redBackSpike3End,
                        redBackShootPose
                )
        );
        intake3ToShoot.setLinearHeadingInterpolation(redBackSpike3End.getHeading(), redBackShootPose.getHeading());

        Path shootToIntakeHPZone = new Path(
                new BezierLine(
                        redBackShootPose,
                        redBackSpike2Start
                )
        );
        shootToIntakeHPZone.setLinearHeadingInterpolation(redBackShootPose.getHeading(), redBackSpike2Start.getHeading());

        Path intakeHP = new Path(
                new BezierLine(
                        redBackSpike2Start,
                        redBackSpike2End
                )
        );
        intakeHP.setLinearHeadingInterpolation(redBackSpike2Start.getHeading(), redBackSpike2End.getHeading());

        Path intakeHpToShoot = new Path(
                new BezierLine(
                        redBackSpike2End,
                        redBackShootPose
                )
        );
        intakeHpToShoot.setLinearHeadingInterpolation(redBackSpike2End.getHeading(), redBackShootPose.getHeading());
        Path toCenter = new Path(
                new BezierLine(
                        redBackShootPose,
                        redLeavePose
                )
        );
        toCenter.setLinearHeadingInterpolation(redBackShootPose.getHeading(), redLeavePose.getHeading());


        new TelemetryItem(()->"Pose: "+PedroComponent.follower().getPose());
        Command autoRoutine = new SequentialGroup(

                new ParallelGroup(
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject,
                                new Delay(.5)
                        ),
                        new SequentialGroup(
                                new BetterParallelRaceGroup(
                                        LimelightSubsystem.INSTANCE.detectObelisk,
                                        new Delay(2)
                                ),
                                new ParallelGroup(
                                        startTurnToShootCommand,
                                        new InstantCommand(DrumSubsystem.INSTANCE::preparePattern)
                                )
                        ),

                        LauncherSubsystem.INSTANCE.runToCalculatedPos
                ),


                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runBackToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,

                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),

                new BetterParallelRaceGroup(
                        DrumSubsystem.INSTANCE.intakeThreeBalls,
                        new SequentialGroup(
                                backToIntake3Command,
                                new ParallelGroup(
                                        new SequentialGroup(
                                                new Delay(.3),
                                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.5))
                                        ),
                                        intake3Command
                                ),
                                new Delay(1)
                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new ParallelGroup(
                        new FollowPath(intake3ToShoot),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject
                        )
                ),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(()->LauncherSubsystem.INSTANCE.stop.update()),
                new BetterParallelRaceGroup(

                        DrumSubsystem.INSTANCE.intakeThreeBalls,
                        new SequentialGroup(
                                new FollowPath(shootToIntakeHPZone),
                                new ParallelGroup(
                                        new SequentialGroup(
                                                new Delay(.3),
                                                new InstantCommand(()->PedroComponent.follower().setMaxPower(.5))
                                        ),
                                        new FollowPath(intakeHP)

                                ),
                                new Delay(3)
                        )
                ),
                new InstantCommand(()->PedroComponent.follower().setMaxPower(1)),
                new ParallelGroup(
                        new FollowPath(intakeHpToShoot),
                        LauncherSubsystem.INSTANCE.runToCalculatedPos,
                        new SequentialGroup(
                                DrumSubsystem.INSTANCE.secureBalls,
                                DrumSubsystem.INSTANCE.servoEject
                        )
                ),
                new InstantCommand(DrumSubsystem.INSTANCE::preparePattern),
                LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootFirstPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootSecondPattern,
                //LauncherSubsystem.INSTANCE.runToCalculatedPos,
                DrumSubsystem.INSTANCE.shootThirdPattern,
                new InstantCommand(LauncherSubsystem.INSTANCE::stop),
                new FollowPath(toCenter)

        );
        autoRoutine.schedule();

    }

    @Override
    public void onStop(){
        PedroComponent.follower().setMaxPower(1);
    }
}