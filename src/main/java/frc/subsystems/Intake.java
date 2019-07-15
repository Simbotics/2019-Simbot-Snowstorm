package frc.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.subsystems.LEDStrip.LEDColourState;

public class Intake extends Subsystem {

    public static Intake instance;

    public enum IntakeState {
        STARTING_CONFIG, HAS_BALL, HAS_PANEL, GETTING_BALL, WAS_INTAKING, OUTTAKE_BALL, OUTTAKE_BALL_DOWN,
        STARTING_TO_GET_CARGO, OUTTAKE_PANEL, STARTING_TO_GET_CARGO_HP, GETTING_PANEL, FLOATING, LIFTING_PANEL,
        GETTING_BALL_HP, HAS_PANEL_NO_WRIST, CLIMB_DONE
    }

    private IntakeState currentState = IntakeState.STARTING_CONFIG;

    private RobotOutput robotOut;
    private SensorInput sensorIn;
    private double cargoOutput;
    private LEDColourState desiredLEDState = LEDColourState.OFF;

    public int cargoMotorCycles = 0;
    public int punchWaitCycles = 0;
    public int startingCargoCycles = 0;

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        return instance;
    }

    public Intake() {
        this.robotOut = RobotOutput.getInstance();
        this.sensorIn = SensorInput.getInstance();
        this.firstCycle();
    }

    @Override
    public void firstCycle() {

    }

    public void setCargoOutput(double output) {

        this.cargoOutput = output;
    }

    public boolean doesIntakeHaveCargo() {
        if (this.currentState != IntakeState.STARTING_TO_GET_CARGO
                && this.currentState != IntakeState.STARTING_TO_GET_CARGO_HP) {

            if (this.sensorIn.getCurrent(10) > 22) {
                cargoMotorCycles++;
            } else {
                cargoMotorCycles = 0;
            }

            return cargoMotorCycles >= 8;
        } else {
            return false;
        }

    }

    @Override
    public void calculate() {

        SmartDashboard.putString("Current Intake State", this.currentState.toString());

        switch (currentState) {
        case GETTING_BALL:
            this.robotOut.setWrist(false);
            this.robotOut.setCargoIntake(this.cargoOutput);
            this.desiredLEDState = LEDColourState.GETTING_BALL;
            break;
        case STARTING_TO_GET_CARGO:
            this.robotOut.setWrist(false);
            this.robotOut.setCargoIntake(this.cargoOutput);
            this.desiredLEDState = LEDColourState.GETTING_BALL;
            this.startingCargoCycles++;
            break;
        case STARTING_TO_GET_CARGO_HP:
            this.robotOut.setWrist(true);
            this.robotOut.setCargoIntake(this.cargoOutput);
            this.desiredLEDState = LEDColourState.GETTING_BALL;
            this.startingCargoCycles++;
            break;
        case GETTING_BALL_HP:
            this.robotOut.setWrist(true);
            this.robotOut.setPunch(false);
            this.robotOut.setFingers(true);
            this.desiredLEDState = LEDColourState.GETTING_BALL;
            this.robotOut.setCargoIntake(this.cargoOutput);
            break;
        case OUTTAKE_BALL:
            this.robotOut.setWrist(true);
            this.robotOut.setCargoIntake(this.cargoOutput);
            this.desiredLEDState = LEDColourState.OFF;
            break;
        case OUTTAKE_BALL_DOWN:
            this.robotOut.setWrist(false);
            this.desiredLEDState = LEDColourState.OFF;
            this.robotOut.setCargoIntake(this.cargoOutput);
            break;
        case WAS_INTAKING:
            this.robotOut.setWrist(false);
            this.robotOut.setFingers(true);
            this.desiredLEDState = LEDColourState.OFF;
            this.robotOut.setCargoIntake(this.cargoOutput);
            this.robotOut.setPunch(false);
            break;
        case HAS_BALL:
            this.robotOut.setWrist(true);
            this.robotOut.setCargoIntake(0.15);
            this.desiredLEDState = LEDColourState.HAS_BALL;
            break;
        case GETTING_PANEL:
            this.robotOut.setWrist(false);
            this.robotOut.setFingers(false);
            this.robotOut.setPunch(false);
            break;
        case OUTTAKE_PANEL:
            this.robotOut.setFingers(false);
            if (this.punchWaitCycles > 15) {
                this.robotOut.setPunch(false);
            } else {
                this.robotOut.setPunch(true);
            }
            this.punchWaitCycles++;
            break;
        case HAS_PANEL_NO_WRIST:
            this.robotOut.setPunch(false);
            this.robotOut.setFingers(true);
            this.robotOut.setWrist(false);
            this.desiredLEDState = LEDColourState.HAS_PANEL;
            break;
        case HAS_PANEL:
            this.robotOut.setPunch(false);
            this.robotOut.setFingers(true);
            this.robotOut.setWrist(true);
            this.desiredLEDState = LEDColourState.HAS_PANEL;
            break;

        case LIFTING_PANEL:
            this.robotOut.setFingers(true);
            this.robotOut.setWrist(false);
            this.desiredLEDState = LEDColourState.HAS_PANEL;
            break;

        case STARTING_CONFIG:
            this.robotOut.setWrist(true);
            this.robotOut.setFingers(true);
            this.robotOut.setPunch(false);
            this.desiredLEDState = LEDColourState.OFF;
            this.robotOut.setCargoIntake(0);
            break;

        case CLIMB_DONE:
            this.robotOut.setWrist(false);
            this.robotOut.setFingers(true);
            this.robotOut.setPunch(false);
            this.robotOut.setCargoIntake(0);
            break;

        case FLOATING:
            this.robotOut.setCargoIntake(this.cargoOutput);
            this.robotOut.setPunch(false);
            break;

        default:
            break;

        }
        if (this.currentState != IntakeState.OUTTAKE_PANEL) {
            this.punchWaitCycles = 0;
        } 

        if (this.currentState != IntakeState.STARTING_TO_GET_CARGO
                && this.currentState != IntakeState.STARTING_TO_GET_CARGO_HP) {
            this.startingCargoCycles = 0;
        } else if (this.startingCargoCycles > 30) {
            if (this.currentState == IntakeState.STARTING_TO_GET_CARGO) {
                this.currentState = IntakeState.GETTING_BALL;
            } else {
                this.currentState = IntakeState.GETTING_BALL_HP;
            }

        }

    }

    public IntakeState getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(IntakeState state) {
        this.currentState = state;
    }

   
    public boolean outtakePanelDone() {
        return this.punchWaitCycles > 16;
    }

    public LEDColourState getDesiredLedState() {
        return this.desiredLEDState;
    }

    @Override
    public void disable() {
        this.robotOut.setCargoIntake(0);
    }

}
