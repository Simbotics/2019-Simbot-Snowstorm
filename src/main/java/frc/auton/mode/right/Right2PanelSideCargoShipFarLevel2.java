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

public class Right2PanelSideCargoShipFarLevel2 implements AutonMode {

    @Override
    public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveSetPosition(0, 1.1, 270));
        ab.addCommand(new SetClimberState(ClimberState.DRIVING, 20));
        
        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL));
        ab.addCommand(new DriveToPoint(0.0, 5.75, 270, 9, 9, 1.0, 15000));
        ab.addCommand(new DriveToPoint(2, 14.7, 265, RobotConstants.DRIVE_MAX_VELOCITY, RobotConstants.DRIVE_MAX_VELOCITY, 1, 15000));
        ab.addCommand(new DriveToPoint(4, 23.8, 205, 1, RobotConstants.DRIVE_MAX_VELOCITY, 0.25, 8, 65, 15000));
        ab.addCommand(new SetIntake(IntakeState.LIFTING_PANEL));
        ab.addCommand(new ElevatorSetPosition(LiftState.PANEL_LOW, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.PANEL_LOW));
        ab.addCommand(new DriveTurnToAngle(180, 5, 1500));
        ab.addCommand(new DriveToVisionTarget(14, 10, 0.1,true, 15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));

        ab.addCommand(new IntakeWait());

        ab.addCommand(new DriveToPoint(5.0, 22.0, 180, 3, 14, 1.0, 15000));
        ab.addCommand(new DriveToPoint(4.2, 13.6, 280, RobotConstants.DRIVE_MAX_VELOCITY, RobotConstants.DRIVE_MAX_VELOCITY, 1.0, 15000));
        ab.addCommand(new AutonOverride(RobotComponent.ELEVATOR));
        ab.addCommand(new ElevatorSetPosition(LiftState.GROUND_PANEL, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.GROUND_PANEL));
        ab.addCommand(new DriveToPoint(7.2, 6.3, 280, 14, RobotConstants.DRIVE_MAX_VELOCITY, 1.0,9, 15000));
        ab.addCommand(new DriveToVisionTarget(14, 15000));
        ab.addCommand(new SetIntake(IntakeState.GETTING_PANEL));
        ab.addCommand(new DriveWait());

        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL_NO_WRIST));
        ab.addCommand(new AutonWait(0));

        ab.addCommand(new DriveToPoint(3.3, 21.1, 280, 0, RobotConstants.DRIVE_MAX_VELOCITY, 0.1, 15000));
        ab.addCommand(new DriveTurnToAngle(185, 2, 1500));
        ab.addCommand(new AutonOverride(RobotComponent.ELEVATOR));
        ab.addCommand(new ElevatorSetPosition(LiftState.PANEL_LOW, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.PANEL_LOW));
        ab.addCommand(new DriveToVisionTarget(7,true, 15000));
        ab.addCommand(new SetIntake(IntakeState.LIFTING_PANEL));
        ab.addCommand(new DriveWait());
        ab.addCommand(new DriveSetOutput(0.2, 0));
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());
        ab.addCommand(new DriveSetOutput(-0.3, 0));
        ab.addCommand(new AutonWait(400));
        ab.addCommand(new DriveSetOutput(0, 0));
        ab.addCommand(new AutonWait(20));

	}
}
