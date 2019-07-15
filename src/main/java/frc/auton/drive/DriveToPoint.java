package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.Dashboard;
import frc.io.RobotOutput;
import frc.subsystems.Drive;


public class DriveToPoint extends AutonCommand {

	private double x;
	private double y;
	private double theta; // The desired angle
	private double minVelocity;
	private double maxVelocity;
	private double eps; // The acceptable range
	private RobotOutput robotOut;
	private double turnRate;
	private double maxTurn;
	private Drive drive;

	public DriveToPoint(double x, double y, double theta, long timeout) {
		this(x, y, theta, 0, 11, 0.25,Dashboard.getInstance().getPathTurnP(), 90, timeout);
	}

	public DriveToPoint(double x, double y, double theta, double minVelocity, double eps, long timeout) {
		this(x, y, theta, minVelocity, 11, eps, Dashboard.getInstance().getPathTurnP(), 90, timeout);
	}

	public DriveToPoint(double x, double y, double theta, double minVelocity, double maxVelocity, double eps,
			long timeout) {
		this(x, y, theta, minVelocity, maxVelocity, eps, Dashboard.getInstance().getPathTurnP(), 90, timeout);
	}

	public DriveToPoint(double x, double y, double theta, double minVelocity, double maxVelocity, double eps,
			double turnRate,  long timeout) {
		this(x, y, theta, minVelocity, maxVelocity, eps, turnRate, 90, timeout);
	}

	

	public DriveToPoint(double x, double y, double theta, double minVelocity, double maxVelocity,
			double eps, double turnRate, double maxTurn, long timeout) {
		super(RobotComponent.DRIVE, timeout);
		this.x = x;
		this.y = y;
		this.theta = theta;
		this.minVelocity = minVelocity;
		this.maxVelocity = maxVelocity;
		this.eps = eps;

		
		this.turnRate = turnRate;
		this.maxTurn = maxTurn;

		this.drive = Drive.getInstance();
		
		this.robotOut = RobotOutput.getInstance();
	}

	@Override
	public void firstCycle() {
		

	}

	@Override
	public boolean calculate() {
		return this.drive.DriveToPoint(x, y, theta, minVelocity, maxVelocity, turnRate, maxTurn, eps);
	}

	@Override
	public void override() {

		this.robotOut.setDriveLeft(0);
		this.robotOut.setDriveRight(0);

	}

}
