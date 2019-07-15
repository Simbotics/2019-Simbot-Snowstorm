package frc.auton.util;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.subsystems.Climber;
import frc.subsystems.Drive;
import frc.subsystems.Intake;
import frc.subsystems.Lift;

public class RunFirstCycle extends AutonCommand {

    private Drive drive;
    private Lift lift;
    private Climber climber;
    private Intake intake;

    public RunFirstCycle() {
        super(RobotComponent.UTIL);
        this.drive = Drive.getInstance();
        this.lift = Lift.getInstance();
        this.climber = Climber.getInstance();
        this.intake = Intake.getInstance();

    }

    @Override
    public void firstCycle() {
        this.drive.firstCycle();
        this.lift.firstCycle();
        this.climber.firstCycle();
        this.intake.firstCycle();
    }

    @Override
    public boolean calculate() {
        return true;
    }

    @Override
    public void override() {

    }

}
