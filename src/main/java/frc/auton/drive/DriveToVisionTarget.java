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

public class DriveToVisionTarget extends AutonCommand {

    private RobotOutput robotOut;
    private Drive drive;
    private LEDStrip led;
    private SimLimelight limelight;
    private boolean isDone;
    private int finishingVisionCycles;
    private double speed;
    private double finishingSpeed;
    private int cyclesUntilDone;
    private boolean driverAim;
    private DriverInput driverIn;

    public DriveToVisionTarget(long timeout) {
        this(5, timeout);
    }

    public DriveToVisionTarget(boolean driverAim,long timeout) {
        this(5,driverAim, timeout);
    }

    public DriveToVisionTarget(double speed, long timeout) {
        this(speed, 5, timeout);
    }

    public DriveToVisionTarget(double speed, boolean driverAim,long timeout) {
        this(speed, 5, driverAim,timeout);
    }

    public DriveToVisionTarget(double speed, int cyclesUntilDone, long timeout) {
        this(speed, cyclesUntilDone, 0.06,false, timeout);
    }

    public DriveToVisionTarget(double speed, int cyclesUntilDone,boolean driverAim, long timeout) {
        this(speed, cyclesUntilDone, 0.06,driverAim, timeout);
    }

    public DriveToVisionTarget(double speed, int cyclesUntilDone, double finishingSpeed,long timeout) {
        this(speed, cyclesUntilDone, finishingSpeed,false, timeout);
    }

    public DriveToVisionTarget(double speed, int cyclesUntilDone, double finishingSpeed,boolean driverAim, long timeout) {
        super(RobotComponent.DRIVE, timeout);
        this.led = LEDStrip.getInstance();
        this.robotOut = RobotOutput.getInstance();
        this.drive = Drive.getInstance();
        this.speed = speed;
        this.limelight = SimLimelight.getInstance();
        this.cyclesUntilDone = cyclesUntilDone;
        this.finishingSpeed = finishingSpeed;
        this.driverAim = driverAim;
        this.driverIn = DriverInput.getInstance();
    }

    @Override
    public void firstCycle() {
        this.limelight.setLimelight(LimelightTargetType.PANEL_TARGET);
        this.limelight.getTargetInfo();
        this.isDone = false;
        this.finishingVisionCycles = 0;
        this.drive.firstCycle();
    }

    @Override
    public boolean calculate() {
        if (this.limelight.getTargetExists()) {
            if (!this.isDone) {
                this.drive.driveLimeLightXY(this.speed);
                this.led.setLed(this.drive.getDesiredLedState());
            }
            if (drive.getDriveStraightPID().isDone()) {
                this.isDone = true;
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

        if (this.isDone) {
            this.led.setLed(LEDColourState.VISION_COMPLETE);
            this.finishingVisionCycles++;
            this.robotOut.setDriveLeft(this.finishingSpeed);
            this.robotOut.setDriveRight(this.finishingSpeed);
        }

        
        return this.finishingVisionCycles > this.cyclesUntilDone;
    }

    @Override
    public void override() {
        this.robotOut.setDriveLeft(0);
        this.robotOut.setDriveRight(0);
    }
}
