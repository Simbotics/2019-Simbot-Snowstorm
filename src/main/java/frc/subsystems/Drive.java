package frc.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.imaging.SimLimelight;
import frc.io.Dashboard;
import frc.io.DriverInput;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.robot.RobotConstants;
import frc.subsystems.LEDStrip.LEDColourState;
import frc.util.Debugger;
import frc.util.SimLib;
import frc.util.SimPID;
import frc.util.SimPIDF;
import frc.util.SimPoint;

public class Drive extends Subsystem {

	public enum DriveState {
		OUTPUT, VELOCITY, TURN_TO_LIMELIGHT_TARGET, DRIVE_TO_LIMELIGHT_TARGET, DRIVE_LOCK, 
		FINISHING_VISION,
	}

	private static Drive instance;
	private RobotOutput robotOut;
	private SensorInput sensorIn;
	private DriverInput driverIn;
	private SimLimelight limelight;
	private DriveState currentState = DriveState.OUTPUT;
	private double leftOut;
	private double rightOut;
	private double yOutput = 0;
	private double xOutput = 0;
	private double driveLock;
	private boolean startingDriveLock = true;
	private double desiredAngle;
	private boolean afterVisionFirstCycle = true;
	private int afterVisionCycles = 0;
	private boolean isVisionDone = false;
	private boolean withinAngle = false;
	private LEDColourState desiredLEDState = LEDColourState.OFF;

	private boolean hasSeenTarget = false;
	private double lastYOutput;

	private SimPIDF leftVelPID;
	private SimPIDF rightVelPID;
	private SimPID straightPID;
	private SimPID turnPID;
	private SimPID limelightTurnPID;
	private SimPID driveLockPID;

	public static Drive getInstance() {
		if (instance == null) {
			instance = new Drive();
		}
		return instance;
	}

	private Drive() {
		this.robotOut = RobotOutput.getInstance();
		this.sensorIn = SensorInput.getInstance();
		this.driverIn = DriverInput.getInstance();
		this.limelight = SimLimelight.getInstance();
		this.firstCycle();
	}

	// rotates the xy coordinates to be relative to the angle of the target
	private SimPoint getRotatedError(double theta, double desiredX, double desiredY) {
		double currentX = this.sensorIn.getDriveXPos();
		double currentY = this.sensorIn.getDriveYPos();
		double rotation = 90 - theta;

		SimPoint currentPosition = new SimPoint(currentX, currentY);
		SimPoint finalPosition = new SimPoint(desiredX, desiredY);

		currentPosition.rotateByAngleDegrees(rotation);
		finalPosition.rotateByAngleDegrees(rotation);

		double xError = finalPosition.getX() - currentPosition.getX();
		double yError = finalPosition.getY() - currentPosition.getY();

		return new SimPoint(xError, yError);

	}

	@Override
	public void firstCycle() {
		this.straightPID = new SimPID(RobotConstants.getDriveStraightPID());
		this.straightPID.setMinDoneCycles(1);
		this.leftVelPID = new SimPIDF(RobotConstants.getDriveVelocityPID().p, RobotConstants.getDriveVelocityPID().i,
				RobotConstants.getDriveVelocityPID().d, RobotConstants.getDriveVelocityPID().ff,
				RobotConstants.getDriveVelocityPID().eps);
		this.leftVelPID.setMaxOutput(1);
		this.rightVelPID = new SimPIDF(RobotConstants.getDriveVelocityPID().p, RobotConstants.getDriveVelocityPID().i,
				RobotConstants.getDriveVelocityPID().d, RobotConstants.getDriveVelocityPID().ff,
				RobotConstants.getDriveVelocityPID().eps);
		this.rightVelPID.setMaxOutput(1);
		this.turnPID = new SimPID(RobotConstants.getDriveTurnPID());
		this.turnPID.setMinDoneCycles(10);
		this.turnPID.setMaxOutput(10);
		this.straightPID.setMinDoneCycles(10);
		this.limelightTurnPID = new SimPID(RobotConstants.getLimeLightTurnPID());
		this.limelightTurnPID.setMinDoneCycles(10);
		this.driveLockPID = new SimPID(RobotConstants.getDriveLockPID());
		this.driveLockPID.setMinDoneCycles(10);
		this.straightPID.setIRange(1);
		this.turnPID.setIRange(10);

	}

	// Sets the motor output for the drive base
	public void setOutput(double y, double turn) {
		this.leftOut = y + turn;
		this.rightOut = y - turn;
	}

	public void setTargetVelocity(double targetVel) {
		this.leftVelPID.setDesiredValue(targetVel);
		this.rightVelPID.setDesiredValue(targetVel);
	}

	public void driveAtVelocity(double velocity) {
		this.currentState = DriveState.VELOCITY;
		this.setTargetVelocity(velocity);
		double left = this.leftVelPID.calcPID(this.sensorIn.getDriveSpeedFPS());
		double right = this.rightVelPID.calcPID(this.sensorIn.getDriveSpeedFPS());
		this.robotOut.setDriveLeft(left);
		this.robotOut.setDriveRight(right);
	}

