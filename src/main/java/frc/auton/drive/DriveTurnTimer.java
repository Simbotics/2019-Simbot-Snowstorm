package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;

public class DriveTurnTimer extends AutonCommand {

	private RobotOutput robotOut;
	private double speed;
	private long length;
	private long startTime;

	// Declares the variables for the turn speed and how long the robot turns for
	public DriveTurnTimer(double speed, long howLong) {
		super(RobotComponent.DRIVE);
		this.speed = speed;
		this.length = howLong;

		this.robotOut = RobotOutput.getInstance();
	}

	@Override
	public void firstCycle() {
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public boolean calculate() {
		// Sets the motor outputs
		this.robotOut.setDriveLeft(this.speed);
		this.robotOut.setDriveRight(-this.speed);

		long timePassed = System.currentTimeMillis() - this.startTime;

		// Decides when to stop turning
		if (timePassed > this.length) {
			this.robotOut.setDriveLeft(0.0);
			this.robotOut.setDriveRight(0.0);

			return true;
		} else {
			return false;
		}
	}

	@Override
	// When activated, stops the robot
	public void override() {
		this.robotOut.setDriveLeft(0.0);
		this.robotOut.setDriveRight(0.0);

	}

}
