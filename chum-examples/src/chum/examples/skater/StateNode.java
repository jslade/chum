package chum.examples.skater;

import chum.engine.*;


/**
   This is the main controller / logic for the game
*/
public class StateNode extends GameNode {

    // ------------------------------------------------------------
    // The events:

    // Used when the screen is touched, to move the skater up or down
    public static final int Steered = 1;




    // Transitions between states are controlled by events
    @Override
    public boolean onGameEvent(GameEvent event) {
        boolean handled = false;
        switch(event.type) {
        case Steered:
            //Log.d("newY = %d", event.ival);
            handled = true;
            break;

        }

        
        return handled;
    }


}
