package chum.engine.common;

import chum.engine.GameEvent;
import chum.engine.GameNode;


/**
 * A Trigger is a special node that listens for a specific GameEvent, and executes 
 * some code (itself) when that happens.
 * 
 * @author jeremy
 *
 */
public abstract class Trigger extends GameNode
    implements Runnable
{
    // The GameEvent type being waited on
    public int eventType;
    
    // Whether to remove itself from the tree after triggering 
    public boolean oneShot;
    
    
    public Trigger(int eventType) {
        this(eventType,true);
    }
    

    public Trigger(int eventType,boolean oneShot) {
        super();
        this.eventType = eventType;
        this.oneShot = oneShot;
    }
    
    
    @Override
    public boolean onGameEvent(GameEvent event) {
        if ( event.type == this.eventType ) {
            run();
            if ( oneShot ) {
                remove();
            }
            return true; // consumed
        }
        return super.onGameEvent(event);
    }
    
}
