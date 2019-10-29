package MyProject;

import java.util.HashMap;
import java.util.Random;

import org.cloudbus.cloudsim.power.PowerHost;
import MyProject.PlanetLabRunner;

public class CALAPlacementVM {

	// public static List<PowerHost> hostList = null;
	public static HashMap<Integer, PowerHost> host;
	public static boolean fX;
	public static boolean fM;

	public static double randActionXCALA(HashMap<Integer, CALA> mapCALA,
			double mean, double sdiv, int hostID) {

		// mean = PlanetLabRunner.mapCALA.get(hostID).getMeanProbability();
		//
		// sdiv =
		// PlanetLabRunner.mapCALA.get(hostID).getStandardDivProbability();
		// System.out.println("mean sdive" + mean + sdiv);
		double guess = normal(mean, sdiv);
		PlanetLabRunner.mapCALA.get(hostID).setXProbability(guess);
		// System.out.println("x" + guess);
		return guess;
	}

	private static double second;
	private static boolean secondValid = false;
	private static int i = 0;

	static double normal(double mean, double std) {
		double v1, v2, y1, y2, x1, x2, w;
		Random generator = new Random();
		if (secondValid) {
			secondValid = false;
			return second;
		}

		do {
			v1 = 2 * generator.nextDouble() - 1;
			v2 = 2 * generator.nextDouble() - 1;
			w = v1 * v1 + v2 * v2;
		} while (w > 1);

		y1 = v1 * Math.sqrt(-2 * Math.log(w) / w);
		y2 = v2 * Math.sqrt(-2 * Math.log(w) / w);
		x1 = mean + y1 * std;
		x2 = mean + y2 * std;
		second = x2;
		secondValid = true;
		return x1;
	}

	public static int responseXCALA(boolean responseformips) {
		int respX;

		if ((responseformips == true)
				&& isFX()
				&& (PowerVmAllocationPolicyMigrationAbstract.calaCheckpoint == true))
			respX = 1;

		respX = 0;

		return respX;
	}

	public static int responseMCALA(boolean responseMformips) {
		int respM;

		if ((responseMformips == true) && isFM()
				&& (PowerVmAllocationPolicyMigrationAbstract.calaCheckpoint == true))
			return respM = 1;

		else
			return respM = 0;
	}

	public static void updateProbCALA(HashMap<Integer, CALA> mapCALA,
			int hostID, int xB, int mB) {
		double t = 0;
		double avg = .5;
		double standardiv = .5;

		if (PlanetLabRunner.mapCALA.get(hostID).getStandardDivProbability() <= 1) {
			t = 1;
		} else
			t = PlanetLabRunner.mapCALA.get(hostID).getStandardDivProbability();

		avg = PlanetLabRunner.mapCALA.get(hostID).getMeanProbability()
				+ 0.1
				* ((xB - mB) / t)
				* ((PlanetLabRunner.mapCALA.get(hostID).getXProbability() - PlanetLabRunner.mapCALA
						.get(hostID).getMeanProbability()) / t);
		PlanetLabRunner.mapCALA.get(hostID).setMeanProbability(avg);
		//System.out.println("avg" + avg);
		standardiv = PlanetLabRunner.mapCALA.get(hostID)
				.getStandardDivProbability()
				+ 0.1
				* ((xB - mB) / t)
				* ((Math.pow((PlanetLabRunner.mapCALA.get(hostID)
						.getXProbability() - PlanetLabRunner.mapCALA
						.get(hostID).getMeanProbability())
						/ t, 2)) - 1)
				- (0.1 * 5 * (PlanetLabRunner.mapCALA.get(hostID)
						.getStandardDivProbability() - 0.01));
		PlanetLabRunner.mapCALA.get(hostID).setStandardDivProbability(
				standardiv);
		//System.out.println("div" + standardiv);
		CALAPlacementVM.randActionXCALA(PlanetLabRunner.mapCALA, avg,
				standardiv, hostID);

	}

	public static boolean isFX() {
		return fX;
	}

	public static void setFX(boolean fx) {
		fX = fx;
	}

	public static boolean isFM() {
		return fM;
	}

	public static void setFM(boolean fm) {
		fM = fm;
	}

}
