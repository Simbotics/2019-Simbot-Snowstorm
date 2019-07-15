package frc.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// Thank you Team 254!

public class SimMotionProfile {

	protected double pConst;
	protected double iConst;
	protected double dConst;
	protected double velocityConstFF;
	protected double accelerationConstFF;
	protected double deccelerationConstFF;
	protected double gravityFF;
	protected double errorSum;
	protected double previousError;
	protected double positionEps;
	protected double velocityEps;
	protected double desiredVelocity;
	protected double desiredVal;
	protected double deltaTime;
	protected double percentFinished;
	protected double initalDistFromTarget;
	protected boolean firstCycle = true;
	protected boolean debug = false;

	protected boolean timerReset = true;
	protected double lastTime;
	protected TrajectorySetpoint nextState = new TrajectorySetpoint(); // creates a new blank setpoint to predict the
																		// future
	protected TrajectorySetpoint currentSetpoint = new TrajectorySetpoint();
	protected TrajectoryConfig profileConfig = new TrajectoryConfig(); // max vel and accel values

	public SimMotionProfile(ProfileConstants constants) {
		this(constants.p, constants.i, constants.d, constants.vFF, constants.aFF, constants.dFF, constants.gravityFF,
				constants.positionEps, constants.velocityEps);
	}

	public SimMotionProfile(double p, double i, double d, double vFF, double aFF, double dFF, double gravityFF,
			double positionEps, double velocityEps) {
		this.pConst = p;
		this.iConst = i;
		this.dConst = d;
		this.velocityConstFF = vFF;
		this.accelerationConstFF = aFF;
		this.deccelerationConstFF = dFF;
		this.gravityFF = gravityFF;
		this.positionEps = positionEps;
		this.velocityEps = velocityEps;

	}

	public void setConstants(ProfileConstants constants) {
		this.setConstants(constants.p, constants.i, constants.d, constants.vFF, constants.aFF, constants.gravityFF);
		this.setPositionEps(constants.positionEps);
		this.setVelocityEps(constants.velocityEps);
		this.deccelerationConstFF = constants.dFF;
	}

	public void setConstants(double p, double i, double d, double vFF, double aFF, double gravityFF) {
		this.pConst = p;
		this.iConst = i;
		this.dConst = d;
		this.velocityConstFF = vFF;
		this.accelerationConstFF = aFF;
		this.gravityFF = gravityFF;
	}

	public void setConstants(double p, double i, double d, double vFF, double aFF) {
		this.setConstants(p, i, d, vFF, aFF, 0);
	}

	public void configureProfile(TrajectoryConfig config) {
		this.profileConfig = config;
	}

	public void configureProfile(double dt, double maxVel, double maxAccel, double maxDecel) {
		this.profileConfig.maxVelocity = maxVel;
		this.profileConfig.maxAcceleration = maxAccel;
		this.profileConfig.maxDeceleration = maxDecel;
	}

	public void setDebug(boolean isOn) {
		this.debug = isOn;
	}

	public void setDesiredValue(double position, double velocity, double acceleration, double val) {
		this.setDesiredValue(position, velocity, acceleration, val, 0.0);
	}

	public void setDesiredValue(double position, double velocity, double acceleration, double val,
			double desiredVelocity) {
		this.desiredVal = val;
		this.desiredVelocity = desiredVelocity;
		this.currentSetpoint.position = position;
		this.currentSetpoint.velocity = velocity;
		this.currentSetpoint.acceleration = acceleration;
		this.timerReset = true;
		this.errorSum = 0.0;
		this.firstCycle = true;
		this.percentFinished = 0;
	}

	public void resetErrorSum() {
		this.errorSum = 0.0;
	}

	public void setPositionEps(double positionEps) {
		this.positionEps = positionEps;
	}

	public void setVelocityEps(double velocityEps) {
		this.velocityEps = velocityEps;
	}

	public double getDesiredVal() {
		return this.desiredVal;
	}

	public double getDesiredVelocity() {
		return this.desiredVelocity;
	}

	public TrajectoryConfig getProfileConfig() {
		return this.profileConfig;
	}

	public TrajectorySetpoint getCurrentSetpoint() {
		return this.currentSetpoint;
	}

