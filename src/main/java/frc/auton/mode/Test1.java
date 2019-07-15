package frc.auton.mode;

import frc.auton.drive.DriveTurnToVisionTarget;
import frc.auton.drive.DriveWait;

public class Test1 implements AutonMode {

    @Override
    public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveTurnToVisionTarget(true,15000));
        ab.addCommand(new DriveWait());
    }
}
