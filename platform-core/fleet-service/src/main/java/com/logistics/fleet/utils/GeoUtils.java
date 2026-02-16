package com.logistics.fleet.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeoUtils {
    private static final int SRID = 4326; // WGS84
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), SRID);

    public static Point createPoint(double latitude, double longitude) {
        // PostGIS expects (longitude, latitude) for 4326
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    public static double getLatitude(Point point) {
        return point != null ? point.getY() : 0.0;
    }

    public static double getLongitude(Point point) {
        return point != null ? point.getX() : 0.0;
    }
}
