package frc.io;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;
import frc.robot.RobotConstants;
import frc.util.PIDConstants;

public class RobotOutput {
	private static RobotOutput instance;

	private CANSparkMax driveL1;
	private CANSparkMax driveL2;
	private CANSparkMax driveR1;
	private CANSparkMax driveR2;

	//// Climb motors
	private TalonSRX climbMotor1;
	private VictorSPX climbMotor2;
	private VictorSPX climbMotor3;
	private VictorSPX climbMotor4;

	// The intake motors

	private VictorSP cargoIntake;


	private Solenoid intakePunch;
	private Solenoid wristUp;
	private Solenoid wristDown;
	private Solenoid fingersIn;
	private Solenoid fingersOut;

	// Lift Motors
	private TalonSRX lift1;
	private VictorSPX lift2;

	private double leftDriveTarget = 0;
	private double rightDriveTarget = 0;
	private double lastDesired = 0;
	private double driveRampRate = 0;

	private double climbRampRate = 0;
	private Compressor compressor;

	private Spark ledStrip;

	private RobotOutput() {

		this.driveL1 = new CANSparkMax(13, MotorType.kBrushless);
		this.driveL2 = new CANSparkMax(15, MotorType.kBrushless);
		this.driveR1 = new CANSparkMax(18, MotorType.kBrushless);
		this.driveR2 = new CANSparkMax(2, MotorType.kBrushless);

		this.cargoIntake = new VictorSP(0);

		this.climbMotor1 = new TalonSRX(3); // right
		this.climbMotor2 = new VictorSPX(4);
		this.climbMotor3 = new VictorSPX(11); // left
		this.climbMotor4 = new VictorSPX(12);

		this.lift1 = new TalonSRX(5);
		this.lift2 = new VictorSPX(6);

		this.wristUp = new Solenoid(0);
		this.wristDown = new Solenoid(1);
		this.intakePunch = new Solenoid(2);
		this.fingersIn = new Solenoid(3);
		this.fingersOut = new Solenoid(4);

		this.configureSpeedControllers();

		this.compressor = new Compressor();

		this.ledStrip = new Spark(9);

	}

	public static RobotOutput getInstance() {
		if (instance == null) {
			instance = new RobotOutput();
		}
		return instance;
	}

	// Motor Commands

	public void configureSpeedControllers() {

		this.driveL2.follow(this.driveL1, false);
		this.driveR2.follow(this.driveR1, false);

		this.driveL1.setSmartCurrentLimit(60, 10);
		this.driveL2.setSmartCurrentLimit(60, 10);
		this.driveR1.setSmartCurrentLimit(60, 10);
		this.driveR2.setSmartCurrentLimit(60, 10);

		this.climbMotor1.setControlFramePeriod(ControlFrame.Control_3_General, 20);
		this.climbMotor2.setControlFramePeriod(ControlFrame.Control_3_General, 20);
		this.climbMotor3.setControlFramePeriod(ControlFrame.Control_3_General, 20);
		this.climbMotor4.setControlFramePeriod(ControlFrame.Control_3_General, 20);

		this.lift1.setControlFramePeriod(ControlFrame.Control_3_General, 20);
		this.lift2.setControlFramePeriod(ControlFrame.Control_3_General, 20);

		this.driveL1.setInverted(false);
		this.driveL2.setInverted(true);
		this.driveR1.setInverted(true);
		this.driveR2.setInverted(true);

		this.cargoIntake.setInverted(false);

		this.climbMotor1.setInverted(!RobotConstants.IS_COMPBOT); 
		this.climbMotor2.setInverted(false);
		this.climbMotor3.setInverted(RobotConstants.IS_COMPBOT);
		this.climbMotor4.setInverted(true);

		this.climbMotor2.follow(this.climbMotor1);
		this.climbMotor3.follow(this.climbMotor1);
		this.climbMotor4.follow(this.climbMotor1);

		this.lift2.follow(this.lift1);

		this.climbMotor1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
		this.lift1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

		this.climbMotor1.setSelectedSensorPosition(0, 0, 0);
		this.lift1.setSelectedSensorPosition(0, 0, 0);

		this.climbMotor1.setSensorPhase(!RobotConstants.IS_COMPBOT);

		this.lift1.setInverted(RobotConstants.IS_COMPBOT);
		this.lift2.setInverted(false);
		this.lift1.setSensorPhase(!RobotConstants.IS_COMPBOT); 

	}

	// Drive

	public void setDriveLeft(double output) {
		this.driveL1.set(output);
	}

	public void setDriveRight(double output) {
		this.driveR1.set(output);
	}

	public double getDriveLeftTarget() {
		return this.leftDriveTarget;
	}

	public double getDriveRightTarget() {
		return this.rightDriveTarget;
	}

	public int getLiftEncoderTicks() {
		return this.lift1.getSelectedSensorPosition(0);
	}

	public void setDriveRampRate(double rampRateSecondsToFull) {
		this.driveL1.setOpenLoopRampRate(rampRateSecondsToFull);
		this.driveL2.setOpenLoopRampRate(rampRateSecondsToFull);
		this.driveR1.setOpenLoopRampRate(rampRateSecondsToFull);
		this.driveR2.setOpenLoopRampRate(rampRateSecondsToFull);
	}

