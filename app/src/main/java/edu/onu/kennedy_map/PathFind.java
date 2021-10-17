package edu.onu.kennedy_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class PathFind {
    private final DrawableSurfaceView floor1DSV;
    private DrawableSurfaceView floor2DSV;
    private DrawableSurfaceView floor3DSV;
    private final Context context;

    private ArrayList<ArrayList<PathNode>> nodes = new ArrayList<>();
    private PriorityQueue<PathNode> openSet = new PriorityQueue<>(new NodePriorityComparator());

    public PathFind(Context context, DrawableSurfaceView floor1DSV){
        this.context = context;
        this.floor1DSV = floor1DSV;
    }

    //Priority Queue Comparator to get the least fScore
    private class NodePriorityComparator implements Comparator<PathNode>{
        @Override
        public int compare(PathNode a, PathNode b){
            return a.get_fScore() - b.get_fScore();
        }
    }

    public void initialize(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB);
        options.outColorSpace = ColorSpace.get(ColorSpace.Named.SRGB);

        Bitmap boundaryBitMap = BitmapFactory.decodeResource(context.getResources(), R.raw.boundry_floor_1a, options);
        floor1DSV.initSize(boundaryBitMap);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        //Handles reset from previous runs
        nodes.clear();
        openSet.clear();
        floor1DSV.clearCanvas();

        for (int i = 0; i < imageWidth; i++){
            nodes.add(new ArrayList<PathNode>());
            for (int j = 0; j < imageHeight; j++){
                nodes.get(i).add(j, new PathNode(i, j, 0));
                nodes.get(i).get(j).setNodeType(boundaryBitMap.getColor(i, j).equals(Color.valueOf(Color.BLACK)) ? NodeType.BARRIER : NodeType.UNASSIGNED);

                //Can be uncommented to see the boundary initialize (must uncomment the clear at end of function to use)
                if (nodes.get(i).get(j).getNodeType() == NodeType.BARRIER) {
                    floor1DSV.drawNode(nodes.get(i).get(j));
                }
            }
        }

        //Populates the neighbors for all nodes (can be done as needed for added efficiency)
        for(ArrayList<PathNode> column : nodes){
            for (PathNode node : column){
                if (node.getNodeType()!=NodeType.BARRIER) {
                    node.updateNeighbors(nodes);
                }
            }
        }

        //floor1DSV.clearCanvas(1000);
    }

    private void reconstructPath( PathNode start, PathNode end){
        PathNode node = end;

        //Render start and end node
        start.setNodeType(NodeType.START);
        floor1DSV.drawNode(start, 10);
        end.setNodeType(NodeType.END);
        floor1DSV.drawNode(end,10);

        //Render nodes in path from end to start
        while (node.getCameFrom() != null){
            node.getCameFrom().setNodeType(NodeType.PATH);
            floor1DSV.drawNode(node.getCameFrom(), 10);
            node = node.getCameFrom();
        }
    }

    //Will be changed to take in start and end rooms (used coordinates for testing)
    public boolean pathFindBetween(int startNodeX, int startNodeY, int endNodeX, int endNodeY){
        PathNode startNode = nodes.get(startNodeX).get(startNodeY);
        startNode.setNodeType(NodeType.START);

        PathNode endNode = nodes.get(endNodeX).get(endNodeY);
        endNode.setNodeType(NodeType.END);

        PathNode currentNode;

        //gScore is the cost to get from the current node to the start node
        startNode.set_gScore(startNode, startNode);
        //hScore is the cost to get from the current node to the end node
        startNode.set_hScore(startNode, endNode);
        //fScore is the sum of the gScore and the hScore
        startNode.set_fScore();

        //The set of discovered nodes that may be part of the path and may have neighbors that are part of the path
        openSet.add(startNode);

        floor1DSV.drawNode(startNode);
        floor1DSV.drawNode(endNode);

        while (!openSet.isEmpty()){
            floor1DSV.drawNode(startNode);
            floor1DSV.drawNode(endNode);

            currentNode = openSet.poll();

            //The win condition
            if (currentNode.equals(endNode)){
                //Reconstruct Path
                floor1DSV.drawNode(50);
                floor1DSV.clearCanvas(1500);
                reconstructPath(startNode, endNode);
                return true;
            }

            //If the current node has any neighbors that would have a better gScore from the current node then that of the current
            //node then set this potential gScore as the new gScore and add this neighbor to the open set
            for (PathNode neighbor : currentNode.neighbors){
                if(neighbor.tentative_gScore(currentNode, endNode)){
                    neighbor.setNodeType(NodeType.OPEN);
                    openSet.add(neighbor);
                }
                floor1DSV.drawNode(neighbor);
            }

            //After this iteration the current node will change, so if it's not the start then set it to closed
            if (currentNode.getNodeType() != NodeType.START){
                currentNode.setNodeType(NodeType.CLOSED);
            }

            //Done to speedup playback (likely can be improved)
            if (openSet.size() % 150 == 0){
                floor1DSV.drawNode(50);
            }
        }

        return false;
    }
}