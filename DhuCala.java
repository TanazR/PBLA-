
package MyProject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;



public abstract class DhuCala {
	
	public static void main(String[] args) throws IOException {
		boolean enableOutput = true;
		boolean outputToFile = false;
		String inputFolder = DhuCala.class.getClassLoader().getResource("workload/planetlab").getPath();
		String outputFolder = "output";
		String workload = "2013-86"; // PlanetLab workload
		String vmAllocationPolicy = "dhucala"; 
		String vmSelectionPolicy = "mmt";
		
//		PrintStream out = new PrintStream(new FileOutputStream("MyOutputFile.txt"));
//		System.setOut(out);

		new PlanetLabRunner(
				enableOutput,
				outputToFile,
				inputFolder,
				outputFolder,
				workload,
				vmAllocationPolicy,
				vmSelectionPolicy);
		
		
	}

}

