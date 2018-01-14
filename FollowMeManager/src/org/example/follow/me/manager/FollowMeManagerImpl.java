package org.example.follow.me.manager;

import org.example.follow.me.administration.FollowMeAdministration;
import org.example.follow.me.configuration.FollowMeConfiguration;
import org.example.follow.me.illuminance.IlluminanceGoal;

import java.util.Map;

public class FollowMeManagerImpl implements FollowMeAdministration {

	/** Field for followMeConfigurationServices dependency */
	private FollowMeConfiguration followMeConfigurationServices;
	private IlluminanceGoal IlluminanceGoal;

	@Override
	public void setIlluminancePreference(IlluminanceGoal illuminanceGoal) {
		this.IlluminanceGoal = illuminanceGoal;
		if (illuminanceGoal==IlluminanceGoal.FULL){
			followMeConfigurationServices.setTargetedIlluminance(30000.0d);
		}
		if (illuminanceGoal==IlluminanceGoal.MEDIUM){
			followMeConfigurationServices.setTargetedIlluminance(10000.0d);
		}
		if (illuminanceGoal==IlluminanceGoal.SOFT){
			followMeConfigurationServices.setTargetedIlluminance(6000.0d);
		}
	}

	@Override
	public IlluminanceGoal getIlluminancePreference() {
		return this.IlluminanceGoal;
	}

	/** Bind Method for followMeConfigurationServices dependency */
	public void bindFollowMeConfiguration(FollowMeConfiguration followMeConfiguration, Map<Object, Object> properties) {
		// TODO: Add your implementation code here
	}

	/** Unbind Method for followMeConfigurationServices dependency */
	public void unbindFollowMeConfiguration(FollowMeConfiguration followMeConfiguration,
			Map<Object, Object> properties) {
		// TODO: Add your implementation code here
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component Manager stoping");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component Manager starting");
	}

}
