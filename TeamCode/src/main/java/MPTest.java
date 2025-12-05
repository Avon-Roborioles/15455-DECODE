import static dev.nextftc.extensions.pedro.PedroComponent.follower;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.PedroPathing.Constants;
import com.pedropathing.paths.Path;


import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;

@Autonomous
public class MPTest extends NextFTCOpMode {
    Command path1C;

    public MPTest(){
        addComponents(
                new PedroComponent(Constants::createFollower)
        );
    }
    @Override
    public void onInit(){
        follower().setPose(new Pose(0,0,3*Math.PI/2));
        Path path1 = new Path(
                new BezierLine(
                        follower().getPose(),
                        new Pose(72,0,Math.toRadians(180))
                )
        );
        path1C = new FollowPath(
                path1
        );
    }

    @Override
    public void onStartButtonPressed(){
        new SequentialGroup(
                path1C
        ).schedule();

    }

}
