package frc.io;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.imaging.SimLimelight;
import frc.robot.RobotConstants;
import frc.util.SimEncoder;
import frc.util.SimNavx;

public class SensorInput {


	private static SensorInput instance;

	private RobotOutput robotOut;

	private DriverStation driverStation;
	private PowerDistributionPanel pdp;
	private SimNavx navx;

	private double lastTime = 0.0;
	private double deltaTime = 20.0;

	private boolean firstCycle = true;

	private DriverStationState driverStationMode = DriverStationState.DISABLED;
	private DriverStation.Alliance alliance;
	private double matchTime;

	private long SystemTimeAtAutonStart = 0;
	private long timeSinceAutonStarted = 0;

	private boolean usingNavX = true;

	private double leftDriveSpeedFPS;
	private double rightDriveSpeedFPS;
	private double lastLeftDriveSpeedFPS = 0;
	private double lastRightDriveSpeedFPS = 0;

	private double leftDriveAccelerationFPSSquared;
	private double rightDriveAccelerationFPSSquared;

	private double xPosition = 0;
	private double yPosition = 0;
	private double autoStartAngle = 90;

	private double drivePositionState = 0;
	private double driveAccelerationState = 0;
	private double driveVelocityState = 0;
	private double gyroPositionState = 0;
	private double gyroAngle;
	private double lastGyroAngle;

	private SimEncoder leftDriveEncoder;
	private SimEncoder rightDriveEncoder;


	private SimLimelight limelight;
	private AnalogInput pressureSensor;


	private SensorInput() {
		this.robotOut = RobotOutput.getInstance();
		this.pdp = new PowerDistributionPanel();
		this.driverStation = DriverStation.getInstance();
		this.navx = new SimNavx();
		this.pressureSensor = new AnalogInput(0);
		this.leftDriveEncoder = new SimEncoder(1, 0);
		if (RobotConstants.IS_COMPBOT) {
			this.rightDriveEncoder = new SimEncoder(2, 3);
		} else {
			this.rightDriveEncoder = new SimEncoder(3, 2);//Comp bot is 2, 3, prac is 3, 2
		}

		if (this.usingNavX) {
			this.navx = new SimNavx();
		}

		this.reset();
	}

	public static SensorInput getInstance() {
		if (instance == null) {
			instance = new SensorInput();
		}
		return instance;
	}

	public void reset() {
		this.firstCycle = true;
		this.navx.reset();
		if (this.usingNavX) {
			this.navx.reset();
		}

		this.leftDriveEncoder.reset();
		this.rightDriveEncoder.reset();

		this.robotOut.resetEncoders();

		this.xPosition = 0;
		this.yPosition = 0;
	}

	public void update() {

		if (this.lastTime == 0.0) {
			this.deltaTime = 20;
			this.lastTime = System.currentTimeMillis();
		} else {
			this.deltaTime = System.currentTimeMillis() - this.lastTime;
			this.lastTime = System.currentTimeMillis();
		}

		if (this.driverStation.isAutonomous()) {
			this.timeSinceAutonStarted = System.currentTimeMillis() - this.SystemTimeAtAutonStart;
			SmartDashboard.putNumber("12_Time Since Auto Start:", this.timeSinceAutonStarted);
		}

		if (this.firstCycle) {
			this.firstCycle = false;
			if (this.usingNavX) {
				this.lastGyroAngle = this.navx.getAngle() + (this.autoStartAngle - 90);
			} else {
				this.lastGyroAngle = 0.0;
			}
			this.driveVelocityState = 0;
		} else {
			this.driveVelocityState = this.getDriveSpeedFPS();
		}

		if (this.usingNavX) {
			this.navx.update();
		}
		this.leftDriveEncoder.updateSpeed();
		this.rightDriveEncoder.updateSpeed();

		this.alliance = this.driverStation.getAlliance();
		this.matchTime = this.driverStation.getMatchTime();

		if (this.driverStation.isDisabled()) {
			this.driverStationMode = DriverStationState.DISABLED;
		} else if (this.driverStation.isAutonomous()) {
			this.driverStationMode = DriverStationState.AUTONOMOUS;
		} else if (this.driverStation.isOperatorControl()) {
			this.driverStationMode = DriverStationState.TELEOP;
		}

		double leftTicksPerCycle = this.getEncoderLeftSpeed();
		double rightTicksPerCycle = this.getEncoderRightSpeed();

		this.leftDriveSpeedFPS = (((leftTicksPerCycle / 1024.0) * (Math.PI * 6.18)) / 12.0) * (1000.0 / this.deltaTime);
		this.rightDriveSpeedFPS = (((rightTicksPerCycle / 1024.0) * (Math.PI * 6.18)) / 12.0)
				* (1000.0 / this.deltaTime);

		this.leftDriveAccelerationFPSSquared = (this.leftDriveSpeedFPS - this.lastLeftDriveSpeedFPS)
				/ (this.deltaTime / 1000.0);
		this.rightDriveAccelerationFPSSquared = (this.rightDriveSpeedFPS - this.lastRightDriveSpeedFPS)
				/ (this.deltaTime / 1000.0);

		this.lastLeftDriveSpeedFPS = this.leftDriveSpeedFPS;
		this.lastRightDriveSpeedFPS = this.rightDriveSpeedFPS;

		if (this.usingNavX) {
			this.gyroAngle = this.navx.getAngle() + (this.autoStartAngle - 90);
		} else {
			this.gyroAngle = 0.0;
		}

		this.drivePositionState = this.getDriveFeet();

		this.driveAccelerationState = this.getDriveAcceleration();

		this.gyroPositionState = this.gyroAngle;

		double driveXSpeed = this.driveVelocityState * Math.cos(Math.toRadians(this.gyroPositionState));
		double driveYSpeed = this.driveVelocityState * Math.sin(Math.toRadians(this.gyroPositionState));
		xPosition += driveXSpeed * this.deltaTime / 1000.0;
		yPosition += driveYSpeed * this.deltaTime / 1000.0;

	}

