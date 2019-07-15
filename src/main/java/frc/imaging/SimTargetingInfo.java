package frc.imaging;

import frc.util.Debugger;

// Yaw: getAngleX
//Pitch: getAngleY

public class SimTargetingInfo {

	private SimTargetGroup target;

	public static final int IMAGE_WIDTH = 320;
	public static final int IMAGE_HEIGHT = 240;
	public static final double IMAGE_CENTER_X = 159.5;
	public static final double IMAGE_CENTER_Y = 119.5;

	// camera offsets in inches
	public static final double CAMERA_X_OFFSET = 0;
	public static final double CAMERA_Y_OFFSET = 10.5 / 12;

	// robot/field properties in inches
	public static final double CAMERA_HEIGHT = 32.0;
	public static final double GOAL_HEIGHT = 20.5; // Height of the middle of top target
	public static final double CAMERA_PITCH_OFFSET_DEGREES = -2.802971; // if its pointing up 5 degrees from forward, offset is
																// positive 5

	// offsets
	public static final double SHOOTER_TO_CAMERA_OFFSET = 0;
	public static final double GOAL_RADIUS = 0;

	// FOV constants
	public static final double DIAG_FOV = 68.5; // diagonal field of view in degrees (from datasheet)
	public static final double HORIZONTAL_FOV = 53.1; // empirically measured
	public static final double VERTICAL_FOV = 35.1; // empirically measured

	public static final double FOCAL_LENGTH = IMAGE_WIDTH / (2.0 * Math.tan(Math.toRadians(HORIZONTAL_FOV / 2)));

	public static final double VISION_TARGET_HEIGHT = 28.5;

	public static final double BALL_DISTANCE_TO_AREA_RATIO = 0; // will eventually be some known distance divided by the apparent size at that distance

	// HOW TO CALCULATE HORIZONTAL FOV FROM DIAGONAL FOV
	// here are the equations relating horiz, vert, and diag FOV to the focal point
	// derived from pinhole camera model
	// just isolate the focal point of eq 1 and 3, and equate them to solve for fh
	// in terms of fd
	// FOVHoriz = 2 * atan2(W/2, f)
	// FOVVert = 2 * atan2(H/2, f)
	// FOVDiag = 2 * atan2(sqrt(W^2 + H^2)/2, f)
	// focal length = IMAGE_WIDTH/(2.0 *
	// Math.tan(Math.toRadians(HORIZONTAL_FOV/2)));

	public SimTargetingInfo() {

	}

	public double getYawToTargetDegrees(SimTargetGroup target) {// robot angle to target
		this.target = target;
		double xPixel = this.target.getMidPointMiddle().x;
		double xDistFromImageCenter = IMAGE_CENTER_X - xPixel;

		double yawInRadians = Math.atan2(xDistFromImageCenter, FOCAL_LENGTH);
		

		return Math
				.toDegrees(Math.atan2(getDistanceToTargetInches(this.target) * Math.sin(yawInRadians) + CAMERA_X_OFFSET,
						getDistanceToTargetInches(this.target) * Math.cos(yawInRadians) + CAMERA_Y_OFFSET));
	}

	public double getPitchToTargetDegrees(SimTargetGroup target) {
		this.target = target;
		double yPixel = this.target.getMidPointMiddle().y;
		Debugger.println("Y Pixel: " + yPixel);
		double yDistFromImageCenter = IMAGE_CENTER_Y - yPixel;

		double pitchInRadians = Math.atan2(yDistFromImageCenter, FOCAL_LENGTH);
		return Math.toDegrees(pitchInRadians);
	}

	public double getDistanceToTargetInches(SimTargetGroup target) {
		this.target = target;

		double leftDistance = VISION_TARGET_HEIGHT
				/ (2 * Math.tan((VERTICAL_FOV * (target.getLeftTarget().getBoundingRectHeight() / IMAGE_HEIGHT)) / 2));
		double rightDistance = VISION_TARGET_HEIGHT
				/ (2 * Math.tan((VERTICAL_FOV * (target.getRightTarget().getBoundingRectHeight() / IMAGE_HEIGHT)) / 2));

		return (leftDistance + rightDistance) / 2;
	}

	public double getDistanceToTargetFeet(SimTargetGroup target) {
		return getDistanceToTargetInches(target) / 12.0;
	}

}