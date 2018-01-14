package org.example.follow.me.command;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.example.follow.me.administration.FollowMeAdministration;
import org.example.follow.me.illuminance.IlluminanceGoal;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "follow.me.mananger.command")
//Use the handler command and declare the command as a command provider. The
//namespace is used to prevent name collision.
@CommandProvider(namespace = "followme")
public class FollowMeCommandImpl {

	// Declare a dependency to a FollowMeAdministration service
	@Requires
	private FollowMeAdministration m_administrationService;

	/**
	* Felix shell command implementation to sets the illuminance preference.
	*
	* @param goal the new illuminance preference ("SOFT", "MEDIUM", "FULL")
	*/

	// Each command should start with a @Command annotation
	@Command
	public void setIlluminancePreference(String goal) {
		// The targeted goal
		IlluminanceGoal illuminanceGoal = null;
		Boolean fail = false;
		// TODO : Here you have to convert the goal string into an illuminance
		// goal and fail if the entry is not "SOFT", "MEDIUM" or "HIGH"
		if (goal.equals("SOFT")) {
			illuminanceGoal = IlluminanceGoal.SOFT;
		} else if (goal.equals("MEDIUM")) {
			illuminanceGoal = IlluminanceGoal.MEDIUM;
		} else if (goal.equals("HIGH")) {
			illuminanceGoal = IlluminanceGoal.FULL;
		} else {
			fail = true;
		}
		//call the administration service to configure it :
		if (!fail) {
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else {
			System.out.println("you must choose between \"SOFT\",\"MEDIUM\" and \"HIGH\"");
		}
	}

	@Command
	public void getIlluminancePreference() {
		//TODO : implement the command that print the current value of the goal
		System.out.println("The illuminance goal is " + m_administrationService.getIlluminancePreference()); //...
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component Command stoping");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component Command starting");
	}

}