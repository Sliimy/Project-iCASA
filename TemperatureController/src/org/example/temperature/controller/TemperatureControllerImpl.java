package org.example.temperature.controller;

import java.util.concurrent.TimeUnit;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemperatureControllerImpl implements PeriodicRunnable, DeviceListener<GenericDevice> {

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
	public static final double TEMPERATURE_WANTED_KICHEN = 288.15d;
	public static final double TEMPERATURE_WANTED_BATHROOM = 296.15d;
	public static final double TEMPERATURE_WANTED_BEDROOM = 293.15d;
	public static final double TEMPERATURE_WANTED_LIVINGROOM = 291.15d;
	/**
	* The temperature wanted in a room in kelvin:
	**/
	private double temperatureWanted = 296.15d;
	
	/** Bind Method for thermometers dependency */
	public synchronized void bindThermometer(Thermometer thermometer, Map<Object, Object> properties) {
		System.out.println("bind thermometer :"+thermometer.getSerialNumber());
		thermometer.addListener(this);
	}

	/** Unbind Method for thermometers dependency */
	public synchronized void unbindThermometer(Thermometer thermometer, Map<Object, Object> properties) {
		System.out.println("unbind thermometer :"+thermometer.getSerialNumber());
		thermometer.removeListener(this);
	}

	/** Bind Method for coolers dependency */
	public void bindCooler(Cooler cooler, Map<Object, Object> properties) {
		System.out.println("bind cooler :"+cooler.getSerialNumber());
		setCoolerPower(cooler);
		cooler.addListener(this);
	}

	/** Unbind Method for coolers dependency */
	public void unbindCooler(Cooler cooler, Map<Object, Object> properties) {
		System.out.println("unbind cooler :"+cooler.getSerialNumber());
		cooler.removeListener(this);
	}

	/** Bind Method for heaters dependency */
	public void bindHeater(Heater heater, Map<Object, Object> properties) {
		System.out.println("bind heater :"+heater.getSerialNumber());
		setHeaterPower(heater);
		heater.addListener(this);
	}

	/** Unbind Method for heaters dependency */
	public void unbindHeater(Heater heater, Map<Object, Object> properties) {
		System.out.println("unbind heater :"+heater.getSerialNumber());
		heater.removeListener(this);
	}
	
	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Temperature controller is stoping ...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Temperature controller is starting ...");
	}

	@Override
	public void run() {
		System.out.println("in function run");
		for (Thermometer thermometer : thermometers) {
			String thermometerLocation=(String) thermometer.getPropertyValue(LOCATION_PROPERTY_NAME);
			setTemperatureChangementFromLocation(thermometerLocation);
		}
		
	}

	@Override
	public long getPeriod() {
		System.out.println("In function getPeriod.");
		return 3000;
	}

	@Override
	public TimeUnit getUnit() {
		return TimeUnit.SECONDS;
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
			Thermometer changingThermometer=(Thermometer) device;
			if (propertyName.equals(Thermometer.LOCATION_PROPERTY_NAME)) {
				if(!changingThermometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(LOCATION_UNKNOWN)){
					setTemperatureChangementFromLocation((String)newValue);				
				}
				setTemperatureChangementFromLocation((String)oldValue); 
			}
		}else if (device instanceof Cooler) {
			Cooler changingCooler = (Cooler) device;
			if (propertyName.equals(Cooler.LOCATION_PROPERTY_NAME)) {
				if (newValue.equals(LOCATION_UNKNOWN)) {
					changingCooler.setPowerLevel(0.0d);
				}
				setCoolerPower(changingCooler);
			}
		}else if (device instanceof Heater) {
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
	 * 	 * Récupère l'ensemble des coolers d'une pièce
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
	 * 	 * Récupère l'ensemble des heaters d'une pièce
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
	 * 	 * Récupère l'ensemble un thermomètre d'une pièce
	 * @param location
	 * @return
	 */
	private synchronized Thermometer getThermometerFromlocation(String location) {
		Thermometer thermometerFromLocation=null;
		for (Thermometer thermometer : thermometers) {
			if (thermometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				thermometerFromLocation=thermometer;
				break;
			}
		}
		return thermometerFromLocation;
	}
	
	/**
	 * Configure le cooler par rapport au thermometre de meme location
	 * @param cooler
	 */
	private void setCoolerPower(Cooler cooler) {
		String coolerLocation = (String) cooler.getPropertyValue(LOCATION_PROPERTY_NAME);
		Thermometer thermometerFromLocation = null;
		thermometerFromLocation = getThermometerFromlocation(coolerLocation);
		if (thermometerFromLocation!=null) {			
			double temperatureCapturing=thermometerFromLocation.getTemperature();
			if(temperatureCapturing>getTemperatureWantedFromLocation(coolerLocation)+1) {
				cooler.setPowerLevel(0.1d);
			}else {
				cooler.setPowerLevel(0.0d);
			}
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
		if (thermometerFromLocation!=null) {			
			double temperatureCapturing=thermometerFromLocation.getTemperature();
			if(temperatureCapturing<getTemperatureWantedFromLocation(heaterLocation)-1) {
				heater.setPowerLevel(0.1d);
			}else {
				heater.setPowerLevel(0.0d);
			}
		}
	}
	
	private void setTemperatureChangementFromLocation(String location) {
		for(Cooler cooler : getCoolersFromlocation(location)) {
			setCoolerPower(cooler);
		}
		for (Heater heater : getHeatersFromlocation(location)) {
			setHeaterPower(heater);
		}
	}
	
	private double getTemperatureWantedFromLocation(String location) {
		double res=0.0d;
		switch (location){
		case "kitchen":
			res=TEMPERATURE_WANTED_KICHEN;
			break;
		case "livingroom":
			res=TEMPERATURE_WANTED_LIVINGROOM;
			break;
		case "bedroom":
			res=TEMPERATURE_WANTED_BEDROOM;
			break;
		case "bathroom":
			res=TEMPERATURE_WANTED_BATHROOM;
			break;
		}
		System.out.println("gettemp res :"+res);
		return res;
	}
	
}
