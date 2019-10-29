package MyProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LAutomata {
	private boolean action;
	private double sIdleAcceptProbability;
	private double sIdleRejectProbability;
	private double sAvgAcceptProbability;
	private double sAvgRejectProbability;
	private double sActiveAcceptProbability;
	private double sActivRejectProbability;
	private double sOverAcceptProbability;
	private double sOverRejectProbability;
	private double allProb;
	private int rewardCount;
	private double hostState;
	static Status LAstate;
	
	public static Map <Integer, String> laState = new HashMap <Integer, String>();
	
	public enum Status {
		IDLE, AVGUTIZALTION, ACTIVE, OVER
	};

	public LAutomata(boolean action, double idleAcceptProbability,
			double idleRejectProbability, double avgAcceptProbability,
			double avgRejectProbability, double activeAcceptProbability,
			double activRejectProbability, 
			double overRejectProbability, double overAcceptProbability,
			int rewardCount, double allProb,
			double hostState, Status nameOFLAstate) {
		super();
		this.action = action;
		this.sIdleAcceptProbability = idleAcceptProbability;
		this.sIdleRejectProbability = idleRejectProbability;
		this.sAvgAcceptProbability = avgAcceptProbability;
		this.sAvgRejectProbability = avgRejectProbability;
		this.sActiveAcceptProbability = activeAcceptProbability;
		this.sActivRejectProbability = activRejectProbability;
		this.sOverAcceptProbability = overAcceptProbability;
		this.sOverRejectProbability = overRejectProbability;
		this.allProb = allProb;
		this.rewardCount = rewardCount;
		this.hostState = hostState;
		this.LAstate = nameOFLAstate;
	}

	public double getSIdleAcceptProbability() {
		return sIdleAcceptProbability;
	}

	public void setSIdleAcceptProbability(double idleAcceptProbability) {
		sIdleAcceptProbability = idleAcceptProbability;
	}

	public double getSIdleRejectProbability() {
		return sIdleRejectProbability;
	}

	public void setSIdleRejectProbability(double idleRejectProbability) {
		sIdleRejectProbability = idleRejectProbability;
	}

	public double getSAvgAcceptProbability() {
		return sAvgAcceptProbability;
	}

	public void setSAvgAcceptProbability(double avgAcceptProbability) {
		sAvgAcceptProbability = avgAcceptProbability;
	}

	public double getSAvgRejectProbability() {
		return sAvgRejectProbability;
	}

	public void setSAvgRejectProbability(double avgRejectProbability) {
		sAvgRejectProbability = avgRejectProbability;
	}

	public double getSActiveAcceptProbability() {
		return sActiveAcceptProbability;
	}

	public void setSActiveAcceptProbability(double activeAcceptProbability) {
		sActiveAcceptProbability = activeAcceptProbability;
	}

	public double getSActivRejectProbability() {
		return sActivRejectProbability;
	}

	public void setSActivRejectProbability(double activRejectProbability) {
		sActivRejectProbability = activRejectProbability;
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public double getAllProb() {
		return allProb;
	}

	public void setAllProb(double allProb) {
		this.allProb = allProb;
	}

	public int getRewardCount() {
		return rewardCount;
	}

	public void setRewardCount(int rewardCount) {
		this.rewardCount = rewardCount;
	}

	public double getHostState() {
		return hostState;
	}

	public void setHostState(double hostState) {
		this.hostState = hostState;
	}

	public void setStateOfLA(Status astate) {
		this.LAstate = astate;
	}
	public double getSOverAcceptProbability() {
		return sOverAcceptProbability;
	}

	public void setSOverAcceptProbability(double sOverAcceptProbability) {
		this.sOverAcceptProbability = sOverAcceptProbability;
	}

	public double getSOverRejectProbability() {
		return sOverRejectProbability;
	}

	public void setSOverRejectProbability(double sOverRejectProbability) {
		this.sOverRejectProbability = sOverRejectProbability;
	}
	

	public Map<Integer, String> getStateOfLA() {
		List<Double> st = PowerVmAllocationPolicyMigrationAbstract.getStateofHost();
	
		for (int i = 0; (i < st.size() ); i++) {

			if ((0 < st.get(i).doubleValue()) && (st.get(i).doubleValue() <= 0.10)) {
				setHostState(st.get(i).doubleValue());
				LAstate = Status.IDLE;
				PlanetLabRunner.mapLAPlacement.get(i).setStateOfLA(LAstate);
				laState.put(i, "IDLE");
		//		System.out.println("chap1" + getHostState());
//				
		//	System.exit(0);

			} else if ((0.10 < st.get(i).doubleValue())
					&& (st.get(i).doubleValue() <= 0.60)) {
				setHostState(st.get(i).doubleValue());
				LAstate = Status.AVGUTIZALTION;
				PlanetLabRunner.mapLAPlacement.get(i).setStateOfLA(LAstate);
				laState.put(i, "AVGUTIZALTION");
			//	System.out.println("chap1" + getHostState());
			//System.exit(0);

			} else if (0.60 < st.get(i).doubleValue()
					&& st.get(i).doubleValue() < 0.90) {
				setHostState(st.get(i).doubleValue());
				LAstate = Status.ACTIVE;
				PlanetLabRunner.mapLAPlacement.get(i).setStateOfLA(LAstate);
				laState.put(i, "ACTIVE");
							
			}else if (st.get(i).doubleValue() >= 0.90) {
					setHostState(st.get(i).doubleValue());
					LAstate = Status.OVER;
					PlanetLabRunner.mapLAPlacement.get(i).setStateOfLA(LAstate);
					laState.put(i, "OVER");
				// 
			}
		//System.out.println("ey falak" + PlanetLabRunner.mapLAPlacement.get(Helper.hostList.get(i)).getHostState());
		//System.exit(0);
		}
		 return laState;
	}


}