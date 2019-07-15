package frc.auton.mode;

import frc.auton.drive.DriveToVisionTarget;
import frc.auton.drive.DriveWait;

/**
 *
 * @author Michael
 */
public class Test2 implements AutonMode {

	@Override
	public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveToVisionTarget(4,true, 15000));
        ab.addCommand(new DriveWait());
    }

}
