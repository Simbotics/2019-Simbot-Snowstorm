/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.auton.mode.left;

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

/**
 * Add your docs here.
 */
public class Left2PanelSideCargoShip implements AutonMode {

    @Override
    public void addToMode(AutonBuilder ab) {
        ab.addCommand(new DriveSetPosition(0, 3, 90));
        ab.addCommand(new SetClimberState(ClimberState.DRIVING, 20));

        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL));
        ab.addCommand(new DriveToPoint(0.0, 6.25, 180 - 90, 7, 7, 1.0, 15000));
        ab.addCommand(new DriveToPoint(-4.0, 19.2, 180 - 90, 0, 12, 1.0, 15000));
        ab.addCommand(new SetIntake(IntakeState.LIFTING_PANEL));
        ab.addCommand(new ElevatorSetPosition(LiftState.GROUND_PANEL, 20));
        ab.addCommand(new ElevatorHoldPosition(LiftState.GROUND_PANEL));
        ab.addCommand(new DriveTurnToAngle(180 - 177, 2, 1500));
        ab.addCommand(new DriveToVisionTarget(5, 10, 0.1, 15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());

        ab.addCommand(new DriveToPoint(-3.0, 21.0, 180 - 207, 1, 6, 1.0, 15000));
        ab.addCommand(new DriveToPoint(-3.9, 14.1, 180 - 280, RobotConstants.DRIVE_MAX_VELOCITY,
                RobotConstants.DRIVE_MAX_VELOCITY, 1.0, 15000));
        ab.addCommand(new DriveToPoint(-7.1, 5.1, 180 - 270, 7, RobotConstants.DRIVE_MAX_VELOCITY, 1.0, 9, 15000));
        ab.addCommand(new DriveToVisionTarget(RobotConstants.DRIVE_MAX_VELOCITY, 15000));
        ab.addCommand(new SetIntake(IntakeState.GETTING_PANEL));
        ab.addCommand(new DriveWait());

        ab.addCommand(new SetIntake(IntakeState.HAS_PANEL_NO_WRIST));
        ab.addCommand(new AutonWait(150));
        ab.addCommand(new DriveSetPosition(-3.2, true));

        ab.addCommand(new DriveToPoint(-2.8, 18.5, 180 - 280, 2, RobotConstants.DRIVE_MAX_VELOCITY, 1.0, 15000));
        ab.addCommand(new DriveTurnToAngle(180 - 180, 2, 1500));
        ab.addCommand(new DriveToVisionTarget(5, 10, 0.1, 15000));
        ab.addCommand(new DriveWait());
        ab.addCommand(new SetIntake(IntakeState.OUTTAKE_PANEL));
        ab.addCommand(new IntakeWait());
        ab.addCommand(new DriveSetOutput(-0.2, 0));
        ab.addCommand(new AutonWait(600));
        ab.addCommand(new DriveSetOutput(0, 0));

    }
}
