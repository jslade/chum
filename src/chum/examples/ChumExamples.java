package chum.examples;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;

import chum.util.Log;



public class ChumExamples extends ListActivity
{
    private HashMap<String,Class<?>> examples = new HashMap<String,Class<?>>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        examples.put("Fastest-possble FPS", FastestPossibleFPS.class);
        examples.put("Background Color / Touch", BackgroundColorTouch.class);
        examples.put("Many-many nodes test", ManyManyNodes.class);
        examples.put("Force-close test", ForceCloseExample.class);
        examples.put("Random color rects (2D)", RandomRectangles.class);
        examples.put("Colored spinning pyramid (3D)", SpinningColorPyramid.class);
        examples.put("Textured cube (3D)", TexturedCube.class);

        String[] items = new String[0]; // Has to be non-null for toArray()
        items = examples.keySet().toArray(items);
        setListAdapter(new ArrayAdapter<String>
                       (this, android.R.layout.simple_list_item_1, items));
    }

    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
	
        String item = this.getListAdapter().getItem(position).toString();
        Log.d("ChumExamples","Picked example: %s", item);

        Class<?> klass = examples.get(item);
        if ( klass != null ) {
            Intent intent = new Intent(this, klass);
            startActivity( intent );
	}
    }
}
