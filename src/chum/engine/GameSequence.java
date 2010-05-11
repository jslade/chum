package chum.engine;

import chum.util.Log;

import android.view.animation.Interpolator;


/**
   An GameSequence is used to emit events at certain times.  This is used to control
   things like animations, game AI, etc

   A GameSequence itself is a {@link GameNode} in the {@link GameTree}, so it
   automatically updates each frame.
*/
public class GameSequence extends GameNode {
    
    /** Whether the sequence can start */
    public boolean hold = false;

    /** The intended duration */
    public long duration;

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
        this.duration = duration;
    }


    /**
       Set the sequence to hold (not start)
    */
    public void hold() {
        hold = true;
        //Log.d("Holding sequence: %s",this);
    }
      

    @Override
    public void onAdded(GameNode parent) {
        super.onAdded(parent);
        start();
    }


    /**
       Set the start and end time of the sequence, using the current
       time as the start and the given duration for the end
    */
    public void start() {
        startTime = 0;
        if ( gameController != null )
            startTime = gameController.totalElapsed;
        endTime = startTime + duration;
        //Log.d("Sequence.start(): %s start=%d end=%d duration=%d",
        //      this, startTime, endTime, duration);
        hold = false;
    }


    /**
       Update the sequence every frame
    */
    @Override
    public boolean updatePrefix(long millis) {
        if ( started ) {
            elapsedTime += millis;
            //Log.d("Sequence %s: %d elapsed", this, elapsedTime);
        }

        if ( hold )
            return false;

        if ( !started ) {
            if ( shouldStart() ) {
                //Log.d("Starting sequence: %s", this);
                started = true;
                postStart();
                stepTime = scheduleNextStep();
            }
        }
        else if ( shouldStep() ) {
            postStep();
            stepTime = scheduleNextStep();
            
            if ( stepTime == 0 && endTime == 0 ) {
                // if at least one step was made, but no more, and if no end
                // time is defined, then use now as the end time
                endTime = gameController.totalElapsed;
            }
        }
        else if ( shouldEnd() ) {
            //Log.d("Ending sequence: %s", this);
            ended = true;
            postEnd();
        }
        else if ( ended && removeOnEnd ) {
            //Log.d("Removing finished sequence: %s", this);
            parent.removeNode(this);
        }


        return true;
    }


    public boolean shouldStart() {
        return ( !hold &&
                 !started &&
                 gameController.totalElapsed >= startTime );
    }


    public boolean shouldStep() {
        return ( started &&
                 stepTime > 0 &&
                 gameController.totalElapsed >= stepTime );
    }


    public boolean shouldEnd() {
        return ( started && 
                 !ended &&
                 endTime > 0 &&
                 gameController.totalElapsed >= endTime );
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



    /**
       A Series is a set of GameSequences that run back-to-back (in serial), each starting
       in turn after the previous one completes.  The sequences are added to the
       chain as normal nodes, but each node is not given a chance to start
       (by calling its update()) method until the previous one is done.
    */
    public static class Series extends GameSequence {

        public Series() {
            this(0);
        }


        public Series(long duration) {
            super(duration);
        }


        /** 
            Whenever a new sequence is added to the chain, set it to hold.
        */
        @Override
        public GameNode addNode(GameNode n) {
            super.addNode(n);
            if ( n instanceof GameSequence ) {
                GameSequence seq = (GameSequence)n;
                seq.hold();
            }
            return this;
        }

        /**
           When this sequence starts, start the first held child sequence
        */
        @Override
        protected void postStart() {
            super.postStart();
            startNext();
        }


        /**
           Whenever a child sequence finishes, start the next one
        */
        @Override
        public boolean onGameEvent(GameEvent event) {
            if ( event.origin instanceof GameSequence ) {
                GameSequence seq = (GameSequence)event.origin;
                if ( event.type == seq.endType &&
                     seq.parent == this ) {
                    startNext();
                }
            }
            return super.onGameEvent(event);
        }


        /**
           Find the next child sequence that is being held and start it
        */
        public void startNext() {
            //Log.d("GameSequence.Series startNext()");
            synchronized(this) {
                for(int i=0; i<num_children; ++i) {
                    GameNode child = children[i];
                    if ( child instanceof GameSequence ) {
                        GameSequence seq = (GameSequence)child;
                        if ( seq.hold ) {
                            seq.start();
                            if ( seq.endTime > endTime )
                                endTime = seq.endTime;
                            break;
                        }
                    }
                }
            }
        }


        // Don't end until all the child sequences indicate they are done
        @Override
        public boolean shouldEnd() {
            if ( !super.shouldEnd() ) return false;

            for(int i=0; i<num_children; ++i) {
                GameNode child = children[i];
                if ( child instanceof GameSequence ) {
                    GameSequence seq = (GameSequence)child;
                    if ( !seq.shouldEnd() )
                        return false;
                }
            }
            
            return true;
        }
    }


    /**
       A Parallel is a set of GameSequences that run in parallel.
       The sequences are added to the set as normal nodes, and this sequence is considered
       complete when the last one ends.
    */
    public static class Parallel extends GameSequence {

        public Parallel() {
            this(0);
        }


        public Parallel(long duration) {
            super(duration);
        }


        // Don't end until all the child sequences indicate they are done
        @Override
        public boolean shouldEnd() {
            if ( !super.shouldEnd() ) return false;

            for(int i=0; i<num_children; ++i) {
                GameNode child = children[i];
                if ( child instanceof GameSequence ) {
                    GameSequence seq = (GameSequence)child;
                    if ( !seq.shouldEnd() )
                        return false;
                }
            }
            
            return true;
        }


        /**
           Whenever a child sequence finishes, see if this sequence should end
        */
        @Override
        public boolean onGameEvent(GameEvent event) {
            if ( event.origin instanceof GameSequence ) {
                GameSequence seq = (GameSequence)event.origin;
                if ( event.type == seq.endType &&
                     seq.parent == this ) {
                    if ( seq.endTime > endTime )
                        endTime = seq.endTime;
                }
            }
            return super.onGameEvent(event);
        }
    }


    /**
       An interpolated sequence uses an Interpolator to affect the rate
       of progress in the sequence
    */
    public static class Interpolated extends GameSequence {

        /** The raw percent complete (0-1.0)*/
        public float rawProgress;

        /** The interpolated perceont complete (0-1.0) */
        public float progress;

        /** The interpolator */
        public Interpolator interpolator;


        public Interpolated(long duration, Interpolator interp) {
            super(duration);
            this.interpolator = interp;
        }
        

        @Override
        public boolean updatePrefix(long millis) {
            boolean updated = super.updatePrefix(millis);
            if ( started ) updateProgress();
            return updated;
        }

        
        protected void updateProgress() {
            if ( duration == 0 ) return;

            rawProgress = 1.0f * elapsedTime / duration;
            if (rawProgress > 1.0f) rawProgress = 1.0f;

            if ( interpolator != null )
                progress = interpolator.getInterpolation(rawProgress);
            else
                progress = rawProgress;
        }
    }

}

