package org.fog.test.perfeval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
import org.fog.entities.MicroserviceFogDevice;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;

public class SWMS {
	// Create the list of fog devices
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	// Create the list of sensors
	static List<Sensor> sensors = new ArrayList<Sensor>();
	// Create the list of actuators

	static List<Actuator> actuators = new ArrayList<Actuator>();
	// Define the number of areas
	static int numOfTotalAreas = 10;
	// Define the number of waste bins with each fog nodes
	static int numOfBinsPerArea = 1;
	// We are using the fog nodes to perform the operations.
	// cloud is set to false
	private static boolean CLOUD = false;

	public static void main(String[] args) {
		Log.printLine("Waste Management system...");
		try {
			Log.disable();
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);
			String appId = "swms"; // identifier of the application
			FogBroker broker = new FogBroker("broker");
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			createFogDevices(broker.getId(), appId);
			Controller controller = null;
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping();
			for (FogDevice device : fogDevices) {
				if (device.getName().startsWith("b")) {
					// names of all Smart Bins start with ’b’
					moduleMapping.addModuleToDevice("waste-info-module", device.getName());
					// mapping waste information module on waste bins
				}
			}

			for (FogDevice device : fogDevices) {
				if (device.getName().startsWith("a")) {
					// names of all fog devices start with ’a’
					// mapping master-module on area devices.
					moduleMapping.addModuleToDevice("master-module", device.getName());
					// mapping health-module on area devices
					moduleMapping.addModuleToDevice("health-module", device.getName());
					// mapping recycle-module on area devices.
					moduleMapping.addModuleToDevice("recycle-module", device.getName());
					// mapping municipal-module on area devices.
					moduleMapping.addModuleToDevice("municipal-module", device.getName());
				}
			}
			if (CLOUD) { // if the mode of deployment is cloud-based
				// placing all instances of master-module in the Cloud
				moduleMapping.addModuleToDevice("master-module", "cloud");
				// placing all instances of health-module in the Cloud
				moduleMapping.addModuleToDevice("health-module", "cloud");
				// placing all instances of recycle-module in the Cloud
				moduleMapping.addModuleToDevice("recycle-module", "cloud");
				// placing all instances of municipal-module in the Cloud
				moduleMapping.addModuleToDevice("municipal-module", "cloud");
			}
			controller = new Controller("master-controller", fogDevices, sensors, actuators);
			controller.submitApplication(application, (CLOUD)
					? (new ModulePlacementMapping(fogDevices, application, moduleMapping))
					: (new ModulePlacementEdgewards(fogDevices, sensors, actuators, application, moduleMapping)));
			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());
			CloudSim.startSimulation();