	public double getMatchTimeLeft() {
		return this.matchTime;
	}

	public DriverStationState getDriverStationMode() {
		return this.driverStationMode;
	}

	public void resetAutonTimer() {
		this.SystemTimeAtAutonStart = System.currentTimeMillis();
	}

	public void setAutoStartAngle(double angle) {
		this.autoStartAngle = angle;
	}

	public double getDriveSpeedFPS() {
		return (this.leftDriveSpeedFPS + this.rightDriveSpeedFPS) / 2.0;
	}

	public double getDriveLeftFPS() {
		return this.leftDriveSpeedFPS;
	}

	public double getDriveRightFPS() {
		return this.rightDriveSpeedFPS;
	}

	public int getEncoderLeftSpeed() {
		return this.leftDriveEncoder.speed();
	}

	public int getEncoderRightSpeed() {
		return this.rightDriveEncoder.speed();
	}

	public double getDriveFeet() {
		return this.getDriveInches() / 12.0;
	}

	public void setDriveXPos(double x) {
		this.xPosition = x;
	}

	public void setDriveYPos(double y) {
		this.yPosition = y;
	}

	public double getDriveXPos() {
		return this.xPosition;
	}

	public double getDriveYPos() {
		return this.yPosition;
	}

	public double getGyroPositionState() {
		return this.gyroPositionState;
	}

	public double getAngle() {
		return this.gyroAngle;
	}

	public double getDrivePositionState() {
		return this.drivePositionState;
	}

	public double getDriveVelocityState() {
		return this.driveVelocityState;
	}

	public double getDriveAccelerationState() {
		return this.driveAccelerationState;
	}

	public double getDriveAcceleration() {
		return (this.getLeftDriveAcceleration() + this.getRightDriveAcceleration()) / 2.0;
	}

	public double getLeftDriveAcceleration() {
		return this.leftDriveAccelerationFPSSquared;
	}

	public double getRightDriveAcceleration() {
		return this.rightDriveAccelerationFPSSquared;
	}

	public double getDeltaTime() {
		return this.deltaTime;
	}

	public enum DriverStationState {
		AUTONOMOUS, TELEOP, DISABLED,
	}

	public DriverStation.Alliance getAllianceColour() {
		return this.alliance;
	}

	public double getLiftHeight() {
		return this.robotOut.getLiftEncoderTicks() / RobotConstants.ELEVATOR_TICKS_PER_FOOT;
	}

	public double getLiftHeightTicks() {
		return this.robotOut.getLiftEncoderTicks();
	}

	public double getMatchTime() {
		return this.matchTime;
	}

	public long getTimeSinceAutoStarted() {
		return this.timeSinceAutonStarted;
	}

	// PDP //

	public double getVoltage() {
		return this.pdp.getVoltage();
	}

	public double getCurrent(int port) {
		return this.pdp.getCurrent(port);
	}

	// NAVX //
	public double getGyroAngle() {
		if (this.usingNavX) {
			return this.gyroAngle;
		} else {
			return 0;
		}

	}

	public int getEncoderRight() {
		return this.rightDriveEncoder.get();
	}

	public int getEncoderLeft() {
		return this.leftDriveEncoder.get();
	}

	public double getDriveEncoderAverage() {
		return (this.getEncoderRight() + this.getEncoderLeft()) / 2.0;
	}

	public double getDriveInches() {
		return this.getDriveEncoderAverage() / RobotConstants.DRIVE_TICKS_PER_INCH_HIGH;
	}

	public double getPressure(){
		return 250 * (pressureSensor.getVoltage()/5.0) - 25;
	}

}