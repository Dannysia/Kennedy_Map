package edu.onu.kennedy_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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

    private int currentFloor = 0;

    private float[] transMatrix = new float[6];
    private Rect boundaryRect;

    public boolean showDebugBorder = false;

    private ArrayList<ArrayList<AnimationCMD>> CMDs = new ArrayList<>();

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

    private static class AnimationCMD {
        public DrawNode drawNode;
        private AnimationCmdType cmdType;
        private long time;

        public boolean checked = false;

        public AnimationCMD(DrawNode drawNode, long time, AnimationCmdType cmdType) {
            this.drawNode = drawNode;
            this.time = time;
            this.cmdType = cmdType;
        }

        public AnimationCMD(DrawNode drawNode, long time) {
            this.drawNode = drawNode;
            this.time = time;
        }

        public AnimationCmdType getCmdType() {
            return cmdType;
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
        float imageBmpScale = Math.min((float) this.getMeasuredWidth() / boundaryBitmap.getWidth(), (float) this.getMeasuredHeight() / boundaryBitmap.getHeight());

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

    private DrawNode pathNodeToDrawNode(PathNode node){
        return new DrawNode(
                (node.getX() * transMatrix[TransMatrixID.Scale] + transMatrix[TransMatrixID.xOffset]),
                (node.getY() * transMatrix[TransMatrixID.Scale] + transMatrix[TransMatrixID.yOffset]),
                node.getNodeType(),
                transMatrix[TransMatrixID.Scale] / 4);
    }

    public void drawCMD(PathNode node){
        AnimationCMD cmd = new AnimationCMD(pathNodeToDrawNode(node), 0, AnimationCmdType.DRAW);
        int floor;

        switch (node.getZ()){
            case 0:
                floor = 0;
                break;
            case 2:
                floor = 1;
                break;
            case 4:
                floor = 2;
                break;
            default:
                return;
        }

        CMDs.get(floor).add(cmd);
    }

    public void clearCMD(int delayInMillis){
        AnimationCMD cmd = new AnimationCMD(null, delayInMillis, AnimationCmdType.CLEAR);

        CMDs.get(0).add(cmd);
        CMDs.get(1).add(cmd);
        CMDs.get(2).add(cmd);
    }

    public void waitCMD(int delayInMillis){
        AnimationCMD cmd = new AnimationCMD(null, delayInMillis, AnimationCmdType.WAIT);

        CMDs.get(0).add(cmd);
        CMDs.get(1).add(cmd);
        CMDs.get(2).add(cmd);
    }

    public void changeFloorCMD(int floor){
        currentFloor = floor - 1;
    }

    private void renderNodes(Canvas canvas){
        for (int cmdIndex = 0; cmdIndex < CMDs.get(currentFloor).size(); cmdIndex++){
            if (CMDs.get(currentFloor) != null) {
                //Store the current node with respect to the current floor
                AnimationCMD currentCMD = CMDs.get(currentFloor).get(cmdIndex);
                if (currentCMD.cmdType == AnimationCmdType.CLEAR || currentCMD.cmdType == AnimationCmdType.WAIT){
                    if (!currentCMD.checked){
                        //if this is the first time this node has been checked, mark it as checked and set the time to pass to the specified delay + current time
                        currentCMD.checked = true;
                        currentCMD.setTime(currentCMD.getTime() + System.currentTimeMillis());
                        return;
                    }

                    //if we are beyond out time to pass then do the relevant actions based on the CMD
                    if (currentCMD.time <= System.currentTimeMillis()){
                        switch (currentCMD.cmdType){
                        case CLEAR:
                            if (cmdIndex == 0){
                                CMDs.get(currentFloor).remove(cmdIndex);
                            } else {
                                CMDs.get(currentFloor).subList(0, cmdIndex).clear();
                            }
                            return;
                        case WAIT:
                            CMDs.get(currentFloor).remove(cmdIndex);
                            break;
                        }
                    } else {
                        return;
                    }
                } else {
                    DrawNode node = currentCMD.drawNode;
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
    }

    public void update(){
        //anything that runs per tick that isn't a drawcall (the number of drawcalls = the number of times update runs)
        Log.d("Rendering", "update: happened");
        while (CMDs.size() < 3) CMDs.add(new ArrayList<>());
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

    @Override
    public boolean onTouchEvent(MotionEvent me){
        return false;
    }

    // Change these to change what is drawn
    private void initPaints(){
        // Debug paints
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

        barrierNodePaint = new Paint();
        barrierNodePaint.setStyle(Paint.Style.STROKE);
        barrierNodePaint.setStrokeWidth(2);
        barrierNodePaint.setAntiAlias(true);
        barrierNodePaint.setAlpha(127);
        barrierNodePaint.setColor(Color.rgb(242,90,48));

        // Real stuff
        pathNodePaint = new Paint();
        pathNodePaint.setStyle(Paint.Style.STROKE);
        pathNodePaint.setStrokeWidth(10);
        pathNodePaint.setAntiAlias(true);
        //pathNodePaint.setAlpha(127);
        pathNodePaint.setColor(Color.rgb(176,38,255));

        startNodePaint = new Paint();
        startNodePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        startNodePaint.setStrokeWidth(15);
        startNodePaint.setAntiAlias(true);
        //startNodePaint.setAlpha(127);
        startNodePaint.setColor(Color.rgb(64,224,208));
        startNodePaint.setShadowLayer(5f,2f,2f,Color.rgb(0,0,0));

        endNodePaint = new Paint();
        endNodePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        endNodePaint.setStrokeWidth(15);
        endNodePaint.setAntiAlias(true);
        //endNodePaint.setAlpha(127);
        endNodePaint.setColor(Color.rgb(64,224,208));
        endNodePaint.setShadowLayer(5f,2f,2f,Color.rgb(0,0,0));
    }
}