	public double getDriveRampRate() {
		return this.driveRampRate;
	}

	public void setCargoIntake(double output) {
		this.cargoIntake.set(-output);
	}

	public void setPunch(boolean punched) {
		this.intakePunch.set(punched);
	}

	public void setWrist(boolean up) {
		this.wristDown.set(up);
		this.wristUp.set(!up);
	}

	public void setFingers(boolean out) {
		this.fingersIn.set(!out);
		this.fingersOut.set(out);
	}


	public void configureLiftUpPID(PIDConstants constants) {
		this.lift1.selectProfileSlot(0, 0);
		this.lift1.config_kP(0, constants.p, 20);
		this.lift1.config_kI(0, constants.i, 20);
		this.lift1.config_kD(0, constants.d, 20);
		this.lift1.configAllowableClosedloopError(0, (int) constants.eps, 20);
		this.lift1.config_IntegralZone(0, 4096, 20);
		this.lift1.config_kF(0, constants.ff, 20);
		this.lift1.config_IntegralZone(0, 10000, 20);
	}

	public void configureLiftDownPID(PIDConstants constants) {
		this.lift1.selectProfileSlot(1, 0);
		this.lift1.config_kP(1, constants.p, 20); 
		this.lift1.config_kI(1, constants.i, 20);
		this.lift1.config_kD(1, constants.d, 20);
		this.lift1.configAllowableClosedloopError(1, (int) constants.eps, 20);
		this.lift1.config_IntegralZone(1, 6000, 20);
		this.lift1.config_kF(1, constants.ff, 20);
	}

	public void setLiftProfileSlot(int profileSlot) {
		this.lift1.selectProfileSlot(profileSlot, 0);
	}

	public void setLiftProfileSlot(int profileSlot, int pidIdx) {
		this.lift1.selectProfileSlot(profileSlot, pidIdx);
	}

	public void setLift(double output) {
		this.lift1.set(ControlMode.PercentOutput, output);
	}

	public void setLiftHeight(double desiredHeightFeet) {
		this.lift1.set(ControlMode.Position, desiredHeightFeet * RobotConstants.ELEVATOR_TICKS_PER_FOOT,
				DemandType.ArbitraryFeedForward, 0.07);
	}

	public double getLiftEnc() {
		return this.lift1.getSelectedSensorPosition(0);
	}

	public double getLiftFeet() {
		return this.lift1.getSelectedSensorPosition(0) / RobotConstants.ELEVATOR_TICKS_PER_FOOT;
	}

	public void setClimberClosedLoopRamp(double rampRateSecondsToFull, int timeoutMS) {
		this.climbRampRate = rampRateSecondsToFull;
		this.climbMotor1.configClosedloopRamp(rampRateSecondsToFull, timeoutMS);
	}

	public void setClimberRampRate(double rampRateSecondsToFull, int timeoutMS) {
		this.climbRampRate = rampRateSecondsToFull;
		this.climbMotor1.configOpenloopRamp(rampRateSecondsToFull, timeoutMS);
	}

	public double getClimbRampRate() {
		return this.climbRampRate;
	}

	public void setClimberProfileSlot(int profileSlot) {
		this.climbMotor1.selectProfileSlot(profileSlot, 0);
	}

	public void setClimberProfileSlot(int profileSlot, int pidIdx) {
		this.climbMotor1.selectProfileSlot(profileSlot, pidIdx);
	}

	public void setClimber(double output) {
		this.climbMotor1.set(ControlMode.PercentOutput, output);
	}

	public double getClimberVelocity() {
		return this.climbMotor1.getSelectedSensorVelocity();
	}

	public double getClimberAngularVelocity() {
		return this.getClimberVelocity() / RobotConstants.CLIMBER_TICKS_PER_DEGREE * 10;
	}

	public void resetElevatorEncoder(){
		this.lift1.setSelectedSensorPosition(0, 0, 0);
	}

	public void resetEncoders() {
		this.climbMotor1.setSelectedSensorPosition(0, 0, 0);
		this.lift1.setSelectedSensorPosition(0, 0, 0);
	}

	public double getClimberEnc() {
		return this.climbMotor1.getSelectedSensorPosition(0);
	}

	public double getClimberAngle() {
		return (this.getClimberEnc() / RobotConstants.CLIMBER_TICKS_PER_DEGREE) + RobotConstants.CLIMBER_ANGLE_OFFSET;
	}

	public void setClimber(ControlMode controlMode, double desiredVal) {
		this.climbMotor1.set(controlMode, desiredVal);
	}

	public double getClimberOutput() {
		return this.climbMotor1.getMotorOutputPercent();
	}

	public void setCompressor(boolean on) {
		if (on) {
			this.compressor.start();
		} else {
			this.compressor.stop();
		}
	}

	public boolean getCompressorState() {
		return this.compressor.enabled();
	}

	public void stopAll() {
		setDriveLeft(0);
		setDriveRight(0);
		setCargoIntake(0);
		setClimber(0);
		// shut off things here
	}

	public void setLEDStrip(double pattern) {
		ledStrip.set(pattern);
	}

	public double getLEDStrip() {
		return this.ledStrip.get();
	}

}
