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
import frc.robot.RobotConstants;
import frc.subsystems.Climber.ClimberState;
import frc.subsystems.Intake.IntakeState;
import frc.subsystems.Lift.LiftState;

public class LeftLevel2FrontSideCargoShip implements AutonMode {

    @Override
    public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveSetPosition(0, 0, 180 - 90));
        ab.addCommand(new SetClimberState(ClimberState.DRIVING, 20));

        ab.addCommand(new DriveToPoint(0, 7.2, 180 - 90, 7, 7, 0.5, 15000));
        ab.addCommand(new DriveToPoint(2.0, 11.9, 180 - 90, 7, 7, 0.1, 25, 15000));
        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL_NO_WRIST));
        ab.addCommand(new ElevatorSetPosition(LiftState.GROUND_PANEL, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.GROUND_PANEL));
        ab.addCommand(new DriveToVisionTarget(7, 15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());

        ab.addCommand(new DriveToPoint(2.06, 13.23, 180 - 95, 7, 7, 0.1, 20, 15000));
        ab.addCommand(new DriveToPoint(-8.0, 4.64, 180 - -90, 14, RobotConstants.DRIVE_MAX_VELOCITY, 0.5, 10, 15000));
        ab.addCommand(new SetIntake(IntakeState.GETTING_PANEL));
        ab.addCommand(new DriveToVisionTarget(14, 15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL_NO_WRIST));
        ab.addCommand(new AutonWait(0));
        ab.addCommand(new DriveToPoint(-1.85, 23.3, 180 - -76, 1, RobotConstants.DRIVE_MAX_VELOCITY, 0.25, 15000));
        ab.addCommand(new DriveTurnToAngle(180 - -190, 2, 15000));
        ab.addCommand(new DriveToVisionTarget(15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());
        ab.addCommand(new DriveToPoint(-1.85, 22.75, 180 - -199, 1, 3, 0.05, 15000));
        ab.addCommand(new DriveToPoint(-5, 3, 180 - -80, 0, 14, 0.5, 15000));
        ab.addCommand(new SetIntake(IntakeState.STARTING_CONFIG));
        ab.addCommand(new DriveWait());

    }
}
