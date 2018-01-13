package org.example.temperature.manager.administration;

import org.example.temperature.configuration.TemperatureConfiguration;
import org.example.temperature.manager.administration.service.TemperatureManagerAdministration;

public class TemperatureManagerImpl implements TemperatureManagerAdministration {

	/** Field for temperatureConfiguration dependency */
	private TemperatureConfiguration temperatureConfiguration;

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Temperature Manager is stoping ...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Temperature Manager is starting ...");
	}

	@Override
	public void temperatureIsTooHigh(String roomName) {
		double actualTemperature = temperatureConfiguration.getTargetedTemperature(roomName);
		double newTemperature = actualTemperature - 5.0d;
		//double newTemperature = 293.15d;
		temperatureConfiguration.setTargetedTemperature(roomName, newTemperature);
		System.out.println("New temperature in room " + roomName + " is " + newTemperature);
	}

	@Override
	public void temperatureIsTooLow(String roomName) {
		double actualTemperature = temperatureConfiguration.getTargetedTemperature(roomName);
		//double newTemperature = 270.00d;
		double newTemperature = actualTemperature + 5.0d;
		temperatureConfiguration.setTargetedTemperature(roomName, newTemperature);
		System.out.println("New temperature in room " + roomName + " is " + newTemperature);
	}

}
