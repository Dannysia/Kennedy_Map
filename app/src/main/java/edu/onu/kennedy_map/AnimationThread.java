package edu.onu.kennedy_map;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * This class handles presenting the path animation on the UI, which utilizes another thread
 */
public class AnimationThread extends Thread{
    private int FPS = 60;
    private double avgFPS;
    private boolean running = false;

    private DrawableSurfaceView view;
    private SurfaceHolder surfaceHolder;
    private static Canvas canvas;

    public AnimationThread(SurfaceHolder surfaceHolder, DrawableSurfaceView view) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.view = view;
    }

    /**
     * Function used to set the running boolean for the thread
     * @param running The thread running boolean
     */
    public void setRunning(boolean running){
        this.running = running;
    }

    /**
     * Handles thread running behavior for the animations
     */
    @Override
    public void run() {
        long startTime,timeMillis,waitTime, totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/FPS;

        // Don't want to render when scrolling

        while (running){
            startTime = System.nanoTime();
            canvas = null;

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    //This is the ActionLoop
                    this.view.update();
                    this.view.draw(canvas);
                }
            } catch (Exception e){

            } finally {
                if (canvas != null){
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;

            try{
                this.sleep(waitTime);
            } catch (Exception e){

            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == FPS){
                avgFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                Log.d("Anim", "run: avgFps:" + avgFPS);
            }
        }
    }
}