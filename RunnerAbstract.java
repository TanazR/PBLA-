package MyProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationInterQuartileRange;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegression;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegressionRobust;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMaximumCorrelation;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumMigrationTime;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyRandomSelection;

/**
 * The Class RunnerAbstract.
 * 
 * If you are using any algorithms, policies or workload included in the power
 * package, please cite the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 */
public abstract class RunnerAbstract {

	/** The enable output. */
	private static boolean enableOutput;

	/** The broker. */
	protected static DatacenterBroker broker;

	/** The cloudlet list. */
	protected static List<Cloudlet> cloudletList;

	/** The vm list. */
	protected static List<Vm> vmList;

	/** The host list. */



	/**
	 * Run.
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
	public RunnerAbstract(boolean enableOutput, boolean outputToFile,
			String inputFolder, String outputFolder, String workload,
			String vmAllocationPolicy, String vmSelectionPolicy) {
		try {
			initLogOutput(enableOutput, outputToFile, outputFolder, workload,
					vmAllocationPolicy, vmSelectionPolicy);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		init(inputFolder + "/" + workload);
		start(outputFolder, getVmAllocationPolicy(vmAllocationPolicy,
				vmSelectionPolicy));
	}
	
	/**
	 * Inits the log output.
	 * 
	 * @param enableOutput
	 *            the enable output
	 * @param outputToFile
	 *            the output to file
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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	protected void initLogOutput(boolean enableOutput, boolean outputToFile,
			String outputFolder, String workload, String vmAllocationPolicy,
			String vmSelectionPolicy) throws IOException, FileNotFoundException {
		setEnableOutput(enableOutput);
		Log.setDisabled(!isEnableOutput());
		if (isEnableOutput() && outputToFile) {
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			File folder2 = new File(outputFolder + "/log");
			if (!folder2.exists()) {
				folder2.mkdir();
			}

			File file = new File(outputFolder + "/log/"
					+ "DhuCALA&placementFALA" + ".txt");
			
			System.out.println(file);
			file.createNewFile();
			Log.setOutput(new FileOutputStream(file));
		}
	}

	/**
	 * Inits the simulation.
	 * 
	 * @param inputFolder
	 *            the input folder
	 */
	protected abstract void init(String inputFolder);

	/**
	 * Starts the simulation.
	 * 
	 * @param experimentName
	 *            the experiment name
	 * @param outputFolder
	 *            the output folder
	 * @param vmAllocationPolicy
	 *            the vm allocation policy
	 */
	protected void start(String outputFolder,
			VmAllocationPolicy vmAllocationPolicy) {
		System.out.println("Starting" + " DhuCALA&placementFALA");

		try {
			
			PowerDatacenter datacenter = (PowerDatacenter) Helper.createDatacenter("Datacenter", PowerDatacenter.class,
							Helper.hostList, vmAllocationPolicy);
						
		
			datacenter.setDisableMigrations(false);
			
			broker.submitVmList(vmList);
			broker.submitCloudletList(cloudletList);

			CloudSim.terminateSimulation(Constants.SIMULATION_LIMIT);
			double lastClock = CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();
			Log.printLine("Received " + newList.size() + " cloudlets");

			CloudSim.stopSimulation();

			Helper.printResults(datacenter, vmList, lastClock,
					Constants.OUTPUT_CSV, outputFolder);
			
						

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}

		Log.printLine("Finished " + "DhuCALA&placementFALA");
	}

	

	/**
	 * Gets the vm allocation policy.
	 * 
	 * @param vmAllocationPolicyName
	 *            the vm allocation policy name
	 * @param vmSelectionPolicyName
	 *            the vm selection policy name
	 * @param parameterName
	 *            the parameter name
	 * @return the vm allocation policy
	 */
	protected VmAllocationPolicy getVmAllocationPolicy(
			String vmAllocationPolicyName, String vmSelectionPolicyName) {
		VmAllocationPolicy vmAllocationPolicy = null;
		PowerVmSelectionPolicy vmSelectionPolicy = null;

		if (!vmSelectionPolicyName.isEmpty()) {
			vmSelectionPolicy = getVmSelectionPolicy(vmSelectionPolicyName);
		} 
		
		if (vmAllocationPolicyName.equals("dhucala")) {
			
			vmAllocationPolicy = new PowerVmAllocationPolicyPredictionUsingCALA(
					Helper.hostList, vmSelectionPolicy);} 
		 else {
			System.out.println("Unknown VM allocation policy: "
					+ vmAllocationPolicyName);
			System.exit(0);
		}
		return vmAllocationPolicy;
	}

	/**
	 * Gets the vm selection policy.
	 * 
	 * @param vmSelectionPolicyName
	 *            the vm selection policy name
	 * @return the vm selection policy
	 */
	protected PowerVmSelectionPolicy getVmSelectionPolicy(
			String vmSelectionPolicyName) {
		PowerVmSelectionPolicy vmSelectionPolicy = null;
		if (vmSelectionPolicyName.equals("mc")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMaximumCorrelation(
					new PowerVmSelectionPolicyMinimumMigrationTime());
		} else if (vmSelectionPolicyName.equals("mmt")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMinimumMigrationTime();
		} else if (vmSelectionPolicyName.equals("mu")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMinimumUtilization();
		} else if (vmSelectionPolicyName.equals("rs")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyRandomSelection();
		} else {
			System.out.println("Unknown VM selection policy: "
					+ vmSelectionPolicyName);
			System.exit(0);
		}
		return vmSelectionPolicy;
	}

	/**
	 * Sets the enable output.
	 * 
	 * @param enableOutput
	 *            the new enable output
	 */
	public void setEnableOutput(boolean enableOutput) {
		RunnerAbstract.enableOutput = enableOutput;
	}

	/**
	 * Checks if is enable output.
	 * 
	 * @return true, if is enable output
	 */
	public boolean isEnableOutput() {
		return enableOutput;
	}

}
