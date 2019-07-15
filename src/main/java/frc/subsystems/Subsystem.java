//Import everything from the subsystems folder
package frc.subsystems;

/* 
  *Create an abstract class called Subsystem
 * which every subsytem can extend from
 */

public abstract class Subsystem {

	/*
	 * Create an abstract class that initializes everything in the subsystem
	 */
	public abstract void firstCycle();

	// calculates (do everything)
	public abstract void calculate();

	// disables
	public abstract void disable();

}
