/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.auton.drive;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.imaging.SimLimelight;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.robot.RobotConstants;
import frc.subsystems.Drive;

/**
 * Add your docs here.
 */
public class DriveStraightToLimelightTarget extends AutonCommand {

    private Drive drive;
    private SensorInput sensorIn;
    private SimLimelight limelight;
    private RobotOutput robotOut;
    private double distance;
    private double maxOutput;

    public DriveStraightToLimelightTarget(long timeout) {
        this(5.0, timeout);
    }

    public DriveStraightToLimelightTarget(double maxOutput, long timeout) {
        super(RobotComponent.DRIVE, timeout);
        this.maxOutput = maxOutput;
        this.drive = Drive.getInstance();
        this.limelight = SimLimelight.getInstance();
        this.robotOut = RobotOutput.getInstance();
        this.sensorIn = SensorInput.getInstance();
        this.distance = this.limelight.getDistanceToPrevLimelightTarget() - 3.0;
        this.drive.firstCycle();
    }

    @Override
    public void firstCycle() {
        this.drive.getDriveStraightPID().setMinMaxOutput(0, maxOutput);
        this.drive.getDriveStraightPID().setFinishedRange(0.3);
        this.drive.getDriveStraightPID().setMinDoneCycles(1);
        this.drive.getDriveStraightPID().setDesiredValue(this.distance + this.sensorIn.getDriveFeet());

    }

    @Override
    public boolean calculate() {
        double yOutput = this.drive.getDriveStraightPID().calcPID(this.sensorIn.getDriveFeet())
                / RobotConstants.DRIVE_MAX_VELOCITY;
        this.robotOut.setDriveLeft(yOutput);
        this.robotOut.setDriveRight(yOutput);

        return this.drive.getDriveStraightPID().isDone();
    }

    @Override
    public void override() {
        this.robotOut.setDriveLeft(0);
        this.robotOut.setDriveRight(0);
    }

}
