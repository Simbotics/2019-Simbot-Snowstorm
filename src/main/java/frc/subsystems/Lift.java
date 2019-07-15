package frc.subsystems;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.robot.RobotConstants;

public class Lift extends Subsystem {

    private static Lift instance;

    public enum LiftState {
        HUMAN_LOAD,HUMAN_LOAD_VISION, PANEL_LOW, PANEL_MIDDLE_VISION, PANEL_MIDDLE, PANEL_HIGH, CARGO_CARGO_SHIP, CARGO_ROCKET_LOW,
        CARGO_ROCKET_MIDDLE, CARGO_ROCKET_HIGH, GROUND, FLOATING, GROUND_PANEL, AUTO_PANEL_MIDDLE
    }

    private LiftState previousLiftState = LiftState.GROUND;
    private LiftState currentState = LiftState.GROUND;
    private LiftState desiredLiftState = LiftState.GROUND;

    private RobotOutput robotOut;
    private SensorInput sensorIn;
    private double output;

    private double groundOffset = 0;

    public static Lift getInstance() {
        if (instance == null) {
            instance = new Lift();
        }
        return instance;
    }

    private Lift() {
        this.sensorIn = SensorInput.getInstance();
        this.robotOut = RobotOutput.getInstance();
        this.output = 0;
        this.firstCycle();
    }

    @Override
    public void firstCycle() {
        this.robotOut.configureLiftUpPID(RobotConstants.getLiftUpPID());
        this.robotOut.configureLiftDownPID(RobotConstants.getLiftDownPID());
    }

    public void setOutput(double output) {
        if (output > -0.1) {
            this.output = output + RobotConstants.LIFT_GRAVITY_OFFSET;
        } else {
            this.output = output;
        }
    }

    public double getLiftStateHeight(LiftState state) {
        switch (state) {
        case HUMAN_LOAD:
            return RobotConstants.HUMAN_LOADING_STATION;
        case PANEL_LOW:
            return RobotConstants.PANEL_LOW;
        case PANEL_MIDDLE:
            return RobotConstants.PANEL_MEDIUM;
        case PANEL_MIDDLE_VISION:
            return RobotConstants.PANEL_MEDIUM_VISION;
        case HUMAN_LOAD_VISION:
            return RobotConstants.PANEL_MEDIUM_VISION;
        case PANEL_HIGH:
            return RobotConstants.PANEL_HIGH;
        case CARGO_CARGO_SHIP:
            return RobotConstants.CARGO_CARGO_SHIP;
        case CARGO_ROCKET_LOW:
            return RobotConstants.CARGO_ROCKET_LOW;
        case CARGO_ROCKET_MIDDLE:
            return RobotConstants.CARGO_ROCKET_MEDIUM;
        case CARGO_ROCKET_HIGH:
            return RobotConstants.CARGO_ROCKET_HIGH;
        case GROUND:
            return RobotConstants.GROUND + this.groundOffset;
        case GROUND_PANEL:
            return RobotConstants.GROUND_PANEL;
        case AUTO_PANEL_MIDDLE:
            return RobotConstants.AUTON_PANEL_MEDIUM;
        case FLOATING:
            return this.robotOut.getLiftEnc();
        default:
            return 0;
        }
    }

    public double getLiftHeight() {
        return this.robotOut.getLiftFeet();
    }

    public void setTarget(LiftState state) {
        currentState = state;
        if (getLiftStateHeight(currentState) > this.sensorIn.getLiftHeight()) { // desired is above us
            this.robotOut.setLiftProfileSlot(0, 0);
        } else { // desired is below us
            this.robotOut.setLiftProfileSlot(1, 0);
        }
    }

    public double ticksToFeet(double ticks) {
        return ticks * RobotConstants.ELEVATOR_TICKS_PER_FOOT;
    }

    @Override
    public void calculate() {
        if (this.sensorIn.getCurrent(5) > 100 || this.sensorIn.getCurrent(6) > 100) {
           // this.currentState = LiftState.FLOATING;
        }
        this.setTarget(this.currentState);
        
        double err = this.getLiftStateHeight(this.currentState) - this.sensorIn.getLiftHeight();

        SmartDashboard.putString("Current Lift State", this.currentState.toString());
       // SmartDashboard.putNumber("lift error", err);

        if (this.currentState == LiftState.GROUND && sensorIn.getLiftHeightTicks() <= 500 && this.groundOffset < 0.1) {
            robotOut.setLift(0);
        } else {
            if (this.currentState == LiftState.FLOATING) {
                this.robotOut.setLift(this.output);
            } else {
                this.robotOut.setLiftHeight(this.getLiftStateHeight(this.currentState));
            }
        }
    }

    public boolean isLiftDone() {
        return (Math.abs(this.getLiftStateHeight(currentState) - this.getLiftStateHeight(desiredLiftState)) < 0.1);
    }

    public LiftState getCurrentLiftState() {
        return this.currentState;
    }

    public void setCurrentLiftState(LiftState liftState) {
        this.currentState = liftState;
    }

    // Autos
    public boolean setLiftPosition(LiftState desiredLiftState) {
        this.desiredLiftState = desiredLiftState;
        this.setTarget(desiredLiftState);
        this.robotOut.setLiftHeight(this.getLiftStateHeight(desiredLiftState));
        return true;
    }

    public boolean holdLiftPosition(LiftState desiredLiftState) {
        this.desiredLiftState = desiredLiftState;
        this.setTarget(desiredLiftState);
        this.robotOut.setLiftHeight(this.getLiftStateHeight(desiredLiftState));
        return false;
    }

    public void setGroundOffset(double groundOffset) {
        this.groundOffset = groundOffset;
    }

    @Override
    public void disable() {
        this.robotOut.setLift(0);
    }
}
