package com.logistics.tracking.util;

/**
 * Simple utility for Geohash encoding/decoding.
 * Avoids heavy GIS dependencies for this specific use case.
 */
public class GeohashUtils {

    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * Encodes a latitude and longitude into a geohash string.
     *
     * @param lat       Latitude
     * @param lon       Longitude
     * @param precision Number of characters in the geohash (e.g., 6 is ~1.2km x
     *                  0.6km)
     * @return Geohash string
     */
    public static String encode(double lat, double lon, int precision) {
        double[] latInterval = { -90.0, 90.0 };
        double[] lonInterval = { -180.0, 180.0 };

        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0;
        int ch = 0;

        while (geohash.length() < precision) {
            double mid;
            if (isEven) {
                mid = (lonInterval[0] + lonInterval[1]) / 2;
                if (lon > mid) {
                    ch |= (1 << (4 - bit));
                    lonInterval[0] = mid;
                } else {
                    lonInterval[1] = mid;
                }
            } else {
                mid = (latInterval[0] + latInterval[1]) / 2;
                if (lat > mid) {
                    ch |= (1 << (4 - bit));
                    latInterval[0] = mid;
                } else {
                    latInterval[1] = mid;
                }
            }

            isEven = !isEven;

            if (bit < 4) {
                bit++;
            } else {
                geohash.append(BASE32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }

        return geohash.toString();
    }
}
