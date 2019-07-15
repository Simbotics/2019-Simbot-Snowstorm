package frc.auton.util;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.SensorInput;

/**
 *
 * @author Michael
 */
public class AutonWaitUntilYPosition extends AutonCommand {

	private double y;
	private boolean goingForward;
	private SensorInput sensorIn;

	public AutonWaitUntilYPosition(double y, boolean goingForward) {
		super(RobotComponent.UTIL);
		this.y = y;
		this.goingForward = goingForward;
		this.sensorIn = SensorInput.getInstance();
	}

	@Override
	public void firstCycle() {
		// nothing
	}

	/*
	 * need to override checkAndRun so that it blocks even before going in to its
	 * "run seat"
	 */
	@Override
	public boolean checkAndRun() {
		if (this.goingForward) {
			if (this.sensorIn.getDriveYPos() < this.y) {
				return false;
			} else {
				return super.checkAndRun();
			}
		} else {
			if (this.sensorIn.getDriveYPos() > this.y) {
				return false;
			} else {
				return super.checkAndRun();
			}
		}
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
