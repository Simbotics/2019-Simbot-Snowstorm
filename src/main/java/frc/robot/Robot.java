package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.auton.AutonControl;
import frc.imaging.SimLimelight;
import frc.imaging.SimLimelight.LimelightTargetType;
import frc.io.Dashboard;
import frc.io.Logger;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.teleop.TeleopControl;
import frc.util.Debugger;

public class Robot extends TimedRobot {
	private RobotOutput robotOut;
	private SensorInput sensorInput;
	private TeleopControl teleopControl;
	private Logger logger;
	private Dashboard dashboard;
	private SimLimelight limelight;
	private boolean pushToDashboard = true;
	public static boolean teleopInitialized = false;

	
	@Override
	public void robotInit() {
		Debugger.defaultOn();
		this.dashboard = Dashboard.getInstance();
		if (this.pushToDashboard) {
			RobotConstants.pushValues();
		}
		this.robotOut = RobotOutput.getInstance();
		this.sensorInput = SensorInput.getInstance();
		this.teleopControl = TeleopControl.getInstance();
		this.limelight = SimLimelight.getInstance();
		this.logger = Logger.getInstance();
		LiveWindow.disableAllTelemetry();
		this.robotOut.configureSpeedControllers();
		this.limelight.setLimelight(LimelightTargetType.DRIVER);
		this.limelight.getTargetInfo();

	}

	@Override
	public void disabledInit() {
		this.robotOut.stopAll();
		this.teleopControl.disable();
		this.logger.close();
	
	}

	
	@Override
	public void disabledPeriodic() {
		this.sensorInput.update();
		this.dashboard.updateAll();
		AutonControl.getInstance().updateModes();

	}


	@Override
	public void autonomousInit() {
		robotOut.setCompressor(false);
		robotOut.setDriveRampRate(0.0);
		AutonControl.getInstance().initialize();
		AutonControl.getInstance().setRunning(true);
		AutonControl.getInstance().setOverrideAuto(false);
		this.sensorInput.reset();
		this.sensorInput.resetAutonTimer();
		this.logger.openFile();

	}

	@Override
	public void autonomousPeriodic() {
		this.sensorInput.update();
		this.dashboard.updateAll();
		AutonControl.getInstance().runCycle();
		this.logger.logAll();
	}

	public void testInit() {

	}

	public void testPeriodic() {

	}

	@Override
	public void teleopInit() {
		if (!AutonControl.getInstance().isRunning()) {
			robotOut.setCompressor(true);
			robotOut.setDriveRampRate(0.15);
			this.teleopControl.initialize();
			Robot.teleopInitialized = true;
		}
	}

	@Override
	public void teleopPeriodic() {

		if (AutonControl.getInstance().isRunning()) {
			this.autonomousPeriodic();
		} else {
			if (!Robot.teleopInitialized) {
				this.teleopInit();
			}
			this.sensorInput.update();
			this.dashboard.updateAll();
			this.teleopControl.runCycle();
		}
	}
}
