package frc.auton.intake;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.subsystems.Intake;
import frc.subsystems.Intake.IntakeState;

public class SetIntake extends AutonCommand {

    IntakeState desiredState;
    double cargoOutput;
    Intake intake;
    RobotOutput robotOut;

    public SetIntake(IntakeState intakeState) {
        this(intakeState, 0);
    }
    public SetIntake(IntakeState intakeState, double cargoOutput) {
        super(RobotComponent.INTAKE);

        this.desiredState = intakeState;
        this.cargoOutput = cargoOutput;

        this.intake = Intake.getInstance();
        this.robotOut = RobotOutput.getInstance();
    }

    @Override
    public void firstCycle() {
        intake.firstCycle();
    }

    @Override
    public boolean calculate() {
        this.intake.setCurrentState(this.desiredState);
        this.intake.setCargoOutput(this.cargoOutput);
        this.intake.calculate();
        if(this.desiredState == IntakeState.OUTTAKE_PANEL){
            return this.intake.outtakePanelDone();
        } else {
            return true;
        }
       
    }

    @Override
    public void override() {
        this.robotOut.setCargoIntake(0.0);
    }
}
