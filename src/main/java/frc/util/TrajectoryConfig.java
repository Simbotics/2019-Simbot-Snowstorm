package frc.util;

public class TrajectoryConfig {
	public double maxAcceleration;
	public double maxDeceleration;
	public double maxVelocity;

	public TrajectoryConfig() {
		this(0, 0, 0);
	}

	public TrajectoryConfig(double maxAcceleration, double maxDeceleration, double maxVelocity) {
		this.maxAcceleration = maxAcceleration;
		this.maxDeceleration = maxDeceleration;
		this.maxVelocity = maxVelocity;
	}

	@Override
	public String toString() {
		return " maxAccel: " + maxAcceleration + " maxVelocity: " + maxVelocity;
	}
}
