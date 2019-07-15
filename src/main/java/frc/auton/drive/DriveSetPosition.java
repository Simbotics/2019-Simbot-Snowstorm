package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.SensorInput;
import frc.util.SimPoint;

public class DriveSetPosition extends AutonCommand {

	private double x;
	private double y;
	private double angle;
	private SensorInput sensorIn;
	private boolean settingY;
	private boolean settingX;
	private boolean settingAngle;

	// Declares the variables for the position of the robot using SimPoints
	public DriveSetPosition(SimPoint p, double angle) {
		this(p.getX(), p.getY(), angle);
	}

	public DriveSetPosition(double value, boolean y){
		super(RobotComponent.DRIVE);
		this.sensorIn = SensorInput.getInstance();
		this.settingAngle = false;
		if(y){
			this.y = value;
			this.settingY = true;
			this.settingX = false;
		} else {
			this.x = value;
			this.settingY = false;
			this.settingX = true;
		}
	}

	// Declares the variables for the regular way of setting the position of the
	// robot
	public DriveSetPosition(double x, double y, double angle) {
		super(RobotComponent.DRIVE);
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.settingY = true;
		this.settingX = true;
		this.settingAngle = true;
		this.sensorIn = SensorInput.getInstance();
	}

	@Override
	public void firstCycle() {

	}

	@Override
	// Sets the position of the robot on the field
	public boolean calculate() {
		if(this.settingX){
			this.sensorIn.setDriveXPos(this.x);
		}

		if(this.settingY){
			this.sensorIn.setDriveYPos(this.y);
		}

		if(this.settingAngle){
			this.sensorIn.setAutoStartAngle(this.angle);
		}
		
		
		

		return true;
	}

	@Override
	public void override() {

	}

}
