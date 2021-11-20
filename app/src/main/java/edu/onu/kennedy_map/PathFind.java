package edu.onu.kennedy_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Handles the bulk of the animation logic for the path screen
 */
public class PathFind {
    private final DrawableSurfaceView drawable;
    private final Context context;

    private ArrayList<ArrayList<ArrayList<PathNode>>> nodes = new ArrayList<>();
    private PriorityQueue<PathNode> openSet = new PriorityQueue<>(new NodePriorityComparator());

    private boolean animShouldShow = false;

    public PathFind(Context context, DrawableSurfaceView drawable){
        this.context = context;
        this.drawable = drawable;
    }

    public void toggleAnimVisibility(boolean animShouldShow){
        this.animShouldShow = animShouldShow;
    }

    //Priority Queue Comparator to get the least fScore
    private class NodePriorityComparator implements Comparator<PathNode>{
        @Override
        public int compare(PathNode a, PathNode b){
            return (int) (a.get_fScore() - b.get_fScore());
        }
    }

    /**
     * Loads the boundary bitmaps for path finding
     */
    public void initialize(){
        //Handles reset from previous runs
        nodes.clear();
        openSet.clear();
        drawable.clearCMD(0);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB);
        options.outColorSpace = ColorSpace.get(ColorSpace.Named.SRGB);

        Bitmap boundaryBitMap1 = BitmapFactory.decodeResource(context.getResources(), R.raw.boundary_floor_1a, options);
        pathNodePopulate(boundaryBitMap1, options);
        drawable.initSize(boundaryBitMap1);

        Bitmap boundaryBitMap2 = BitmapFactory.decodeResource(context.getResources(), R.raw.boundary_floor_1b, options);
        pathNodePopulate(boundaryBitMap2, options);

        Bitmap boundaryBitMap3 = BitmapFactory.decodeResource(context.getResources(), R.raw.boundary_floor_2a, options);
        pathNodePopulate(boundaryBitMap3, options);

        Bitmap boundaryBitMap4 = BitmapFactory.decodeResource(context.getResources(), R.raw.boundary_floor_2b, options);
        pathNodePopulate(boundaryBitMap4, options);

        Bitmap boundaryBitMap5 = BitmapFactory.decodeResource(context.getResources(), R.raw.boundary_floor_3a, options);
        pathNodePopulate(boundaryBitMap5, options);

        //Populates the neighbors for all nodes (can be done as needed for added efficiency)
        /*for(ArrayList<ArrayList<PathNode>> height : nodes){
            for(ArrayList<PathNode> column : height) {
                for (PathNode node : column) {
                    if (node.getNodeType() != NodeType.BARRIER) {
                        node.updateNeighbors(nodes);
                    }
                }
            }
        }*/

        //floor1DSV.clearCanvas(1000);
    }

    /**
     * Sets the node type to boundary if the bitmap is filled, otherwise its an accessible spot
     * @param boundry The bitmap to check
     * @param options The options from the bitmap factory we used to load it in
     */
    private void pathNodePopulate(Bitmap boundry, BitmapFactory.Options options){
        nodes.add(new ArrayList<>());
        for (int i = 0; i < options.outWidth; i++){
            nodes.get(nodes.size() - 1).add(new ArrayList<>());
            for (int j = 0; j < options.outHeight; j++){
                nodes.get(nodes.size() - 1).get(i).add(j, new PathNode(i, j, nodes.size() - 1));
                nodes.get(nodes.size() - 1).get(i).get(j).setNodeType(boundry.getColor(i, j).equals(Color.valueOf(Color.BLACK)) ? NodeType.BARRIER : NodeType.UNASSIGNED);

                //Can be uncommented to see the boundary initialize (must uncomment the clear at end of function to use)
                /*if (nodes.get(nodes.size() - 1).get(i).get(j).getNodeType() == NodeType.BARRIER) {
                    //drawable.drawCMD(nodes.get(nodes.size() - 1).get(i).get(j));
                }*/
            }
            //drawable.waitCMD(200);
        }
        //drawable.clearCMD(2000);
    }

    /**
     * Used to draw the full path out after getting done with the calculations
     * @param start The first node in the path
     * @param end The last node in the path
     */
    private void reconstructPath( PathNode start, PathNode end){
        PathNode node = end;
        ArrayList<PathNode> path = new ArrayList<>();

        //Render start and end node
        start.setNodeType(NodeType.START);
        drawable.drawCMD(start);
        end.setNodeType(NodeType.END);
        drawable.drawCMD(end);

        //Render nodes in path from end to start
        while (node.getCameFrom() != null){
            node.getCameFrom().setNodeType(NodeType.PATH);
            path.add(0, node.getCameFrom());
            node = node.getCameFrom();
        }

        for (int i = 0; i < path.size(); i++){
            //Used to artistically change the path by specifying every nth number of path nodes to render (To use: change mod value)
            if (i % 3 == 0 || i == 0 || i == path.size() - 1){
                drawable.drawCMD(path.get(i));
                drawable.waitCMD(1);
            }
        }

        //drawable.clearCMD(1000);
        drawable.drawCMD(end);
        drawable.drawCMD(start);
        drawable.waitCMD(1);
        /*for (PathNode pathNode : path){
            drawable.drawCMD(pathNode);
            drawable.waitCMD(1);
        }*/
    }

    // Takes in the starting room and ending room and calculates, then displays the path.
    public boolean pathFindBetween(Room startRoom, Room endRoom){
        if (startRoom == null || endRoom == null){return false;}

        PathNode startNode = nodes.get(startRoom.getCenter().getZ()).get(startRoom.getCenter().getX()).get(startRoom.getCenter().getY());
        //PathNode startNode = nodes.get(0).get(20).get(205); //For Testing ONLY
        startNode.setNodeType(NodeType.START);

        PathNode endNode = nodes.get(endRoom.getCenter().getZ()).get(endRoom.getCenter().getX()).get(endRoom.getCenter().getY());
        //PathNode endNode = nodes.get(2).get(148).get(165); //For Testing ONLY
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

        if(animShouldShow) {
            drawable.drawCMD(startNode);
            drawable.drawCMD(endNode);
        }

        while (!openSet.isEmpty()){
            if(animShouldShow) {
                drawable.drawCMD(startNode);
                drawable.drawCMD(endNode);
            }

            currentNode = openSet.poll();

            //The win condition
            if (currentNode.equals(endNode)){
                //Reconstruct Path
                drawable.clearCMD(1000);
                reconstructPath(startNode, endNode);
                return true;
            }

            //If the current node has any neighbors that would have a better gScore from the current node then that of the current
            //node then set this potential gScore as the new gScore and add this neighbor to the open set
            currentNode.updateNeighbors(nodes);
            for (PathNode neighbor : currentNode.neighbors){
                if(neighbor.tentative_gScore(currentNode, endNode)){
                    neighbor.setNodeType(NodeType.OPEN);
                    openSet.add(neighbor);
                }
                if(animShouldShow) {
                    drawable.drawCMD(currentNode);
                }
            }
            //drawable.waitCMD(0);

            //After this iteration the current node will change, so if it's not the start then set it to closed
            if (currentNode.getNodeType() != NodeType.START){
                currentNode.setNodeType(NodeType.CLOSED);
            }

            //Done to speedup playback (likely can be improved)
            if (openSet.size() % 150 == 0){
                if(animShouldShow) {
                    drawable.waitCMD(1);
                }
            }
        }
        return false;
    }
}