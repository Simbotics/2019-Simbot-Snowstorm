package frc.auton.util;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;

/**
 *
 * @author Michael
 */
public class AutonUtilWait extends AutonCommand {

	public AutonUtilWait() {
		super(RobotComponent.UTIL);
	}

	@Override
	public void firstCycle() {
		// nothing
	}

	@Override
	public boolean calculate() {
		return true;
	}

	@Override
	public void override() {
		// nothing to do

	}

}
