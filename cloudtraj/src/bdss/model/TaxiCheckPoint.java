package bdss.model;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class TaxiCheckPoint extends TableServiceEntity {
	public int timestamps;
	public double lat;
	public double lng;
	
	public TaxiCheckPoint() {
		
	}
	
	public TaxiCheckPoint(String partitionKey, int timestamps, double lat, double lng) {
		super();
		this.partitionKey = partitionKey;
		this.rowKey = Integer.toString(timestamps);
		this.lat = lat;
		this.lng = lng;
	}
	
	public TaxiCheckPoint(String row, double lat, double lng) {
		super();
		this.rowKey = row;
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
