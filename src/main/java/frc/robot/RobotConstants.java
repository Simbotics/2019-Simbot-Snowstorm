package frc.robot;

import frc.io.Dashboard;
import frc.util.PIDConstants;


public class RobotConstants {

	public static final boolean USING_DASHBOARD = false;
	public static final boolean IS_COMPBOT = true;
	// Drive Size Constants (Feet)
	public static final double DRIVE_MAX_VELOCITY = 14.0;

	// Climber Presets (Degrees)
	public static final double DRIVING = 0.0;
	public static final double BASE_LOCK = 0.0;
	public static final double CLIMB_START = 0.0;
	public static final double PREPARE_CLIMB = -29.0;
	public static final double CLIMB_DECCELERATE = 85;
	public static final double CLIMB_RETRACT = 125.0;
	public static final double CLIMB_BACK_DOWN = 135.0;
	public static final double LEVEL_2 = 173.0;
	public static final double LEVEL_2_UP = 180.0;
	public static final double CLIMB_END = 215.0;


	// Lift Presets (Feet)
	public static final double GROUND = 0.0;
	public static final double GROUND_PANEL = 0.25;
	public static final double HUMAN_LOADING_STATION = 2.65;
	public static final double PANEL_LOW = 0.4;
	public static final double PANEL_MEDIUM = 3.3;
	public static final double AUTON_PANEL_MEDIUM = 3.8;
	public static final double PANEL_MEDIUM_VISION = 4.0;
	public static final double PANEL_HIGH = 6.0;
	public static final double CARGO_CARGO_SHIP = 2.2;
	public static final double CARGO_ROCKET_LOW = 1.05;
	public static final double CARGO_ROCKET_MEDIUM = 3.8;
	public static final double CARGO_ROCKET_HIGH = 6.4;

	public static final double DRIVE_TICKS_PER_INCH_HIGH = 1024.0 / Math.PI * 6.0;
	public static final double DRIVE_FPS_TO_RPM = (60.0 / (Math.PI * 6.0)) * 12.0;
	public static final double CLIMBER_TICKS_PER_DEGREE = 4096.0 / ((18.0 / 80.0) * (12.0 / 32.0) * 360.0);
	public static final double ELEVATOR_TICKS_PER_FOOT = (4096 / (Math.PI * 2.0 * 0.748)) * 12.0 * 3.0 / 2.0;

	private static Dashboard dashboard = Dashboard.getInstance();

	private static PIDConstants LiftUpPID = new PIDConstants(0.055, 0.0005, 2.0, 250);// NEED TO ASSIGN THESE
	private static PIDConstants LiftDownPID = new PIDConstants(0.025, 0.0005, 1.0, 250);// NEED TO ASSIGN THESE

	
	private static PIDConstants driveLockPID = new PIDConstants(11, 0, 0, 0.05);
	private static PIDConstants driveStraightPID = new PIDConstants(3.0, 0.04, 5.0, 0.05);
	private static PIDConstants driveTurnPID = new PIDConstants(0.2, 0.005, 0.6, 1);
	private static PIDConstants driveVelocityPID = new PIDConstants(0.0, 0, 0.0, 1.0 / 14.0, 0);
	private static PIDConstants gyroPID = new PIDConstants(0, 0, 0, 0);
	private static PIDConstants limeLightTurnPID = new PIDConstants(0.02, 0.000, 0.09, 1);

	public static final double PATH_TURN_P = 6;
	public static final double LIFT_GRAVITY_OFFSET = 0.06;
	public static final double CLIMBER_GRAVITY_OFFSET = 0.0;
	public static final double CLIMBER_ANGLE_OFFSET = -47;



	public static final int intakeDelayCycles = 1;

	// All of these return the current PID constants/values
	public static PIDConstants getDriveStraightPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("DRIVE_PID", driveStraightPID);
		} else {
			return driveStraightPID;
		}
	}

	public static PIDConstants getDriveVelocityPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("DRIVE_VELOCITY_PID", driveVelocityPID);
		} else {
			return driveVelocityPID;
		}
	}

	public static PIDConstants getDriveTurnPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("TURN_PID", driveTurnPID);
		} else {
			return driveTurnPID;
		}
	}

	public static PIDConstants getDriveLockPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("DRIVE_LOCK_PID", driveLockPID);
		} else {
			return driveLockPID;
		}
	}

	public static PIDConstants getGyroPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("DRIVE_GYRO", gyroPID);
		} else {
			return gyroPID;
		}
	}

	public static PIDConstants getLiftUpPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("LIFT_UP_PID", LiftUpPID);
		} else {
			return LiftUpPID;
		}
	}

	public static PIDConstants getLiftDownPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("LIFT_DOWN_PID", LiftDownPID);
		} else {
			return LiftDownPID;
		}
	}

	public static PIDConstants getLimeLightTurnPID() {
		if (USING_DASHBOARD) {
			return dashboard.getPIDConstants("LIMELIGHT_TURN_PID", limeLightTurnPID);
		} else {
			return limeLightTurnPID;
		}
	}

	

	// Pushes the values to the smart dashboard

	public static void pushValues() {
		dashboard.putPIDConstants("DRIVE_PID", driveStraightPID);
		//dashboard.putPIDConstants("DRIVE_LOCK_PID", driveLockPID);
		dashboard.putPIDConstants("TURN_PID", driveTurnPID);
		// dashboard.putPIDConstants("DRIVE_VELOCITY_PID", driveVelocityPID);
	 	dashboard.putPIDConstants("LIMELIGHT_TURN_PID", limeLightTurnPID);
		// dashboard.putPIDConstants("TALON_VELOCITY_PID", talonVelocityPID);
		// dashboard.putPIDConstants("LIFT_UP_PID", LiftUpPID);
		// dashboard.putPIDConstants("LIFT_DOWN_PID", LiftDownPID);
	

	}
}
