package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.DriverInput;
import frc.teleop.TeleopControl;

public class DriveTeleopControlUntilButton extends AutonCommand {

    private TeleopControl teleopControl;
    private DriverInput driverIn;

    public DriveTeleopControlUntilButton() {
        super(RobotComponent.DRIVE);
    }

    @Override
    public void firstCycle() {
        this.teleopControl.initialize();
    }

    @Override
    public boolean calculate() {
        this.teleopControl.runCycle();
        return driverIn.getResumeAutoButton();
    }

    @Override
    public void override() {

    }
}
