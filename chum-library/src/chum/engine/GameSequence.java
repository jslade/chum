package chum.engine;

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

    /** Is it a one-shot sequence? */
    public boolean oneShot = true;


    /** Create a new sequence with the given duration */
    protected GameSequence(long duration) {
        super();
        this.duration = duration;
    }
    
    
    /**
       Reset the sequence to its original state
     */
    public void reset() {
        hold = started = ended = false;
        startTime = endTime = stepTime = elapsedTime = 0;
        for(int i=0;i < this.num_children; ++i){
            GameNode child = this.children[i];
            if ( child instanceof GameSequence )
                ((GameSequence)child).reset();
        }
    }  
 
    
    protected void resetInternal() {
        duration = 0;
        oneShot = true;
        startType = GameEvent.SEQUENCE_START;
        stepType = GameEvent.SEQUENCE_STEP;
        endType = GameEvent.SEQUENCE_END;
        name = null;
    }   

    
    protected void resetAll() {
        resetInternal();
        reset();
    }
    
    
    /**
       Set the sequence to hold (not start)
    */
    public void hold() {
        hold = true;
    }
      

    @Override
    public void onAdded(GameNode parent) {
        super.onAdded(parent);
        if ( parent instanceof GameSequence ) {
            GameSequence pseq = (GameSequence)parent;
            if ( pseq.started ) start();
        } else {
            start();
        }
    }
    
    
    /**
       Set the start and end time of the sequence, using the current
       time as the start and the given duration for the end
    */
    public void start() {
        startTime = 0;
        if ( startTime == 0 && gameController != null)
            startTime = gameController.totalElapsed;
        if ( endTime == 0 ) endTime = startTime + duration;
        hold = false;
    }


    /**
       Update the sequence every frame
    */
    @Override
    public boolean updatePrefix(long millis) {
        if ( hold )
            return false;

        if ( started ) {
            elapsedTime += millis;
        }

        if ( !started ) {
            if ( shouldStart() ) {
                started = true;
                if ( startTime == 0 ) startTime = gameController.totalElapsed;
                if ( endTime == 0 ) endTime = startTime + duration;
                postStart();
                stepTime = scheduleNextStep();
            }
        }
        else if ( shouldStep() ) {
            postStep();
            stepTime = scheduleNextStep();
            
            if ( endTime == 0 ) {
                // if no end  time is defined, then use now as the end time
                endTime = gameController.totalElapsed;
            }
        }
        else if ( !ended && shouldEnd() ) {
            ended = true;
            postEnd();
        }
        else if ( ended ) {
            if ( oneShot ) {
                parent.removeNode(this);
                this.recycle();
            }
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
    
    
    
    private static GameSequence first_avail; // TODO: need synchronized() on obtain() and recycle()
    protected GameSequence next_avail;
    
    
    public static GameSequence obtain() {
        if ( first_avail == null ) first_avail = new GameSequence(0);
        GameSequence seq = first_avail;
        first_avail = first_avail.next_avail;
        seq.resetAll();
        return seq;
    }

    
    public void recycle() {
        next_avail = first_avail;
        first_avail = this;
    }

    
    
    /**
       Special sequence that fires an extra event at the end, separate from it's
       normal finish type.  This is intended to be used as part of a nested
       sequence, where the normal finish type is used to signal the next step
       of the nested sequence (and therefore doesn't get propagated)
     */
    public static class Poster extends GameSequence {
    
        public int postType;
        public long postDelay;
        
        protected Poster() {
            this(0);
        }
     
        protected Poster(long duration) {
            super(duration);
        }
     
        @Override
        protected void postEnd() {
            super.postEnd();
            if ( postDelay != 0 ) {
                postUpDelayed(GameEvent.obtain(postType,this),postDelay);
            } else {
                postUp(GameEvent.obtain(postType,this));
            }
        }

        private static Poster first_avail;
        
        public static Poster obtain() {
            if ( first_avail == null ) first_avail = new Poster(0);
            Poster p = first_avail;
            first_avail = (Poster)first_avail.next_avail;

            p.postType = 0;
            p.postDelay = 0;
            p.resetAll();

            return p;
        }

        @Override
        public void recycle() {
            next_avail = first_avail;
            first_avail = this;
        }
        
     }
     
    

    /**
       Base class for sequences intended to hold other sequences
     */
    public static class Nested extends GameSequence {
        
        protected Nested() {
            this(0);
        }
        
        protected Nested(long duration) {
            super(duration);
        }
        
        /** Add a child Sequence */
        @Override
        public GameNode addNode(GameNode child) {
            GameNode node = super.addNode(child);
            if ( child instanceof GameSequence ) {
                GameSequence seq = (GameSequence)child;
                seq.oneShot = this.oneShot;
            }
            return node;
        }



        // Reset all child sequences along with this one
        @Override
        public void reset() {
            super.reset();
            for(int i=0; i<num_children;++i) {
                GameNode child = children[i];
                if ( child instanceof GameSequence ) {
                    GameSequence seq = (GameSequence)child;
                    seq.reset();
                    seq.hold();
                }
            }
            
        }
        

        // Nested sequences get held until explicitly started
        @Override
        protected void postStart() {
            super.postStart();
            holdAll();            
        }
        
        protected void holdAll() {
            for(int i=0; i<num_children; ++i) {
                GameNode child = children[i];
                if ( child instanceof GameSequence ) {
                    GameSequence seq = (GameSequence)child;
                    seq.hold();
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
       A Series is a set of GameSequences that run back-to-back (in serial), each starting
       in turn after the previous one completes.  The sequences are added to the
       chain as normal nodes, but each node is not given a chance to start
       (by calling its update()) method until the previous one is done.
    */
    public static class Series extends Nested {

        protected Series() {
            this(0);
        }


        protected Series(long duration) {
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
            startNext(); // Start the first child
        }


        /**
           Find the next child sequence that is being held and start it
        */
        public void startNext() {
            for(int i=0; i<num_children; ++i) { 
                GameNode child = children[i];
                if ( child instanceof GameSequence ) {
                    GameSequence seq = (GameSequence)child;
                    if ( seq.hold ) {
                        seq.start();
                        if ( seq.endTime > endTime )
                            endTime = seq.endTime;
                        return;
                    }
                }
            }
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
                    return true;
                }
            }
            return super.onGameEvent(event);
        }


        private static Series first_avail;
        
        public static Series obtain() {
            if ( first_avail == null ) first_avail = new Series(0);
            Series seq = first_avail;
            first_avail = (Series)first_avail.next_avail;
            seq.resetAll();
            return seq;
        }

    
        @Override
        public void recycle() {
            next_avail = first_avail;
            first_avail = this;
        }
    }


    /**
       A Parallel is a set of GameSequences that run in parallel.
       The sequences are added to the set as normal nodes, and this sequence is considered
       complete when the last one ends.
    */
    public static class Parallel extends Nested {

        protected Parallel() {
            this(0);
        }


        protected Parallel(long duration) {
            super(duration);
        }


        // Whenever a new sequence is added to the list, set it to hold.
        @Override
        public GameNode addNode(GameNode n) {
            super.addNode(n);
            if ( n instanceof GameSequence ) {
                GameSequence seq = (GameSequence)n;
                if ( started ) seq.start();
                else seq.hold();
            }
            return this;
        }


        @Override
        protected void postStart() {
            super.postStart();
            startAll();
        }
        
        
        protected void startAll() {
            for(int i=0; i<num_children; ++i) {
                GameNode child = children[i];
                if ( child instanceof GameSequence ) {
                    GameSequence seq = (GameSequence)child;
                    if ( !seq.started ) seq.start();
                }
            }
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
                    return true;
                }
            }
            return super.onGameEvent(event);
        }


        private static Parallel first_avail;
        
        public static Parallel obtain() {
            if ( first_avail == null ) first_avail = new Parallel();
            Parallel seq = first_avail;
            first_avail = (Parallel)first_avail.next_avail;
            seq.resetAll();
            return seq;
        }

    
        @Override
        public void recycle() {
            next_avail = first_avail;
            first_avail = this;
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


        protected Interpolated(long duration, Interpolator interp) {
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
        

        private static Interpolated first_avail;
        
        public static Interpolated obtain() {
            if ( first_avail == null ) first_avail = new Interpolated(0,null);
            Interpolated seq = first_avail;
            first_avail = (Interpolated)first_avail.next_avail;
            seq.resetAll();
            return seq;
        }

    
        @Override
        public void recycle() {
            next_avail = first_avail;
            first_avail = this;
        }
        
    }

}

