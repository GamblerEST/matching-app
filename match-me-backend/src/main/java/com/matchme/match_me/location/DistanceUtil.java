package com.matchme.match_me.location;


 //class for calculating distances between coordinates
 //Uses the Haversine formula to calculate great-circle distances
 
public class DistanceUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     *calculate the distance between two points on Earth using the Haversine formula
     * 
     * @param lat1 Latitude of first point in degrees
     * @param lon1 Longitude of first point in degrees
     * @param lat2 Latitude of second point in degrees
     * @param lon2 Longitude of second point in degrees
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        //convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        //Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     *check if two locations are within a specified radius
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @param maxRadiusKm Maximum distance in kilometers
     * @return true if within radius, false otherwise
     */
    public static boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double maxRadiusKm) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= maxRadiusKm;
    }

    /**
     *calculate distance between two location entities
     * 
     * @param location1 First location
     * @param location2 Second location
     * @return Distance in kilometers
     */
    public static double calculateDistance(Location location1, Location location2) {
        return calculateDistance(
            location1.getLatitude(), 
            location1.getLongitude(),
            location2.getLatitude(), 
            location2.getLongitude()
        );
    }

    /**
     * Check if two Location entities are within a specified radius.
     * 
     * @param location1 First location
     * @param location2 Second location
     * @param maxRadiusKm Maximum distance in kilometers
     * @return true if within radius, false otherwise
     */
    public static boolean isWithinRadius(Location location1, Location location2, double maxRadiusKm) {
        return isWithinRadius(
            location1.getLatitude(), 
            location1.getLongitude(),
            location2.getLatitude(), 
            location2.getLongitude(),
            maxRadiusKm
        );
    }
}
