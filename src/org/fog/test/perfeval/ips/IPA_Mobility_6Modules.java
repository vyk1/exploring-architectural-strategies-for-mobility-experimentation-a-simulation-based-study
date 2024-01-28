package org.fog.test.perfeval.ips;

/**
 * Based on CHM - No Microservices
 * Edited by Victoria Martins 2023
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.mobilitydata.DataParser;
import org.fog.mobilitydata.RandomMobilityGenerator;
import org.fog.mobilitydata.References;
import org.fog.placement.LocationHandler;
import org.fog.placement.MobilityController;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementMobileEdgewards;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;
import org.json.simple.parser.ParseException;

public class IPA_Mobility_6Modules {
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	static Map<Integer, Integer> userMobilityPattern = new HashMap<Integer, Integer>();
	static LocationHandler locator;

	static int CLOUD_USERS = 1;
	static double SENSOR_TRANSMISSION_TIME = 10;
	static int numberOfMobileUser = 25;
//    static int numberOfMobileUser = 5;

	// if random mobility generator for users is True, new random dataset will be
	// created for each user
	static boolean randomMobility_generator = false; // To use random datasets
	static boolean renewDataset = true; // To overwrite existing random datasets

	public static void main(String[] args) {

		String appId = "Indoor Positioning Application @ UFSC BR"; // identifier of the application

		try {
			Log.printLine("Starting " + appId);
			Log.disable();
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(CLOUD_USERS, calendar, trace_flag);

			FogBroker broker = new FogBroker("broker");

			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());

			DataParser dataObject = new DataParser();
			locator = new LocationHandler(dataObject);

			String datasetReference = References.dataset_reference;

			if (randomMobility_generator) {
				datasetReference = References.dataset_random;
				createRandomMobilityDatasets(References.random_walk_mobility_model, datasetReference, renewDataset);
			}

			createMobileUser(broker.getId(), appId, datasetReference);
			createFogDevices(broker.getId(), appId);

			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping

			moduleMapping.addModuleToDevice("database", "cloud");

			MobilityController controller = new MobilityController("master-controller", fogDevices, sensors, actuators,
					locator);

			controller.submitApplication(application, 0,
					(new ModulePlacementMobileEdgewards(fogDevices, sensors, actuators, application, moduleMapping)));

			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			Log.printLine(appId + " finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	private static void createRandomMobilityDatasets(int mobilityModel, String datasetReference, boolean renewDataset)
			throws IOException, ParseException {
		RandomMobilityGenerator randMobilityGenerator = new RandomMobilityGenerator();
		for (int i = 0; i < numberOfMobileUser; i++) {

			randMobilityGenerator.createRandomData(mobilityModel, i + 1, datasetReference, renewDataset);
		}
	}

	private static void createMobileUser(int userId, String appId, String datasetReference) throws IOException {

		for (int id = 1; id <= numberOfMobileUser; id++)
			userMobilityPattern.put(id, References.DIRECTIONAL_MOBILITY);

		locator.parseUserInfo(userMobilityPattern, datasetReference);

		List<String> mobileUserDataIds = locator.getMobileUserDataId();

		for (int i = 0; i < numberOfMobileUser; i++) {
			FogDevice mobile = addMobile("mobile_" + i, userId, appId, References.NOT_SET); // adding mobiles to the
																							// physical topology.
																							// Smartphones have been
																							// modeled as fog devices as
																							// well.
			mobile.setUplinkLatency(2); // latency of connection between the smartphone and proxy server is 2 ms
			locator.linkDataWithInstance(mobile.getId(), mobileUserDataIds.get(i));
			mobile.setLevel(3);

			fogDevices.add(mobile);
		}

	}

	/**
	 * Creates the fog devices in the physical topology of the simulation.
	 *
	 * @param userId
	 * @param appId
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private static void createFogDevices(int userId, String appId) throws NumberFormatException, IOException {

		locator.parseResourceInfo();

		if (locator.getLevelWiseResources(locator.getLevelID("Cloud")).size() == 1) {

			FogDevice cloud = createFogDevice("cloud", 44800, 40000, 100, 10000, 0.01, 16 * 103, 16 * 83.25); // creates
																												// the
																												// fog
																												// device
																												// Cloud
																												// at
																												// the
																												// apex
																												// of
																												// the
																												// hierarchy
																												// with
																												// level=0
			cloud.setParentId(References.NOT_SET);
			locator.linkDataWithInstance(cloud.getId(),
					locator.getLevelWiseResources(locator.getLevelID("Cloud")).get(0));
			cloud.setLevel(0);
			fogDevices.add(cloud);

			for (int i = 0; i < locator.getLevelWiseResources(locator.getLevelID("Proxy")).size(); i++) {

				FogDevice proxy = createFogDevice("proxy-server_" + i, 2800, 4000, 10000, 10000, 0.0, 107.339, 83.4333); // creates
																															// the
																															// fog
																															// device
																															// Proxy
																															// Server
																															// (level=1)
				locator.linkDataWithInstance(proxy.getId(),
						locator.getLevelWiseResources(locator.getLevelID("Proxy")).get(i));
				proxy.setParentId(cloud.getId()); // setting Cloud as parent of the Proxy Server
				proxy.setUplinkLatency(100); // latency of connection from Proxy Server to the Cloud is 100 ms
				proxy.setLevel(1);
				fogDevices.add(proxy);

			}

			for (int i = 0; i < locator.getLevelWiseResources(locator.getLevelID("Gateway")).size(); i++) {

				FogDevice gateway = createFogDevice("gateway_" + i, 2800, 4000, 10000, 10000, 0.0, 107.339, 83.4333);
				locator.linkDataWithInstance(gateway.getId(),
						locator.getLevelWiseResources(locator.getLevelID("Gateway")).get(i));
				gateway.setParentId(locator.determineParent(gateway.getId(), References.SETUP_TIME));
				gateway.setUplinkLatency(4);
				gateway.setLevel(2);
				fogDevices.add(gateway);
			}

		}

	}

	private static FogDevice addMobile(String name, int userId, String appId, int parentId) {
		FogDevice mobile = createFogDevice(name, 200, 2048, 10000, 270, 0, 87.53, 82.44);
		mobile.setParentId(parentId);
		// locator.setInitialLocation(name,drone.getId());
		Sensor mobileSensor = new Sensor("sensor-" + name, "SENSOR", userId, appId,
				new DeterministicDistribution(SENSOR_TRANSMISSION_TIME)); // inter-transmission time of EEG sensor
																			// follows a deterministic distribution
		sensors.add(mobileSensor);
		Actuator mobileDisplay = new Actuator("actuator-" + name, userId, appId, "DISPLAY");
		actuators.add(mobileDisplay);
		mobileSensor.setGatewayDeviceId(mobile.getId());
		mobileSensor.setLatency(6.0); // latency of connection between EEG sensors and the parent Smartphone is 6 ms
		mobileDisplay.setGatewayDeviceId(mobile.getId());
		mobileDisplay.setLatency(1.0); // latency of connection between Display actuator and the parent Smartphone is 1
										// ms
		return mobile;
	}

	/**
	 * Creates a vanilla fog device
	 *
	 * @param nodeName    name of the device to be used in simulation
	 * @param mips        MIPS
	 * @param ram         RAM
	 * @param upBw        uplink bandwidth
	 * @param downBw      downlink bandwidth
	 * @param level       hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static FogDevice createFogDevice(String nodeName, long mips, int ram, long upBw, long downBw,
			double ratePerMips, double busyPower, double idlePower) {

		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram), new BwProvisionerOverbooking(bw), storage,
				peList, new StreamOperatorScheduler(peList), new FogLinearPowerModel(busyPower, idlePower));

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = -3; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
		// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
		// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(arch, os, vmm, host, time_zone, cost,
				costPerMem, costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, new AppModuleAllocationPolicy(hostList), storageList,
					10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// fogdevice.setLevel(level);
		return fogdevice;
	}

	/**
	 * Function to create the EEG Tractor Beam game application in the DDF model.
	 *
	 * @param appId  unique identifier of the application
	 * @param userId identifier of the user of the application
	 * @return
	 */
	@SuppressWarnings({ "serial" })
	private static Application createApplication(String appId, int userId) {

		Application application = Application.createApplication(appId, userId);

		/*
		 * Adding modules (vertices) to the application model (directed graph)
		 */
		application.addAppModule("clientModule", 128, 150, 100);
		application.addAppModule("kalman_filter", 512, 250, 200);
		application.addAppModule("distance_calc", 512, 250, 200);
		application.addAppModule("trilateration", 512, 250, 200);
		application.addAppModule("position_estim", 512, 350, 200);
		application.addAppModule("database", 2048, 450, 1000);

		/*
		 * Connecting the application modules (vertices) in the application model
		 * (directed graph) with edges
		 */

		application.addAppEdge("SENSOR", "clientModule", 0, 0, "SENSOR", Tuple.UP, AppEdge.SENSOR); // as sensor and
																									// mobile device are
																									// both same. this
																									// is a dummy edge.
		application.addAppEdge("clientModule", "kalman_filter", 1000, 500, "RAW_DATA", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("kalman_filter", "distance_calc", 2000, 500, "FILTERED_DATA1", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("distance_calc", "trilateration", 1800, 500, "FILTERED_DATA2", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("trilateration", "position_estim", 1000, 500, "FILTERED_DATA3", Tuple.UP,
				AppEdge.MODULE);
		application.addAppEdge("position_estim", "database", 500, 500, "PROCESSED_DATA1", Tuple.UP, AppEdge.MODULE);

		application.addAppEdge("position_estim", "clientModule", 14, 500, "PROCESSED_DATA2", Tuple.DOWN,
				AppEdge.MODULE);
		application.addAppEdge("clientModule", "DISPLAY", 14, 500, "RESULT_DISPLAY", Tuple.DOWN, AppEdge.ACTUATOR);

		/*
		 * Defining the input-output relationships (represented by selectivity) of the
		 * application modules.
		 */
		application.addTupleMapping("clientModule", "SENSOR", "RAW_DATA", new FractionalSelectivity(0.9));
		application.addTupleMapping("kalman_filter", "RAW_DATA", "FILTERED_DATA1", new FractionalSelectivity(1.0));
		application.addTupleMapping("distance_calc", "FILTERED_DATA1", "FILTERED_DATA2",
				new FractionalSelectivity(1.0));
		application.addTupleMapping("trilateration", "FILTERED_DATA2", "FILTERED_DATA3",
				new FractionalSelectivity(1.0));
		application.addTupleMapping("position_estim", "FILTERED_DATA3", "PROCESSED_DATA1",
				new FractionalSelectivity(1.0));
		application.addTupleMapping("position_estim", "FILTERED_DATA3", "PROCESSED_DATA2",
				new FractionalSelectivity(1.0));
		application.addTupleMapping("clientModule", "PROCESSED_DATA2", "RESULT_DISPLAY",
				new FractionalSelectivity(1.0));

		application.setSpecialPlacementInfo("database", "cloud");

		final AppLoop loop1 = new AppLoop(new ArrayList<String>() {
			{
				add("SENSOR");
				add("clientModule");
				add("kalman_filter");
				add("distance_calc");
				add("trilateration");
				add("position_estim");
				add("clientModule");
				add("DISPLAY");
			}
		});

		List<AppLoop> loops = new ArrayList<AppLoop>() {
			{
				add(loop1);
			}
		};

		application.setLoops(loops);

		return application;
	}

}