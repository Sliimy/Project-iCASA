package org.example.follow.me.administration;

import org.example.follow.me.illuminance.IlluminanceGoal;

/**
* The Interface FollowMeAdministration allows the administrator to configure
* its preference regarding the management of the Follow Me application.
*/
public interface FollowMeAdministration {

   /**
    * Sets the illuminance preference. The manager will try to adjust the
    * illuminance in accordance with this goal.
    * 
    * @param illuminanceGoal
    *            the new illuminance preference
    */
   public void setIlluminancePreference(IlluminanceGoal illuminanceGoal);

   /**
    * Get the current illuminance preference.
    * 
    * @return the new illuminance preference
    */
   public IlluminanceGoal getIlluminancePreference();

}