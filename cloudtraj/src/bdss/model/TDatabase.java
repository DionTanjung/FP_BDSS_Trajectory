package bdss.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TDatabase {
	public List<Trajectory> trajectories = new ArrayList<Trajectory>();
	public List<coordinates> coordinate = new ArrayList<coordinates>();
	public List<Double> lats = new ArrayList<Double>();
	public List<Double> lngs = new ArrayList<Double>();
	public coordinates upper;
	
	// Find the upper most point. In case of a tie, get the left most point.
	public coordinates upperLeft() {
	    coordinates top = this.coordinate.get(0);
	    for(int i = 1; i < this.coordinate.size(); i++) {
	        coordinates temp = this.coordinate.get(i);
	        if(temp.lat > top.lat || (temp.lat == top.lat && temp.lng < top.lng)) {
	            top = temp;
	        }
	    }
	    return top;
	}
	
	
	public void importDataset(String src) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(src));
		try {
			br.readLine();
			String line = br.readLine();
			while (true) {
//				System.out.println(line);
				// note that csv file is separated by comma
				processDataset(line.split(","));
				line = br.readLine();
				if (line == null)
					break;
			}
		} finally {
			coordinates upper=this.upperLeft();
			Collections.sort(this.coordinate,new LocationComparator(upper));
			br.close();
		}
	}

	// set array value in variable tempCategory
	public void processDataset(String[] data) {
		String tripId = data[0].replace("\"", "");
		char call_type = data[1].charAt(1);
		int origin_call = (data[2] == "") ? Integer.parseInt(data[2].replace("\"", "")) : -1;
		int origin_stand = (data[3] == "") ? Integer.parseInt(data[3].replace("\"", "")) : -1;
		int taxiId = Integer.parseInt(data[4].replace("\"", ""));
		int timestamp = Integer.parseInt(data[5].replace("\"", ""));
		char day_type = data[6].charAt(1);

		int time = timestamp;
		List<Marker> trip = new ArrayList<Marker>();
		double lat = 0, lng = 0;
		for (int i = 8; i < data.length; i++) {
			if (!data[i].replaceAll("[\\[\\]\"]", "").equals("")) {
				if (i % 2 == 0) {
					lat = Double.parseDouble(data[i].replaceAll("[\\[\\]\"]", ""));
					if (!this.lats.contains(lat)) {
						this.lats.add(lat);
					}
				} else {
					lng = Double.parseDouble(data[i].replaceAll("[\\[\\]\"]", ""));
					trip.add(new Marker(time, lat, lng));
					coordinate.add(new coordinates(lat, lng,tripId));
					if (!this.lngs.contains(lng)) {
						this.lngs.add(lng);
					}
				}

				time = time + 15;
			}
		}
		trajectories
				.add(new Trajectory(tripId, call_type, origin_call, origin_stand, taxiId, timestamp, day_type, trip));
	}

	public List<Trajectory> getTrajectories() {
		return trajectories;
	}

	public void view() {

		for (Trajectory t : trajectories) {
			t.view();
			System.out.println();
		}
	}

	public void viewCoordinates() {
//		for (Trajectory t : trajectories) {
//			t.viewCoordinates();
//			System.out.println();
//		}
		for (coordinates c: this.coordinate) {
			c.view();
		}
	}
	
	
}
