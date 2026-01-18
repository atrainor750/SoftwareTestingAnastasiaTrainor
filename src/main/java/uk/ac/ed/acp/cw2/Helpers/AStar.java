package uk.ac.ed.acp.cw2.Helpers;

import uk.ac.ed.acp.cw2.dtos.Position;

import java.util.LinkedList;
import java.util.List;



import uk.ac.ed.acp.cw2.dtos.Position;
import uk.ac.ed.acp.cw2.Helpers.NoFlyZoneHelper;

import java.util.*;

public class AStar {

    // Tuned resolution for movement; change as needed for smoothness/speed
    private static final double STEP = 0.00015;

    // SNAP grid for coordinate normalization (useful for floating point stability)
    private static final double SNAP = 1e-6;

    private static final double MIN_LNG = -3.30;
    private static final double MAX_LNG = -3.10;
    private static final double MIN_LAT = 55.90;
    private static final double MAX_LAT = 56.00;

    private final NoFlyZoneHelper noFlyZoneHelper;

    public AStar(NoFlyZoneHelper noFlyZoneHelper) {
        this.noFlyZoneHelper = noFlyZoneHelper;
    }

    // Compact node definition
    private static class Node {
        final double lng, lat;
        final double g, h;
        final Node parent;
        Node(double lng, double lat, double g, double h, Node p) {
            this.lng = lng; this.lat = lat; this.g = g; this.h = h; this.parent = p;
        }
        double f() { return g + h; }
        Position toPos() { return new Position(lng, lat); }
    }

    // ----------------------------------------------------------------------
    // PUBLIC: find path
    // ----------------------------------------------------------------------
    public List<Position> findPath(Position start, Position goal) {
        Position s = snap(start);
        Position g = snap(goal);

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        Set<String> visited = new HashSet<>();

        Node startNode = new Node(s.getLng(), s.getLat(), 0, heuristic(s, g), null);
        open.add(startNode);

        while (!open.isEmpty()) {
            Node cur = open.poll();
            String ck = key(cur.lng, cur.lat);

            if (!visited.add(ck))
                continue;

            // Reached within 1-step radius
            if (distance(cur.toPos(), g) <= STEP)
                return buildPath(cur, goal); // final destination exact

            // Generate neighbours
            List<Node> nbrs = neighbours(cur, g);

            for (Node n : nbrs) {
                if (!visited.contains(key(n.lng, n.lat)))
                    open.add(n);
            }
        }

        // fallback: no path (should not happen under correct zones)
        return List.of(start);
    }

    // ----------------------------------------------------------------------
    // SNAP TO GRID (uses SNAP constant and Math.round)
    // ----------------------------------------------------------------------
    private Position snap(Position p) {
        double lng = Math.round(p.getLng() / SNAP) * SNAP;
        double lat = Math.round(p.getLat() / SNAP) * SNAP;
        return new Position(lng, lat);
    }

    // ----------------------------------------------------------------------
    // OPTIMISED NEIGHBOURS
    // only 8 directions: N, NE, E, SE, S, SW, W, NW
    // ----------------------------------------------------------------------
    private List<Node> neighbours(Node node, Position goal) {
        double[][] DIRS = {
                { STEP, 0 },
                { -STEP, 0 },
                { 0, STEP },
                { 0, -STEP },
                { STEP, STEP },
                { STEP, -STEP },
                { -STEP, STEP },
                { -STEP, -STEP }
        };

        List<Node> out = new ArrayList<>(8);

        for (double[] d : DIRS) {
            double nlng = node.lng + d[0];
            double nlat = node.lat + d[1];

            // Bounding box = stops wandering
            if (nlng < MIN_LNG || nlng > MAX_LNG) continue;
            if (nlat < MIN_LAT || nlat > MAX_LAT) continue;

            Position np = new Position(nlng, nlat);

            // Quick no-fly zone avoidance
            if (noFlyZoneHelper.pointInNoFlyZone(np)) continue;

            // Only check intersection every 2 steps to reduce cost
            if (noFlyZoneHelper.edgeCutsNoFlyZone(node.toPos(), np)) continue;



            double g = node.g + distance(node.toPos(), np);      // uses real distance

            double h = heuristic(np, goal);                      // uses real distance

            out.add(new Node(nlng, nlat, g, h, node));
        }

        return out;
    }

    // ----------------------------------------------------------------------
    // SUPPORT
    // ----------------------------------------------------------------------
    private double heuristic(Position a, Position b) {
        return distance(a, b);        // uses real distance (not grid steps)
    }

    private double distance(Position a, Position b) {
        double dx = a.getLng() - b.getLng();
        double dy = a.getLat() - b.getLat();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private String key(double lng, double lat) {
        long x = Math.round(lng / SNAP);  // Use SNAP for key normalization
        long y = Math.round(lat / SNAP);
        return x + "," + y;
    }

    private List<Position> buildPath(Node endNode, Position exactGoal) {
        LinkedList<Position> path = new LinkedList<>();
        Node n = endNode;

        while (n != null) {
            path.addFirst(n.toPos());
            n = n.parent;
        }

        // final exact point
        path.add(exactGoal);
        return path;
    }

    }



