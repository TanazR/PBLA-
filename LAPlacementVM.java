package MyProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.power.PowerHost;

public class LAPlacementVM {

	public static HashMap<Integer, PowerHost> host;
	public static int [] numberofsetaction = {0,0,0,0,0,0,0,0};
	
	// public static List<PowerHost> hostList = null;
	// public static int i = 0;

	public static void LAPlacement() {

		Log.printLine("Placement by using LA finished!");

	}


	public static int randomValues() {
		Random randAction = new Random();
		int randomAction = randAction.nextInt(999);
		return randomAction;
	}

	public static void selectAction(HashMap<Integer, LAutomata> mapLAPlacement,
			int i) {

		int randNum = randomValues();

		PlanetLabRunner.mapLAPlacement.get(i).getStateOfLA();

		// System.out.println("dhjv" + LAutomata.laState.get(5));

		if (LAutomata.laState.get(i) == "IDLE") {

			if (randNum >= 1
					&& randNum <= (PlanetLabRunner.mapLAPlacement.get(i)
							.getSIdleAcceptProbability() * 1000)) {
				PlanetLabRunner.mapLAPlacement.get(i).setAction(true);
				// LA action is Accept
			} else
				PlanetLabRunner.mapLAPlacement.get(i).setAction(false);

		}
		if (LAutomata.laState.get(i) == "AVGUTIZALTION") {
			if (randNum >= 1
					&& randNum <= (PlanetLabRunner.mapLAPlacement.get(i)
							.getSAvgAcceptProbability() * 1000)) {
				PlanetLabRunner.mapLAPlacement.get(i).setAction(true);
				// LA action is Accept
			} else
				PlanetLabRunner.mapLAPlacement.get(i).setAction(false);
			// LA action is Reject
		}

		if (LAutomata.laState.get(i) == "ACTIVE") {
			if (randNum >= 1
					&& randNum <= (PlanetLabRunner.mapLAPlacement.get(i)
							.getSActiveAcceptProbability() * 1000)) {
				PlanetLabRunner.mapLAPlacement.get(i).setAction(true);
				// LA action is Accept
			} else
				PlanetLabRunner.mapLAPlacement.get(i).setAction(false);
			// LA action is Reject}

		}

		if (LAutomata.laState.get(i) == "OVER") {
			if (randNum >= 1
					&& randNum <= (PlanetLabRunner.mapLAPlacement.get(i)
							.getSOverAcceptProbability() * 1000)) {
				PlanetLabRunner.mapLAPlacement.get(i).setAction(true);
				// LA action is Accept
			} else
				PlanetLabRunner.mapLAPlacement.get(i).setAction(false);
			// LA action is Reject
		}
		// System.exit(0);
	}

	public static double enviromentResponse(
			HashMap<Integer, LAutomata> mapLAPlacement, int ohostID, int thostID) {

		double respons;
		Map<Integer, Double> st = new HashMap<Integer, Double>();
		List<Double> rightval = PowerVmAllocationPolicyMigrationAbstract
				.getStateofHost();

		for (int i = 0; (i < rightval.size()); i++) {
			st.put(i, rightval.get(i).doubleValue());
		}

		respons = 1 / ((1 + st.get(thostID) / st.get(ohostID)));

		return respons;
	}

