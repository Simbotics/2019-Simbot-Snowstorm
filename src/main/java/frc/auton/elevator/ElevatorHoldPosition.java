package frc.auton.elevator;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.subsystems.Lift;
import frc.subsystems.Lift.LiftState;

public class ElevatorHoldPosition extends AutonCommand {

    private LiftState desiredState;
    private Lift lift;
    private RobotOutput robotOut;

    public ElevatorHoldPosition(LiftState DesiredliftState) {
        super(RobotComponent.ELEVATOR);

        this.desiredState = DesiredliftState;
        this.lift = Lift.getInstance();
        this.robotOut = RobotOutput.getInstance();
    }

    @Override
    public void firstCycle() {
        this.lift.firstCycle();
    }

    @Override
    public boolean calculate() {
        return this.lift.holdLiftPosition(this.desiredState);
    }

    @Override
    public void override() {
        this.robotOut.setLift(0);
    }

}
