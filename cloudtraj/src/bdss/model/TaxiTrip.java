package bdss.model;

import java.util.ArrayList;
import java.util.List;

public class TaxiTrip {
	String taxiId;
	List<Marker> polylines = new ArrayList<Marker>();
	
	public TaxiTrip() {
		// TODO Auto-generated constructor stub
	}

	public TaxiTrip(String taxiId, List<Marker> polylines) {
		super();
		this.taxiId = taxiId;
		this.polylines = polylines;
	}

	public String getTaxiId() {
		return taxiId;
	}

	public void setTaxiId(String taxiId) {
		this.taxiId = taxiId;
	}

	public List<Marker> getPolylines() {
		return polylines;
	}

	public void setPolylines(List<Marker> polylines) {
		this.polylines = polylines;
	}
}
