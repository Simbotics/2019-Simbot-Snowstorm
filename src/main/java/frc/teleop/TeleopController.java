package frc.teleop;

import frc.imaging.SimLimelight;
import frc.imaging.SimLimelight.LimelightTargetType;
import frc.imaging.SimLimelightTarget;
import frc.imaging.SimTargetingInfo;
import frc.io.DriverInput;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.robot.RobotConstants;
import frc.subsystems.Climber;
import frc.subsystems.Climber.ClimberState;
import frc.subsystems.Drive;
import frc.subsystems.Drive.DriveState;
import frc.subsystems.Intake;
import frc.subsystems.Intake.IntakeState;
import frc.subsystems.LEDStrip;
import frc.subsystems.LEDStrip.LEDColourState;
import frc.subsystems.Lift;
import frc.subsystems.Lift.LiftState;
import frc.util.SimLib;

public class TeleopController extends TeleopComponent {

	private static TeleopController instance;
	private DriverInput driverIn;
	private SensorInput sensorIn;
	private RobotOutput robotOut;
	private Drive drive;
	private Intake intake;
	private Lift lift;
	private Climber climber;
	private LEDStrip led;
	private SimTargetingInfo targetInfo;
	private boolean turningToAngle = false;
	private boolean driverManual = false;
	private boolean operatorManual = false;
	private boolean isPressed = false;
	private SimLimelight limelight;
	private SimLimelightTarget desiredTarget;
	private double currentLiftOffset = 0;
	private int intakeFallingCycles;
	private int fingerCycles;
	private boolean wasVisionPressed = false;
	private LEDColourState desiredLedState = LEDColourState.OFF;

	private boolean gettingPanelFlag = false;

	public static TeleopController getInstance() {
		if (instance == null) {
			instance = new TeleopController();
		}
		return instance;
	}

	private TeleopController() {
		this.drive = Drive.getInstance();
		this.intake = Intake.getInstance();
		this.lift = Lift.getInstance();
		this.climber = Climber.getInstance();
		this.led = LEDStrip.getInstance();
		this.sensorIn = SensorInput.getInstance();
		this.driverIn = DriverInput.getInstance();
		this.robotOut = RobotOutput.getInstance();
		this.targetInfo = new SimTargetingInfo();
		this.limelight = SimLimelight.getInstance();
	}

	@Override
	public void firstCycle() {
		// TODO Auto-generated method stub
		this.drive.firstCycle();
		this.intake.firstCycle();
		this.lift.firstCycle();
		this.climber.firstCycle();

		intakeFallingCycles = 0;
	}

