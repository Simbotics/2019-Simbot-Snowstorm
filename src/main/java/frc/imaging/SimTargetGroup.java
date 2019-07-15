/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.imaging;

import org.opencv.core.Point;

import frc.io.SensorInput;
import frc.util.Debugger;
import frc.util.SimPoint;

/**
 * Add your docs here.
 */
public class SimTargetGroup {

    private SensorInput sensorIn;

    private SimTarget leftTarget;
    private SimTarget rightTarget;

    private SimPoint topLeft;
    private SimPoint bottomLeft;
    private SimPoint topRight;
    private SimPoint bottomRight;
    private Point centerLeft;
    private Point centerRight;
    private int top;
    private int bottom;

    private Point midPointMiddle;
    private Point midPointInside;
    private Point midPointOutside;

    private SimTargetingInfo targetInfo;

    private SimPoint coordinate;

    public SimTargetGroup(SimTarget leftTarget, SimTarget rightTarget) {
        this.leftTarget = leftTarget;
        this.rightTarget = rightTarget;

        this.targetInfo = new SimTargetingInfo();

        this.sensorIn = SensorInput.getInstance();

        this.top = Math.max(this.leftTarget.getBoundingRectCenterY() + this.leftTarget.getBoundingRectHeight() / 2,
                this.rightTarget.getBoundingRectCenterY() + this.rightTarget.getBoundingRectHeight() / 2);

        this.bottom = Math.min(this.leftTarget.getBoundingRectCenterY() - this.leftTarget.getBoundingRectHeight() / 2,
                this.rightTarget.getBoundingRectCenterY() - this.rightTarget.getBoundingRectHeight() / 2);

        this.topLeft = new SimPoint(
                this.leftTarget.getBoundingRectCenterX() + this.leftTarget.getBoundingRectWidth() / 2, this.top);

        this.bottomLeft = new SimPoint(
                this.leftTarget.getBoundingRectCenterX() + this.leftTarget.getBoundingRectWidth() / 2, this.bottom);

        this.topRight = new SimPoint(
                this.rightTarget.getBoundingRectCenterX() - this.rightTarget.getBoundingRectWidth() / 2, this.top);

        this.bottomRight = new SimPoint(
                this.rightTarget.getBoundingRectCenterX() - this.rightTarget.getBoundingRectWidth() / 2, this.bottom);

        this.midPointOutside = new Point((this.topLeft.getX() + this.topRight.getX()) / 2,
                (this.topLeft.getY() + this.bottomLeft.getY()) / 2);

        this.centerLeft = new Point(
                this.leftTarget.getBoundingRectCenterX() - this.leftTarget.getBoundingRectWidth() / 2,
                this.leftTarget.getBoundingRectCenterY());
        this.centerRight = new Point(
                this.rightTarget.getBoundingRectCenterX() + this.rightTarget.getBoundingRectWidth() / 2,
                this.rightTarget.getBoundingRectCenterY());

        this.midPointInside = new Point((this.centerLeft.x + this.centerRight.x) / 2,
                (this.centerLeft.y + this.centerRight.y) / 2);

        this.midPointMiddle = new Point(
                (this.leftTarget.getBoundingRectCenterX() + this.rightTarget.getBoundingRectCenterX()) / 2.0,
                (this.leftTarget.getBoundingRectCenterY() + this.rightTarget.getBoundingRectCenterY()) / 2);
    }

    public SimPoint getCoordinate() {

        double xPixelLeft = this.leftTarget.getBoundingRectCenterX();
        double xDistFromImageCenterLeft = SimTargetingInfo.IMAGE_CENTER_X - xPixelLeft;
        double yawInRadiansLeft = Math.atan2(xDistFromImageCenterLeft, SimTargetingInfo.FOCAL_LENGTH);

        double xPixelRight = this.rightTarget.getBoundingRectCenterX();
        double xDistFromImageCenterRight = SimTargetingInfo.IMAGE_CENTER_X - xPixelRight;
        double yawInRadiansRight = Math.atan2(xDistFromImageCenterRight, SimTargetingInfo.FOCAL_LENGTH);

        SimPoint leftCoordinate = new SimPoint(
                this.sensorIn.getDriveXPos() + (SimTargetingInfo.CAMERA_X_OFFSET * Math.cos(this.sensorIn.getAngle())
                        + (this.targetInfo.getDistanceToTargetFeet(this) * Math.cos(yawInRadiansLeft))),
                this.sensorIn.getDriveYPos() + (SimTargetingInfo.CAMERA_Y_OFFSET * Math.sin(this.sensorIn.getAngle())
                        + (this.targetInfo.getDistanceToTargetFeet(this) * Math.sin(yawInRadiansLeft))));

        SimPoint rightCoordinate = new SimPoint(
                this.sensorIn.getDriveXPos() + (SimTargetingInfo.CAMERA_X_OFFSET * Math.cos(this.sensorIn.getAngle())
                        + (this.targetInfo.getDistanceToTargetFeet(this) * Math.cos(yawInRadiansRight))),
                this.sensorIn.getDriveYPos() + (SimTargetingInfo.CAMERA_Y_OFFSET * Math.sin(this.sensorIn.getAngle())
                        + (this.targetInfo.getDistanceToTargetFeet(this) * Math.sin(yawInRadiansRight))));


        SimPoint targetCoordinate = new SimPoint((leftCoordinate.getX() + rightCoordinate.getX()) / 2,
        (leftCoordinate.getY() + rightCoordinate.getY()) / 2);

        Debugger.println("Target coordinate: X: " + targetCoordinate.getX() + ", Y: " + targetCoordinate.getY());

        return targetCoordinate;
    }

    public SimPoint getTopLeft() {
        return this.topLeft;
    }

    public SimPoint getBottomLeft() {
        return this.bottomLeft;
    }

    public SimPoint getTopRight() {
        return this.topRight;
    }

    public SimPoint getBottomRight() {
        return this.bottomRight;
    }

    public Point getMidPointMiddle() {
        return this.midPointMiddle;
    }

    public Point getMidPointInside() {
        return this.midPointInside;
    }

    public Point getMidPointOutside() {
        return this.midPointOutside;
    }

    public Point getCenterLeft() {
        return this.centerLeft;
    }

    public Point getCenterRight() {
        return this.centerRight;
    }

    public SimTarget getLeftTarget() {
        return this.leftTarget;
    }

    public SimTarget getRightTarget() {
        return this.rightTarget;
    }

}
