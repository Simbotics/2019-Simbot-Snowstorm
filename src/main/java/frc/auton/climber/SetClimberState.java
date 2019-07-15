package frc.auton.climber;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.subsystems.Climber;
import frc.subsystems.Climber.ClimberState;

public class SetClimberState extends AutonCommand {
    ClimberState climberState = ClimberState.DRIVING;
    Climber climber;
    RobotOutput robotOut;

    public SetClimberState(ClimberState climberState, long timeout) {
        super(RobotComponent.CLIMBER, timeout);
        this.climberState = climberState;
    }
    @Override
    public void firstCycle() {
        this.robotOut = RobotOutput.getInstance();
        this.climber = Climber.getInstance();

        this.climber.setTarget(this.climberState);
    }

    @Override
    public boolean calculate() {
        this.climber.calculate();
        return true;
    }

    @Override
    public void override() {
        this.robotOut.setClimber(0.0);
    }
}
