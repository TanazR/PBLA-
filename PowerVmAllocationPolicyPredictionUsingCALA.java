/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package MyProject;

import java.util.List;


import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumMigrationTime;

 /* @author Anton Beloglazov
 * @since CloudSim Toolkit 3.0
 */
public class PowerVmAllocationPolicyPredictionUsingCALA extends PowerVmAllocationPolicyMigrationAbstract {

	
	
	/**
	 * @param hostList the host list
	 * @param vmSelectionPolicy the vm selection policy
	 */
	
	public PowerVmAllocationPolicyPredictionUsingCALA(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy)
	{
		super(hostList, (PowerVmSelectionPolicy) vmSelectionPolicy);
		setPredictionThreshold(predictionThreshold);
	}
	private double predictionThreshold = 0.25;

	protected double getPredictionThreshold() {
		return predictionThreshold;
	}

	protected void setPredictionThreshold(double predictionThreshold) {
		this.predictionThreshold = predictionThreshold;
	}

	
}
