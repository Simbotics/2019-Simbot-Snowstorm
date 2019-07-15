package frc.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SimPIDFA extends SimPID {

	private double feedForward;
	private double accelFF;

	public SimPIDFA(ProfileConstants constants) {
		this(constants.p, constants.i, constants.d, constants.vFF, constants.aFF, constants.positionEps);
	}

	public SimPIDFA(double p, double i, double d, double f, double a, double eps) {
		super(p, i, d, eps);
		this.feedForward = f;
		this.accelFF = a;
	}

	public double calcPID(double current, double currentDesiredAcceleration) {
		double feedForwardOutput = (super.getDesiredVal() * this.feedForward);
		double accelOutput = (currentDesiredAcceleration * this.accelFF);
		if (this.debug) {
			SmartDashboard.putNumber("FF out", feedForwardOutput);
			SmartDashboard.putNumber("AFF out", accelOutput);
		}
		return super.calcPID(current) + feedForwardOutput + accelOutput;
	}

	public void setConstants(double p, double i, double d, double f, double a) {
		super.setConstants(p, i, d);
		this.feedForward = f;
		this.accelFF = a;
	}

	@Override
	public boolean isDone() {
		double currError = Math.abs(this.previousError);

		// close enough to target
		if (currError <= this.finishedRange) {
			return true;
		} else {
			return false;
		}
	}

	public void setFeedForward(double f) {
		this.feedForward = f;
	}

}
