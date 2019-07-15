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
import frc.imaging.SimLimelight.LimelightTargetType;
import frc.io.DriverInput;
import frc.io.RobotOutput;
import frc.subsystems.Drive;
import frc.subsystems.LEDStrip;
import frc.subsystems.LEDStrip.LEDColourState;
import frc.util.SimLib;

/**
 * Add your docs here.
 */
public class DriveTurnToVisionTarget extends AutonCommand {

    private RobotOutput robotOut;
    private Drive drive;
    private LEDStrip led;
    private SimLimelight limelight;
    private boolean driverAim;
    private DriverInput driverIn;

    public DriveTurnToVisionTarget(long timeout){
        this(false,timeout);
    }


    public DriveTurnToVisionTarget( boolean driverAim,long timeout) {
        super(RobotComponent.DRIVE, timeout);
        this.robotOut = RobotOutput.getInstance();
        this.drive = Drive.getInstance();
        this.limelight = SimLimelight.getInstance();
        this.led = LEDStrip.getInstance();
        this.driverAim = driverAim;
        this.driverIn = DriverInput.getInstance();
    }

    @Override
    public void firstCycle() {
        this.limelight.setLimelight(LimelightTargetType.PANEL_TARGET);
        this.limelight.getTargetInfo();
        this.drive.firstCycle();
    }

    @Override
    public boolean calculate() {
        if (this.limelight.getTargetExists()) {
            this.drive.driveTurnToAngleWithForwardVelocity(0);
            this.led.setLed(this.drive.getDesiredLedState());
            if (this.drive.IsWithinAngle()) {
                this.limelight.setDistanceToLimelightTarget(this.limelight.getVisionTargetDistance());
                this.led.setLed(LEDColourState.VISION_COMPLETE);
                return true;
            }
        } else {

            if(this.driverAim) {
                double x = this.driverIn.getDriverX();
			    double y = this.driverIn.getDriverY();

                if (Math.abs(x) < 0.05) {
                    x = 0;
                }
                if (Math.abs(y) < 0.1) {
                    y = 0;
                }

                

                x = SimLib.squareMaintainSign(x);
                y = SimLib.squareMaintainSign(y);

                x*= 0.5;
                y*= 0.5;

                this.robotOut.setDriveLeft(y + x);
                this.robotOut.setDriveRight(y - x);
                this.led.setLed(LEDColourState.DRIVER_TURNING);


            } else {
                this.robotOut.setDriveLeft(0);
                this.robotOut.setDriveRight(0);
                this.led.setLed(LEDColourState.VISION_NOT_AIMED);
            }

            
        }
        return false;
    }

    @Override
    public void override() {
        if (this.limelight.getTargetExists()) {
            this.limelight.setDistanceToLimelightTarget(this.limelight.getVisionTargetDistance());
        } else {
            this.limelight.setDistanceToLimelightTarget(3.0);
        }
        this.robotOut.setDriveLeft(0);
        this.robotOut.setDriveRight(0);
    }
}
