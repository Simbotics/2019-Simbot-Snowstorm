package frc.imaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat4;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoException;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SimCamera implements Runnable {

	private UsbCamera camera;

	private CvSink cvSink;
	private CvSource outputStream;
	private boolean processing = false;
	private boolean initialized = false;

	private Point midPoint;
	private Mat source;
	private SimProcessing processingPipeline;

	ArrayList<SimTargetGroup> simTargetGroupArrayList = new ArrayList<SimTargetGroup>();
	private SimTargetGroup centerTargetGroup;

	public SimCamera() {
		this.processingPipeline = new SimProcessing();
		this.source = new Mat();
	}

	public void setProcessingOn(boolean on) {
		this.processing = on;
	}

	public void init() {
		SmartDashboard.putBoolean("2_CAMERA PROCESSING: ", processing);

		try {
			this.camera = new UsbCamera("usb cam", "/dev/video0");
		} catch (Exception e) {
			e.printStackTrace();
		}

		CameraServer.getInstance().addCamera(camera);
		this.camera.setResolution(SimTargetingInfo.IMAGE_WIDTH, SimTargetingInfo.IMAGE_HEIGHT);
		this.camera.setFPS(30);
		this.camera.setBrightness(0);
		this.camera.setExposureManual(10);

		this.cvSink = CameraServer.getInstance().getVideo();
		this.outputStream = CameraServer.getInstance().putVideo("cam0", 320, 240);
		this.initialized = true;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) { // thread is always running
			simTargetGroupArrayList.clear(); // clear out the arraylist
			if (this.camera == null && this.initialized == false) {
				try {
					this.init();
				} catch (VideoException ve) {
					ve.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (this.camera.isConnected() && this.initialized == true) {
				this.midPoint = null; // reset the midpoint, will stay null if
										// not seeing target
				if (CameraServer.getInstance() != null) {
					this.cvSink.grabFrameNoTimeout(this.source);

					if (this.processing) {

						/*
						 * Camera Image Retrieval ---------------------------------------------------
						 */
						// Retrieve the current output that the Camera sees

						this.processingPipeline.process(this.source);
						Mat output = new Mat();
						output = this.processingPipeline.hsvThresholdOutput();

						/*
						 * Generate List of Contours ---------------------------------------------------
						 */
						ArrayList<SimTarget> simTargetArrayList;
						simTargetArrayList = this.processingPipeline.filterContoursOutput(); // list
																								// of
																								// contours

						// Now that we have the list of contours we need to sort
						// them. This function
						// will provide of with a list of sorted contours
						// ranging from least -> greatest
						List<SimTarget> simTargetList = simTargetArrayList;
						Collections.sort(simTargetList, new Comparator<SimTarget>() {
							@Override
							public int compare(SimTarget mop1, SimTarget mop2) {
								return (int) mop2.getBoundingRectCenterX() - (int) mop1.getBoundingRectCenterX();
							}
						});

						if (simTargetList.size() > 1) {
							for (int i = 0; i < simTargetList.size() - 1; i++) {

								SimTarget target = simTargetList.get(i);

								// Draw a fitted line for each detected contour
								MatOfFloat4 fitLine = new MatOfFloat4();
								Imgproc.fitLine(target.getMop(), fitLine, Imgproc.DIST_L2, 0.0, 0.01, 0.01);
								float vX = fitLine.toArray()[0];
								float vY = fitLine.toArray()[1];
								float x = fitLine.toArray()[2];
								float y = fitLine.toArray()[3];
								
								int leftY = (int)((-x*vY/vX) + y);
								int rightY = (int)(((output.cols()-x)*vY/vX)+y);

								Imgproc.line(output, new Point(output.cols()-1,rightY), new Point(0,leftY), new Scalar(0, 255, 0, 255));

								/*
								 * Finding pairs of targets
								 * 
								 */

								 

								if (target.getMinAreaRectAngle() < 0 && target.getMinAreaRectAngle() > -35) {// Right:-14
									for (int j = i; j < simTargetList.size(); j++) {
										SimTarget comparisonTarget = simTargetList.get(j);
										if (comparisonTarget.getMinAreaRectAngle() > -80
												&& comparisonTarget.getMinAreaRectAngle() < -45 // left: -69
												&& Math.abs(comparisonTarget.getBoundingRectCenterY()
														- target.getBoundingRectCenterY()) < target
																.getBoundingRectHeight() / 2
												&& Math.abs(comparisonTarget.getBoundingRectCenterX()
														- target.getBoundingRectCenterX()) < target
																.getBoundingRectWidth() * 3.7) {
											simTargetGroupArrayList.add(new SimTargetGroup(target, comparisonTarget));
											// draw rectangle around target pair
											Imgproc.rectangle(output, new Point(
													simTargetGroupArrayList.get(simTargetGroupArrayList.size() - 1)
															.getTopLeft().getX(),
													simTargetGroupArrayList.get(simTargetGroupArrayList.size() - 1)
															.getTopLeft().getY()),
													new Point(
															simTargetGroupArrayList
																	.get(simTargetGroupArrayList.size() - 1)
																	.getBottomRight().getX(),
															simTargetGroupArrayList
																	.get(simTargetGroupArrayList.size() - 1)
																	.getBottomRight().getY()),
													new Scalar(255, 0, 0), 2);

											i++;// if you find a pair of targets, skip over the next target, as it is
												// already in the array
											break;
										}
									}
								}

							}
						}

						if (this.simTargetGroupArrayList.size() > 0){//if there is a target...
							SimTargetGroup centerTargetGroup = this.simTargetGroupArrayList.get(0);
				
							for (int i = 0; i < this.simTargetGroupArrayList.size(); i++){
								if (Math.abs(this.simTargetGroupArrayList.get(i).getMidPointMiddle().x - SimTargetingInfo.IMAGE_CENTER_X)
									< Math.abs(centerTargetGroup.getMidPointMiddle().x) - SimTargetingInfo.IMAGE_CENTER_X){
										centerTargetGroup = this.simTargetGroupArrayList.get(i);
									}
							}
				
							this.centerTargetGroup = centerTargetGroup;

							Imgproc.line(output, this.centerTargetGroup.getMidPointMiddle(), this.centerTargetGroup.getMidPointMiddle(), new Scalar(255, 0, 0));
				
						} else {
							this.centerTargetGroup = null;
						}

						this.outputStream.putFrame(output); // put the output
															// Mat in the stream
					} else {
						this.outputStream.putFrame(source);
					}
				}
			}

		}
	}

	public SimTargetGroup getCenterTargetGroup(){
		return this.centerTargetGroup;
	}

	public Point getMidPointMiddle() {
		return this.midPoint;
	}

}
