package org.example.temperature.controller;

import java.util.concurrent.TimeUnit;

import org.example.temperature.configuration.TemperatureConfiguration;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemperatureControllerImpl
		implements PeriodicRunnable, TemperatureConfiguration, DeviceListener<GenericDevice> {

	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for coolers dependency */
	private Cooler[] coolers;

	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";
	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";
	/**
	 * Temperatures wanted for each room
	 */
	private double temperatureWantedKitchen = 288.15d;
	private double temperatureWantedLivingRoom = 296.15d;
	private double temperatureWantedBedroom = 293.15d;
	private double temperatureWantedBathroom = 291.15d;
	private double erreur_preced, somme_erreur = 0.0d;

	/**
	* The temperature wanted in a room in kelvin:
	**/

	/** Bind Method for thermometers dependency */
	public synchronized void bindThermometer(Thermometer thermometer, Map<Object, Object> properties) {
		System.out.println("bind thermometer :" + thermometer.getSerialNumber());
		thermometer.addListener(this);
	}

	/** Unbind Method for thermometers dependency */
	public synchronized void unbindThermometer(Thermometer thermometer, Map<Object, Object> properties) {
		System.out.println("unbind thermometer :" + thermometer.getSerialNumber());
		thermometer.removeListener(this);
	}

	/** Bind Method for coolers dependency */
	public void bindCooler(Cooler cooler, Map<Object, Object> properties) {
		System.out.println("bind cooler :" + cooler.getSerialNumber());
		setCoolerPower(cooler);
		cooler.addListener(this);
	}

	/** Unbind Method for coolers dependency */
	public void unbindCooler(Cooler cooler, Map<Object, Object> properties) {
		System.out.println("unbind cooler :" + cooler.getSerialNumber());
		cooler.removeListener(this);
	}

	/** Bind Method for heaters dependency */
	public void bindHeater(Heater heater, Map<Object, Object> properties) {
		System.out.println("bind heater :" + heater.getSerialNumber());
		setHeaterPower(heater);
		heater.addListener(this);
	}

	/** Unbind Method for heaters dependency */
	public void unbindHeater(Heater heater, Map<Object, Object> properties) {
		System.out.println("unbind heater :" + heater.getSerialNumber());
		heater.removeListener(this);
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Temperature controller is stoping ...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Temperature controller is starting ...");
		System.out.println("test");
	}

	@Override
	public void run() {
		System.out.println("in function run");
		for (Thermometer thermometer : thermometers) {
			String thermometerLocation = (String) thermometer.getPropertyValue(LOCATION_PROPERTY_NAME);
			setTemperatureChangementFromLocation(thermometerLocation);
		}

	}

	@Override
	public long getPeriod() {
		System.out.println("In function getPeriod.");
		return 300;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.SECONDS;
	}

	@Override
	public void setTargetedTemperature(String targetedRoom, double temperature) {
		switch (targetedRoom) {
		case "kitchen":
			System.out.println("Val temp Kitchen: "+temperature);
			temperatureWantedKitchen = temperature;
			break;
		case "livingroom":
			temperatureWantedLivingRoom = temperature;
			break;
		case "bedroom":
			temperatureWantedBedroom = temperature;
			break;
		case "bathroom":
			temperatureWantedBathroom = temperature;
			break;
		default:
			System.out.println("Piece non reconnu");
		}
	}

	@Override
	public double getTargetedTemperature(String room) {
		double res = 0.0d;
		switch (room) {
		case "kitchen":
			res = temperatureWantedKitchen;
			break;
		case "livingroom":
			res = temperatureWantedLivingRoom;
			break;
		case "bedroom":
			res = temperatureWantedBedroom;
			break;
		case "bathroom":
			res = temperatureWantedBathroom;
			break;
		default:
			System.out.println("Piece non reconnu");
		}
		System.out.println("gettemp res :" + res);
		return res;
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
	}

	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		if (device instanceof Thermometer) {
			Thermometer changingThermometer = (Thermometer) device;
			if (propertyName.equals(Thermometer.LOCATION_PROPERTY_NAME)) {
				if (!changingThermometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(LOCATION_UNKNOWN)) {
					setTemperatureChangementFromLocation((String) newValue);
				}
				setTemperatureChangementFromLocation((String) oldValue);
			}
		} else if (device instanceof Cooler) {
			Cooler changingCooler = (Cooler) device;
			if (propertyName.equals(Cooler.LOCATION_PROPERTY_NAME)) {
				if (newValue.equals(LOCATION_UNKNOWN)) {
					changingCooler.setPowerLevel(0.0d);
				}
				setCoolerPower(changingCooler);
			}
		} else if (device instanceof Heater) {
			Heater changingHeater = (Heater) device;
			if (propertyName.equals(Heater.LOCATION_PROPERTY_NAME)) {
				if (newValue.equals(LOCATION_UNKNOWN)) {
					changingHeater.setPowerLevel(0.0d);
				}
				setHeaterPower(changingHeater);
			}
		}
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
	}

	/**
	 * 	 * R�cup�re l'ensemble des coolers d'une pi�ce
	 * @param location
	 * @return
	 */
	private synchronized List<Cooler> getCoolersFromlocation(String location) {
		List<Cooler> coolerFromLocation = new ArrayList<Cooler>();
		for (Cooler cooler : coolers) {
			if (cooler.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				coolerFromLocation.add(cooler);
			}
		}
		return coolerFromLocation;
	}

	/**
	 * 	 * R�cup�re l'ensemble des heaters d'une pi�ce
	 * @param location
	 * @return
	 */
	private synchronized List<Heater> getHeatersFromlocation(String location) {
		List<Heater> heatersFromLocation = new ArrayList<Heater>();
		for (Heater heater : heaters) {
			if (heater.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				heatersFromLocation.add(heater);
			}
		}
		return heatersFromLocation;
	}

	/**
	 * 	 * R�cup�re l'ensemble un thermom�tre d'une pi�ce
	 * @param location
	 * @return
	 */
	private synchronized Thermometer getThermometerFromlocation(String location) {
		Thermometer thermometerFromLocation = null;
		for (Thermometer thermometer : thermometers) {
			if (thermometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				thermometerFromLocation = thermometer;
				break;
			}
		}
		return thermometerFromLocation;
	}
	
	private double correcteur(double temperatureCapturing, double targetTemperature, double erreur_preced ) {
		double commande, erreur, variations_erreur;
		double Kp=0.08;
		double Ki=0.0;
		double Kd=0.0;
		erreur=Math.abs(targetTemperature-temperatureCapturing);
		somme_erreur +=erreur;
		variations_erreur = erreur - erreur_preced;
		commande=Kp*erreur + Ki*somme_erreur + Kd*variations_erreur;
		if (commande > 1.0) {
			commande=1.0;
		}
		System.out.println("\n\rValeur Kp : "+Kp);
		System.out.println("\n\rValeur Ki : "+Ki);
		System.out.println("\n\rValeur Kd : "+Kd);
		System.out.println("\n\rValeur commande : "+commande);
		return commande;
	}

	/**
	 * Configure le cooler par rapport au thermometre de meme location
	 * @param cooler
	 */
	private void setCoolerPower(Cooler cooler) {
		String coolerLocation = (String) cooler.getPropertyValue(LOCATION_PROPERTY_NAME);
		Thermometer thermometerFromLocation = null;
		thermometerFromLocation = getThermometerFromlocation(coolerLocation);
		if (thermometerFromLocation != null) {
			double temperatureCapturing = thermometerFromLocation.getTemperature();
			double commande = correcteur(temperatureCapturing,getTargetedTemperature(coolerLocation),erreur_preced);
			if (temperatureCapturing > getTargetedTemperature(coolerLocation)) {
				System.out.println("\n\ril faut refroidir");
				cooler.setPowerLevel(commande);
			} else {
				cooler.setPowerLevel(0.0d);
			}
			erreur_preced=temperatureCapturing;
		}
	}

	/**
	 * Configure le cooler par rapport au thermometre de meme location
	 * @param heater
	 */
	private void setHeaterPower(Heater heater) {
		String heaterLocation = (String) heater.getPropertyValue(LOCATION_PROPERTY_NAME);
		Thermometer thermometerFromLocation = null;
		thermometerFromLocation = getThermometerFromlocation(heaterLocation);
		if (thermometerFromLocation != null) {
			double temperatureCapturing = thermometerFromLocation.getTemperature();
			double commande = correcteur(temperatureCapturing,getTargetedTemperature(heaterLocation), erreur_preced);
			if (temperatureCapturing < getTargetedTemperature(heaterLocation)) {
				heater.setPowerLevel(commande);
			} else {
				heater.setPowerLevel(0.0d);
			}
			erreur_preced=temperatureCapturing;
		}
	}

	private void setTemperatureChangementFromLocation(String location) {
		for (Cooler cooler : getCoolersFromlocation(location)) {
			setCoolerPower(cooler);
		}
		for (Heater heater : getHeatersFromlocation(location)) {
			setHeaterPower(heater);
		}
	}

}
