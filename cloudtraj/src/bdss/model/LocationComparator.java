package bdss.model;

import java.util.Comparator;

public class LocationComparator implements Comparator<coordinates> {
	coordinates upper;

	LocationComparator(coordinates origin) {
		this.upper = origin;
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

//	@Override
//	public int compare(coordinates o1, coordinates o2) {
//		return Double.compare(distance(o1.lat, origin.lat, o1.lng, origin.lng),
//                distance(o2.lat, origin.lat, o2.lng, origin.lng));
//	}
	
	@Override
	public int compare(coordinates p1, coordinates p2) {
	    if(p1 == upper) return -1;
	    if(p2 == upper) return 1;

	    // Find the slopes of 'p1' and 'p2' when a line is 
	    // drawn from those points through the 'upper' point.
	    double m1 = upper.slope(p1);
	    double m2 = upper.slope(p2);

	    // 'p1' and 'p2' are on the same line towards 'upper'.
	    if(m1 == m2) {
	        // The point closest to 'upper' will come first.
	        return p1.distance(upper) < p2.distance(upper) ? -1 : 1;
	    }

	    // If 'p1' is to the right of 'upper' and 'p2' is the the left.
	    if(m1 <= 0 && m2 > 0) return -1;

	    // If 'p1' is to the left of 'upper' and 'p2' is the the right.
	    if(m1 > 0 && m2 <= 0) return 1;

	    // It seems that both slopes are either positive, or negative.
	    return m1 > m2 ? -1 : 1;
	}

}
