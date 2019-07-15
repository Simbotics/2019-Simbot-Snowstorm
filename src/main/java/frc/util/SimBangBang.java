package frc.util;

/**
 *
 * @author Mike
 */
public class SimBangBang {

	private double maxOutput;
	private double minOutput;
	private double desiredVal;

	private boolean aboveSpeed = false;

	public SimBangBang(double onOutput, double offOutput) {
		this.setMaxOutput(onOutput);
		this.setMinOutput(offOutput);
	}

	public void setMaxOutput(double maxOutput) {
		if (maxOutput < 0.0) {
			this.maxOutput = 0.0;
		} else if (maxOutput > 1.0) {
			this.maxOutput = 1.0;
		} else {
			this.maxOutput = maxOutput;
		}
	}

	public void setMinOutput(double minOutput) {
		if (minOutput < 0.0) {
			this.minOutput = 0.0;
		} else if (minOutput > 1.0) {
			this.minOutput = 1.0;
		} else {
			this.minOutput = minOutput;
		}
	}

	public void setDesiredValue(double val) {
		this.desiredVal = val;
	}

	public void setMinMax(double min, double max) {
		this.setMinOutput(min);
		this.setMaxOutput(max);
	}

	public double calculate(double currentVal) {
		if (currentVal < this.desiredVal) {
			this.aboveSpeed = false;
			return this.maxOutput;
		} else {
			this.aboveSpeed = true;
			return this.minOutput;
		}
	}

	public double getDesiredValue() {
		return this.desiredVal;
	}

	public boolean isAboveSpeed() {
		return this.aboveSpeed;
	}

	public boolean isAtSpeed(double currentSpeed, double eps) {
		if (currentSpeed < this.desiredVal + eps && currentSpeed > this.desiredVal - eps) {
			return true;
		} else {
			return false;
		}
	}

	public void autoMinMax(double maxSpeed) {
		this.maxOutput = (this.desiredVal / maxSpeed) * 1.2;
		this.minOutput = (this.desiredVal / maxSpeed) * 0.7;
	}

}