package frc.auton.intake;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;

public class IntakeWait extends AutonCommand {

	public IntakeWait() {
		super(RobotComponent.INTAKE);
	}

	@Override
	public boolean calculate() {
		return true;
	}

	@Override
	public void override() {
		// TODO Auto-generated method stub

	}

	@Override
	public void firstCycle() {
		// TODO Auto-generated method stub

	}

}