	public static void rewardPenalty(
			HashMap<Integer, LAutomata> mapLAPlacement, int ohostID, int thostID) {

		double B = enviromentResponse(PlanetLabRunner.mapLAPlacement, ohostID,
				thostID);
		
		// System.out.println("problem is here" + B);

		double pAccpet = 0.5;
		double pReject = 0.5;
		String sSituation = new String();
		String dSituation = new String();
		int count = 0;
		
		String ostate = LAutomata.laState.get(ohostID);
		String tstate = LAutomata.laState.get(thostID);

		// System.out.println("problem is here" + ostate + tstate);

		if (mapLAPlacement.get(thostID).isAction() == true ) {
			if (tstate == "IDLE")
				numberofsetaction [0]++;
			else if (tstate == "AVGUTIZALTION")
				numberofsetaction [2]++;
				else	if (tstate == "ACTIVE")
					numberofsetaction [4]++;
				else		if (tstate == "OVER")
					numberofsetaction [6]++;

		}

		else {
			if (tstate == "IDLE")
				numberofsetaction [1]++;
			else if (tstate == "AVGUTIZALTION")
				numberofsetaction [3]++;
				else	if (tstate == "ACTIVE")
					numberofsetaction [5]++;
				else		if (tstate == "OVER")
					numberofsetaction [7]++;
		}

		if (ostate == "IDLE")
			sSituation = "IDLE";
		else if (ostate == "AVGUTIZALTION")
			sSituation = "AVGUTIZALTION";
		else if (ostate == "ACTIVE")
			sSituation = "ACTIVE";
		else if (ostate == "OVER")
			sSituation = "OVER";

		if (tstate == "IDLE")
			dSituation = "IDLE";
		else if (tstate == "AVGUTIZALTION")
			dSituation = "AVGUTIZALTION";
		else if (tstate == "ACTIVE")
			dSituation = "ACTIVE";
		else if (tstate == "OVER")
			dSituation = "OVER";

		if ((mapLAPlacement.get(thostID).isAction() == false && dSituation == "IDLE")
				&& (sSituation == "ACTIVE")) {

			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActiveAcceptProbability()

					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability();

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActiveAcceptProbability(pAccpet);

			// System.out.println("accept ama false" +
			// PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSActiveAcceptProbability() );

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActivRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability();
			
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActiveAcceptProbability(pReject);
			// System.out.println("reject ama false" +
			// PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSActiveAcceptProbability() );

		} else if ((mapLAPlacement.get(thostID).isAction() == false && dSituation == "IDLE")
				&& (sSituation == "IDLE")) {
			if (mapLAPlacement.get(ohostID).getHostState() != 0.0) {
				pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
						.getSIdleAcceptProbability()

						+ 0.3
						* (1 - B)
						* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
								.getSIdleAcceptProbability())
						- 0.3
						* B
						* PlanetLabRunner.mapLAPlacement.get(thostID)
								.getSIdleAcceptProbability();

				PlanetLabRunner.mapLAPlacement.get(thostID)
						.setSIdleAcceptProbability(pAccpet);

				// System.out.println("accp ama false" +
				// PlanetLabRunner.mapLAPlacement.get(thostID).getSIdleAcceptProbability());

				pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
						.getSIdleRejectProbability()
						- 0.3
						* (1 - B)
						* PlanetLabRunner.mapLAPlacement.get(thostID)
								.getSIdleRejectProbability()
						+ 0.3
						* B
						* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
								.getSIdleRejectProbability())
						- 0.3
						* (1 - B)
						* PlanetLabRunner.mapLAPlacement.get(thostID)
								.getSIdleRejectProbability();
				
				PlanetLabRunner.mapLAPlacement.get(thostID)
						.setSIdleRejectProbability(pReject);
				// System.out.println("reject ama false" +
				// PlanetLabRunner.mapLAPlacement.get(thostID).getSIdleRejectProbability());
				// System.exit(0);
			}
		}

		else if ((PlanetLabRunner.mapLAPlacement.get(thostID).isAction() == true && dSituation == "AVGUTIZALTION")
				&& (sSituation == "IDLE")) {
			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSAvgAcceptProbability()
					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgAcceptProbability();
			
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSAvgAcceptProbability(pAccpet);
			
			PlanetLabRunner.mapLAPlacement.get(thostID).setRewardCount(count++);

			// System.out.println("eyva "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSAvgAcceptProbability());

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSAvgRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSAvgRejectProbability(pReject);

			// System.out.println("reject "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSAvgRejectProbability());
			// System.exit(0);
		} else if ((PlanetLabRunner.mapLAPlacement.get(thostID).isAction() == true && dSituation == "AVGUTIZALTION")
				&& (sSituation == "AVGUTIZALTION")) {
			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSAvgAcceptProbability()
					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgAcceptProbability();
			

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSAvgAcceptProbability(pAccpet);
			
			PlanetLabRunner.mapLAPlacement.get(thostID).setRewardCount(count++);

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSAvgRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability();
			
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSAvgRejectProbability(pReject);

			// System.out.println("reject "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSAvgRejectProbability());
			// System.exit(0);
		}

		else if ((PlanetLabRunner.mapLAPlacement.get(thostID).isAction() == true && dSituation == "AVGUTIZALTION")
				&& (sSituation == "OVER")) {
			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSAvgAcceptProbability()
					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgAcceptProbability();
			
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSAvgAcceptProbability(pAccpet);
			
			PlanetLabRunner.mapLAPlacement.get(thostID).setRewardCount(count++);

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSAvgRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSAvgRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSAvgRejectProbability(pReject);

			// System.out.println("reject "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSAvgRejectProbability());
			// System.exit(0);
		} else if (mapLAPlacement.get(thostID).isAction() == false
				&& (dSituation == "IDLE") && (sSituation == "AVGUTIZALTION")) {

			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSIdleAcceptProbability()
					+ (0.3 * (1 - B))
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSIdleAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSIdleAcceptProbability();

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSIdleAcceptProbability(pAccpet);

			// System.out.println("bia dg" +
			// PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSIdleAcceptProbability() );

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSIdleRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSIdleRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSIdleRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSIdleRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSIdleRejectProbability(pReject);
			// System.out.println("bia afarin + reject" +
			// PlanetLabRunner.mapLAPlacement.get(thostID).getSIdleRejectProbability());
			// System.exit(0);
		} else if ((PlanetLabRunner.mapLAPlacement.get(thostID).isAction() == true && dSituation == "ACTIVE")
				&& (sSituation == "IDLE")) {
			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActiveAcceptProbability()
					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability();
			

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActiveAcceptProbability(pAccpet);
			PlanetLabRunner.mapLAPlacement.get(thostID).setRewardCount(count++);

			// System.out.println("eyva "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSActiveAcceptProbability());

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActivRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActivRejectProbability(pReject);

		} else if ((PlanetLabRunner.mapLAPlacement.get(thostID).isAction() == true && dSituation == "ACTIVE")
				&& (sSituation == "AVGUTIZALTION")) {
			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActiveAcceptProbability()
					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability();
			
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActiveAcceptProbability(pAccpet);
			PlanetLabRunner.mapLAPlacement.get(thostID).setRewardCount(count++);

			// System.out.println("eyva "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSActiveAcceptProbability());

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActivRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActivRejectProbability(pReject);

		} else if ((PlanetLabRunner.mapLAPlacement.get(thostID).isAction() == true && dSituation == "ACTIVE")
				&& (sSituation == "OVER")) {
			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActiveAcceptProbability()
					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActiveAcceptProbability();
			

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActiveAcceptProbability(pAccpet);
			PlanetLabRunner.mapLAPlacement.get(thostID).setRewardCount(count++);

			// System.out.println("eyva "+PlanetLabRunner.mapLAPlacement.get(thostID)
			// .getSActiveAcceptProbability());

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSActivRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSActivRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSActivRejectProbability(pReject);

		} else if ((mapLAPlacement.get(thostID).isAction() == false && dSituation == "OVER")
				&& (sSituation == "IDLE")) {

			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSOverAcceptProbability()

					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverAcceptProbability();

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSOverAcceptProbability(pAccpet);

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSOverRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSOverRejectProbability(pReject);

		} else if ((mapLAPlacement.get(thostID).isAction() == false && dSituation == "OVER")
				&& (sSituation == "ACTIVE")) {

			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSOverAcceptProbability()

					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverAcceptProbability();

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSOverAcceptProbability(pAccpet);

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSOverRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSOverRejectProbability(pReject);

		} else if ((mapLAPlacement.get(thostID).isAction() == false && dSituation == "OVER")
				&& (sSituation == "AVGUTIZALTION")) {

			pAccpet = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSOverAcceptProbability()

					+ 0.3
					* (1 - B)
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverAcceptProbability())
					- 0.3
					* B
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverAcceptProbability();

			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSOverAcceptProbability(pAccpet);

			pReject = PlanetLabRunner.mapLAPlacement.get(thostID)
					.getSOverRejectProbability()
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability()
					+ 0.3
					* B
					* (1 - PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability())
					- 0.3
					* (1 - B)
					* PlanetLabRunner.mapLAPlacement.get(thostID)
							.getSOverRejectProbability();
			PlanetLabRunner.mapLAPlacement.get(thostID)
					.setSOverRejectProbability(pReject);

		}

	}
}
	
	

