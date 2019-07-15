package frc.util;

public class TrajectorySetpoint {

	public double position;
	public double velocity;
	public double acceleration;

	@Override
	public String toString() {
		return "Pos: " + position + " Vel: " + velocity + " Accel: " + acceleration;
	}
}
