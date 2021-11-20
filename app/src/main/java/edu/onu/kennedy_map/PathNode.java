package edu.onu.kennedy_map;

import java.util.ArrayList;

/**
 * A PathNode is a position on the bitmap that can be used for path finding
 */
public class PathNode {

    private final int x;
    private final int y;
    private final int z;

    private float gScore = Float.MAX_VALUE;
    private float hScore = Float.MAX_VALUE;
    private float fScore = Float.MAX_VALUE;

    private PathNode cameFrom;

    // Initially unassigned node type
    private NodeType nodeType = NodeType.UNASSIGNED;
    public ArrayList<PathNode> neighbors = new ArrayList<>();

    public PathNode(int x, int y, int z, NodeType nodeType)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.nodeType = nodeType;
    }

    public PathNode(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * Heuristics approximation to determine distance between 2 nodes (Uses Manhattan which is the sum of x, y, and z distance)
     * @param pathNodeA start node to consider
     * @param pathNodeB destination node to consider
     * @return distance between
     */
    private int ManhattanApproximation(PathNode pathNodeA, PathNode pathNodeB) {
        //using Manhattan Approximation to determine Classical H value
        return Math.abs(pathNodeA.getX() - pathNodeB.getX()) + Math.abs(pathNodeA.getY() - pathNodeB.getY()) + Math.abs(pathNodeA.getZ() - pathNodeB.getZ());
    }

    /**
     * Heuristics approximation to determine distance between 2 nodes (Uses Pythagorean which is the exact x, y, and z distance)
     * @param pathNodeA start node to consider
     * @param pathNodeB destination node to consider
     * @return distance between
     */
    private float PythagoreanApproximation(PathNode pathNodeA, PathNode pathNodeB) {
        //using Pythagorean Approximation to determine Classical H value
        return (float) Math.sqrt(Math.pow(pathNodeA.getX() - pathNodeB.getX(), 2) + Math.pow(pathNodeA.getY() - pathNodeB.getY(), 2) +Math.pow(pathNodeA.getZ() - pathNodeB.getZ(), 2));
    }

    public float get_gScore() {
        return gScore;
    }

    public void set_gScore(PathNode pathNodeA, PathNode pathNodeB) {
        gScore = ManhattanApproximation(pathNodeA,pathNodeB);
        //gScore = ManhattanApproximation(pathNodeA,pathNodeB);
    }

    public boolean tentative_gScore(PathNode otherPathNode, PathNode endNode) {
        float temp_gScore = ManhattanApproximation(otherPathNode,this) + otherPathNode.get_gScore();
        //float temp_gScore = PythagoreanApproximation(otherPathNode,this) + otherPathNode.get_gScore();

        if (temp_gScore < get_gScore()){
            cameFrom = otherPathNode;
            gScore = temp_gScore;
            set_hScore(this, endNode);
            set_fScore();
            return true;
        } else {
            return false;
        }
    }

    public float get_hScore() {
        return hScore;
    }

    public void set_hScore(PathNode pathNodeA, PathNode pathNodeB) {
        hScore = ManhattanApproximation(pathNodeA, pathNodeB);
        //hScore = PythagoreanApproximation(pathNodeA, pathNodeB);
    }

    public float get_fScore() {
        return fScore;
    }

    public void set_fScore() {
        fScore = gScore + hScore;
    }

    public PathNode getCameFrom() {
        return cameFrom;
    }

    /**
     * The node that lead to this node used in determining the path
     * @param cameFrom The node that lead to this node
     */
    public void setCameFrom(PathNode cameFrom) {
        this.cameFrom = cameFrom;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Stores all valid traversable nodes that are no more than 1 unit away along x, y, z or a combination of 2 of the axis
     * @param nodes the nodes that have been loaded that make up the building
     */
    public void updateNeighbors(ArrayList<ArrayList<ArrayList<PathNode>>> nodes){
        for (int xOffset = -1; xOffset <= 1; xOffset++){
            for (int yOffset = -1; yOffset <= 1; yOffset++){
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if ((z + zOffset < nodes.size() && z + zOffset >= 0) && (x + xOffset < nodes.get(z).size() && x + xOffset >= 0) && (y + yOffset < nodes.get(z).get(x).size() && y + yOffset >= 0) && !(xOffset == 0 && yOffset == 0 && zOffset == 0)) { //If our x, y and z with offsets is a valid node and not the node in question
                        if (nodes.get(z + zOffset).get(x + xOffset).get(y + yOffset).nodeType != NodeType.BARRIER) { //and not a barrier
                            neighbors.add(nodes.get(z + zOffset).get(x + xOffset).get(y + yOffset)); //then we have a new neighbor
                        }
                    }
                }
            }
        }
    }
}