			CloudSim.stopSimulation();
			Log.printLine("waste management simulation finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * Creates a vanilla fog device
	 *
	 * @param nodeName    name of the device to be used in simulation
	 * @param mips        MIPS
	 * @param ram         RAM
	 * @param upBw        uplink bandwidth
	 * @param downBw      downlink bandwidth
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static MicroserviceFogDevice createFogDevice(String nodeName, long mips, int ram, long upBw, long downBw,
			double ratePerMips, double busyPower, double idlePower, String deviceType) {

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
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
		// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
		// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(arch, os, vmm, host, time_zone, cost,
				costPerMem, costPerStorage, costPerBw);

		MicroserviceFogDevice fogdevice = null;
		try {
			fogdevice = new MicroserviceFogDevice(nodeName, characteristics, new AppModuleAllocationPolicy(hostList),
					storageList, 10, upBw, downBw, 10000, 0, ratePerMips, deviceType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fogdevice;
	}

	private static void createFogDevices(int userId, String appId) {
		FogDevice cloud = createFogDevice("cloud", 44800, 40000, 100, 10000, 0.01, 16 * 103, 16 * 83.25,
				MicroserviceFogDevice.CLOUD);
		cloud.setParentId(-1);
		fogDevices.add(cloud);
		FogDevice router = createFogDevice("proxy-server", 7000, 4000, 10000, 10000, 0.0, 107.339, 83.4333,
				MicroserviceFogDevice.FCN);
		router.setParentId(cloud.getId());
		// latency of connection between proxy server and cloud is 100 ms
		router.setUplinkLatency(100.0);
		fogDevices.add(router);
		for (int i = 0; i < numOfTotalAreas; i++) {
			addArea(i + "", userId, appId, router.getId());
		}
	}

	// creating the fog nodes for each area
	private static FogDevice addArea(String id, int userId, String appId, int parentId) {
		FogDevice area_fognode = createFogDevice("a-" + id, 5000, 4000, 10000, 10000, 0.0, 107.339, 83.4333,
				MicroserviceFogDevice.FCN);
		fogDevices.add(area_fognode);
		area_fognode.setUplinkLatency(1.0);
		for (int i = 0; i < numOfBinsPerArea; i++) {
			String mobileId = id + "-" + i;
			FogDevice bin = addBin(mobileId, userId, appId, area_fognode.getId());
			bin.setUplinkLatency(2.0);
			fogDevices.add(bin);
		}
		// assigning x coordinate value to the fog node
		area_fognode.setxCoordinate(getCoordinatevalue(10));
		// assigning y coordinate value to the fog node
		area_fognode.setyCoordinate(getCoordinatevalue(10));
		area_fognode.setParentId(parentId);
		return area_fognode;
	}

	// creating the smart waste bins
	private static FogDevice addBin(String id, int userId, String appId, int parentId) {
		FogDevice bin = createFogDevice("b-" + id, 5000, 1000, 10000, 10000, 0, 87.53, 82.44,
				MicroserviceFogDevice.FCN);
		bin.setParentId(parentId);
		Sensor sensor = new Sensor("s-" + id, "BIN", userId, appId,
				new DeterministicDistribution(getCoordinatevalue(5)));
		sensors.add(sensor);
		Actuator ptz = new Actuator("act-" + id, userId, appId, "ACT_CONTROL");
		actuators.add(ptz);
		sensor.setGatewayDeviceId(bin.getId());
		sensor.setLatency(1.0);
		ptz.setGatewayDeviceId(parentId);
		ptz.setLatency(1.0);
		// assigning x coordinate value to the smart bin
		bin.setxCoordinate(getCoordinatevalue(10));
		// assigning y coordinate value to the smart bin
		bin.setyCoordinate(getCoordinatevalue(10));
		return bin;
	}

	private static Application createApplication(String appId, int userId) {
		Application application = Application.createApplication(appId, userId);
		application.addAppModule("waste-info-module", 10);
		application.addAppModule("master-module", 10);
		application.addAppModule("recycle-module", 10);
		application.addAppModule("health-module", 10);
		application.addAppModule("municipal-module", 10);
		application.addAppEdge("BIN", "waste-info-module", 1000, 2000, "BIN", Tuple.UP, AppEdge.SENSOR);
		application.addAppEdge("waste-info-module", "master-module", 1000, 2000, "Task1", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("master-module", "municipal-module", 1000, 2000, "Task2", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("master-module", "recycle-module", 1000, 2000, "Task3", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("master-module", "health-module", 1000, 2000, "Task4", Tuple.UP, AppEdge.MODULE);
		application.addAppEdge("master-module", "ACT_CONTROL", 100, 28, 100, "ACT_PARAMS", Tuple.UP, AppEdge.ACTUATOR);
		application.addTupleMapping("waste-info-module", "BIN", "Task1", new FractionalSelectivity(1.0));
		application.addTupleMapping("master-module", "BIN", "Task2", new FractionalSelectivity(1.0));
		application.addTupleMapping("master-module", "BIN", "Task3", new FractionalSelectivity(1.0));
		application.addTupleMapping("master-module", "BIN", "Task4", new FractionalSelectivity(1.0));
		application.addTupleMapping("master-module", "BIN", "ACT_CONTROL", new FractionalSelectivity(1.0));
		final AppLoop loop1 = new AppLoop(new ArrayList<String>() {
			{
				add("BIN");
				add("waste-info-module");
				add("master-module");
				add("municipal-module");
				add("recycle-module");
				add("health-module");
				add("ACT_CONTROL");
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

	private static double getCoordinatevalue(double min) {
		Random rn = new Random();
		return rn.nextDouble() + min;
	}
}