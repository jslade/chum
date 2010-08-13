package chum.examples;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import chum.util.Log;



public class ChumExamples extends ListActivity
{
    private ArrayList<String> examples = new ArrayList<String>();
    private HashMap<String,Class<?>> exampleClasses = new HashMap<String,Class<?>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addExample("Fastest-possble FPS", FastestPossibleFPS.class);
        addExample("Background Color / Touch", BackgroundColorTouch.class);
        addExample("Random color rects (2D)", RandomRectangles.class);
        addExample("Animated Text (2D)", AnimatedTextExample.class);
        addExample("SkaterDroid (2D)", SkaterDroidExample.class);
        addExample("Colored spinning pyramid (3D)", SpinningColorPyramid.class);
        addExample("Textured cube (3D)", TexturedCube.class);
        addExample("Many-many nodes test", ManyManyNodes.class);
        addExample("Force-close test", ForceCloseExample.class);

        setListAdapter(new ArrayAdapter<String>
                       (this, android.R.layout.simple_list_item_1, examples));
    }

    protected void addExample(String label, Class<?> klass) {
        examples.add(label);
        exampleClasses.put(label,klass);
    }
        
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
	
        String label = examples.get(position);
        Log.d("ChumExamples","Picked example: %s", label);

        Class<?> klass = exampleClasses.get(label);
        if ( klass != null ) {
            Intent intent = new Intent(this, klass);
            startActivity( intent );
	}
    }
}