	@Override
	public void calculate() {

		////////////////////////////////////
		///////// MISCELLANEOUS CODE ///////
		////////////////////////////////////

		if (this.driverIn.getDriverManualButton()) {
			this.driverManual = true;
		} else if (this.driverIn.getDriverManualntButton()) {
			this.driverManual = false;
		}

		if (this.driverIn.getOperatorManualButton()) {
			this.operatorManual = true;
		} else if (this.driverIn.getOperatorManualntButton()) {
			this.operatorManual = false;
		}
		if (this.climber.getCurrentState() == ClimberState.DRIVING
				|| this.climber.getCurrentState() == ClimberState.FLOATING) {
			if (this.driverIn.getOperatorStopCompressorButton()) {
				this.robotOut.setCompressor(false);
			} else if (this.driverIn.getOperatorStartCompressorButton()) {
				this.robotOut.setCompressor(true);
			}
		}

		//////////////////////////////
		///////// DRIVE CODE /////////
		//////////////////////////////

		// ΜΑNUAL
		if (driverManual) {

			double x = this.driverIn.getDriverX();
			double y = this.driverIn.getDriverY();

			if (Math.abs(x) < 0.05) {
				x = 0;
			}
			if (Math.abs(y) < 0.1) {
				y = 0;
			}

			x = SimLib.squareMaintainSign(x);
			y = SimLib.squareMaintainSign(y);

			this.drive.setOutput(y, x);

			this.drive.setState(DriveState.OUTPUT);

			this.limelight.setLimelight(LimelightTargetType.DRIVER);
			this.desiredTarget = this.limelight.getTargetInfo();

		} else {

			//////////////////////////////
			///////// CAMERA CODE ////////
			//////////////////////////////

			double x = this.driverIn.getDriverX();
			double y = this.driverIn.getDriverY();

			if (Math.abs(x) < 0.05) {
				x = 0;
			}
			if (Math.abs(y) < 0.1) {
				y = 0;
			}

			x = SimLib.squareMaintainSign(x);
			y = SimLib.squareMaintainSign(y);

			if (this.lift.getCurrentLiftState() == LiftState.PANEL_HIGH
					|| this.lift.getCurrentLiftState() == LiftState.PANEL_MIDDLE
					|| this.lift.getCurrentLiftState() == LiftState.PANEL_MIDDLE_VISION
					|| (this.lift.getCurrentLiftState() == LiftState.PANEL_LOW
							&& this.drive.getDriveState() == DriveState.TURN_TO_LIMELIGHT_TARGET)) {
				y *= 0.5;
				x *= 0.5;
			}

			if ((this.driverIn.prepareForClimbSequence() && Math.abs(x) < 0.05 && Math.abs(y) < 0.05
					&& this.climber.getCurrentState() != ClimberState.RETRACTING)
					// || this.climber.getCurrentState() == ClimberState.WAITING_ON_PLATFORM
					|| this.climber.getCurrentState() == ClimberState.DRIVING_ON_PLATFORM) {
				x = 0;
				y = 0.15;
				robotOut.setCompressor(false);

			} else if ((this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_LOW
					|| this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_MIDDLE
					|| this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_HIGH)
					&& this.intake.getCurrentState() == IntakeState.OUTTAKE_BALL) {
				y += 0.08;
			} else if (this.intake.getCurrentState() == IntakeState.OUTTAKE_PANEL && !this.intake.outtakePanelDone()) {
				y += 0.05;
			}

			if (this.climber.getCurrentState() != ClimberState.FLOATING
					&& this.climber.getCurrentState() != ClimberState.DRIVING) {
				x *= 0.3;
				y *= 0.3;
			}

			this.drive.setOutput(y, x);
			this.drive.setyOutput(y);
			this.drive.setxOutput(x);

			if (((this.lift.getCurrentLiftState() == LiftState.GROUND_PANEL
					|| this.lift.getCurrentLiftState() == LiftState.FLOATING)
					&& this.intake.getCurrentState() == IntakeState.GETTING_PANEL)) {
				this.intakeFallingCycles++;
			} else if(this.intake.getCurrentState() == IntakeState.WAS_INTAKING) {
				this.intakeFallingCycles = 100;
			} else {
				this.intakeFallingCycles = 0;
			}

			if (this.driverIn.getWeirdVisionButton()
					&& (this.intake.getCurrentState() == IntakeState.GETTING_PANEL
							|| (this.lift.getCurrentLiftState() != LiftState.GROUND))
					&& (this.lift.getCurrentLiftState() != LiftState.GROUND_PANEL || this.intakeFallingCycles > 50)) {
				this.limelight.setLimelight(LimelightTargetType.PANEL_TARGET);
				this.drive.setState(DriveState.TURN_TO_LIMELIGHT_TARGET);
			} else if (this.climber.getCurrentState() == ClimberState.WAITING_ON_PLATFORM
					|| this.climber.getCurrentState() == ClimberState.RETRACTING) {
				this.drive.setState(DriveState.OUTPUT);
				this.drive.setOutput(0.1, 0);
			} else if (this.driverIn.getVisionBumper() && this.drive.getDriveState() != DriveState.FINISHING_VISION
					&& !this.wasVisionPressed && this.lift.getCurrentLiftState() != LiftState.HUMAN_LOAD
					&& (this.intake.getCurrentState() == IntakeState.GETTING_PANEL
							|| this.lift.getCurrentLiftState() != LiftState.GROUND)
					&& (this.lift.getCurrentLiftState() != LiftState.GROUND_PANEL || this.intakeFallingCycles > 50)) {

				if (this.lift.getCurrentLiftState() == LiftState.PANEL_HIGH
						|| this.lift.getCurrentLiftState() == LiftState.HUMAN_LOAD_VISION) {
					if (this.sensorIn.getLiftHeight() > 3.90) {
						this.drive.setState(DriveState.TURN_TO_LIMELIGHT_TARGET);
						this.limelight.setLimelight(LimelightTargetType.PANEL_TARGET);
						this.wasVisionPressed = true;
					}
				} else {
					if (this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_HIGH
							|| this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_LOW
							|| this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_MIDDLE) {
						this.limelight.setLimelight(LimelightTargetType.BALL_TARGET);
					} else {
						this.limelight.setLimelight(LimelightTargetType.PANEL_TARGET);
					}

					if (this.intake.getCurrentState() == IntakeState.GETTING_PANEL || this.intake.getCurrentState() == IntakeState.WAS_INTAKING) {
						this.drive.setState(DriveState.DRIVE_TO_LIMELIGHT_TARGET);
						// this.drive.setState(DriveState.TURN_TO_LIMELIGHT_TARGET);
					} else {
						this.drive.setState(DriveState.TURN_TO_LIMELIGHT_TARGET);
					}
					this.wasVisionPressed = true;
				}

			} else if (this.drive.getDriveState() != DriveState.FINISHING_VISION && !this.wasVisionPressed) {
				this.limelight.setLimelight(LimelightTargetType.DRIVER);
				this.drive.setState(DriveState.OUTPUT);
			}

			if (!this.driverIn.getVisionBumper()) {
				this.wasVisionPressed = false;

				if (this.drive.getDriveState() == DriveState.FINISHING_VISION) {
					this.drive.setState(DriveState.OUTPUT);
				}
			}

			if (this.drive.getDriveState() == DriveState.FINISHING_VISION && this.driverIn.getVisionBumper()
					&& (this.lift.getCurrentLiftState() == LiftState.GROUND
							|| this.lift.getCurrentLiftState() == LiftState.GROUND_PANEL
							|| this.lift.getCurrentLiftState() == LiftState.FLOATING)) {
				this.intake.setCurrentState(IntakeState.HAS_PANEL_NO_WRIST);
			}

			this.desiredTarget = this.limelight.getTargetInfo();

		}

		if (this.lift.getCurrentLiftState() == LiftState.HUMAN_LOAD) {
			this.drive.setState(DriveState.OUTPUT);
			this.limelight.setLimelight(LimelightTargetType.DRIVER);
		}

		////////////////////////////////////
		///////// CARGO INTAKE CODE ////////
		////////////////////////////////////

		if (this.driverIn.getCargoIntakeTrigger() > 0.2) {
			if (this.intake.getCurrentState() != IntakeState.HAS_BALL) {
				if (this.lift.getCurrentLiftState() == LiftState.HUMAN_LOAD) {
					if (this.intake.getCurrentState() != IntakeState.GETTING_BALL_HP) {
						this.intake.setCurrentState(IntakeState.STARTING_TO_GET_CARGO_HP);
					}
				} else if (this.lift.getLiftHeight() >= this.lift.getLiftStateHeight(LiftState.CARGO_ROCKET_LOW)) {
					this.intake.setCurrentState(IntakeState.STARTING_CONFIG);
				} else {
					if (this.intake.getCurrentState() != IntakeState.GETTING_BALL) {
						this.intake.setCurrentState(IntakeState.STARTING_TO_GET_CARGO);
					}

				}

				this.intake.setCargoOutput(this.driverIn.getCargoIntakeTrigger());
			}
		} else if (this.driverIn.getCargoOuttakeTrigger() > 0.2) {
			if (this.lift.getCurrentLiftState() != LiftState.GROUND) {
				this.intake.setCurrentState(IntakeState.OUTTAKE_BALL);
			} else if (this.lift.getLiftHeight() >= this.lift.getLiftStateHeight(LiftState.CARGO_ROCKET_LOW)) {
				this.intake.setCurrentState(IntakeState.OUTTAKE_BALL); // wait until elevator is actually down before
																		// moving wrist
			} else {
				this.intake.setCurrentState(IntakeState.OUTTAKE_BALL_DOWN);
			}

			if (this.lift.getCurrentLiftState() == LiftState.CARGO_CARGO_SHIP) {
				this.intake.setCargoOutput(-this.driverIn.getCargoOuttakeTrigger() / 3.0);
			} else {
				this.intake.setCargoOutput(-this.driverIn.getCargoOuttakeTrigger());
			}

		} else {
			if (this.intake.getCurrentState() == IntakeState.GETTING_BALL
					|| this.intake.getCurrentState() == IntakeState.OUTTAKE_BALL) {
				if (this.lift.getCurrentLiftState() == LiftState.GROUND) {
					this.intake.setCurrentState(IntakeState.WAS_INTAKING);
				} else {
					this.intake.setCurrentState(IntakeState.STARTING_CONFIG);
				}

			}
			this.intake.setCargoOutput(0);
		}

		if ((this.intake.getCurrentState() == IntakeState.GETTING_BALL
				|| this.intake.getCurrentState() == IntakeState.GETTING_BALL_HP) && this.intake.doesIntakeHaveCargo()) {
			this.intake.setCurrentState(IntakeState.HAS_BALL);
		}

		////////////////////////////////////
		///////// PANEL INTAKE CODE ////////
		////////////////////////////////////

		if (this.driverIn.getIntakePanelBumper()
				&& (this.lift.getCurrentLiftState() == LiftState.GROUND
						|| this.lift.getCurrentLiftState() == LiftState.GROUND_PANEL
						|| this.lift.getCurrentLiftState() == LiftState.FLOATING)
				&& (this.intake.getCurrentState() != IntakeState.HAS_PANEL
						&& this.intake.getCurrentState() != IntakeState.HAS_PANEL_NO_WRIST
						&& this.intake.getCurrentState() != IntakeState.LIFTING_PANEL)) {
			this.intake.setCurrentState(IntakeState.GETTING_PANEL);
			this.gettingPanelFlag = true;
		} else if (this.driverIn.getOuttakePanelBumper()) {
			this.intake.setCurrentState(IntakeState.OUTTAKE_PANEL);
		} else if (!this.driverIn.getIntakePanelBumper()) {
			this.gettingPanelFlag = false;
		}

		if (!this.driverIn.getIntakePanelBumper() && this.intake.getCurrentState() == IntakeState.GETTING_PANEL) {
			this.intake.setCurrentState(IntakeState.HAS_PANEL_NO_WRIST);
		}

		if (this.intake.getCurrentState() == IntakeState.HAS_PANEL_NO_WRIST
				&& this.drive.getDriveState() != DriveState.FINISHING_VISION) {
			if (this.driverIn.getDriverY() < -0.1) {
				this.fingerCycles++;
			} else {
				this.fingerCycles = 0;
			}
			if (this.fingerCycles > 10) {
				this.lift.setCurrentLiftState(LiftState.GROUND);
				this.intake.setCurrentState(IntakeState.HAS_PANEL_NO_WRIST);
			}
			this.drive.setIsVisionDone(false);
		}

		if (this.intake.getCurrentState() == IntakeState.OUTTAKE_PANEL && !this.driverIn.getOuttakePanelBumper()) {
			this.intake.setCurrentState(IntakeState.WAS_INTAKING);
		}

		if (this.driverIn.getDriverHavePanelButton() && intake.getCurrentState() != IntakeState.HAS_BALL) {
			this.intake.setCurrentState(IntakeState.HAS_PANEL);
		}

		if(this.driverIn.getOperatorWristUpButton()){
			this.intake.setCurrentState(IntakeState.STARTING_CONFIG);
		}
		
		if (this.driverIn.getDriverSoftOuttakePanelButton()) {
			this.intake.setCurrentState(IntakeState.WAS_INTAKING);
			if(this.driverIn.getVisionBumper()){
				this.drive.setState(DriveState.DRIVE_TO_LIMELIGHT_TARGET);
				this.limelight.setLimelight(LimelightTargetType.PANEL_TARGET);
			}
		}

		if (this.driverIn.getHasCargoTrigger() && !this.driverIn.getCargoShipTrigger()) {
			this.intake.setCurrentState(IntakeState.HAS_BALL);
		}

		if (operatorManual) {
			this.climber.setTarget(ClimberState.FLOATING);
			this.climber.setOutput(this.driverIn.getOperatorClimbStick() - 0.06);

			this.lift.setOutput(this.driverIn.getLiftManualStick());
			this.lift.setTarget(LiftState.FLOATING);
			
			if(this.driverIn.getGroundButton()){
				this.robotOut.resetElevatorEncoder();
			}
		} else {

			////////////////////////////
			///////// LIFT CODE ////////
			////////////////////////////
			if (climber.getCurrentState() == ClimberState.CLIMB_DONE) {
				if (this.driverIn.getDriverClimberIntakeUpButton()) {
					this.intake.setCurrentState(IntakeState.STARTING_CONFIG);
				} else if (this.driverIn.getDriverClimberIntakeDownButton()) {
					this.intake.setCurrentState(IntakeState.CLIMB_DONE);
				}

			}

			if ((this.lift.getCurrentLiftState() == LiftState.GROUND 
					|| this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_LOW)
					&& this.intake.getCurrentState() == IntakeState.GETTING_PANEL) {
				this.lift.setCurrentLiftState(LiftState.GROUND_PANEL);
			}

			if((this.lift.getCurrentLiftState() == LiftState.GROUND_PANEL 
				|| this.lift.getCurrentLiftState() == LiftState.PANEL_LOW  
				|| this.lift.getCurrentLiftState() == LiftState.CARGO_ROCKET_LOW) 
				&& this.intake.getCurrentState() == IntakeState.GETTING_BALL){
					this.lift.setCurrentLiftState(LiftState.GROUND);
				}

			if ((this.driverIn.getLowButton() || this.driverIn.getMediumButton() || this.driverIn.getHighButton())
					&& (this.intake.getCurrentState() == IntakeState.HAS_PANEL
							|| this.intake.getCurrentState() == IntakeState.HAS_PANEL_NO_WRIST)) {
				this.intake.setCurrentState(IntakeState.LIFTING_PANEL);
			}

			if (this.driverIn.getGroundButton()) {
				this.lift.setTarget(LiftState.GROUND);
			} else if (this.driverIn.getLowButton()) {
				if (this.intake.getCurrentState() == IntakeState.HAS_BALL) {
					this.lift.setTarget(LiftState.CARGO_ROCKET_LOW);
				} else if (this.intake.getCurrentState() == IntakeState.HAS_PANEL
						|| this.intake.getCurrentState() == IntakeState.LIFTING_PANEL
						|| this.intake.getCurrentState() == IntakeState.HAS_PANEL_NO_WRIST) {
					this.lift.setTarget(LiftState.PANEL_LOW);
				} else {
					this.lift.setTarget(LiftState.HUMAN_LOAD_VISION);
				}
			} else if (this.driverIn.getCargoShipTrigger() && !this.driverIn.getHasCargoTrigger() 
					&& this.climber.getCurrentState() == ClimberState.DRIVING ) {
				if (this.intake.getCurrentState() == IntakeState.HAS_BALL) {
					this.lift.setTarget(LiftState.CARGO_CARGO_SHIP);
				}
			} else if (this.driverIn.getMediumButton()) {
				if (this.intake.getCurrentState() == IntakeState.HAS_BALL) {
					this.lift.setTarget(LiftState.CARGO_ROCKET_MIDDLE);
				} else {
					this.lift.setTarget(LiftState.PANEL_MIDDLE);
				}
			} else if (this.driverIn.getHighButton()) {
				if (this.intake.getCurrentState() == IntakeState.HAS_BALL) {
					this.lift.setTarget(LiftState.CARGO_ROCKET_HIGH);
				} else {
					this.lift.setTarget(LiftState.PANEL_HIGH);
				}
			} else if (this.driverIn.getLiftManualStick() < -0.2
					&& this.lift.getCurrentLiftState() == LiftState.HUMAN_LOAD_VISION) {
				this.lift.setTarget(LiftState.HUMAN_LOAD);
			} else if (this.driverIn.getLiftManualStick() > -0.2
					&& this.lift.getCurrentLiftState() == LiftState.HUMAN_LOAD
					&& this.intake.getCurrentState() != IntakeState.HAS_BALL) {
				this.lift.setTarget(LiftState.HUMAN_LOAD_VISION);
			} else if (this.intake.getCurrentState() == IntakeState.WAS_INTAKING
					&& this.lift.getCurrentLiftState() == LiftState.PANEL_LOW) {
				this.lift.setCurrentLiftState(LiftState.GROUND);
			} else if (this.intake.getCurrentState() == IntakeState.HAS_PANEL_NO_WRIST
					&& this.lift.getCurrentLiftState() == LiftState.GROUND && this.driverIn.getIntakePanelBumper()
					&& !this.gettingPanelFlag) {
				this.lift.setCurrentLiftState(LiftState.PANEL_LOW);
				this.intake.setCurrentState(IntakeState.LIFTING_PANEL);
			}

			if (this.lift.getCurrentLiftState() != LiftState.GROUND) { // not in ground
				this.currentLiftOffset = 0;
			} else {
				this.currentLiftOffset = this.driverIn.getLiftManualStick() * 1.0;
				if (this.currentLiftOffset < 0) {
					this.currentLiftOffset = 0;
				}
			}
			this.lift.setGroundOffset(this.currentLiftOffset);

			////////////////////////////////
			///////// CLIMBER CODE /////////
			////////////////////////////////

			if (this.climber.getCurrentState() == ClimberState.FLOATING && !this.operatorManual) {
				this.climber.setTarget(ClimberState.DRIVING);
			}

			if (this.driverIn.getClimberAutoSequence()) {
				this.intake.setCurrentState(IntakeState.STARTING_CONFIG);
				this.climber.setTarget(ClimberState.PREPARE_CLIMB);
				if (this.lift.getCurrentLiftState() != LiftState.FLOATING) {
					this.lift.setCurrentLiftState(LiftState.GROUND);
				}

			}

			if(this.climber.getCurrentState() != ClimberState.FLOATING 
			&& this.climber.getCurrentState() != ClimberState.DRIVING) {
				this.lift.setCurrentLiftState(LiftState.GROUND);
			}

			if (this.climber.getCurrentState() == ClimberState.PREPARE_CLIMB
					&& this.robotOut.getClimberAngle() > RobotConstants.PREPARE_CLIMB) {
				this.climber.setTarget(ClimberState.CLIMBING);
			}

			if (this.climber.getCurrentState() == ClimberState.CLIMBING
					&& this.robotOut.getClimberAngle() > RobotConstants.CLIMB_RETRACT) {
				this.climber.setTarget(ClimberState.WAITING_ON_PLATFORM);
			}

			if (this.climber.getCurrentState() == ClimberState.RETRACTING
					&& this.robotOut.getClimberAngle() > RobotConstants.CLIMB_END) {
				this.climber.setTarget(ClimberState.DRIVING_ON_PLATFORM);
			}

			if (this.climber.getCurrentState() == ClimberState.WAITING_ON_PLATFORM) {
				// this.intake.setCurrentState(IntakeState.CLIMB_DONE);
			}

			if (this.climber.getCurrentState() != ClimberState.DRIVING
					&& this.climber.getCurrentState() != ClimberState.FLOATING) {
				if (this.driverIn.getLevel2SitButton()) {
					this.climber.setTarget(ClimberState.LEVEL_2);
				} else if (this.driverIn.getLevel2UpButton()
						&& this.climber.getCurrentState() == ClimberState.BACK_DOWN) {
					this.climber.setTarget(ClimberState.RETRACTING);
				} else if (this.driverIn.getLevel2UpButton()
						&& this.climber.getCurrentState() == ClimberState.LEVEL_2) {
					this.climber.setTarget(ClimberState.LEVEL_2_UP);
				}
			}

			if (this.climber.getCurrentState() != ClimberState.DRIVING
					&& this.climber.getCurrentState() != ClimberState.FLOATING) {
				if (this.driverIn.getFootDownButton()) {
					this.climber.setTarget(ClimberState.BACK_DOWN);
				}
			}
		}

		////////////////////////////
		///////// LED CODE /////////
		////////////////////////////

		if (this.climber.getCurrentState() == ClimberState.DRIVING) { // Don't use vision for led control
			if (this.drive.getDriveState() == DriveState.OUTPUT) { // dont use climb for led control
				this.desiredLedState = this.intake.getDesiredLedState();
			} else {
				this.desiredLedState = this.drive.getDesiredLedState();
			}
		} else {
			this.desiredLedState = this.climber.getDesiredLedState();
		}

		this.led.setLed(this.desiredLedState);

		this.drive.calculate();
		this.intake.calculate();
		this.climber.calculate();
		this.lift.calculate();

	}

	@Override
	public void disable() {
		this.drive.disable();
		this.intake.disable();
		this.lift.disable();
		this.climber.disable();
	}
}
