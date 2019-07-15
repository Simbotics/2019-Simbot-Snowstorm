package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.teleop.TeleopControl;

public class DriveTeleopTimed extends AutonCommand {

    private TeleopControl teleopControl;

    public DriveTeleopTimed(long timeout) {
        super(RobotComponent.DRIVE, timeout);
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