	public void setVelocityOutput(double leftOut, double rightOut) {
		this.currentState = DriveState.VELOCITY;

		this.leftVelPID.setDesiredValue(leftOut);
		this.rightVelPID.setDesiredValue(rightOut);

		rightOut = this.rightVelPID.calcPID(this.sensorIn.getDriveRightFPS());
		leftOut = this.leftVelPID.calcPID(this.sensorIn.getDriveLeftFPS());

		this.robotOut.setDriveLeft(leftOut);
		this.robotOut.setDriveRight(rightOut);
	}

	public void setRampRate(double rate) {
		this.robotOut.setDriveRampRate(rate);
	}

	@Override
	public void calculate() {
		SmartDashboard.putString("DRIVE_STATE: ", this.currentState.toString());
		if (this.currentState == DriveState.OUTPUT) {
			this.robotOut.setDriveLeft(this.leftOut);
			this.robotOut.setDriveRight(this.rightOut);
		} else if (this.currentState == DriveState.VELOCITY) {
			this.robotOut.setDriveLeft(this.leftOut);
			this.robotOut.setDriveRight(this.rightOut);
		} else if (this.currentState == DriveState.TURN_TO_LIMELIGHT_TARGET) {
			// System.out.println("Does target exist: " + this.limelight.getTargetExists());
			// SmartDashboard.putNumber("Area of Target ", this.limelight.getTargetArea());

			if (this.limelight.getTargetExists()) {
				this.driveTurnToAngleWithForwardVelocity(this.yOutput);
			} else {
				this.desiredLEDState = LEDColourState.VISION_NOT_AIMED;
				this.robotOut.setDriveLeft(this.leftOut);
				this.robotOut.setDriveRight(this.rightOut);
			}
		} else if (this.currentState == DriveState.DRIVE_TO_LIMELIGHT_TARGET) {
			this.isVisionDone = false;
			if (this.limelight.getTargetExists()) {
				this.driveLimeLightXY(14);
				if (this.straightPID.isDone()) {
					this.currentState = DriveState.FINISHING_VISION;
				}
			} else {
				this.desiredLEDState = LEDColourState.VISION_NOT_AIMED;
				this.robotOut.setDriveLeft(this.leftOut);
				this.robotOut.setDriveRight(this.rightOut);
			}
		} else if (this.currentState == DriveState.DRIVE_LOCK) {
			if (this.startingDriveLock) {
				this.driveLockPID.setDesiredValue(this.sensorIn.getDriveFeet());
				this.startingDriveLock = false;
			} else {
				double output = this.driveLockPID.calcPID(this.sensorIn.getDriveFeet());
				this.robotOut.setDriveLeft(output);
				this.robotOut.setDriveRight(output);
			}
		} else if (this.currentState == DriveState.FINISHING_VISION) {
			this.afterVisionCycles++;
			this.robotOut.setDriveLeft(0.01);
			this.robotOut.setDriveRight(0.01);
			this.desiredLEDState = LEDColourState.VISION_COMPLETE;
			if (this.afterVisionCycles > 15) {
				this.isVisionDone = true;
				this.currentState = DriveState.OUTPUT;
			}
		}
		if (this.currentState != DriveState.DRIVE_LOCK) {
			this.startingDriveLock = true;
		}

		if (this.currentState != DriveState.TURN_TO_LIMELIGHT_TARGET) {
			this.hasSeenTarget = false;
		}

		if (this.currentState != DriveState.FINISHING_VISION) {
			this.afterVisionCycles = 0;
			this.afterVisionFirstCycle = true;
		}
	}

	public void setyOutput(double yOutput) {
		this.yOutput = yOutput;
	}

	public void setxOutput(double xOutput) {
		this.xOutput = xOutput;
	}

	@Override
	public void disable() {
		this.robotOut.setDriveLeft(0.0);
		this.robotOut.setDriveRight(0.0);
	}

	public void driveLimeLightXY(double maxOutput) {
		this.straightPID.setMinMaxOutput(1, maxOutput);
		this.straightPID.setFinishedRange(0.3);
		this.straightPID.setMinDoneCycles(1);
		this.straightPID.setDesiredValue(this.limelight.getVisionTargetDistance() + this.sensorIn.getDriveFeet() - 2.1);
		this.driveTurnToAngleWithForwardVelocity(
				this.straightPID.calcPID(this.sensorIn.getDriveFeet()) / RobotConstants.DRIVE_MAX_VELOCITY);

	}

	public void driveLimeLightXY() {
		this.driveLimeLightXY(6.0);
	}

