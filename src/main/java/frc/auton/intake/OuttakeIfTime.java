/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.auton.intake;

import frc.auton.AutonCommand;
import frc.auton.RobotComponent;
import frc.io.RobotOutput;
import frc.io.SensorInput;
import frc.subsystems.Intake;
import frc.subsystems.Intake.IntakeState;

/**
 * Add your docs here.
 */
public class OuttakeIfTime extends AutonCommand {

    Intake intake;
    RobotOutput robotOut;
    SensorInput sensorIn;
    private boolean goodToOuttake = false;

    public OuttakeIfTime() {
        super(RobotComponent.INTAKE);

        this.intake = Intake.getInstance();
        this.robotOut = RobotOutput.getInstance();
        this.sensorIn = SensorInput.getInstance();
    }

    @Override
    public void firstCycle() {
        intake.firstCycle();
        if(this.sensorIn.getTimeSinceAutoStarted() < 14400){
            this.goodToOuttake = true;
        }
    }

    @Override
    public boolean calculate() {

        if (this.goodToOuttake) {
            this.intake.setCurrentState(IntakeState.OUTTAKE_PANEL);
            this.intake.calculate();
            return this.intake.outtakePanelDone();
        } else {
            return false;
        }

    }

    @Override
    public void override() {

    }
}
