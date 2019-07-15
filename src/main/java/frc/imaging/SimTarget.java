package frc.imaging;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

public class SimTarget {
	private RotatedRect minAreaRect;
	private double minAreaRectArea;
	private int minAreaRectX;
	private int minAreaRectY;
	private double minAreaRectWidth;
	private double minAreaRectHeight;
	private double minAreaRectAngle;
	private Point minAreaRectCenterPoint;
	private Point[] minAreaRectVertices;

	private Rect boundingRect;
	private double boundingRectArea;
	private int boundingRectX;
	private int boundingRectY;
	private int boundingRectWidth;
	private int boundingRectHeight;
	private int boundingRectCenterX;
	private int boundingRectCenterY;
	private MatOfPoint mop;
	private MatOfPoint2f mop2f;

	public SimTarget(MatOfPoint mop) {
		this.mop = mop;
		this.mop2f = new MatOfPoint2f(mop.toArray());

		this.minAreaRect = Imgproc.minAreaRect(mop2f);
		this.minAreaRectVertices = new Point[4];
		this.minAreaRect.points(minAreaRectVertices);
		this.minAreaRectArea = this.minAreaRect.size.area();
		this.minAreaRectWidth = this.minAreaRect.size.width;
		this.minAreaRectHeight = this.minAreaRect.size.height;
		this.minAreaRectAngle = this.minAreaRect.angle;
		this.minAreaRectCenterPoint = this.minAreaRect.center;

		this.boundingRect = Imgproc.boundingRect(mop);
		this.boundingRectArea = this.boundingRect.area();
		this.boundingRectX = this.boundingRect.x;
		this.boundingRectY = this.boundingRect.y;
		this.boundingRectWidth = this.boundingRect.width;
		this.boundingRectHeight = this.boundingRect.height;
		this.boundingRectCenterX = this.boundingRectX + (this.boundingRectWidth / 2);
		this.boundingRectCenterY = this.boundingRectY + (this.boundingRectHeight / 2);
	}

	public MatOfPoint getMop() {
		return this.mop;
	}

	public MatOfPoint2f getMop2f() {
		return this.mop2f;
	}

	public double getMinAreaRectArea() {
		return this.minAreaRectArea;
	}

	public Point[] getMinAreaRectVertices() {
		return this.minAreaRectVertices;
	}

	public int getMinAreaRectX() {
		return this.minAreaRectX;
	}

	public int getMinAreaRectY() {
		return this.minAreaRectY;
	}

	public double getMinAreaRectWidth() {
		return this.minAreaRectWidth;
	}

	public double getMinAreaRectHeight() {
		return this.minAreaRectHeight;
	}

	public double getMinAreaRectAngle() {
		return this.minAreaRectAngle;
	}

	public Point getMinAreaRectCenter() {
		return this.minAreaRectCenterPoint;
	}

	public double getBoundingRectArea() {
		return this.boundingRectArea;
	}

	public int getBoundingRectX() {
		return this.boundingRectX;
	}

	public int getBoundingRectY() {
		return this.boundingRectY;
	}

	public int getBoundingRectWidth() {
		return this.boundingRectWidth;
	}

	public int getBoundingRectHeight() {
		return this.boundingRectHeight;
	}

	public int getBoundingRectCenterX() {
		return this.boundingRectCenterX;
	}

	public int getBoundingRectCenterY() {
		return this.boundingRectCenterY;
	}
}