	public void driveTurnToAngleWithForwardVelocity(double yOutput) {

		double angle = this.sensorIn.getGyroAngle();
		double offset = angle % 360;
		double eps = 0.5;
		double theta = this.limelight.getTargetX() + angle;
		double maxTurn = 1;
		this.yOutput = yOutput;
		SmartDashboard.putNumber("Motor Output: ", yOutput);

		// this.turnPID.setMaxOutput(11);
		this.limelightTurnPID.setFinishedRange(eps);

		this.limelightTurnPID.setDesiredValue(theta);

		// System.out.println("LIMELIGHT DESIRED ANGLE: " +
		// this.limelightTurnPID.getDesiredVal());
		double x = this.limelightTurnPID.calcPID(this.sensorIn.getGyroAngle());
		if (x > maxTurn) {
			x = maxTurn;
		} else if (x < -maxTurn) {
			x = -maxTurn;
		}

		if (Math.abs(this.sensorIn.getGyroAngle() - theta) < 3.5) {
			this.withinAngle = true;
		} else {
			this.withinAngle = false;
		}
		if (Math.abs(this.sensorIn.getGyroAngle() - theta) < 2) {
			x = 0;
			
			if(this.currentState == DriveState.TURN_TO_LIMELIGHT_TARGET){
				this.desiredLEDState = LEDColourState.VISION_COMPLETE;	
			} else {
				this.desiredLEDState = LEDColourState.VISION_MOVING;
			}
			
		} else {
		
			this.desiredLEDState = LEDColourState.VISION_TURNING;
		}

		double leftOut = SimLib.calcLeftTankDrive(x, yOutput);
		double rightOut = SimLib.calcRightTankDrive(x, yOutput);

		this.robotOut.setDriveLeft(leftOut);
		this.robotOut.setDriveRight(rightOut);
	}

	public boolean DriveToPoint(double x, double y, double theta, double minVelocity, double maxVelocity,
		double turnRate, double maxTurn, double eps) {
		this.straightPID.setMinMaxOutput(minVelocity, maxVelocity);
		SimPoint error = getRotatedError(theta, x, y);
		double targetHeading;
		this.straightPID.setFinishedRange(eps);
		this.turnPID.setMaxOutput(10);

		if (error.getY() < 0) { // flip X if we are going backwards
			error.setX(-error.getX());
		}

		double turningOffset = (error.getX() * turnRate); // based on how far we are in x turn more
		
		if (turningOffset > maxTurn) { 
			turningOffset = maxTurn;
		} else if (turningOffset < -maxTurn) {
			turningOffset = -maxTurn;
		}
		
		targetHeading = theta - turningOffset;

		double angle = sensorIn.getGyroAngle();

		this.turnPID.setDesiredValue(targetHeading);

		

		double yError = error.getY();
		double yOutput;

		if (Math.abs(yError) > 3.0) {
			this.robotOut.setDriveRampRate(0.20);
		} else {
			this.robotOut.setDriveRampRate(0);
		}

		yOutput = this.straightPID.calcPIDError(yError);
		

		double distanceFromTargetHeading = Math.abs(this.turnPID.getDesiredVal() - this.sensorIn.getAngle());
		if (distanceFromTargetHeading > 90) { // prevents the y output from being reversed in the next calculation
			distanceFromTargetHeading = 90;
		}

		yOutput = yOutput * (((-1 * distanceFromTargetHeading) / 90.0) + 1);

		double xOutput = -this.turnPID.calcPID(this.sensorIn.getAngle());
	

		double leftOut = SimLib.calcLeftTankDrive(xOutput, yOutput);
		double rightOut = SimLib.calcRightTankDrive(xOutput, yOutput);

		this.setVelocityOutput(leftOut, rightOut);

		double dist = (yError);
		if (this.straightPID.isDone()) {
			System.out.println("I have reached the epsilon!");
		}

		boolean isDone = false;
		if (minVelocity <= 0.5) {
			if (this.straightPID.isDone()) {
				disable();
				isDone = true;
				this.robotOut.setDriveRampRate(0);
			}
		} else if (Math.abs(dist) < eps) {
			isDone = true;
			this.robotOut.setDriveRampRate(0);
		}

		return isDone;
	}

	public boolean DriveToLimelightTarget(double minVelocity, double maxVelocity, double turnRate,
			double maxTurn) {
		double x = this.limelight.getTargetInfo().x;
		double y = this.limelight.getTargetInfo().y;
		double theta = 0; 
		double eps = 0.5;

		return DriveToPoint(x, y, theta, minVelocity, maxVelocity, turnRate, maxTurn, eps);
	}

	public void setState(DriveState state) {
		this.currentState = state;
	}

	public DriveState getDriveState() {
		return this.currentState;
	}

	public SimPID getDriveTurnPID() {
		return this.turnPID;
	}

	public SimPID getDriveStraightPID() {
		return this.straightPID;
	}

	public SimPID getLimelightTurnPID(){
		return this.limelightTurnPID;
	}

	public boolean getIsVisionDone() {
		return this.isVisionDone;
	}

	public LEDColourState getDesiredLedState() {
		return this.desiredLEDState;
	}

	public void setIsVisionDone(boolean isDone) {
		this.isVisionDone = isDone;
	}

	public boolean IsWithinAngle() {
		return this.withinAngle;
	}
}