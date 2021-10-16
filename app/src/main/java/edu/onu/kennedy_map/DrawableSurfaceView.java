package edu.onu.kennedy_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DrawableSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private long totalElapsedTime = 0;
    private AnimationThread animThread;

    private Paint openNodePaint;
    private Paint closedNodePaint;
    private Paint pathNodePaint;
    private Paint barrierNodePaint;
    private Paint startNodePaint;
    private Paint endNodePaint;

    private float[] transMatrix = new float[6];
    private Rect boundaryRect;

    public boolean showDebugBorder = false;

    private ArrayList<ArrayList<DrawNode>> nodesToDraw = new ArrayList<>();
    private ArrayList<GroupEvent> groupScheduler = new ArrayList<>();

    private static class DrawNode{
        private final float x;
        private final float y;
        private final NodeType nodeType;
        private final float radius;

        DrawNode(float x, float y, NodeType nodeType, float radius){
            this.x = x;
            this.y = y;
            this.nodeType = nodeType;
            this.radius = radius;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public NodeType getNodeType() {
            return nodeType;
        }

        public float getRadius() {
            return radius;
        }
    }

    private static class GroupEvent {
        private int groupIndex;
        private long time;
        public boolean checked = false;
        private boolean clearEvent = false;

        public GroupEvent(int groupIndex, long time) {
            this.groupIndex = groupIndex;
            this.time = time;
        }

        public GroupEvent(int groupIndex, long time, boolean clearEvent) {
            this.groupIndex = groupIndex;
            this.time = time;
            this.clearEvent = clearEvent;
        }

        public int getGroupIndex() {
            return groupIndex;
        }

        public void setGroupIndex(int groupIndex) {
            this.groupIndex = groupIndex;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }

    public DrawableSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //This allows an image view behind to show through
        this.setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);

        getHolder().addCallback(this);

        initPaints();
        animThread = new AnimationThread(getHolder(), this);
        //setFocusable(true);
    }

    public void initSize(Bitmap boundaryBitmap){
        float imageBmpScale;

        if ((float) this.getMeasuredWidth() / boundaryBitmap.getWidth() > (float) this.getMeasuredHeight() / boundaryBitmap.getHeight()){
            imageBmpScale = (float) this.getMeasuredHeight() / boundaryBitmap.getHeight();
        } else {
            imageBmpScale = (float) this.getMeasuredWidth() / boundaryBitmap.getWidth();
        }

        transMatrix[TransMatrixID.Width] = boundaryBitmap.getWidth();
        transMatrix[TransMatrixID.Height] = boundaryBitmap.getHeight();

        transMatrix[TransMatrixID.Scale] = imageBmpScale;

        transMatrix[TransMatrixID.xOffset] = (this.getMeasuredWidth() - (transMatrix[TransMatrixID.Width] * transMatrix[TransMatrixID.Scale])) / 2;
        transMatrix[TransMatrixID.yOffset] = (this.getMeasuredHeight() - (transMatrix[TransMatrixID.Height]* transMatrix[TransMatrixID.Scale])) / 2;

        boundaryRect = new Rect((int) transMatrix[TransMatrixID.xOffset],
                (int) transMatrix[TransMatrixID.yOffset],
                (int) (transMatrix[TransMatrixID.Width] * transMatrix[TransMatrixID.Scale] + transMatrix[TransMatrixID.xOffset]),
                (int) (transMatrix[TransMatrixID.Height] * transMatrix[TransMatrixID.Scale] + transMatrix[TransMatrixID.yOffset]));
    }

    public void clearCanvas(){
        nodesToDraw.clear();
        groupScheduler.clear();
    }

    public void clearCanvas(int delayInMillis){
        groupScheduler.add(new GroupEvent(nodesToDraw.size() - 1, delayInMillis, true));
    }

    private DrawNode pathNodeToDrawNode(PathNode node){
        return new DrawNode(
                (node.getX() * transMatrix[TransMatrixID.Scale] + transMatrix[TransMatrixID.xOffset]),
                (node.getY() * transMatrix[TransMatrixID.Scale] + transMatrix[TransMatrixID.yOffset]),
                node.getNodeType(),
                transMatrix[TransMatrixID.Scale] / 4);
    }

    public void drawNode(int delayInMillis){
        if (!nodesToDraw.isEmpty()) {
            DrawNode drawNode = nodesToDraw.get(nodesToDraw.size() - 1).get(nodesToDraw.get(nodesToDraw.size() - 1).size() - 1);
            nodesToDraw.get(nodesToDraw.size() - 1).remove(nodesToDraw.get(nodesToDraw.size() - 1).size() - 1);

            nodesToDraw.add(new ArrayList<>());
            nodesToDraw.get(nodesToDraw.size() - 1).add(drawNode);

            groupScheduler.add(new GroupEvent(nodesToDraw.size() - 1, delayInMillis));
        }
    }

    public void drawNode(PathNode node){
        DrawNode drawNode = pathNodeToDrawNode(node);

        if (nodesToDraw.isEmpty()) {
            nodesToDraw.add(new ArrayList<>());
        }

        nodesToDraw.get(nodesToDraw.size() - 1).add(drawNode);
    }

    public void drawNode(PathNode node, int delayInMillis){
        DrawNode drawNode = pathNodeToDrawNode(node);

        nodesToDraw.add(new ArrayList<>());
        nodesToDraw.get(nodesToDraw.size() - 1).add(drawNode);

        groupScheduler.add(new GroupEvent(nodesToDraw.size() - 1, delayInMillis));
    }

    private void renderNodes(Canvas canvas){
        for (int groupIndex = 0; groupIndex < nodesToDraw.size(); groupIndex++){
            if (!groupScheduler.isEmpty()) {
                if (groupIndex == groupScheduler.get(0).getGroupIndex()) {
                    if(!groupScheduler.get(0).checked) {
                        groupScheduler.get(0).checked = true;
                        groupScheduler.get(0).setTime(groupScheduler.get(0).getTime() + System.currentTimeMillis());
                        return;
                    } else if (groupScheduler.get(0).checked && System.currentTimeMillis() >= groupScheduler.get(0).getTime()){
                        if (groupScheduler.get(0).clearEvent){
                            nodesToDraw.subList(0, groupIndex).clear();
                            groupScheduler.remove(0);
                            for(GroupEvent event : groupScheduler){
                                event.setGroupIndex(event.getGroupIndex() - (groupIndex + 1));
                            }
                            return;
                        }
                        groupScheduler.remove(0);
                    } else {
                        return;
                    }
                }
            }

            for (DrawNode node : nodesToDraw.get(groupIndex)){
                switch (node.getNodeType()) {
                    case OPEN:
                        canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), openNodePaint);
                        break;
                    case CLOSED:
                        canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), closedNodePaint);
                        break;
                    case PATH:
                        //canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), pathNodePaint);
                        canvas.drawRect(node.getX() - (node.getRadius() / 1.5f), node.getY() - (node.getRadius() / 1.5f), node.getX() + (node.getRadius() / 1.5f), node.getY() + (node.getRadius() / 1.5f), pathNodePaint);
                        break;
                    case BARRIER:
                        //canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), barrierNodePaint);
                        canvas.drawRect(node.getX() - (node.getRadius() / 1.5f), node.getY() - (node.getRadius() / 1.5f), node.getX() + (node.getRadius() / 1.5f), node.getY() + (node.getRadius() / 1.5f), barrierNodePaint);
                        break;
                    case START:
                        canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), startNodePaint);
                        break;
                    case END:
                        canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), endNodePaint);
                        break;
                    default:
                        //canvas.drawCircle(node.getX(), node.getY(), node.getRadius(), barrierNodePaint);
                        break;
                }
            }
        }
    }

    public void update(){
        //anything that runs per tick that isn't a drawcall (the number of drawcalls = the number of times update runs)
        Log.d("Rendering", "update: happened");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null){
            //make draw calls in this container

            canvas.drawColor(0, PorterDuff.Mode.CLEAR); //This clears the screen before the next draw!

            //Handles Show Border Flag
            if (showDebugBorder) {
                canvas.drawRect(boundaryRect, barrierNodePaint);
            }

            renderNodes(canvas);
        }
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        animThread.setRunning(true);
        animThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retying = true;
        while (retying){
            try {
                animThread.setRunning(false);
                animThread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            retying = false;
        }
    }

    private  void initPaints(){
        openNodePaint = new Paint();
        openNodePaint.setStyle(Paint.Style.STROKE);
        openNodePaint.setStrokeWidth(5);
        openNodePaint.setAntiAlias(true);
        openNodePaint.setAlpha(127);
        openNodePaint.setColor(Color.rgb(0,255,0));

        closedNodePaint = new Paint();
        closedNodePaint.setStyle(Paint.Style.STROKE);
        closedNodePaint.setStrokeWidth(5);
        closedNodePaint.setAntiAlias(true);
        closedNodePaint.setAlpha(127);
        closedNodePaint.setColor(Color.rgb(255,0,0));

        pathNodePaint = new Paint();
        pathNodePaint.setStyle(Paint.Style.STROKE);
        pathNodePaint.setStrokeWidth(2);
        pathNodePaint.setAntiAlias(true);
        //pathNodePaint.setAlpha(127);
        pathNodePaint.setColor(Color.rgb(242,108,40));

        barrierNodePaint = new Paint();
        barrierNodePaint.setStyle(Paint.Style.STROKE);
        barrierNodePaint.setStrokeWidth(2);
        barrierNodePaint.setAntiAlias(true);
        barrierNodePaint.setAlpha(127);
        barrierNodePaint.setColor(Color.rgb(242,90,48));

        startNodePaint = new Paint();
        startNodePaint.setStyle(Paint.Style.STROKE);
        startNodePaint.setStrokeWidth(15);
        startNodePaint.setAntiAlias(true);
        startNodePaint.setAlpha(127);
        startNodePaint.setColor(Color.rgb(64,224,208));

        endNodePaint = new Paint();
        endNodePaint.setStyle(Paint.Style.STROKE);
        endNodePaint.setStrokeWidth(15);
        endNodePaint.setAntiAlias(true);
        endNodePaint.setAlpha(127);
        endNodePaint.setColor(Color.rgb(64,224,208));
    }
}