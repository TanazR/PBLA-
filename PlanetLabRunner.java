package MyProject;

import java.util.Calendar;
import java.util.HashMap;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;

import MyProject.LAutomata.Status;

/**
 * The example runner for the PlanetLab workload.
 * 
 * If you are using any algorithms, policies or workload included in the power
 * package please cite the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class PlanetLabRunner extends RunnerAbstract {
	public static HashMap<Integer, LAutomata> mapLAPlacement = new HashMap<Integer, LAutomata>();
	public static HashMap<Integer, CALA> mapCALA = new HashMap<Integer, CALA>();

	public static int NUMBER_OF_HOSTS = 100;

	/**
	 * Instantiates a new planet lab runner.
	 * 
	 * @param enableOutput
	 *            the enable output
	 * @param outputToFile
	 *            the output to file
	 * @param inputFolder
	 *            the input folder
	 * @param outputFolder
	 *            the output folder
	 * @param workload
	 *            the workload
	 * @param vmAllocationPolicy
	 *            the vm allocation policy
	 * @param vmSelectionPolicy
	 *            the vm selection policy
	 * @param parameter
	 *            the parameter
	 */
	public PlanetLabRunner(boolean enableOutput, boolean outputToFile,
			String inputFolder, String outputFolder, String workload,
			String vmAllocationPolicy, String vmSelectionPolicy) {
		super(enableOutput, outputToFile, inputFolder, outputFolder, workload,
				vmAllocationPolicy, vmSelectionPolicy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cloudbus.cloudsim.examples.power.RunnerAbstract#init(java.lang.String
	 * )
	 */
	@Override
	protected void init(String inputFolder) {
		try {
			CloudSim.init(1, Calendar.getInstance(), false);

			broker = Helper.createBroker();
			int brokerId = broker.getId();

			cloudletList = PlanetLabHelper.createCloudletListPlanetLab(
					brokerId, inputFolder);
			vmList = Helper.createVmList(brokerId, cloudletList.size());
			Helper.hostList = Helper.createHostList(NUMBER_OF_HOSTS);

			for (int hostID = 0; hostID < Helper.hostList.size(); hostID++) {
				mapCALA.put(hostID, new CALA(.2, .6, .4));
				mapLAPlacement.put(
						hostID,
						new LAutomata(false, .5, .5, .5, .5, .5, .5, .5, .5, 0,
								.5, (Helper.hostList.get(hostID)
										.getUtilizationOfCpu()), Status.IDLE));

			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	public static int getNUMBER_OF_HOSTS() {
		return NUMBER_OF_HOSTS;
	}

	public static void setNUMBER_OF_HOSTS(int number_of_hosts) {
		NUMBER_OF_HOSTS = number_of_hosts;
	}

}
