package edu.onu.kennedy_map;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PathScreen extends AppCompatActivity {

    private DrawableSurfaceView floor1DSV;
    private DrawableSurfaceView floor2DSV;
    private DrawableSurfaceView floor3DSV;
    public PathFind pathFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_screen);

        //Get all the surface views and store them
        floor1DSV = findViewById(R.id.Floor1DSV);
        //floor2DSV = findViewById(...);
        //floor2DSV = findViewById(...);

        //For testing just use 3 separate instances of pathFind passing each DSV
        pathFind = new PathFind(this, floor1DSV);
        //pathFind2 = new PathFind(this, floor2DSV);
        //pathFind3 = new PathFind(this, floor3DSV);
    }


    public void debugClick(View view) {
        //this does some processing based on the size of the XML components so it must be called after the View is rendered
        pathFind.initialize();

        //Pathfind returns true if a path was found and false if there is no path
        //this can be used to trigger a message to inform the user (especially if we let the user pick a point themselves)
        if(pathFind.pathFindBetween(26,196,171,134)){
            Log.d("PathFind", "debugClick: Path Found!");
        } else {
            Log.d("PathFind", "debugClick: Path NOT Found!");
        }
    }
}