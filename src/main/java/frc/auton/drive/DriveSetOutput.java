package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.subsystems.Drive;
import frc.subsystems.Drive.DriveState;

public class DriveSetOutput extends AutonCommand {

	private double y;
	private double turn;
	private Drive drive;
	private RobotOutput robotOut;

	public DriveSetOutput(double y, double turn) {
		super(RobotComponent.DRIVE);
		this.y = y;
		this.turn = turn;

		this.drive = Drive.getInstance();
		this.robotOut = RobotOutput.getInstance();
	}

	@Override
	public void firstCycle() {
		// nothing!!!
	}

	@Override
	// Sets the motor outputs
	public boolean calculate() {
		this.drive.setOutput(y, turn);
		this.drive.setState(DriveState.OUTPUT);
		this.drive.setRampRate(0);
		this.drive.calculate();
		return true;

	}

	@Override
	// When activated, stops the robot
	public void override() {
		this.robotOut.setDriveLeft(0.0);
		this.robotOut.setDriveRight(0.0);
	}

}
