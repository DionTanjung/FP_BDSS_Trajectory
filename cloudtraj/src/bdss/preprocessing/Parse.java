package bdss.preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bdss.model.Marker;
import bdss.model.PathTrip;

// Read and parse csv file
// Input: CSV dataset
// Output: Trajectory Object
public class Parse {
	List<PathTrip> tempTrajectory = new ArrayList<PathTrip>();

	// ==================================READ TRAJECTORY
	// =====================================
	// Read from csv file
	public void read(String trajectory) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(trajectory));
		boolean firstLine = true;
		try {
			String line = br.readLine();
			while (true) {
				if (firstLine) {
					firstLine = false;
					continue;
				} else {
					// note that csv file is separated by comma
					processTrajectory(line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"));
					line = br.readLine();
					if (line == null)
						break;					
				}
			}
		} finally {
			br.close();
		}
	}

	// set array value in variable tempTrajectory
	public void processTrajectory(String[] traj) {
//		String[] polyString = traj[8].split(",");
//		for (String latlon : polyString) {
//			System.out.println(latlon);
//		}

		PathTrip t = new PathTrip(traj[4].replace("\"", ""), 
				traj[5].replace("\"", ""), 
				traj[8].replace("\"", "").replace("[[", "[").replace("]]", "]"));
		tempTrajectory.add(t);
	}

	public List<PathTrip> getTempTrajectory() {
		return tempTrajectory;
	}

	public void setTempTrajectory(List<PathTrip> tempTrajectory) {
		this.tempTrajectory = tempTrajectory;
	}
}
