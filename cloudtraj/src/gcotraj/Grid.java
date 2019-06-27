package gcotraj;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bdss.model.Trajectory;
import bdss.model.Marker;
import bdss.model.coordinates;
import bdss.model.TDatabase;



public class Grid {
	public int grid_size;
	public int distance_bound;
	public Map<Integer, List<Trajectory>> temporal = new HashMap<Integer, List<Trajectory>>();
	public Map<Integer, List<coordinates>> init = new HashMap<Integer, List<coordinates>>();
	
	public Map<Integer, List<Trajectory>> grid = new HashMap<Integer, List<Trajectory>>();
	
	public Grid(int size, int distance) {
		this.grid_size = size;
		this.distance_bound = distance;
	}

	//temporal segmentation
	public void segmentation(TDatabase D) {
		// time morning 12-9, afternoon 9-5, evening 5-8, night 8-12
		temporal.put(0, new ArrayList<Trajectory>());
		temporal.put(1, new ArrayList<Trajectory>());
		temporal.put(2, new ArrayList<Trajectory>());
		temporal.put(3, new ArrayList<Trajectory>());
		for (Trajectory t : D.trajectories) {
			Trajectory subTrajectory = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
					t.timestamp, t.day_type);
			int i = -1;
			for (Marker p : t.baseline) {
				Date date = new Date();
				date.setTime((long) p.timestamps * 1000);
				if (date.getHours() <= 9) {
					if (i == 0 || i == -1) {
						subTrajectory.baseline.add(p);
					} else {
						temporal.get(i).add(subTrajectory);
						subTrajectory = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
								t.timestamp, t.day_type);
						subTrajectory.baseline.add(p);
					}
					i = 0;
				} else if (date.getHours() > 9 && date.getHours() < 17) {
//					System.out.println(i + "-" + date.getHours());
					if (i == 1 || i == -1) {
						subTrajectory.baseline.add(p);
					} else {
						temporal.get(i).add(subTrajectory);
						subTrajectory = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
								t.timestamp, t.day_type);
						subTrajectory.baseline.add(p);
					}
					i = 1;
				} else if (date.getHours() >= 17 && date.getHours() <= 20) {
//					System.out.println(i + "-" + date.getHours());
					if (i == 2 || i == -1) {
						subTrajectory.baseline.add(p);
					} else {

						temporal.get(i).add(subTrajectory);
						subTrajectory = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
								t.timestamp, t.day_type);
						subTrajectory.baseline.add(p);
					}
					i = 2;
				} else if (date.getHours() > 20) {
					if (i == 3 || i == -1) {
						subTrajectory.baseline.add(p);
					} else {
						temporal.get(i).add(subTrajectory);
						subTrajectory = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
								t.timestamp, t.day_type);
						subTrajectory.baseline.add(p);
					}
					i = 3;
				}
			}
			if (i!=-1) {
				temporal.get(i).add(subTrajectory);
			}
		}
	}
	
	public void gridPartitioning(TDatabase D) {
		List<coordinates> coordinates=D.coordinate;
		double length=distance(coordinates.get(0).lat, coordinates.get(coordinates.size()-1).lat, coordinates.get(0).lng,coordinates.get(coordinates.size()-1).lng);
		System.out.println(length/this.grid_size);
//		double separator = coordinates.size()/(grid_size*grid_size);
//		int i=0;
//		coordinates origin=null;
//		for (coordinates c : coordinates) {
//			if (i==0) {
//				origin=c;
//				int index = this.init.size();
//				this.init.put(index, new ArrayList<coordinates>());
//				this.init.get(index).add(c);
//			}else {
//				double distance_m = distance(origin.lat, c.lat, origin.lng,c.lng);
//				System.out.println(distance_m);
//				if (distance_m < this.distance_bound) {
//					int index = this.init.size()-1;
//					this.init.get(index).add(c);
//				}else {
//					int index = this.init.size();
//					this.init.put(index, new ArrayList<coordinates>());
//					this.init.get(index).add(c);
//				}
//			}
//			i++;
//		}
	}

	public void gridBased() {
		for (Entry<Integer, List<Trajectory>> entry : temporal.entrySet()) {
			for (Trajectory t : entry.getValue()) {
				Trajectory newTraj = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
						t.timestamp, t.day_type);
				int i=-1;
				for (Marker p : t.baseline) {
					if (newTraj.baseline.isEmpty()) {
						newTraj.baseline.add(p);
					} else {
						double distance_m = distance(newTraj.baseline.get(0).lat, p.lat, newTraj.baseline.get(0).lng,
								p.lng);
						if (distance_m < this.distance_bound) {
							newTraj.baseline.add(p);
						} else {
							if (this.grid.isEmpty()) {
								int index = this.grid.size();
								this.grid.put(index, new ArrayList<Trajectory>());
								this.grid.get(index).add(newTraj);
							} else {
								for (Entry<Integer, List<Trajectory>> gridEntry : this.grid.entrySet()) {
									distance_m = distance(gridEntry.getValue().get(0).baseline.get(0).lat, p.lat,
											gridEntry.getValue().get(0).baseline.get(0).lng, p.lng);
									if (distance_m <= this.distance_bound) {
										gridEntry.getValue().add(newTraj);
									} else {
										i=0;
									}
								}
								if (i==0) {
									int index = this.grid.size();
									this.grid.put(index, new ArrayList<Trajectory>());
									this.grid.get(index).add(newTraj);
									i=-1;
								}
							}
							newTraj = new Trajectory(t.tripId, t.call_type, t.origin_call, t.origin_stand, t.taxiId,
									t.timestamp, t.day_type);
							newTraj.baseline.add(p);
						}
					}
				}
			}
		}
	}

	public static double distance(double lat1, double lat2, double lon1, double lon2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters
		
		distance = Math.pow(distance, 2);

		return Math.sqrt(distance);
	}

	public void viewTemporal() {
		// using for-each loop for iteration over Map.entrySet()
		for (Entry<Integer, List<Trajectory>> entry : temporal.entrySet()) {
			System.out.println("Key = " + entry.getKey()+", Value = " + entry.getValue());
			for (Trajectory t : entry.getValue()) {
//				t.view();
			}
			System.out.println();
		}
	}

	public void viewGrid() {
		// using for-each loop for iteration over Map.entrySet()
		for (Entry<Integer, List<Trajectory>> entry : this.grid.entrySet()) {
			System.out.println("Key = " + entry.getKey());
			for (Trajectory t : entry.getValue()) {
				t.view();
			}
			System.out.println();
		}

	}
	
	public void viewinit() {
		for (Entry<Integer, List<coordinates>> entry : this.init.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

		}
	}
}
