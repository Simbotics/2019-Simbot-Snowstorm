package frc.auton.mode.left;

import frc.auton.climber.SetClimberState;
import frc.auton.drive.DriveSetPosition;
import frc.auton.drive.DriveToPoint;
import frc.auton.drive.DriveToVisionTarget;
import frc.auton.drive.DriveTurnToAngle;
import frc.auton.drive.DriveWait;
import frc.auton.elevator.ElevatorHoldPosition;
import frc.auton.elevator.ElevatorSetPosition;
import frc.auton.intake.IntakeWait;
import frc.auton.intake.SetIntake;
import frc.auton.mode.AutonBuilder;
import frc.auton.mode.AutonMode;
import frc.auton.util.AutonWait;
import frc.subsystems.Climber.ClimberState;
import frc.subsystems.Intake.IntakeState;
import frc.subsystems.Lift.LiftState;

public class Left2LowPanelRocket implements AutonMode {

    public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveSetPosition(0, 0, 180 - 53.7));
        ab.addCommand(new SetClimberState(ClimberState.DRIVING, 20));

        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL));
        ab.addCommand(new DriveToPoint(-2.77, 4.136, 180 - 57, 5.5, 5.5, 1.0, 15000));
        ab.addCommand(new AutonWait(500));
        ab.addCommand(new ElevatorSetPosition(LiftState.GROUND_PANEL, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.GROUND_PANEL));
        ab.addCommand(new SetIntake(IntakeState.LIFTING_PANEL));
        ab.addCommand(new DriveToPoint(-6.9, 6.8, 180 - 59.45, 1, 7, 1, 15000));

        ab.addCommand(new DriveToVisionTarget(15000));

        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));

        ab.addCommand(new IntakeWait());
        ab.addCommand(new DriveToPoint(-4.0, 9.1, 180 - 27.45, 2, 8, 0.5, 15000));

        ab.addCommand(new DriveToPoint(-8.0, 0.5, 180 - -90, 0.5, 10, 0.5, 15, 15000));
        ab.addCommand(new DriveToVisionTarget(4, 30, 0.25, 15000));
        ab.addCommand(new SetIntake(IntakeState.GETTING_PANEL));
        ab.addCommand(new DriveWait());

        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL_NO_WRIST));
        ab.addCommand(new AutonWait(250));
        ab.addCommand(new DriveSetPosition(-3.2, true));

        ab.addCommand(new DriveToPoint(-5.0, 15.6, 180 - -90, 6, 12, 0.5, 15000));
        ab.addCommand(new DriveToPoint(-7.0, 18.5, 180 - -119.5, 0, 6, 0.5, 15000));
        ab.addCommand(new DriveTurnToAngle(180 - -58, 3, 1500));
        ab.addCommand(new DriveToVisionTarget(15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());

    }
}
