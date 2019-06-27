package bdss.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.microsoft.azure.storage.table.TableServiceEntity;


public class Trajectory extends TableServiceEntity {
	public String tripId;
	public char call_type;
	public int origin_call;
	public int origin_stand;
	public int taxiId;
	public int timestamp;
	public char day_type;
	public List<Marker> baseline = new ArrayList<Marker>();
	
	public Trajectory(String tripId, char call_type, int origin_call, int origin_stand, int taxiId, int timestamp,
			char day_type, List<Marker> baseline) {
		super();
		this.tripId = tripId;
		this.call_type = call_type;
		this.origin_call = origin_call;
		this.origin_stand = origin_stand;
		this.taxiId = taxiId;
		this.timestamp = timestamp;
		this.day_type = day_type;
		this.baseline = baseline;
	}
	
	public Trajectory(String tripId, char call_type, int origin_call, int origin_stand, int taxiId, int timestamp,
			char day_type) {
		super();
		this.tripId = tripId;
		this.call_type = call_type;
		this.origin_call = origin_call;
		this.origin_stand = origin_stand;
		this.taxiId = taxiId;
		this.timestamp = timestamp;
		this.day_type = day_type;
	}
	
	public void view() {
		Date date = new Date();
		date.setTime((long) this.timestamp * 1000);
		System.out.print(this.tripId+" "+this.call_type+" "+this.origin_call+" "+this.origin_stand+" "+this.taxiId+" "+date+" "+this.day_type);
		System.out.println();
		for (Marker s : baseline) {
//			s.view();
		}
	}
	
	public void viewCoordinates() {
		for (Marker s : baseline) {
			s.viewCoordinates();
		}
	}
	
}
