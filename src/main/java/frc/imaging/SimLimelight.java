/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.imaging;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.io.SensorInput;

/**
 * Add your docs here.
 */
public class SimLimelight {

    private static SimLimelight instance;
    private NetworkTable netWorkTable;
    private SensorInput sensorIn;

    public LimelightTargetType currentTargetType;

	private double prevDistanceToLimelightTarget = 0;

    public enum LimelightTargetType {
        DRIVER, BALL_TARGET, PANEL_TARGET
    }

    private SimLimelight() {
        this.sensorIn = SensorInput.getInstance();
        this.netWorkTable = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public static SimLimelight getInstance() {
        if (instance == null) {
            instance = new SimLimelight();
        }
        return instance;
    }

    public void setLimelight(LimelightTargetType state) {
        currentTargetType = state;
    }

    public LimelightTargetType getLimelightState() {
        return currentTargetType;
    }

    public SimLimelightTarget getTargetInfo() {

        SimLimelightTarget desiredTarget = new SimLimelightTarget();

        desiredTarget.desiredTargetType = this.currentTargetType;

        SmartDashboard.putString("LIMELIGHT_TARGET", this.currentTargetType.toString());

        if (currentTargetType == LimelightTargetType.PANEL_TARGET) {
            this.netWorkTable.getEntry("pipeline").setNumber(0);
            this.netWorkTable.getEntry("camera").setNumber(0);

            desiredTarget.x = (this.sensorIn.getDriveXPos()
                    + getVisionTargetDistance() * Math.cos(getTargetX() + this.sensorIn.getAngle()));
            desiredTarget.y = (this.sensorIn.getDriveYPos()
                    + getVisionTargetDistance() * Math.sin(getTargetX() + this.sensorIn.getAngle()));

            desiredTarget.targetFound = this.getTargetExists();
        } else if (currentTargetType == LimelightTargetType.BALL_TARGET) {
            this.netWorkTable.getEntry("pipeline").setNumber(0);
            this.netWorkTable.getEntry("camera").setNumber(0);

            desiredTarget.x = (this.sensorIn.getDriveXPos()
                    + getVisionTargetDistance() * Math.cos(getTargetX() + this.sensorIn.getAngle()));
            desiredTarget.y = (this.sensorIn.getDriveYPos()
                    + getVisionTargetDistance() * Math.sin(getTargetX() + this.sensorIn.getAngle()));

            desiredTarget.targetFound = this.getTargetExists();
        } else {
            this.netWorkTable.getEntry("pipeline").setNumber(1);
            this.netWorkTable.getEntry("camera").setNumber(0);
            desiredTarget.targetFound = false;
        }

        return desiredTarget;
    }

    public void setLEDMode(int ledMode) {// 0 is pipeline default, 1 is off, 2 is blink, 3 is on
        this.netWorkTable.getEntry("ledMode").setNumber(ledMode);
    }

    public double getTargetX() {
        return this.netWorkTable.getEntry("ty").getDouble(0); // because portrait
    }

    public double getTargetY() {
        return this.netWorkTable.getEntry("tx").getDouble(0); // because portrait
    }

    public boolean getTargetExists() {
        return this.netWorkTable.getEntry("tv").getDouble(0) == 1;
    }

    public double getTargetArea() {
        return this.netWorkTable.getEntry("ta").getDouble(0);
    }

    public double getVisionTargetDistance() { // uses trig to find distance
        double x = this.getTargetArea();
        double distance;
        if (x == 0 || x > 12.0) {
            distance = 2.3 * 12.0;
        } else {
            distance = Math.pow(x, -0.507) * 7.7562;
        }
        SmartDashboard.putNumber("Distance", distance);
        return distance;
    }

    // public double getTargetDistanceArea() { // uses area to find distance
    //     return this.netWorkTable.getEntry("ta").getDouble(0) * SimTargetingInfo.BALL_DISTANCE_TO_AREA_RATIO;
    // }

	public void setDistanceToLimelightTarget(double distance) {
		this.prevDistanceToLimelightTarget = distance;
	}

	public double getDistanceToPrevLimelightTarget() {
		return this.prevDistanceToLimelightTarget;
	}

}
