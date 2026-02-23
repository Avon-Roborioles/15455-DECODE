package org.firstinspires.ftc.teamcode.Commands;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.Telemetry.TelemetryData;

import java.util.function.DoubleSupplier;

import dev.nextftc.core.commands.Command;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;

public class LazyLockOn extends Command {

    FollowPath path;
    DoubleSupplier angleRad;

    public LazyLockOn(DoubleSupplier angleRad){
        this.angleRad=angleRad;
    }

    public void start(){
        new TelemetryData("Lock On heading deg",()->angleRad.getAsDouble());
        Pose cur = PedroComponent.follower().getPose();
        Path realPath = new Path(
                new BezierLine(
                        PedroComponent.follower().getPose(),
                        new Pose(cur.getX(),cur.getY(),angleRad.getAsDouble())
                )
        );
        realPath.setLinearHeadingInterpolation(cur.getHeading(),angleRad.getAsDouble());
        path=new FollowPath(realPath);

        path.start();
    }

    public void update(){
        path.update();
    }
    public void stop(boolean b){
        path.stop(b);
    }


    public boolean isDone(){
        return path.isDone();
    }
}
