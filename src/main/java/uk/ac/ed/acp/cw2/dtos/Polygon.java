package uk.ac.ed.acp.cw2.dtos;

import uk.ac.ed.acp.cw2.Helpers.Helper;

import java.util.List;

// this class determines if the point actually lies in polygon
public class Polygon {
    public static boolean pointInPolygon(Helper.Point point, List<Helper.Point> polygon) {
        int numVertices = polygon.size();
        double x = point.x, y = point.y;
        boolean inside = false;
        Helper.Point p1 = polygon.get(0);

        for (int i = 1; i <= numVertices; i++) {
            Helper.Point p2 = polygon.get(i % numVertices);

            if (y > Math.min(p1.y, p2.y)) {
                if (y <= Math.max(p1.y, p2.y)) {
                    if (x <= Math.max(p1.x, p2.x)) {
                        double xIntersection = (y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y) + p1.x;

                        if (p1.x == p2.x || x <= xIntersection) {
                            inside = !inside;
                        }
                    }
                }
            }
            p1 = p2;
        }

        return inside;
    }
}


