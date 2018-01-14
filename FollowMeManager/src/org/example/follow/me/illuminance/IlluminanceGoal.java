package org.example.follow.me.illuminance;

/**
 * This enum describes the different illuminance goals associated with the
 * manager.
 */
public enum IlluminanceGoal {
 
    /** The goal associated with soft illuminance. */
    SOFT(1,6000.0d),
    /** The goal associated with medium illuminance. */
    MEDIUM(2,10000d),
    /** The goal associated with full illuminance. */
    FULL(3,30000.0d);
 
    /** The number of lights to turn on. */
    private int numberOfLightsToTurnOn;
    private double targetedIlluminance;
 
    /**
     * Gets the number of lights to turn On.
     * 
     * @return the number of lights to turn On.
     */
    public int getNumberOfLightsToTurnOn() {
        return numberOfLightsToTurnOn;
    }
    public double getTargetedIlluminance() {
        return targetedIlluminance;
    }
 
    /**
     * Instantiates a new illuminance goal.
     * 
     * @param numberOfLightsToTurnOn
     *            the number of lights to turn on.
     */
    private IlluminanceGoal(int numberOfLightsToTurnOn,double targetedIlluminance) {
        this.numberOfLightsToTurnOn = numberOfLightsToTurnOn;
        this.targetedIlluminance = targetedIlluminance;
    }
}