package org.example.follow.me.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.follow.me.algorithm.ClosestSumAlgorithm;
import org.example.follow.me.configuration.FollowMeConfiguration;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.light.Photometer;
import fr.liglab.adele.icasa.service.zone.size.calculator.ZoneSizeCalculator;

public class FollowMeImpl implements FollowMeConfiguration, DeviceListener<GenericDevice> {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;
	private int maximumNumberOfLightsToTurnOn = 3;
	/**
	* The maximum energy consumption allowed in a room in Watt:
	**/
	private double maximumEnergyInRoom = 200.0d;
	/**
	* The maximum power of a light P:
	**/
	private double maximumPowerlight = 100.0d;
	/**
	* The targeted illuminance in each room
	**/
	private double targetedIlluminance = 10000.0d;
	/**
	 * Watt to lumens conversion factor
	 * It has been considered that: 1 Watt=680.0 lumens at 555nm.
	 */
	public final static double ONE_WATT_TO_ONE_LUMEN = 680.0d;

	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";

	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";
	private HashMap<String, Double> binaryLightsIDWithEnergyCunsemption = new HashMap<String, Double>();
	private HashMap<String, Double> binaryLightsIDWithIlluminanceParM2 = new HashMap<String, Double>();
	/** Field for photometers dependency */
	private Photometer[] photometers;
	/** Field for zoneSizeCalculator dependency */
	private ZoneSizeCalculator zoneSizeCalculator;

	@Override
	public int getMaximumNumberOfLightsToTurnOn() {
		return this.maximumNumberOfLightsToTurnOn;
	}

