package frc.auton.intake;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.subsystems.Intake;
import frc.subsystems.Intake.IntakeState;

public class IntakeUntilHasCargo extends AutonCommand {

    double cargoOutput;
    Intake intake;
    RobotOutput robotOut;

    public IntakeUntilHasCargo(long timeout) {
        this(timeout, 1.0);
    }

    public IntakeUntilHasCargo(long timeout, double cargoOutput) {
        super(RobotComponent.INTAKE, timeout);

        this.cargoOutput = cargoOutput;
        this.intake = Intake.getInstance();
        this.robotOut = RobotOutput.getInstance();
    }

    @Override
    public void firstCycle() {
        this.intake.firstCycle();
        this.intake.setCargoOutput(this.cargoOutput);
    }

    @Override
    public boolean calculate() {
        if (this.intake.doesIntakeHaveCargo()) {
            this.intake.setCurrentState(IntakeState.HAS_BALL);
        } else {
            this.intake.setCurrentState(IntakeState.GETTING_BALL);
        }
        this.intake.calculate();
        return this.intake.doesIntakeHaveCargo();
    }

    @Override
    public void override() {
        this.robotOut.setCargoIntake(0);
    }
}
