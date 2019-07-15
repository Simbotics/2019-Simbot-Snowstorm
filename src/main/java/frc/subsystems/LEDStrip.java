package frc.subsystems;

import frc.io.RobotOutput;

public class LEDStrip {

    private static LEDStrip instance;
    private RobotOutput robotOut;

    public enum LEDColourState {
        GETTING_BALL, HAS_BALL, HAS_PANEL, VISION_NOT_AIMED, VISION_MOVING, VISION_TURNING, VISION_COMPLETE, CLIMBING,
        OFF, DRIVER_TURNING,
    }

    private LEDStrip() {
        this.robotOut = RobotOutput.getInstance();
    }

    public static LEDStrip getInstance() {
        if (instance == null) {
            instance = new LEDStrip();
        }
        return instance;
    }

    public void setLed(LEDColourState state) {
        switch (state) {
        case GETTING_BALL:
            this.robotOut.setLEDStrip(0.65); // ORANGE
            break;
        case HAS_BALL:
            this.robotOut.setLEDStrip(0.73); // LIME
            break;
        case HAS_PANEL:
            this.robotOut.setLEDStrip(0.91); // VIOLET
            break;
        case DRIVER_TURNING:
            this.robotOut.setLEDStrip(0.81); // AQUA
            break;
        case VISION_NOT_AIMED: // RED
            this.robotOut.setLEDStrip(0.61);
            break;
        case VISION_MOVING: // ORANGE
            this.robotOut.setLEDStrip(0.65);
            break;
        case VISION_TURNING: // ORANGE
            this.robotOut.setLEDStrip(0.65);
            break;
        case VISION_COMPLETE: // GREEN
            this.robotOut.setLEDStrip(0.77);
            break;
        case CLIMBING: // Confetti
            this.robotOut.setLEDStrip(-0.89);
            break;
        case OFF:
        default:
            robotOut.setLEDStrip(0.99); // black
            break;
        }
    }

}