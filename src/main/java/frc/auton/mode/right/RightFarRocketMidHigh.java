package frc.auton.mode.right;

import frc.auton.AutonOverride;
import frc.auton.RobotComponent;
import frc.auton.climber.SetClimberState;
import frc.auton.drive.DriveSetOutput;
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

public class RightFarRocketMidHigh implements AutonMode {

    @Override
    public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveSetPosition(0, 0, 270));
        ab.addCommand(new SetClimberState(ClimberState.DRIVING, 20));

        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL));
        ab.addCommand(new DriveToPoint(0, 3.35, 270, 9, 9, 0.5, 15000));
        ab.addCommand(new DriveToPoint(6.5, 19.5, 226, 1, RobotConstants.DRIVE_MAX_VELOCITY, 0.3, 15000));
        ab.addCommand(new ElevatorSetPosition(LiftState.PANEL_MIDDLE, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.PANEL_MIDDLE));
        ab.addCommand(new DriveTurnToAngle(300, 5, 1000));

        ab.addCommand(new SetIntake(IntakeState.LIFTING_PANEL));

        ab.addCommand(new DriveToVisionTarget(15000));
        ab.addCommand(new DriveWait());

        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());
        // ab.addCommand(new AutonWait(100));

        ab.addCommand(new DriveToPoint(6.44, 19.8, 300, 1, 8, 0.5, 15000));
        ab.addCommand(new AutonWait(100));
        ab.addCommand(new AutonOverride(RobotComponent.ELEVATOR));
        ab.addCommand(new ElevatorSetPosition(LiftState.GROUND_PANEL, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.GROUND_PANEL));
        ab.addCommand(new SetIntake(IntakeState.GETTING_PANEL));

        ab.addCommand(new DriveToPoint(5.0, 12.14, 275, RobotConstants.DRIVE_MAX_VELOCITY, RobotConstants.DRIVE_MAX_VELOCITY, 0.5, 12, 15000));
        ab.addCommand(new DriveToPoint(7.6, 3.2, 280, RobotConstants.DRIVE_MAX_VELOCITY, RobotConstants.DRIVE_MAX_VELOCITY, 0.5, 15000));
        ab.addCommand(new DriveToVisionTarget(14, 15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL_NO_WRIST));
        ab.addCommand(new AutonWait(0));

        ab.addCommand(new DriveToPoint(5.4, 14.5, 260, 14, RobotConstants.DRIVE_MAX_VELOCITY, 0.5, 15000));
        ab.addCommand(new DriveToPoint(8.0, 18.75, 233, 7, 14, 0.4, 15000));
        ab.addCommand(new AutonOverride(RobotComponent.ELEVATOR));
        ab.addCommand(new SetIntake(IntakeState.LIFTING_PANEL));
        ab.addCommand(new DriveTurnToAngle(300, 4, 1000));
        ab.addCommand(new ElevatorSetPosition(LiftState.PANEL_HIGH, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.PANEL_HIGH));
        ab.addCommand(new DriveToVisionTarget(15000));
        ab.addCommand(new DriveWait());

        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new DriveSetOutput(0.07, 0));
        ab.addCommand(new IntakeWait());
        ab.addCommand(new DriveSetOutput(-0.2, 0));
        ab.addCommand(new AutonWait(300));
        ab.addCommand(new AutonOverride(RobotComponent.ELEVATOR));
        ab.addCommand(new ElevatorSetPosition(LiftState.GROUND, 2000));
        ab.addCommand(new AutonWait(300));
        ab.addCommand(new DriveSetOutput(0, 0));
        ab.addCommand(new ElevatorHoldPosition(LiftState.GROUND));

    }
}
