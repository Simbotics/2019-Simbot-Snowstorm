package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.teleop.TeleopControl;

public class DriveTeleopControl extends AutonCommand {

    private TeleopControl teleopControl;

    public DriveTeleopControl() {
        super(RobotComponent.DRIVE);
    }

    @Override
    public void firstCycle() {
        this.teleopControl.initialize();
    }

    @Override
    public boolean calculate() {
        this.teleopControl.runCycle();
        return false;
    }

    @Override
    public void override() {

    }

}
