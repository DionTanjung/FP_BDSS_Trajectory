package bdss.model;

public class coordinates {
	public double lat;
	public double lng;
	public String tripId;

	public coordinates(double lat, double lng, String trip) {
		super();
//		this.lat = (lat+90)*180;
//		this.lng = (lng+180)*360;
		this.lat = lat;
		this.lng = lng;
		this.tripId=trip;
	}

	public double distance(coordinates that) {
		double dX = that.lng - this.lng;
		double dY = that.lat - this.lat;
		return Math.sqrt((dX * dX) + (dY * dY));
	}

	public double slope(coordinates that) {
		double dX = that.lng - this.lng;
		double dY = that.lat - this.lat;
		return dY / dX;
	}

	public double distance(double lat1, double lat2, double lon1, double lon2) {

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

	public double compare(coordinates o1, coordinates o2) {
		return Double.compare(distance(o1.lat, this.lat, o1.lng, this.lng),
				distance(o2.lat, this.lat, o2.lng, this.lng));
	}

	public void view() {
		System.out.println(this.lat + " " + this.lng);
	}

}
