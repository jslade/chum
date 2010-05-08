package chum.engine;


/**
   An GameSequence is used to emit events at certain times.  This is used to control
   things like animations, game AI, etc

   A GameSequence itself is a {@link GameNode} in the {@link GameTree}, so it
   automatically updates each frame.
*/
public class GameSequence extends GameNode {

    /** The time at which the sequence should start */
    public long startTime;

    /** The time at which the sequence should end */
    public long endTime;

    /** The time for the next step event */
    public long stepTime;

    /** The time elapsed since the sequence was started */
    public long elapsedTime;

    /** The GameEvent type to fire when starting */
    public int startType = GameEvent.SEQUENCE_START;

    /** The GameEvent type to fire when ending */
    public int stepType = GameEvent.SEQUENCE_STEP;

    /** The GameEvent type to fire when ending */
    public int endType = GameEvent.SEQUENCE_END;

    /** Whether start time has been reached previously */
    public boolean started;
    
    /** Whether end time has been reached previously */
    public boolean ended;

    /** Whether to auto-remove from the game tree when done */
    public boolean removeOnEnd = true;


    /** Create a new sequence with the given duration */
    public GameSequence(long duration) {
        super();
        setStartEnd(duration);
    }


    /**
       Set the start and end time of the sequence, using the current
       time as the start and the given duration for the end
    */
    protected void setStartEnd(long duration) {
        long start = 0;
        if ( gameController != null )
            start = gameController.totalElapsed;
        setStartEnd(start, start+duration);
    }


    /**
       Set the start and time of the sequence
    */
    protected void setStartEnd(long start, long end) {
        startTime = start;
        endTime = end;
    }


    /**
       Update the sequence every frame
    */
    @Override
    public boolean update(long millis) {
        if ( started )
            elapsedTime += millis;
        
        if ( gameController.totalElapsed >= startTime ) {
            if ( !started ) {
                postStart();
                started = true;
                stepTime = scheduleNextStep();
            }
        }
        if ( stepTime > 0 && gameController.totalElapsed >= stepTime ) {
            postStep();
            stepTime = scheduleNextStep();

            if ( stepTime == 0 && endTime == 0 ) {
                // if at least one step was made, but no more, and if no end
                // time is defined, then use now as the end time
                endTime = gameController.totalElapsed;
            }
        }
        if ( endTime > 0 && gameController.totalElapsed >= endTime ) {
            if ( !ended ) {
                postEnd();
                ended = true;
                
            }
        }

        boolean updated = super.update(millis);

        if ( ended && removeOnEnd )
            parent.removeNode(this);

        return updated;
    }


    /**
       Post the start-of-sequence event.
       Default behavior is to post an event defined by startType, with this
       GameSequence as the payload.
    */
    protected void postStart() {
        postUp(GameEvent.obtain(startType,this));
    }


    /**
       Post the end-of-sequence event
       Default behavior is to post an event defined by startType, with this
       GameSequence as the payload.
    */
    protected void postEnd() {
        postUp(GameEvent.obtain(endType,this));
    }


    /**
       Post the step-sequence event
       Default behavior is to post an event defined by stepType, with this
       GameSequence as the payload.
    */
    protected void postStep() {
        postUp(GameEvent.obtain(stepType,this));
    }


    /**
       Schedule the next step in the sequence
       @return the time for the next step, or else step
    */
    protected long scheduleNextStep() {
        return 0;
    }

}

