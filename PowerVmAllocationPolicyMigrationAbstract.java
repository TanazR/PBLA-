/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package MyProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.HostDynamicWorkload;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyAbstract;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.lists.PowerVmList;
import org.cloudbus.cloudsim.util.ExecutionTimeMeasurer;

import MyProject.LAutomata.Status;

/**
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmAllocationPolicyMigrationAbstract extends
		PowerVmAllocationPolicyAbstract {
	private static List<PowerHost> allocatedPowerHost = new LinkedList<PowerHost>();
	/** The vm selection policy. */
	private PowerVmSelectionPolicy vmSelectionPolicy;

	/** The saved allocation. */
	private final List<Map<String, Object>> savedAllocation = new ArrayList<Map<String, Object>>();

	/** The utilization history. */
	private final Map<Integer, List<Double>> utilizationHistory = new HashMap<Integer, List<Double>>();

	/** The metric history. */
	private final Map<Integer, List<Double>> metricHistory = new HashMap<Integer, List<Double>>();

	/** The time history. */
	private final Map<Integer, List<Double>> timeHistory = new HashMap<Integer, List<Double>>();

	/** The execution time history vm selection. */
	private final List<Double> executionTimeHistoryVmSelection = new LinkedList<Double>();

	/** The execution time history host selection. */
	private final List<Double> executionTimeHistoryHostSelection = new LinkedList<Double>();

	/** The execution time history vm reallocation. */
	private final List<Double> executionTimeHistoryVmReallocation = new LinkedList<Double>();

	/** The execution time history total. */
	private final List<Double> executionTimeHistoryTotal = new LinkedList<Double>();

	// Scanner input = new Scanner(System.in);

	/**
	 * Instantiates a new power vm allocation policy migration abstract.
	 * 
	 * @param hostList
	 *            the host list
	 * @param vmSelectionPolicy
	 *            the vm selection policy
	 */
	public PowerVmAllocationPolicyMigrationAbstract(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy) {
		super(hostList);
		setVmSelectionPolicy(vmSelectionPolicy);
	}

	@Override
	public List<Map<String, Object>> optimizeAllocation(
			List<? extends Vm> vmList) {

		ExecutionTimeMeasurer.start("optimizeAllocationTotal");
		ExecutionTimeMeasurer.start("optimizeAllocationHostSelection");

		// for (int i = 0; i < (Helper.hostList.size()); i++) {
		// LAPlacementVM.selectAction(PlanetLabRunner.mapLAPlacement, i);
		// }
		List<PowerHostUtilizationHistory> overUtilizedHosts = getOverUtilizedHosts();

		getExecutionTimeHistoryHostSelection().add(
				ExecutionTimeMeasurer.end("optimizeAllocationHostSelection"));

		printOverUtilizedHosts(overUtilizedHosts);
		saveAllocation();

		ExecutionTimeMeasurer.start("optimizeAllocationVmSelection");
		List<? extends Vm> vmsToMigrate = getVmsToMigrateFromHosts(overUtilizedHosts);
		getExecutionTimeHistoryVmSelection().add(
				ExecutionTimeMeasurer.end("optimizeAllocationVmSelection"));

		Log.printLine("Reallocation of VMs from the Over-Utilized hosts:");
		ExecutionTimeMeasurer.start("optimizeAllocationVmReallocation");

		List<Map<String, Object>> migrationMap = getNewVmPlacement(
				vmsToMigrate, new HashSet<Host>(overUtilizedHosts));

		getExecutionTimeHistoryVmReallocation().add(
				ExecutionTimeMeasurer.end("optimizeAllocationVmReallocation"));
		Log.printLine();

		migrationMap.addAll(getMigrationMapFromAVGUtilizedHosts());

		// System.out.println("ino pak nemikomi");
		// System.exit(0);

		migrationMap
				.addAll(getMigrationMapFromUnderUtilizedHosts(overUtilizedHosts));

		restoreAllocation();

		getExecutionTimeHistoryTotal().add(
				ExecutionTimeMeasurer.end("optimizeAllocationTotal"));

		return migrationMap;
	}

	public List<Map<String, Object>> getMigrationMapFromAVGUtilizedHosts() {

		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		List<PowerHost> avgUtilizedHosts = getAVGUtilizedHost();
		// System.out.println("inam pak nemikonio" + avgUtilizedHosts);

		printAVGUtilizedHosts(avgUtilizedHosts);

		saveAllocation();

		List<? extends Vm> vmsToMigrateFromAVG = getVmsToMigrateFromgetAVGUtilizedHost(avgUtilizedHosts);
		// System.out.println("oroste" + vmsToMigrateFromAVG.size() );
		// System.exit(0);
		Log.printLine("Reallocation of VMs from the Average-Utilized hosts:");
		List<Map<String, Object>> newVmPlacement = getNewVmPlacementFromAVGUtilizedHost(
				vmsToMigrateFromAVG, new HashSet<Host>(avgUtilizedHosts));
		migrationMap.addAll(newVmPlacement);

		Log.printLine();
		// System.out.println("kamelan doroste" + migrationMap);
		// System.exit(0);
		return migrationMap;
	}

	/**
	 * Gets the migration map from under utilized hosts.
	 * 
	 * @param overUtilizedHosts
	 *            the over utilized hosts
	 * @return the migration map from under utilized hosts
	 */
	protected List<Map<String, Object>> getMigrationMapFromUnderUtilizedHosts(
			List<PowerHostUtilizationHistory> overUtilizedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		List<PowerHost> switchedOffHosts = getSwitchedOffHosts();

		// over-utilized hosts + hosts that are selected to migrate VMs to from
		// over-utilized hosts
		Set<PowerHost> excludedHostsForFindingUnderUtilizedHost = new HashSet<PowerHost>();
		excludedHostsForFindingUnderUtilizedHost.addAll(overUtilizedHosts);
		excludedHostsForFindingUnderUtilizedHost.addAll(switchedOffHosts);
		excludedHostsForFindingUnderUtilizedHost
				.addAll(extractHostListFromMigrationMap(migrationMap));

		// over-utilized + under-utilized hosts
		Set<PowerHost> excludedHostsForFindingNewVmPlacement = new HashSet<PowerHost>();
		excludedHostsForFindingNewVmPlacement.addAll(overUtilizedHosts);
		excludedHostsForFindingNewVmPlacement.addAll(switchedOffHosts);

		int numberOfHosts = getHostList().size();

		while (true) {
			if (numberOfHosts == excludedHostsForFindingUnderUtilizedHost
					.size()) {
				break;
			}

			PowerHost iDLEHost = getUnderUtilizedHost(excludedHostsForFindingUnderUtilizedHost);

			if (iDLEHost == null) {
				break;
			}
			// PowerHost idle = iDLEHost;
			// System.out.println("fdhg" + idle);
			// System.exit(0);
			excludedHostsForFindingUnderUtilizedHost.add(iDLEHost);
			excludedHostsForFindingNewVmPlacement.add(iDLEHost);

			List<? extends Vm> vmsToMigrateFromUnderUtilizedHost = getVmsToMigrateFromUnderUtilizedHost(iDLEHost);
			// System.out.println("in chi" + vmsToMigrateFromUnderUtilizedHost);
			if (vmsToMigrateFromUnderUtilizedHost.isEmpty()) {
				continue;
			}
			// System.exit(0);
			Log.print("Reallocation of VMs from the IDLE host: ");
			if (!Log.isDisabled()) {
				for (Vm vm : vmsToMigrateFromUnderUtilizedHost) {
					Log.print(vm.getId() + " ");
				}
			}

			List<Map<String, Object>> newVmPlacement = getNewVmPlacementFromUnderUtilizedHost(
					vmsToMigrateFromUnderUtilizedHost,
					excludedHostsForFindingNewVmPlacement);

			Log.printLine();

			excludedHostsForFindingUnderUtilizedHost
					.addAll(extractHostListFromMigrationMap(newVmPlacement));

			migrationMap.addAll(newVmPlacement);
			Log.printLine();
		}

		return migrationMap;
	}

	protected boolean isHostUnderUtilized(PowerHost host) {
		boolean ishavg = false;
		PlanetLabRunner.mapLAPlacement.get(0).getStateOfLA();
		if (LAutomata.laState.get(host.getId()) == "IDLE") {
			ishavg = true;
		}
		return ishavg;
	}

	protected boolean isHostAVGUtilized(PowerHost host) {

		boolean ishavg = false;
		PlanetLabRunner.mapLAPlacement.get(0).getStateOfLA();
		if (LAutomata.laState.get(host.getId()) == "AVGUTIZALTION") {
			ishavg = true;
		}

		// System.exit(0);
		return ishavg;
	}

	protected void printAVGUtilizedHosts(List<PowerHost> avgUtilizedHosts) {
		if (!Log.isDisabled()) {
			Log.printLine("Average-utilized hosts:");
			for (PowerHost host : avgUtilizedHosts) {
				Log.printLine("Host #" + host.getId());
			}
			Log.printLine();
		}
	}

	protected boolean isHostOverUtilized(PowerHost host) {
		addHistoryEntry(host, 0.80);
		double totalRequestedMips = 0;
		for (Vm vm : host.getVmList()) {
			totalRequestedMips += vm.getCurrentRequestedTotalMips();
		}
		double utilization = totalRequestedMips / host.getTotalMips();
		if (utilization >= 0.80) {
			PlanetLabRunner.mapLAPlacement.get(host.getId()).setStateOfLA(
					Status.OVER);
		}
		return utilization >= 0.80;
	}

	/**
	 * Prints the over utilized hosts.
	 * 
	 * @param overUtilizedHosts
	 *            the over utilized hosts
	 */
	protected void printUnderUtilizedHosts(List<PowerHost> underUtilizedHosts) {
		if (!Log.isDisabled()) {
			Log.printLine("Idle hosts:");

			for (PowerHost host : underUtilizedHosts) {

				Log.printLine("Host #" + host.getId());
			}
			Log.printLine();
		}
	}

	static LinkedList<Integer> targetHost = new LinkedList<Integer>();

	// static LinkedList<Integer> test = new LinkedList<Integer>();

	public List<PowerHost> findHostForVm(Vm vm,
			Set<? extends Host> excludedHosts) {
		if (allocatedPowerHost != null && allocatedPowerHost.size() != 0) {
			allocatedPowerHost.clear();
		}

		for (PowerHost host : this.<PowerHost> getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0
						&& isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				} else {
					allocatedPowerHost.add(host);
					targetHost.add(host.getId());

				}

			}
		}
		return allocatedPowerHost;
	}

	public PowerHost findHostForVmReal(Vm vm, Set<? extends Host> excludedHosts) {
		double minPower = Double.MAX_VALUE;
		PowerHost allocatedHost = null;

		for (PowerHost host : this.<PowerHost> getHostList()) {

			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0
						&& isHostOverUtilizedAfterAllocation(host, vm)) {

					continue;
				}

				try {
					double powerAfterAllocation = getPowerAfterAllocation(host,
							vm);

					if (powerAfterAllocation != -1) {
						double powerDiff = powerAfterAllocation
								- host.getPower();
						if (powerDiff < minPower) {
							minPower = powerDiff;
							allocatedHost = host;
							// System.out.println("daliii" +
							// allocatedHost.getId());
							break;
						}
					}

					// System.exit(0);
				} catch (Exception e) {
				}
			}

		}
		return allocatedHost;
	}

	protected List<Map<String, Object>> getNewVmPlacementFromAVGUtilizedHost(
			List<? extends Vm> vmsToMigrate, Set<? extends Host> excludedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		PowerVmList.sortByCpuUtilization(vmsToMigrate);
		List<PowerHost> allocatedHosts = null;
		for (Vm vm : vmsToMigrate) {
			// input.nextLine();
			// in 317 ta vm dare vase migrate khaili repeat ziad mishe

			allocatedHosts = findHostForVm(vm, excludedHosts);

			if (allocatedHosts != null) {
				// System.out.println("\n==================================\nLINE 310 allocatedHost Size:\t"
				// + allocatedHost.size());
				// input.nextLine();

				// 15,800 time repeat;
				Log.printLine("\n\n--------------------------------------------------------------\n\n");
				System.out
						.println("Learning Phase For Average Utizalation Host");

				for (int i = 0; i < allocatedHosts.size(); i++) {
					for (int j = 0; j < 100; j++) {
						LAPlacementVM.selectAction(
								PlanetLabRunner.mapLAPlacement, allocatedHosts
										.get(i).getId());
						if (PlanetLabRunner.mapLAPlacement.get(
								allocatedHosts.get(i).getId()).isAction() == true) {
							// System.out.println("LA" +
							// allocatedHosts.get(i).getId()
							// + "Choose Accept Action");

							LAPlacementVM.rewardPenalty(
									PlanetLabRunner.mapLAPlacement, oldHostAvg
											.get(vm.getId()), allocatedHosts
											.get(i).getId());

						}

						else if (PlanetLabRunner.mapLAPlacement.get(
								allocatedHosts.get(i).getId()).isAction() == false) {
							// System.out.println("LA" +
							// allocatedHosts.get(i).getId()
							// + "Choose Reject Action");

							LAPlacementVM.rewardPenalty(
									PlanetLabRunner.mapLAPlacement, oldHostAvg
											.get(vm.getId()), allocatedHosts
											.get(i).getId());
							// input.nextLine();
						}
					}
				}
			}
		}

		// System.exit(0);

		for (Vm vm : vmsToMigrate) {
			PowerHost allocatedHost = findHostForVmReal(vm, excludedHosts);
			// System.out
			// .println("after learning ");
			if (allocatedHost != null) {

				if (PlanetLabRunner.mapLAPlacement.get(allocatedHost.getId())
						.isAction() == true) {

					System.out.println("LA" + allocatedHost.getId()
							+ "Choose Accept Action");
					allocatedHost.vmCreate(vm);
					Log.printLine("VM #" + vm.getId() + " allocated to host #"
							+ allocatedHost.getId());

					Map<String, Object> migrate = new HashMap<String, Object>();
					migrate.put("vm", vm);
					migrate.put("host", allocatedHost);
					migrationMap.add(migrate);

					LAPlacementVM.rewardPenalty(PlanetLabRunner.mapLAPlacement,
							oldHostAvg.get(vm.getId()), allocatedHost.getId());

				}

			}

		}

		return migrationMap;
	}

	/**
	 * Checks if is host over utilized after allocation.
	 * 
	 * @param host
	 *            the host
	 * @param vm
	 *            the vm
	 * @return true, if is host over utilized after allocation
	 */
	protected boolean isHostOverUtilizedAfterAllocation(PowerHost host, Vm vm) {
		boolean isHostOverUtilizedAfterAllocation = true;
		if (host.vmCreate(vm)) {
			isHostOverUtilizedAfterAllocation = isHostOverUtilized(host);
			host.vmDestroy(vm);
		}
		return isHostOverUtilizedAfterAllocation;
	}

	/**
	 * Extract host list from migration map.
	 * 
	 * @param migrationMap
	 *            the migration map
	 * @return the list
	 */
	protected List<PowerHost> extractHostListFromMigrationMap(
			List<Map<String, Object>> migrationMap) {
		// System.out.println("+++++++++++++++++++++++++++++++++++");
		List<PowerHost> hosts = new LinkedList<PowerHost>();
		for (Map<String, Object> map : migrationMap) {
			hosts.add((PowerHost) map.get("host"));
		}
		return hosts;
	}

	/**
	 * Gets the new vm placement.
	 * 
	 * @param vmsToMigrate
	 *            the vms to migrate
	 * @param excludedHosts
	 *            the excluded hosts
	 * @return the new vm placement
	 */
	protected List<Map<String, Object>> getNewVmPlacement(
			List<? extends Vm> vmsToMigrate, Set<? extends Host> excludedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();
		PowerVmList.sortByCpuUtilization(vmsToMigrate);

		for (Vm vm : vmsToMigrate) {
			List<PowerHost> allocatedHost = findHostForVm(vm, excludedHosts);

			if (allocatedHost != null) {
				Log.printLine("\n\n--------------------------------------------------------------\n\n");
				System.out.println("Learning Phase For OVER Utizalation Host");
				for (int i = 0; i < allocatedHost.size(); i++) {
					if (PlanetLabRunner.mapLAPlacement.get(
							allocatedHost.get(i).getId()).isAction() == true) {

						// System.out.println("LA" +
						// allocatedHost.get(i).getId()
						// + "Accept Action for OverUtiziled Host");

						Log.printLine("VM #" + vm.getId()
								+ " allocated to host #"
								+ allocatedHost.get(i).getId());

						LAPlacementVM.rewardPenalty(
								PlanetLabRunner.mapLAPlacement,
								oldHostover.get(vm.getId()),
								allocatedHost.get(i).getId());

					} else if (PlanetLabRunner.mapLAPlacement.get(
							allocatedHost.get(i).getId()).isAction() == false) {
						LAPlacementVM.rewardPenalty(
								PlanetLabRunner.mapLAPlacement,
								oldHostover.get(vm.getId()),
								allocatedHost.get(i).getId());
					}

				}
			}
		}

		for (Vm vm : vmsToMigrate) {
			PowerHost allocatedHost = findHostForVmReal(vm, excludedHosts);
			// System.out
			// .println("after learning ");
			if (allocatedHost != null) {

				if (PlanetLabRunner.mapLAPlacement.get(allocatedHost.getId())
						.isAction() == true) {

					System.out.println("LA" + allocatedHost.getId()
							+ "Choose Accept Action");
					allocatedHost.vmCreate(vm);
					Log.printLine("VM #" + vm.getId() + " allocated to host #"
							+ allocatedHost.getId());

					Map<String, Object> migrate = new HashMap<String, Object>();
					migrate.put("vm", vm);
					migrate.put("host", allocatedHost);
					migrationMap.add(migrate);

					LAPlacementVM.rewardPenalty(PlanetLabRunner.mapLAPlacement,
							oldHostover.get(vm.getId()), allocatedHost.getId());

				}

			}

		}

		return migrationMap;
	}

	public static boolean calaCheckpoint = true;

	/**
	 * Gets the new vm placement from under utilized host.
	 * 
	 * @param vmsToMigrate
	 *            the vms to migrate
	 * @param excludedHosts
	 *            the excluded hosts
	 * @return the new vm placement from under utilized host
	 */
	protected List<Map<String, Object>> getNewVmPlacementFromUnderUtilizedHost(
			List<? extends Vm> vmsToMigrate, Set<? extends Host> excludedHosts) {
		List<Map<String, Object>> migrationMap = new LinkedList<Map<String, Object>>();

		PowerVmList.sortByCpuUtilization(vmsToMigrate);
		// System.out.println("s" + vmsToMigrate);
		// System.exit(0); 2ta vm num 524 525 darim

		List<PowerHost> allocatedHost;
		for (Vm vm : vmsToMigrate) {

			allocatedHost = findHostForVm(vm, excludedHosts);
			// 411= allocatedHost.size());
			if (allocatedHost != null) {
				Log.printLine("\n\n--------------------------------------------------------------\n\n");
				System.out.println("Learning Phase For IDLE Utizalation Host");
				for (int i = 0; i < allocatedHost.size(); i++) {

					LAPlacementVM.selectAction(PlanetLabRunner.mapLAPlacement,
							allocatedHost.get(i).getId());

					if (PlanetLabRunner.mapLAPlacement.get(
							allocatedHost.get(i).getId()).isAction() == true) {
						// System.out.println("LA" +
						// allocatedHost.get(i).getId()
						// + "Choose Accept Action");
						// Log.printLine("VM #" + vm.getId() +
						// " Allocated to host #" +
						// allocatedHost.get(i).getId());

						LAPlacementVM.rewardPenalty(
								PlanetLabRunner.mapLAPlacement,
								oldHostidle.get(vm.getId()),
								allocatedHost.get(i).getId());

					} else if (PlanetLabRunner.mapLAPlacement.get(
							allocatedHost.get(i).getId()).isAction() == false) {
						// System.out.println("LA" +
						// allocatedHost.get(i).getId()
						// + "Choose Reject Action");

						LAPlacementVM.rewardPenalty(
								PlanetLabRunner.mapLAPlacement,
								oldHostidle.get(vm.getId()),
								allocatedHost.get(i).getId());
					}
				}
			}
		}

		// System.exit(0);
		for (Vm vm : vmsToMigrate) {
			// System.out.println("in mide" +vm.getHost()+ vmsToMigrate.size());
			PowerHost allocatedHosts = findHostForVmReal(vm, excludedHosts);

			if (allocatedHosts != null) {
				if (PlanetLabRunner.mapLAPlacement.get(allocatedHosts.getId())
						.isAction() == true) {

					allocatedHosts.vmCreate(vm);
					Log.printLine("VM #" + vm.getId() + " allocated to host #"
							+ allocatedHosts.getId());

					Map<String, Object> migrate = new HashMap<String, Object>();
					migrate.put("vm", vm);
					migrate.put("host", allocatedHosts);
					migrationMap.add(migrate);
				}
			} else {
				Log.printLine("Not all VMs can be reallocated from the host, reallocation cancelled");
				calaCheckpoint = false;
				for (Map<String, Object> map : migrationMap) {
					((Host) map.get("host")).vmDestroy((Vm) map.get("vm"));
				}
				migrationMap.clear();
				break;
			}
		}
		return migrationMap;
	}

	/**
	 * Gets the vms to migrate from under utilized host.
	 * 
	 * @param underUtilizedHost
	 *            the host
	 * @return the vms to migrate from under utilized host
	 */
	static final Map<Integer, Integer> oldHostidle = new HashMap<Integer, Integer>();

	protected List<? extends Vm> getVmsToMigrateFromUnderUtilizedHost(
			PowerHost host) {

		List<Vm> vmsToMigrate = new LinkedList<Vm>();
		List<Vm> MigrateTest = new LinkedList<Vm>();

		boolean checkpoint = false;

		for (int learning = 0; learning < 400; learning++) {

			if (PlanetLabRunner.mapCALA.get(host.getId()).getXProbability() <= 0.65) {

				CALAPlacementVM.setFX(true);
				for (Vm vm : host.getVmList()) {
					if (!vm.isInMigration()) {
						MigrateTest.add(vm);
					}
				}
			}
			if (PlanetLabRunner.mapCALA.get(host.getId()).getMeanProbability() <= 0.65) {

				CALAPlacementVM.setFM(true);
				for (Vm vm : host.getVmList()) {
					if (!vm.isInMigration()) {
						MigrateTest.add(vm);
					}
				}
			}

			if (targetHost != null) {
				if (targetHost.contains(host.getId()) == true) {
					checkpoint = false;

				} else {
					checkpoint = true;
				}

			}

			int bX = CALAPlacementVM.responseXCALA(checkpoint);
			int bM = CALAPlacementVM.responseMCALA(checkpoint);

			CALAPlacementVM.updateProbCALA(PlanetLabRunner.mapCALA,
					host.getId(), bX, bM);

		}

		// System.out.println("mean" +
		// PlanetLabRunner.mapCALA.get(host.getId()).getMeanProbability();
		// System.out.println("div" +
		// PlanetLabRunner.mapCALA.get(host.getId()).getStandardDivProbability());
		// System.out.println("X" +
		// PlanetLabRunner.mapCALA.get(host.getId()).getXProbability());
		// System.exit(0);
		// id ha motfavatand ro host haye mokhtalef try mikone
		if ((PlanetLabRunner.mapCALA.get(host.getId()).getMeanProbability()) <= 0.65) {

			for (Vm vm : host.getVmList()) {
				oldHostidle.put(vm.getId(), host.getId());
				if (!vm.isInMigration()) {
					vmsToMigrate.add(vm);
				}
			}
		}
		// System.out.println("mean" +
		// PlanetLabRunner.mapCALA.get(host.getId()).getMeanProbability() );
		// System.out.println("vm" + vmsToMigrate);
		// System.exit(0);

		return vmsToMigrate;
	}

	/**
	 * Gets the over utilized hosts.
	 * 
	 * @return the over utilized hosts
	 */
	protected List<PowerHostUtilizationHistory> getOverUtilizedHosts() {
		List<PowerHostUtilizationHistory> overUtilizedHosts = new LinkedList<PowerHostUtilizationHistory>();
		for (PowerHostUtilizationHistory host : this
				.<PowerHostUtilizationHistory> getHostList()) {
			if (isHostOverUtilized(host)) {
				overUtilizedHosts.add(host);

			}
		}
		return overUtilizedHosts;
	}

	/**
	 * Gets the switched off host.
	 * 
	 * @return the switched off host
	 */
	public List<PowerHost> getSwitchedOffHosts() {
		List<PowerHost> switchedOffHosts = new LinkedList<PowerHost>();
		for (PowerHost host : this.<PowerHost> getHostList()) {
			if (host.getUtilizationOfCpu() == 0) {
				switchedOffHosts.add(host);
			}
		}
		return switchedOffHosts;
	}

	public static List<Double> getStateofHost() {
		List<Double> state = new LinkedList<Double>();
		for (PowerHost host : Helper.hostList) {
			double sta = host.getUtilizationOfCpu();
			state.add(sta);
		}

		return state;
	}

	protected List<PowerHost> getAVGUtilizedHost() {
		List<PowerHost> avgUtilizedHosts = new LinkedList<PowerHost>();

		for (PowerHost host : this.<PowerHost> getHostList()) {

			if ((LAutomata.laState.get(host.getId()) == "AVGUTIZALTION")
					&& !areAllVmsMigratingOutOrAnyVmMigratingIn(host)) {
				avgUtilizedHosts.add(host);
			}
		}

		return avgUtilizedHosts;
	}

	/**
	 * Gets the under utilized host.
	 * 
	 * @param excludedHosts
	 *            the excluded hosts
	 * @return the under utilized host
	 */
	protected PowerHost getUnderUtilizedHost(Set<? extends Host> excludedHosts) {
		PowerHost iDLEHost = null;

		PlanetLabRunner.mapLAPlacement.get(1).getStateOfLA();

		for (PowerHost host : this.<PowerHost> getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}

			if ((LAutomata.laState.get(host.getId()) == "IDLE")
					&& !areAllVmsMigratingOutOrAnyVmMigratingIn(host)) {

				iDLEHost = host;
			}
		}

		return iDLEHost;
	}

	/**
	 * Checks whether all vms are in migration.
	 * 
	 * @param host
	 *            the host
	 * @return true, if successful
	 */
	public static boolean areAllVmsMigratingOutOrAnyVmMigratingIn(PowerHost host) {
		for (PowerVm vm : host.<PowerVm> getVmList()) {
			if (!vm.isInMigration()) {
				return false;
			}
			if (host.getVmsMigratingIn().contains(vm)) {
				return true;
			}
		}
		return true;
	}

	/**
	 * Adds the history value.
	 * 
	 * @param host
	 *            the host
	 * @param metric
	 *            the metric
	 */
	protected void addHistoryEntry(HostDynamicWorkload host, double metric) {
		int hostId = host.getId();
		if (!getTimeHistory().containsKey(hostId)) {
			getTimeHistory().put(hostId, new LinkedList<Double>());
		}
		if (!getUtilizationHistory().containsKey(hostId)) {
			getUtilizationHistory().put(hostId, new LinkedList<Double>());
		}
		if (!getMetricHistory().containsKey(hostId)) {
			getMetricHistory().put(hostId, new LinkedList<Double>());
		}
		if (!getTimeHistory().get(hostId).contains(CloudSim.clock())) {
			getTimeHistory().get(hostId).add(CloudSim.clock());
			getUtilizationHistory().get(hostId).add(host.getUtilizationOfCpu());
			getMetricHistory().get(hostId).add(metric);
		}
	}

	/**
	 * Save allocation.
	 */
	protected void saveAllocation() {
		getSavedAllocation().clear();
		for (Host host : getHostList()) {
			for (Vm vm : host.getVmList()) {
				if (host.getVmsMigratingIn().contains(vm)) {
					continue;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("host", host);
				map.put("vm", vm);
				getSavedAllocation().add(map);
			}
		}

	}

	/**
	 * Restore allocation.
	 */
	protected void restoreAllocation() {
		for (Host host : getHostList()) {
			host.vmDestroyAll();
			host.reallocateMigratingInVms();
		}
		for (Map<String, Object> map : getSavedAllocation()) {
			Vm vm = (Vm) map.get("vm");
			PowerHost host = (PowerHost) map.get("host");
			if (!host.vmCreate(vm)) {
				Log.printLine("Couldn't restore VM #" + vm.getId()
						+ " on host #" + host.getId());
				System.exit(0);
			}
			getVmTable().put(vm.getUid(), host);
		}

		// for (int i = 0; i < (Helper.hostList.size()) ; i++) {
		// LAPlacementVM.selectAction(PlanetLabRunner.mapLAPlacement, i);
		// }
	}

	/**
	 * Gets the power after allocation.
	 * 
	 * @param host
	 *            the host
	 * @param vm
	 *            the vm
	 * 
	 * @return the power after allocation
	 */
	protected double getPowerAfterAllocation(PowerHost host, Vm vm) {
		double power = 0;
		try {
			power = host.getPowerModel().getPower(
					getMaxUtilizationAfterAllocation(host, vm));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return power;
	}

	/**
	 * Gets the power after allocation. We assume that load is balanced between
	 * PEs. The only restriction is: VM's max MIPS < PE's MIPS
	 * 
	 * @param host
	 *            the host
	 * @param vm
	 *            the vm
	 * 
	 * @return the power after allocation
	 */
	protected double getMaxUtilizationAfterAllocation(PowerHost host, Vm vm) {
		double requestedTotalMips = vm.getCurrentRequestedTotalMips();
		double hostUtilizationMips = getUtilizationOfCpuMips(host);
		double hostPotentialUtilizationMips = hostUtilizationMips
				+ requestedTotalMips;
		double pePotentialUtilization = hostPotentialUtilizationMips
				/ host.getTotalMips();
		return pePotentialUtilization;
	}

	protected void printOverUtilizedHosts(
			List<PowerHostUtilizationHistory> overUtilizedHosts) {
		if (!Log.isDisabled()) {
			Log.printLine("Over-utilized hosts:");
			for (PowerHostUtilizationHistory host : overUtilizedHosts) {
				Log.printLine("Host #" + host.getId());
			}
			Log.printLine();
		}
	}

	/**
	 * Gets the utilization of the CPU in MIPS for the current potentially
	 * receiver VMs.
	 * 
	 * @param host
	 *            the host
	 * 
	 * @return the utilization of the CPU in MIPS
	 */
	protected double getUtilizationOfCpuMips(PowerHost host) {
		double hostUtilizationMips = 0;
		for (Vm vm2 : host.getVmList()) {
			if (host.getVmsMigratingIn().contains(vm2)) {
				// calculate additional potential CPU usage of a migrating in VM
				hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2) * 0.9 / 0.1;
			}
			hostUtilizationMips += host.getTotalAllocatedMipsForVm(vm2);
		}
		return hostUtilizationMips;
	}

	static final Map<Integer, Integer> oldHostover = new HashMap<Integer, Integer>();

	protected List<? extends Vm> getVmsToMigrateFromHosts(
			List<PowerHostUtilizationHistory> overUtilizedHosts) {
		List<Vm> vmsToMigrate = new LinkedList<Vm>();
		for (PowerHostUtilizationHistory host : overUtilizedHosts) {
			while (true) {
				Vm vm = getVmSelectionPolicy().getVmToMigrate(host);
				if (vm == null) {
					break;
				}
				vmsToMigrate.add(vm);
				oldHostover.put(vm.getId(), vm.getHost().getId());
				host.vmDestroy(vm);
				if (!isHostOverUtilized(host)) {
					break;
				}
			}
		}
		return vmsToMigrate;
	}

	static final Map<Integer, Integer> oldHostAvg = new HashMap<Integer, Integer>();

	protected List<? extends Vm> getVmsToMigrateFromgetAVGUtilizedHost(
			List<PowerHost> avgUtilizedHosts) {
		List<Vm> vmsToMigrate = new LinkedList<Vm>();

		for (PowerHost host : avgUtilizedHosts) {
			while (true) {
				Vm vm = getVmSelectionPolicy().getVmToMigrate(host);
				if (vm == null) {
					break;
				}

				if (vm.getHost() != null) {
					oldHostAvg.put(vm.getId(), vm.getHost().getId());
				}

				vmsToMigrate.add(vm);
				host.vmDestroy(vm);

			}

		}
		// System.exit(0);
		return vmsToMigrate;
	}

	/**
	 * Gets the saved allocation.
	 * 
	 * @return the saved allocation
	 */
	protected List<Map<String, Object>> getSavedAllocation() {
		return savedAllocation;
	}

	/**
	 * Sets the vm selection policy.
	 * 
	 * @param vmSelectionPolicy
	 *            the new vm selection policy
	 */
	protected void setVmSelectionPolicy(PowerVmSelectionPolicy vmSelectionPolicy) {
		this.vmSelectionPolicy = vmSelectionPolicy;
	}

	/**
	 * Gets the vm selection policy.
	 * 
	 * @return the vm selection policy
	 */
	protected PowerVmSelectionPolicy getVmSelectionPolicy() {
		return vmSelectionPolicy;
	}

	/**
	 * Gets the utilization history.
	 * 
	 * @return the utilization history
	 */
	public Map<Integer, List<Double>> getUtilizationHistory() {
		return utilizationHistory;
	}

	/**
	 * Gets the metric history.
	 * 
	 * @return the metric history
	 */
	public Map<Integer, List<Double>> getMetricHistory() {
		return metricHistory;
	}

	/**
	 * Gets the time history.
	 * 
	 * @return the time history
	 */
	public Map<Integer, List<Double>> getTimeHistory() {
		return timeHistory;
	}

	/**
	 * Gets the execution time history vm selection.
	 * 
	 * @return the execution time history vm selection
	 */
	public List<Double> getExecutionTimeHistoryVmSelection() {
		return executionTimeHistoryVmSelection;
	}

	/**
	 * Gets the execution time history host selection.
	 * 
	 * @return the execution time history host selection
	 */
	public List<Double> getExecutionTimeHistoryHostSelection() {
		return executionTimeHistoryHostSelection;
	}

	/**
	 * Gets the execution time history vm reallocation.
	 * 
	 * @return the execution time history vm reallocation
	 */
	public List<Double> getExecutionTimeHistoryVmReallocation() {
		return executionTimeHistoryVmReallocation;
	}

	/**
	 * Gets the execution time history total.
	 * 
	 * @return the execution time history total
	 */
	public List<Double> getExecutionTimeHistoryTotal() {
		return executionTimeHistoryTotal;
	}

}
