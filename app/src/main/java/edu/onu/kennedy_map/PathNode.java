package edu.onu.kennedy_map;

import android.util.Log;

import java.util.ArrayList;

public class PathNode {

    private final int x;
    private final int y;
    private final int z;

    private int gScore = Integer.MAX_VALUE;
    private int hScore = Integer.MAX_VALUE;
    private int fScore = Integer.MAX_VALUE;

    private PathNode cameFrom;

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

    private int ManhattanApproximation(PathNode pathNodeA, PathNode pathNodeB) {
        //using Manhattan Approximation to determine Classical H value
        return Math.abs(pathNodeA.getX() - pathNodeB.getX()) + Math.abs(pathNodeA.getY() - pathNodeB.getY());
    }

    public int get_gScore() {
        return gScore;
    }

    public void set_gScore(PathNode pathNodeA, PathNode pathNodeB) {
        gScore = ManhattanApproximation(pathNodeA,pathNodeB);
    }

    public boolean tentative_gScore(PathNode otherPathNode, PathNode endNode) {
        int temp_gScore = ManhattanApproximation(otherPathNode,this) + otherPathNode.get_gScore();

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

    public int get_hScore() {
        return hScore;
    }

    public void set_hScore(PathNode pathNodeA, PathNode pathNodeB) {
        hScore = ManhattanApproximation(pathNodeA,pathNodeB);
    }

    public int get_fScore() {
        return fScore;
    }

    public void set_fScore() {
        fScore = gScore + hScore;
    }

    public PathNode getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(PathNode cameFrom) {
        this.cameFrom = cameFrom;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void updateNeighbors(ArrayList<ArrayList<PathNode>> nodes){
        for (int xOffset = -1; xOffset <= 1; xOffset++){
            for (int yOffset = -1; yOffset <= 1; yOffset++){
                if ((x + xOffset < nodes.size() && x + xOffset >= 0) && (y + yOffset < nodes.get(x).size() && y + yOffset >= 0) && !(xOffset == 0 && yOffset == 0)){ //If our x and y with offsets is a valid node and not the node in question
                    if (nodes.get(x + xOffset).get(y + yOffset).nodeType != NodeType.BARRIER){ //and not a barrier
                        neighbors.add(nodes.get(x + xOffset).get(y + yOffset)); //then we have a new neighbor
                    }
                }
            }
        }
    }
}