	protected void calculateTrapezoid() {
		if (this.timerReset) {
			this.lastTime = System.currentTimeMillis();
		} else {
			double currentTime = System.currentTimeMillis();
			this.deltaTime = currentTime - lastTime;
			this.lastTime = currentTime;
		}
		this.deltaTime = this.deltaTime / 1000.0;

		if (this.isDone()) { // we are at the target
			this.currentSetpoint.position = this.desiredVal; // stop at desired val
			this.currentSetpoint.velocity = this.desiredVelocity;
			this.currentSetpoint.acceleration = 0;
			this.percentFinished = 1.0;
		} else { // calculate the new position, velocity, and acceleration
			double distanceFromTarget = this.desiredVal - this.currentSetpoint.position;
			double currentVelocity = this.currentSetpoint.velocity;
			double currentVelocitySquared = currentVelocity * currentVelocity;
			boolean goingBackwards = false;

			if (distanceFromTarget < 0) {
				goingBackwards = true;
				distanceFromTarget *= -1;
				currentVelocity *= -1;
			}

			if (this.firstCycle) {
				this.initalDistFromTarget = distanceFromTarget;
				this.firstCycle = false;
			}

			this.percentFinished = (this.initalDistFromTarget - distanceFromTarget) / this.initalDistFromTarget;

			// Calculate the minimum and maximum reachable velocity with the remaining
			// distance
			double maxReachableVelocityDisc = currentVelocitySquared / 2.0
					+ this.profileConfig.maxAcceleration * distanceFromTarget;
			double minReachableVelocityDisc = currentVelocitySquared / 2.0
					- this.profileConfig.maxDeceleration * distanceFromTarget;
			double cruiseVelocity = currentVelocity;

			if (minReachableVelocityDisc < 0 || cruiseVelocity < 0) { // going backwards
				cruiseVelocity = Math.min(this.profileConfig.maxVelocity, Math.sqrt(maxReachableVelocityDisc));
			}

			double rampUpTime = (cruiseVelocity - currentVelocity) / this.profileConfig.maxAcceleration; // how long to
																											// accelerate
			double rampUpDistance = currentVelocity * rampUpTime
					+ (0.5 * this.profileConfig.maxAcceleration * rampUpTime * rampUpTime); // position

			// if (Math.abs(this.desiredVelocity) > Math.abs(cruiseVelocity)) { // if we
			// somehow need to slow down to a
			// higher speed prevent that
			// this.desiredVelocity = cruiseVelocity;
			// }

			double rampDownTime = Math
					.abs((cruiseVelocity - this.desiredVelocity) / this.profileConfig.maxDeceleration); // time to
																										// decelerate to
																										// zero
			double rampDownDistance = cruiseVelocity * rampDownTime
					- (0.5 * this.profileConfig.maxDeceleration * rampDownTime * rampDownTime); // position to slow down

			double cruiseDistance = Math.max(0, distanceFromTarget - rampUpDistance - rampDownDistance); // how long to
																											// cruise
			double cruiseTime = Math.abs(cruiseDistance / cruiseVelocity);

			// Calculate where we should be one cycle into the future
			if (rampUpTime >= this.deltaTime) { // we are in the acceleration phase
				System.out.println("AccelPhase");
				this.nextState.position = (currentVelocity * this.deltaTime
						+ (0.5 * this.profileConfig.maxAcceleration * this.deltaTime * this.deltaTime));
				this.nextState.velocity = (currentVelocity + this.profileConfig.maxAcceleration * this.deltaTime);
				this.nextState.acceleration = this.profileConfig.maxAcceleration;
			} else if (rampUpTime + cruiseTime >= this.deltaTime) { // at cruise velocity phase
				System.out.println("CruisePhase");
				this.nextState.position = rampUpDistance + cruiseVelocity * (this.deltaTime - rampUpTime);
				this.nextState.velocity = cruiseVelocity;
				this.nextState.acceleration = 0;
			} else if (rampUpTime + cruiseTime + rampDownTime >= this.deltaTime) { // at deceleration phase
				System.out.println("DecelPhase");
				double newDT = this.deltaTime - rampUpTime - cruiseTime;
				this.nextState.position = rampUpDistance + cruiseDistance + cruiseVelocity * newDT
						- (0.5 * this.profileConfig.maxDeceleration * newDT * newDT);
				this.nextState.velocity = cruiseVelocity - this.profileConfig.maxDeceleration * newDT;
				this.nextState.acceleration = -this.profileConfig.maxDeceleration;
			} else { // we have finished the trajectory
				System.out.println("DonePhase");
				this.nextState.position = distanceFromTarget;
				this.nextState.velocity = this.desiredVelocity;
				this.nextState.acceleration = 0;
			}

			if (goingBackwards) { // flip everything if we are going backwards
				this.nextState.position = -1 * this.nextState.position;
				this.nextState.velocity = -1 * this.nextState.velocity;
				this.nextState.acceleration = -1 * this.nextState.acceleration;
			}

			// add the future to the current
			this.currentSetpoint.position += this.nextState.position;
			this.currentSetpoint.velocity = this.nextState.velocity;
			this.currentSetpoint.acceleration = this.nextState.acceleration;

		}
	}