	@Override
	public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
		this.maximumNumberOfLightsToTurnOn = maximumNumberOfLightsToTurnOn;
	}

	@Override
	public double getMaximumAllowedEnergyInRoom() {
		return this.maximumEnergyInRoom;
	}

	@Override
	public void setMaximumAllowedEnergyInRoom(double maximumEnergy) {
		this.maximumEnergyInRoom = maximumEnergy;
	}

	@Override
	public double getTargetedIlluminance() {
		return this.targetedIlluminance;
	}

	@Override
	public void setTargetedIlluminance(double illuminance) {
		this.targetedIlluminance = illuminance;
	}

	/** Bind Method for binaryLights dependency */
	public synchronized void bindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
		Double energyCunsemption = new Double(0d);
		energyCunsemption = Math.random() * maximumPowerlight / 2.0d + 50.0d;
		binaryLightsIDWithEnergyCunsemption.put(binaryLight.getSerialNumber(), energyCunsemption);
		binaryLightsIDWithIlluminanceParM2.put(binaryLight.getSerialNumber(),
				1.0d * ONE_WATT_TO_ONE_LUMEN * energyCunsemption);
		binaryLight.addListener(this);
	}

	/** Unbind Method for binaryLights dependency */
	public synchronized void unbindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
		binaryLight.removeListener(this);
	}

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeListener(this);
	}

	/** Bind Method for dimmerLifhts dependency */
	public synchronized void bindDimmerLight(DimmerLight dimmerLight, Map<Object, Object> properties) {
		System.out.println("bind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.addListener(this);

	}

	/** Unbind Method for dimmerLifhts dependency */
	public synchronized void unbindDimmerLight(DimmerLight dimmerLight, Map<Object, Object> properties) {
		System.out.println("unbind dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.removeListener(this);
	}

	/** Bind Method for photometers dependency */
	public void bindPhotometer(Photometer photometer, Map<Object, Object> properties) {
		System.out.println("bind photometer " + photometer.getSerialNumber());
	}

	/** Unbind Method for photometers dependency */
	public void unbindPhotometer(Photometer photometer, Map<Object, Object> properties) {
		System.out.println("unbind photometer " + photometer.getSerialNumber());
	}


	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component is stopping...");
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
		for (BinaryLight binaryLight : binaryLights) {
			binaryLight.removeListener(this);
		}
		for (PresenceSensor presenceSensor : presenceSensors) {
			presenceSensor.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println(" <<<<<<<<<<<<<<<<<Component is starting BINARY...");
	}

	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {
		if (device instanceof PresenceSensor) {
			PresenceSensor changingSensor = (PresenceSensor) device;
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				System.out.println(
						"The device with the serial number : " + changingSensor.getSerialNumber() + " has changed");
				System.out.println("This sensor is in the room:" + detectorLocation);
				setStatusLightsFromPresenceSensor(changingSensor);
			}
			//TODO: gérer bouger les detecteur
		} else if (device instanceof BinaryLight) {
			BinaryLight changingLight = (BinaryLight) device;
			if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)) {
				if (newValue.equals(LOCATION_UNKNOWN)) {
					changingLight.setPowerStatus(false);
				}
				for (PresenceSensor presenceSensor : presenceSensors) {
					if (presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals((String) oldValue)
							|| presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals((String) newValue))
						;
					setStatusLightsFromPresenceSensor(presenceSensor);
				}
			}
		} else if (device instanceof DimmerLight) {
			DimmerLight changingDL = (DimmerLight) device;
			if (propertyName.equals(BinaryLight.LOCATION_PROPERTY_NAME)) {
				if (newValue.equals(LOCATION_UNKNOWN)) {
					changingDL.setPowerLevel(0.0d);
				}
				for (PresenceSensor presenceSensor : presenceSensors) {
					if (presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals((String) oldValue)
							|| presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME).equals((String) newValue))
						;
					setStatusLightsFromPresenceSensor(presenceSensor);
				}
			}
		}
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
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
	}

	/**
	 * 	 * Récupère l'ensemble des Binary lights d'une pièce
	 * @param location
	 * @return
	 */
	private synchronized List<BinaryLight> getBinaryLightFromlocation(String location) {
		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binaryLight);
			}
		}
		return binaryLightsLocation;
	}

	/**
	 * Récupère l'ensemble des dimmer lights d'une pièce
	 * @param location
	 * @return
	 */
	private synchronized List<DimmerLight> getDimmerLightFromlocation(String location) {
		List<DimmerLight> dimmerLightsFromLocation = new ArrayList<DimmerLight>();
		for (DimmerLight dimmerLight : dimmerLights) {
			if (dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				dimmerLightsFromLocation.add(dimmerLight);
			}
		}
		return dimmerLightsFromLocation;
	}

	/**
	 *Permet d'avoir le nombre de Binary Light allumé dans une pièce 
	 * @param location
	 * @return
	 */
	public int getNumberBinaryLightONFromLocation(String location) {
		int res = 0;
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPowerStatus() && binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res++;
		}
		return res;
	}

	/**
	 * Permet d'avoir le nombre de lumière allumé dans une pièce
	 * @param location
	 * @return
	 */
	public int getNumberLightONFromLocation(String location) {
		int res = 0;
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPowerStatus() && binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res++;
		}
		for (DimmerLight dimmerLight : dimmerLights) {
			if (dimmerLight.getPowerLevel() != 0
					&& dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res++;
		}
		return res;
	}

	/**
	 * Retourne l'énergie consommé par les lumières dans une salle
	 * @param location
	 * @return
	 */

	public double getPowerConsumptionONFromLocation(String location) {
		double res = 0;
		for (BinaryLight binaryLight : binaryLights) {
			if (binaryLight.getPowerStatus() && binaryLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res = res + binaryLightsIDWithEnergyCunsemption.get(binaryLight.getSerialNumber());
		}
		for (DimmerLight dimmerLight : dimmerLights) {
			if (dimmerLight.getPowerLevel() != 0
					&& dimmerLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location))
				res = res + dimmerLight.getPowerLevel() * 100.0d;
		}
		return res;
	}

	public double getIlluminanceFromLocation(String location) {
		double res = 0.0d;
		for (Photometer photometer : photometers) {
			if (photometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				res = photometer.getIlluminance();
				break;
			}
		}
		return res;
	}

	/**
	 * Allume les lumières d'une pices en respectant la condition de nombre de lumière maximum allumé dans cette pièce
	 * @param presenceSensor
	 */
	public void setStatusLightsFromPresenceSensor(PresenceSensor presenceSensor) {
		System.out.println(">>>>>>>>> Dans la fonction setStatus");
		String detectorLocation = (String) presenceSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
		double areaZone = zoneSizeCalculator.getSurfaceInMeterSquare(detectorLocation);
		System.out.println("test 2");
		List<BinaryLight> binaryLightsLocation = getBinaryLightFromlocation(detectorLocation);
		HashMap<String, Double> binaryLightsIDWithIlluminanceFromLocation = new HashMap<String, Double>();
		for (BinaryLight binaryLight : binaryLightsLocation) {
			binaryLightsIDWithIlluminanceFromLocation.put(binaryLight.getSerialNumber(),
					binaryLightsIDWithIlluminanceParM2.get(binaryLight.getSerialNumber()) / areaZone);
		}
		ArrayList<String> binaryLightsToTurnOn = ClosestSumAlgorithm.greadySubSetClosestSum(getTargetedIlluminance(),
				binaryLightsIDWithIlluminanceFromLocation);
		System.out.println("valeur illuminance :");
        for (Map.Entry<String,Double> e : binaryLightsIDWithIlluminanceFromLocation.entrySet()) {
        	System.out.println(e.getValue());
        }
        System.out.println("FIN <<<<<<<<<<<<<<<<<<<<<<");
//        for (Map.Entry<String,Double> e : binaryLightsIDWithIlluminanceParM2.entrySet()) {
//        	System.out.println(e.getValue());
//        }
//        System.out.println("FIN <<<<<<<<<<<<<<<<<<<<<<");
		for (BinaryLight binaryLight : binaryLightsLocation) {
			if (binaryLightsToTurnOn.contains(binaryLight.getSerialNumber()) && presenceSensor.getSensedPresence()) {
				binaryLight.setPowerStatus(true);
			} else {
				binaryLight.setPowerStatus(false);
			}
		}
		List<DimmerLight> dimmerLightsLocation = getDimmerLightFromlocation(detectorLocation);
		for (DimmerLight dimmerLight : dimmerLightsLocation) {
			if (presenceSensor.getSensedPresence()) {
				double maximumIlluminanceOfDimmerLightFromLocation = ONE_WATT_TO_ONE_LUMEN * maximumPowerlight
						/ areaZone;
				//				if (maximumEnergyConsumptionAllowedInARoom>=100*getNumberLightONFromLocation(detectorLocation)) {
				if (getIlluminanceFromLocation(detectorLocation) < getTargetedIlluminance()) {
					if (getTargetedIlluminance() - getIlluminanceFromLocation(
							detectorLocation) > maximumIlluminanceOfDimmerLightFromLocation) {
						dimmerLight.setPowerLevel(1.0d);
					} else if (getTargetedIlluminance() - getIlluminanceFromLocation(detectorLocation) > 0.0d) {
						dimmerLight
								.setPowerLevel((getTargetedIlluminance() - getIlluminanceFromLocation(detectorLocation))
										* areaZone / (ONE_WATT_TO_ONE_LUMEN * maximumPowerlight));
					}
				}
			} else {
				dimmerLight.setPowerLevel(0.0d);
			}
		}
	}
}
