package frc.io;

import frc.util.LogitechF310Gamepad;

public class DriverInput {

	private static DriverInput instance;

	private LogitechF310Gamepad driver;
	private LogitechF310Gamepad operator;

	// Creates boolean variables that stores if a certain step/mode was pressed
	private boolean autonIncreaseStepWasPressed = false;
	private boolean autonDecreaseStepWasPressed = false;

	private boolean autonIncreaseModeWasPressed = false;
	private boolean autonDecreaseModeWasPressed = false;

	private boolean autonIncreaseMode10WasPressed = false;
	private boolean autonDecreaseMode10WasPressed = false;

	private DriverInput() {
		this.driver = new LogitechF310Gamepad(0);
		this.operator = new LogitechF310Gamepad(1);
	}

	public static DriverInput getInstance() {
		if (instance == null) {
			instance = new DriverInput();
		}
		return instance;
	}

	/*****************************
	 * DRIVER CONTROLS
	 *****************************/

	// DRIVE
	public double getDriverX() {
		return this.driver.getRightX();
	}

	public double getDriverY() {
		return this.driver.getLeftY();
	}

	// CARGO INTAKE
	public double getCargoOuttakeTrigger() {
		if (this.driver.getLeftTrigger() > 0.2) {
			return this.driver.getLeftTrigger();
		} else {
			return 0;
		}
	}

	public double getCargoIntakeTrigger() {
		if (this.driver.getRightTrigger() > 0.2) {
			return this.driver.getRightTrigger();
		} else {
			return 0;
		}
	}

	public boolean getHasCargoTrigger() {
		return this.operator.getLeftTrigger() > 0.2;
	}
	

	// PANEL INTAKE
	public boolean getOuttakePanelBumper() {
		return this.driver.getLeftBumper();
	}

	public boolean getIntakePanelBumper() {
		return this.driver.getRightBumper();
	}

	public boolean getManualWristUpButton() {
		return this.operator.getRedButton();
	}

	public boolean getManualWristDownButton() {
		return this.operator.getGreenButton();
	}

	public boolean getDriverHavePanelButton() {
		return this.operator.getLeftBumper();
	}

	public boolean getOperatorWristUpButton(){
		return this.operator.getRightBumper();
	}

	public boolean getDriverTeleopButton(){
		return this.driver.getGreenButton();
	}

	// LIFT
	public boolean getGroundButton() {
		return this.operator.getGreenButton();
	}

	public boolean getCargoShipTrigger() {
		return this.operator.getRightTrigger() > 0.2;
	}

	public boolean getLowButton() {
		return this.operator.getRedButton();
	}

	public boolean getMediumButton() {
		return this.operator.getBlueButton();
	}

	public boolean getHighButton() {
		return this.operator.getYellowButton();
	}

	public double getLiftManualStick() {
		return this.operator.getLeftY();
	}

	// CLIMB
	public boolean getDriverClimberIntakeUpButton() {
		return this.driver.getGreenButton();
	}

	public boolean getDriverClimberIntakeDownButton() {
		return this.driver.getRedButton();
	}

	public double getOperatorClimbStick() {
		return this.operator.getRightY();
	}

	public boolean getClimberDefaultButton() {
		return this.operator.getRightBumper();
	}

	public boolean prepareForClimbSequence() {
		return (this.operator.getRightTrigger() > 0.7 && this.operator.getLeftTrigger() > 0.7);
	}

	public boolean getClimberAutoSequence() {
		return (this.operator.getRightTrigger() > 0.7 && this.operator.getLeftTrigger() > 0.7
				&& this.operator.getGreenButton());
	}

	public boolean getLevel2SitButton() {
		return this.operator.getPOVDown();
	}

	public boolean getLevel2UpButton() {
		return this.operator.getPOVUp();
	}
	
	public boolean getFootDownButton() {
		return this.operator.getPOVLeft();
	}

	public boolean getDriverSoftOuttakePanelButton(){
		return this.driver.getBlueButton();
	}

	// VISION
	public boolean getWeirdVisionButton() {
		return false;//this.driver.getBlueButton();
	}
	public boolean getVisionBumper() {
		return this.driver.getRightBumper();
	}

	public boolean getFindBallBumper() {
		// return this.driver.getRightBumper();
		return false;
	}

	public boolean getDriveToTargetButton() {
		return this.driver.getYellowButton();
	}

	// MISC
	public boolean getDriveLockButton() {
		return this.driver.getYellowButton();
	}

	public boolean getDriverManualButton() {
		return this.driver.getStartButton();
	}

	public boolean getDriverManualntButton() {
		return this.driver.getBackButton();
	}

	public boolean getOperatorManualButton() {
		return this.operator.getStartButton();
	}

	public boolean getOperatorManualntButton() {
		return this.operator.getBackButton();
	}

	public boolean getOperatorStopCompressorButton() {
		return this.operator.getPOVDown();
	}

	public boolean getOperatorStartCompressorButton() {
		return this.operator.getPOVUp();
	}

	// ********************************
	// AUTO SELECTION CONTROLS
	// ********************************

	public boolean getResumeAutoButton() {
		return driver.getYellowButton();
	}

	public boolean getDriverAutoOverrideButtons() {
		return this.driver.getGreenButton();
	}

	public boolean getOperatorAutoOverrideButtons() {
		return this.operator.getGreenButton();
	}

	public boolean getAutonSetDelayButton() {
		return false;//this.driver.getRightTrigger() > 0.2;
	}

	public double getAutonDelayStick() {
		return this.driver.getLeftY();
	}

	public boolean getAutonStepIncrease() {
		// only returns true on rising edge
		boolean result = this.driver.getRightBumper() && !this.autonIncreaseStepWasPressed;
		this.autonIncreaseStepWasPressed = this.driver.getRightBumper();
		return result;

	}

	public boolean getAutonStepDecrease() {
		// only returns true on rising edge
		boolean result = this.driver.getLeftBumper() && !this.autonDecreaseStepWasPressed;
		this.autonDecreaseStepWasPressed = this.driver.getLeftBumper();
		return result;

	}

	public boolean getAutonModeIncrease() {
		// only returns true on rising edge
		boolean result = this.driver.getRedButton() && !this.autonIncreaseModeWasPressed;
		this.autonIncreaseModeWasPressed = this.driver.getRedButton();
		return result;

	}

	public boolean getAutonModeDecrease() {
		// only returns true on rising edge
		boolean result = this.driver.getGreenButton() && !this.autonDecreaseModeWasPressed;
		this.autonDecreaseModeWasPressed = this.driver.getGreenButton();
		return result;

	}

	public boolean getAutonModeIncreaseBy10() {
		// only returns true on rising edge
		boolean result = this.driver.getYellowButton() && !this.autonIncreaseMode10WasPressed;
		this.autonIncreaseMode10WasPressed = this.driver.getYellowButton();
		return result;

	}

	public boolean getAutonModeDecreaseBy10() {
		// only returns true on rising edge
		boolean result = this.driver.getBlueButton() && !this.autonDecreaseMode10WasPressed;
		this.autonDecreaseMode10WasPressed = this.driver.getBlueButton();
		return result;

	}

}