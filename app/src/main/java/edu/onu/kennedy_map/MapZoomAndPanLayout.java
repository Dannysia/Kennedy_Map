package edu.onu.kennedy_map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;

public class MapZoomAndPanLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {
    public MapZoomAndPanLayout(@NonNull Context context) {super(context);}

    // Need these two constructors or android isn't happy
    public MapZoomAndPanLayout(Context context, AttributeSet attributeSet) { super(context, attributeSet);init(context); }
    public MapZoomAndPanLayout(Context context, AttributeSet attributeSet, int androidStyle) { super(context, attributeSet, androidStyle);init(context); }

    // Change these if you think we can zoom in too much
    private static final double MIN_ZOOM = 1.0;
    private static final double MAX_ZOOM = 3.0;

    // Image starts at zero
    private double startPositionX = 0f;
    private double startPositionY = 0f;
    private double imageX = 0f;
    private double imageY = 0f;
    private double imageZoom = 1.0;
    private double lastImageZoom = 0.0;

    private double previousPositionX = 0f;
    private double previousPositionY = 0f;

    // 0 = nothing
    // 1 = dragging
    // 2 = zooming
    int actionID = 0;

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        double scaleFactor = scaleGestureDetector.getScaleFactor();
        if (lastImageZoom == 0 || (Math.signum(scaleFactor) == Math.signum(lastImageZoom))) {
            imageZoom = Math.max(MIN_ZOOM, Math.min((imageZoom*scaleFactor), MAX_ZOOM));
            lastImageZoom = scaleFactor;
        } else {
            lastImageZoom = 0;
        }
        return true;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) { return true; }
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { }

    // Looked up the warning, it doesn't really matter so don't worry about it
    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    actionID = 2;
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (imageZoom > MIN_ZOOM) {
                        actionID = 1;
                        startPositionX = motionEvent.getX() - previousPositionX;
                        startPositionY = motionEvent.getY() - previousPositionY;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    actionID = 1;
                    break;
                case MotionEvent.ACTION_UP:
                    actionID = 0;
                    previousPositionX = imageX;
                    previousPositionY = imageY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (actionID == 1) {
                        imageX = motionEvent.getX() - startPositionX;
                        imageY = motionEvent.getY() - startPositionY;
                    }
                    break;
            }
            scaleGestureDetector.onTouchEvent(motionEvent);

            if ((actionID == 1 && imageZoom >= MIN_ZOOM) || actionID == 2) {
                // The first item in the frame view is the picture
                View pictureToZoom  = getChildAt(0);
                pictureToZoom.performClick();
                // Calculates the maximum it can go before it would go off the image.
                imageX = Math.min(Math.max(imageX, - ((pictureToZoom.getWidth() - (pictureToZoom.getWidth() / imageZoom)) / 2 * imageZoom)),
                        ((pictureToZoom.getWidth() - (pictureToZoom.getWidth() / imageZoom)) / 2 * imageZoom));
                imageY = Math.min(Math.max(imageY, - ((pictureToZoom.getHeight() - (pictureToZoom.getHeight() / imageZoom))/ 2 * imageZoom)),
                        ((pictureToZoom.getHeight() - (pictureToZoom.getHeight() / imageZoom))/ 2 * imageZoom));
                pictureToZoom.setScaleX((float) imageZoom);
                pictureToZoom.setScaleY((float) imageZoom);
                pictureToZoom.setTranslationX((float) imageX);
                pictureToZoom.setTranslationY((float) imageY);
            }
            return true;
        });
    }
}