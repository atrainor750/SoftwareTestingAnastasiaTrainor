package uk.ac.ed.acp.cw2.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ed.acp.cw2.Helpers.Helper;
import uk.ac.ed.acp.cw2.dtos.*;


import java.util.List;
import java.util.Objects;

import static uk.ac.ed.acp.cw2.Helpers.Helper.isValid;

@Service
public class RestService {

//given via spec
    private static final double CLOSE_INT = 0.00015;

    //epsilon

    private static final double EPS = 1e-9;


    public double distanceTo(LngLatPairRequest request) {

        if (!isValid(request.getPosition1()) || !isValid(request.getPosition2())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        double pos1lat = request.getPosition1().getLat();
        double pos1lng = request.getPosition1().getLng();
        double pos2lat = request.getPosition2().getLat();
        double pos2lng = request.getPosition2().getLng();

        // euclidean distance
        return Math.sqrt(Math.pow((pos1lat - pos2lat), 2) + Math.pow((pos1lng - pos2lng), 2));
    }


    public boolean isCloseTo(LngLatPairRequest request) {
        if (!isValid(request.getPosition1()) || !isValid(request.getPosition2())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        double distance = distanceTo(request);
        return distance < CLOSE_INT;
    }

    public Position nextPosition(NextMoveRequest request) {
        double lat = request.getStart().getLat();
        double lng = request.getStart().getLng();
        if(request.getAngle() > 360 || request.getAngle() % 22.5 != 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if(!isValid(request.getStart()))
        {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        // normalise angle formula - triple mod

        double normal_angle = (((request.getAngle() % 360) + 360) % 360);

        // according to spec



        double delta_lng = CLOSE_INT * Math.cos(Math.toRadians(normal_angle));
        double delta_lat = CLOSE_INT * Math.sin(Math.toRadians(normal_angle));

        return new Position(
                request.getStart().getLng() + delta_lng, request.getStart().getLat() + delta_lat);
    }



    public boolean isInRegion(InRegionRequest request) {
        if (request.getRegion().getVertices() == null ||
                request.getRegion().getVertices().size() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region must have at least 3 vertices");
        }
        // check for valid co ordinates
        for(Position vertex : request.getRegion().getVertices()) {
            if(!isValid(vertex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outside co ordinate boundary");
            }
        }


        int size = request.getRegion().getVertices().size();
        Position first = request.getRegion().getVertices().get(0);
        Position last = request.getRegion().getVertices().get(size - 1);
// checks if first and last vertice are the same
        if (!Objects.equals(first.getLat(), last.getLat()) || !Objects.equals(first.getLng(), last.getLng())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region not closed: first and last vertices must be the same.");
        }


        double lat = request.getPosition().getLat();
        double lng = request.getPosition().getLng();

        double minLat = request.getRegion().getVertices().stream().mapToDouble(Position::getLat).min().orElseThrow();
        double maxLat = request.getRegion().getVertices().stream().mapToDouble(Position::getLat).max().orElseThrow();
        double minLng = request.getRegion().getVertices().stream().mapToDouble(Position::getLng).min().orElseThrow();
        double maxLng = request.getRegion().getVertices().stream().mapToDouble(Position::getLng).max().orElseThrow();
        // basic check to see if the given position lies outside the max and min - which would make it auto false

        if (lat > maxLat || lat < minLat || lng > maxLng || lng < minLng) {
            return false;
        }
        // calling helper functions

        Helper.Point point = new Helper.Point(lng, lat);
        List<Helper.Point> polygon = Helper.convertToPoints(request.getRegion().getVertices());
        if (Helper.isOnPolygonEdge(point, polygon, EPS)) return true;

        return Polygon.pointInPolygon(point, polygon);
    }
}

















