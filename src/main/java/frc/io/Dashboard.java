package frc.io;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.auton.mode.AutonMode;
import frc.imaging.SimLimelight;
import frc.robot.RobotConstants;
import frc.util.PIDConstants;
import frc.util.ProfileConstants;
import frc.util.TrajectoryConfig;

//The Dashboard prints everything and every value that is important on to the dashboard
public class Dashboard {
	private static Dashboard instance;

	

	private boolean manual = false;

	private Dashboard() {
		SmartDashboard.putNumber("Path Turn P", RobotConstants.PATH_TURN_P);

	}

	public static Dashboard getInstance() {
		if (instance == null) {
			instance = new Dashboard();
		}
		return instance;
	}

	public void updateAll() {
		updateSensorDisplay();
	}

	public void updateSensorDisplay() {
		SensorInput sensorInput = SensorInput.getInstance();
		SimLimelight limelight = SimLimelight.getInstance();
		RobotOutput robotOut = RobotOutput.getInstance();
		
		SmartDashboard.putNumber("123_Gyro", sensorInput.getGyroAngle());
		SmartDashboard.putNumber("123_X Position: ", sensorInput.getDriveXPos());
		SmartDashboard.putNumber("123_Y Position: ", sensorInput.getDriveYPos());
		SmartDashboard.putNumber("123_Left Encoder: ", sensorInput.getEncoderLeft());
		SmartDashboard.putNumber("123_Right Encoder: ", sensorInput.getEncoderRight());
		SmartDashboard.putNumber("123_Drive Speed FPS: ", sensorInput.getDriveSpeedFPS());
		SmartDashboard.putNumber("123_Elevator Encoder Feet: ", robotOut.getLiftFeet());
		SmartDashboard.putNumber("123_Climber Encoder Angle: ", robotOut.getClimberAngle());
		SmartDashboard.putNumber("Elevator Left Current", sensorInput.getCurrent(5));
		SmartDashboard.putNumber("Elevator Right Current", sensorInput.getCurrent(6));
		SmartDashboard.putNumber("123_Pressure_Sensor", sensorInput.getPressure());
		SmartDashboard.putNumber("Vision Target Area", limelight.getTargetArea());
		SmartDashboard.putBoolean("Compressor Enabled", robotOut.getCompressorState());
	}

	public void updateAutoModes(AutonMode set, boolean modified) {

	}

	public void printAutoModes(AutonMode set, boolean modified) {
		System.out.println(" ");

	}

	public void updateAutoDelay(double delay) {
		SmartDashboard.putNumber("1_Auton Delay: ", delay);
	}

	public void printAutoDelay(double delay) {
		System.out.println("1_Auton Delay: " + delay);
	}

	public double getConstant(String name, double defaultValue) {
		return SmartDashboard.getNumber(name, defaultValue);
	}

	// Get the PID Constants
	public PIDConstants getPIDConstants(String name, PIDConstants constants) {
		double p = SmartDashboard.getNumber("5_" + name + " - P Value", constants.p);
		double i = SmartDashboard.getNumber("5_" + name + " - I Value", constants.i);
		double d = SmartDashboard.getNumber("5_" + name + " - D Value", constants.d);
		double ff = SmartDashboard.getNumber("5_" + name + " - FF Value", constants.ff);
		double eps = SmartDashboard.getNumber("5_" + name + " - EPS Value", constants.eps);
		return new PIDConstants(p, i, d, ff, eps);
	}

	// Put the PID Constants on the dashboard
	public void putPIDConstants(String name, PIDConstants constants) {
		SmartDashboard.putNumber("5_" + name + " - P Value", constants.p);
		SmartDashboard.putNumber("5_" + name + " - I Value", constants.i);
		SmartDashboard.putNumber("5_" + name + " - D Value", constants.d);
		SmartDashboard.putNumber("5_" + name + " - FF Value", constants.ff);
		SmartDashboard.putNumber("5_" + name + " - EPS Value", constants.eps);
	}

	public ProfileConstants getProfileConstants(String name, ProfileConstants constants) {
		double p = SmartDashboard.getNumber("5_" + name + " - P Value", constants.p);
		double i = SmartDashboard.getNumber("5_" + name + " - I Value", constants.i);
		double d = SmartDashboard.getNumber("5_" + name + " - D Value", constants.d);
		double vFF = SmartDashboard.getNumber("3_" + name + " - vFF Value", constants.vFF);
		double aFF = SmartDashboard.getNumber("3_" + name + " - aFF Value", constants.aFF);
		double dFF = SmartDashboard.getNumber("3_" + name + " - dFF Value", constants.dFF);
		double gFF = SmartDashboard.getNumber("3_" + name + " - gFF Value", constants.gravityFF);
		double posEps = SmartDashboard.getNumber("3_" + name + " - Pos EPS Value", constants.positionEps);
		double velEps = SmartDashboard.getNumber("3_" + name + " - Vel EPS Value", constants.velocityEps);
		return new ProfileConstants(p, i, d, vFF, aFF, dFF, gFF, posEps, velEps);
	}

	// Put all the profile constants on the smart dashboard
	public void putProfileConstants(String name, ProfileConstants constants) {
		SmartDashboard.putNumber("5_" + name + " - P Value", constants.p);
		SmartDashboard.putNumber("5_" + name + " - I Value", constants.i);
		SmartDashboard.putNumber("5_" + name + " - D Value", constants.d);
		SmartDashboard.putNumber("3_" + name + " - vFF Value", constants.vFF);
		SmartDashboard.putNumber("3_" + name + " - aFF Value", constants.aFF);
		SmartDashboard.putNumber("3_" + name + " - dFF Value", constants.dFF);
		SmartDashboard.putNumber("3_" + name + " - gFF Value", constants.gravityFF);
		SmartDashboard.putNumber("3_" + name + " - Pos EPS Value", constants.positionEps);
		SmartDashboard.putNumber("3_" + name + " - Vel EPS Value", constants.velocityEps);
	}

	// Get the acceleration and velocity values
	public TrajectoryConfig getTrajectoryConfig(String name, TrajectoryConfig constants) {
		double maxAccel = SmartDashboard.getNumber("3_" + name + " - Max Accel Value", constants.maxAcceleration);
		double maxDecel = SmartDashboard.getNumber("3_" + name + " - Max Decel Value", constants.maxDeceleration);
		double maxVel = SmartDashboard.getNumber("3_" + name + " - Max Vel Value", constants.maxVelocity);
		return new TrajectoryConfig(maxAccel, maxDecel, maxVel);

	}

	// Get the PID Turn
	public double getPathTurnP() {
		return SmartDashboard.getNumber("Path Turn P", RobotConstants.PATH_TURN_P);
	}

	// Put all the acceleration and velocity values on screen
	public void putTrajectoryConfig(String name, TrajectoryConfig constants) {
		SmartDashboard.putNumber("3_" + name + " - Max Accel Value", constants.maxAcceleration);
		SmartDashboard.putNumber("3_" + name + " - Max Decel Value", constants.maxDeceleration);
		SmartDashboard.putNumber("3_" + name + " - Max Vel Value", constants.maxVelocity);

	}

	// Return class name of an object
	private String className(Object obj) {
		return obj.getClass().getSimpleName();
	}
}
