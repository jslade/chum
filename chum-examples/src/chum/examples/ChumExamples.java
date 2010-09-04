package chum.examples;

import chum.util.Log;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;



public class ChumExamples extends ListActivity
{
    private final ArrayList<String> examples = new ArrayList<String>();
    private final HashMap<String,Class<?>> exampleClasses = new HashMap<String,Class<?>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addExample("Fastest-possble FPS", FastestPossibleFPS.class);
        addExample("Background Color / Touch", BackgroundColorTouch.class);
        addExample("Random color rects (2D)", RandomRectangles.class);
        addExample("Animated Text (2D)", AnimatedTextExample.class);
        addExample("Colored spinning pyramid (3D)", SpinningColorPyramid.class);
        addExample("Textured cube (3D)", TexturedCube.class);
        addExample("Bouncing Sprites (2D)", BouncingSprites.class);
        addExample("SkaterDroid (2D)", SkaterDroidExample.class);
        addExample("Many-many nodes test", ManyManyNodes.class);
        addExample("Force-close test", ForceCloseExample.class);

        setListAdapter(new ArrayAdapter<String>
                       (this, android.R.layout.simple_list_item_1, examples));
        
    }

    protected void addExample(String label, Class<?> klass) {
        examples.add(label);
        exampleClasses.put(label,klass);
    }
        
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
	
        String label = examples.get(position);
        Log.d("ChumExamples","Picked example: %s", label);

        Class<?> klass = exampleClasses.get(label);
        startExample(klass);
    }
    
    protected void startExample(Class<?> klass) {
        if ( klass != null ) {
            Intent intent = new Intent(this, klass);
            startActivity( intent );
        }
    }
}
