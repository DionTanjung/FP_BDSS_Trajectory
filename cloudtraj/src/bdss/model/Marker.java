package bdss.model;

import java.util.Date;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class Marker  extends TableServiceEntity {
	public int timestamps;
	public double lat;
	public double lng;
	
	public Marker(int timestamps, double lat, double lng) {
		super();
		this.timestamps = timestamps;
		this.lat = lat;
		this.lng = lng;
	}
	
	public void view() {
		Date date = new Date();
		date.setTime((long) timestamps * 1000);
		System.out.println(date+" "+this.lat+" "+this.lng);
	}
	
	public void viewCoordinates() {
		System.out.println(this.lat+" "+this.lng);
	}
	
	
}
