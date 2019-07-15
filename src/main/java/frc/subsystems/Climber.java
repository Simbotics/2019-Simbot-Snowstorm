package frc.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.RobotOutput;
import frc.robot.RobotConstants;
import frc.subsystems.LEDStrip.LEDColourState;

public class Climber extends Subsystem {

    public static Climber instance;

    public enum ClimberState {
        FLOATING, DRIVING, BASE_LOCK, CLIMBING, DECCELERATION, CLIMB_DONE, RETRACTING, PREPARE_CLIMB,
        WAITING_ON_PLATFORM, DRIVING_ON_PLATFORM, LEVEL_2, LEVEL_2_UP, BACK_DOWN
    }

    private ClimberState currentClimberState = ClimberState.DRIVING;
    private RobotOutput robotOut;
    private double output;
    private boolean isClimbing;
    private int waitCycles = 0;
    private int drivingCycles = 0;
    private LEDColourState desiredLedState = LEDColourState.OFF;

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber();
        }
        return instance;
    }

    public Climber() {
        this.robotOut = RobotOutput.getInstance();
        this.firstCycle();
    }

    @Override
    public void firstCycle() {
        this.setTarget(ClimberState.DRIVING);
        this.robotOut.setClimberProfileSlot(0, 0);
      

    }

    public double getClimberAngle(ClimberState state) {
        switch (state) {
        case DRIVING:
            return RobotConstants.DRIVING;
        case BASE_LOCK:
            return RobotConstants.BASE_LOCK;
        case CLIMBING:
            return RobotConstants.CLIMB_START;
        case DECCELERATION:
            return RobotConstants.CLIMB_DECCELERATE;
        case CLIMB_DONE:
            return RobotConstants.CLIMB_END;
        case PREPARE_CLIMB:
            return RobotConstants.PREPARE_CLIMB;
        case BACK_DOWN:
            return RobotConstants.CLIMB_BACK_DOWN;
        case RETRACTING:
            return RobotConstants.CLIMB_RETRACT;
        case LEVEL_2:
            return RobotConstants.LEVEL_2;
        case LEVEL_2_UP:
            return RobotConstants.LEVEL_2_UP;
        case FLOATING:
            return this.robotOut.getClimberEnc();
        default:
            return 0;
        }
    }

    public ClimberState getCurrentState() {
        return this.currentClimberState;
    }

    public ClimberState getClimberState(double angle) {

        if (angle >= RobotConstants.CLIMB_END) {
            return ClimberState.RETRACTING;
        } else if (angle >= RobotConstants.CLIMB_RETRACT) {
            return ClimberState.CLIMB_DONE;
        } else {
            return ClimberState.CLIMBING;
        }

    }

    public void enableClimber() {
        this.currentClimberState = ClimberState.CLIMBING;
    }

    public void setTarget(ClimberState state) {
        this.currentClimberState = state;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    @Override
    public void calculate() {
        SmartDashboard.putString("Current Climber State", this.currentClimberState.toString());

        switch (this.currentClimberState) {
        case DRIVING:
            this.setRampRate(0);
            this.robotOut.setClimber(-0.06);
            this.desiredLedState = LEDColourState.OFF;
            break;
        case PREPARE_CLIMB:
            this.setRampRate(0);
            this.robotOut.setClimber(0.3); // was 0.3
            this.desiredLedState = LEDColourState.CLIMBING;
        case CLIMBING:
            this.setRampRate(1.0); // was 1.0
            this.robotOut.setClimber(1.0);
            this.desiredLedState = LEDColourState.CLIMBING;
            break;
        case DECCELERATION:
            this.setRampRate(0.0);
            this.robotOut.setClimber(-0.05);
            this.desiredLedState = LEDColourState.CLIMBING;
            break;

        case WAITING_ON_PLATFORM:
            this.setRampRate(0);
            this.robotOut.setClimber(0.0);
            this.desiredLedState = LEDColourState.CLIMBING;
            waitCycles++;
            if (waitCycles > 0) {
                this.currentClimberState = ClimberState.RETRACTING;
            }
            break;
        case RETRACTING:
            this.setRampRate(0);
            this.robotOut.setClimber(1.0);
            this.desiredLedState = LEDColourState.CLIMBING;
            break;
        case BACK_DOWN:
            this.setRampRate(0);
            this.desiredLedState = LEDColourState.CLIMBING;
            if (this.robotOut.getClimberAngle() < RobotConstants.CLIMB_BACK_DOWN) {
                this.robotOut.setClimber(-0.05);
            } else {
                this.robotOut.setClimber(-0.6);
            }
            break;
        case DRIVING_ON_PLATFORM:
            this.setRampRate(0);
            this.robotOut.setClimber(0.06);
            this.desiredLedState = LEDColourState.CLIMBING;
            waitCycles++;
            if (waitCycles > 15) {
                this.currentClimberState = ClimberState.CLIMB_DONE;
            }
            break;
        case CLIMB_DONE:
            this.robotOut.setClimber(0.07);
            this.desiredLedState = LEDColourState.CLIMBING;
            break;
        case LEVEL_2:
            if (this.robotOut.getClimberAngle() < RobotConstants.LEVEL_2) {
                this.robotOut.setClimber(-0.05);
            } else {
                this.robotOut.setClimber(-0.5);
            }
            this.desiredLedState = LEDColourState.CLIMBING;
            break;
        case LEVEL_2_UP:
            if (this.robotOut.getClimberAngle() > RobotConstants.LEVEL_2_UP) {
                this.robotOut.setClimber(0.00);
            } else {
                this.robotOut.setClimber(0.2);
            }
            this.desiredLedState = LEDColourState.CLIMBING;
            break;
        case FLOATING:
            this.robotOut.setClimber(this.output);
            this.desiredLedState = LEDColourState.OFF;
            break;
        default:
            break;
        }

        if (this.currentClimberState != ClimberState.WAITING_ON_PLATFORM
                && this.currentClimberState != ClimberState.DRIVING_ON_PLATFORM) {
            this.waitCycles = 0;
        }

    }

    public LEDColourState getDesiredLedState() {
        return this.desiredLedState;
    }

    public void setRampRate(double rate) {
        this.robotOut.setClimberRampRate(rate, 40);
    }

    public void setClosedLoopRampRate(double rate) {
        this.robotOut.setClimberClosedLoopRamp(rate, 40);
    }

    @Override
    public void disable() {
        this.robotOut.setClimber(0);
    }
}