	protected double calculateOutput(double position, double velocity, double angle) {
		double error = this.currentSetpoint.position - position;

		if (this.timerReset) { // reset error
			this.timerReset = false;
			this.previousError = error;
			this.errorSum = 0.0;
		}

		double pFinal = (this.pConst * error);
		double dFinal = (this.dConst
				* (((error - this.previousError) / this.deltaTime) - this.currentSetpoint.velocity));
		double KvFinal = (this.velocityConstFF * this.currentSetpoint.velocity);
		double KaFinal;
		// System.out.println("accel" + this.currentSetpoint.acceleration);
		if (this.currentSetpoint.acceleration > 0) {
			KaFinal = (this.accelerationConstFF * this.currentSetpoint.acceleration);
		} else {
			KaFinal = (this.deccelerationConstFF * this.currentSetpoint.acceleration);
		}

		double gravityFinal = (this.gravityFF * Math.cos(angle)); // angle must be in degrees

		// calculate the output, subtract desired velocity from d term because we want
		// to be moving that speed
		double output = pFinal + dFinal + KvFinal + KaFinal + gravityFinal;

		if (output < 1.0 && output > -1.0) { // only add I when we are not going full speed already
			this.errorSum += error * deltaTime;
		} else {
			this.errorSum = 0.0;
		}

		double iFinal = this.iConst * this.errorSum;
		output += iFinal; // now add I to the final output

		SmartDashboard.putNumber("2_Current Setpoint: ", this.currentSetpoint.position);
		SmartDashboard.putNumber("2_Desired Position: ", this.desiredVal);

		if (this.debug) {
			SmartDashboard.putNumber("6_Error Pos: ", error);
			SmartDashboard.putNumber("6_Current Target V: ", this.currentSetpoint.velocity);
			SmartDashboard.putNumber("6_Delta V From target: ", velocity - this.currentSetpoint.velocity);
			SmartDashboard.putNumber("6_P Output: ", pFinal);
			SmartDashboard.putNumber("6_I Output: ", iFinal);
			SmartDashboard.putNumber("6_D Output: ", dFinal);
			SmartDashboard.putNumber("6_Kv Output: ", KvFinal);
			SmartDashboard.putNumber("6_Ka Output: ", KaFinal);
			SmartDashboard.putNumber("6_Kg Output: ", gravityFinal);
			SmartDashboard.putNumber("6_Total Output: ", output);

		}

		this.previousError = error;

		return output;
	}

	protected double calculateOutput(double position, double velocity) {
		return this.calculateOutput(position, velocity, 90); // cos of 90 is 0 so gravity value is not used
	}

	public double calculate(double position, double velocity) {
		this.calculateTrapezoid();
		return this.calculateOutput(position, velocity);

	}

	public double calculate(double position, double velocity, double angle) {
		this.calculateTrapezoid();
		return this.calculateOutput(position, velocity, angle);
	}

	public double getPercentFinished() {
		return this.percentFinished;
	}

	public boolean isDone() {
		return (Math.abs(this.currentSetpoint.position - this.desiredVal) <= this.positionEps)
				&& (Math.abs(this.currentSetpoint.velocity - this.desiredVelocity) <= this.velocityEps);
	}

}
