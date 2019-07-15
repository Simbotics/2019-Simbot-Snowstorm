package frc.auton.elevator;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.subsystems.Lift;
import frc.subsystems.Lift.LiftState;

public class ElevatorSetPosition extends AutonCommand {

    private LiftState desiredState;
    private Lift lift;
    private RobotOutput robotOut;

    public ElevatorSetPosition(LiftState DesiredliftState, long timeout) {
        super(RobotComponent.ELEVATOR, timeout);

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
        lift.setLiftPosition(this.desiredState);
        return Math.abs(this.robotOut.getLiftFeet() - this.lift.getLiftStateHeight(this.desiredState)) < 0.1;
    }

    @Override
    public void override() {
        this.robotOut.setLift(0);
    }

}
