package frc.util;

public class ProfileConstants {
	public final double p;
	public final double i;
	public final double d;
	public final double vFF;
	public final double aFF;
	public final double dFF;
	public final double gravityFF;
	public final double positionEps;
	public final double velocityEps;

	public ProfileConstants(double p, double i, double d, double vFF, double aFF) {
		this(p, i, d, vFF, aFF, aFF, 0.0, 0.01, 0.01);
	}

	public ProfileConstants(double p, double i, double d, double vFF, double aFF, double dFF) {
		this(p, i, d, vFF, aFF, dFF, 0, 0.01, 0.01);
	}

	public ProfileConstants(double p, double i, double d, double vFF, double aFF, double dFF, double gravityFF,
			double positionEps, double velocityEps) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.vFF = vFF;
		this.aFF = aFF;
		this.dFF = dFF;
		this.gravityFF = gravityFF;
		this.positionEps = positionEps;
		this.velocityEps = velocityEps;
	}
}
