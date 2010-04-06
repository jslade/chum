package chum.engine;

/**
   Describes an event in the game play.  

   GameEvents can be genenerated by user input or other logic.  They
   are delivered to GameNodes in the GameTree for handling.
*/
public final class GameEvent
{
    /** The type of the event -- user-definable */
    public int type;

    /** Arbitrary object payload for the event */
    public Object object;

    /** Integer payload for the event */
    public int ival;

    /** Float payload for the event */
    public float fval;

    /** Boolean payload for the event */
    public boolean bval;


    /** Reference to next event in the queue */
    GameEvent nextQueued = null;

    private static Object sync = new Object();


    /** Create a new event.  This is not public, because the public use
        of the class is intended to be through obtain() */
    protected GameEvent() {
        this.type = type;
        this.object = null;
    }


    // first_avail / next_avail keep track of the pool of available
    // GameEvent instances for obtain() / recycle()
    private static GameEvent first_avail = null;
    private GameEvent next_avail = null;


    /**
       Get a new GameEvent instance.  Note that this will likely be a
       recycled instance, so the member fields should be initialized
       as needed
    */
    public static GameEvent obtain() {
        synchronized(sync) {
            if ( first_avail == null ) {
                first_avail = new GameEvent();
            }
            GameEvent ev = first_avail;
            first_avail = first_avail.next_avail;
            ev.next_avail = null;
            return ev;
        }
    }

    /**
       Get a new GameEvent instance with a specific type.

       Note that this will likely be a recycled instance, so the
       other member fields should be initialized as needed
    */
    public static GameEvent obtain(int t) {
        GameEvent ev = GameEvent.obtain();
        ev.type = t;
        return ev;
    }

    /**
       Get a new GameEvent instance with an specific type and Object payload.

       Note that this will likely be a recycled instance, so the
       other member fields should be initialized as needed
    */
    public static GameEvent obtain(int t,Object o) {
        GameEvent ev = GameEvent.obtain();
        ev.type = t;
        ev.object = o;
        return ev;
    }

    /**
       Get a new GameEvent instance with an specific type and integer payload.

       Note that this will likely be a recycled instance, so the
       other member fields should be initialized as needed
    */
    public static GameEvent obtain(int t,int v) {
        GameEvent ev = GameEvent.obtain();
        ev.type = t;
        ev.ival = v;
        return ev;
    }

    /**
       Get a new GameEvent instance with an specific type and float payload.

       Note that this will likely be a recycled instance, so the
       other member fields should be initialized as needed
    */
    public static GameEvent obtain(int t,float v) {
        GameEvent ev = GameEvent.obtain();
        ev.type = t;
        ev.fval = v;
        return ev;
    }

    /**
       Get a new GameEvent instance with an specific type and boolean payload.

       Note that this will likely be a recycled instance, so the
       other member fields should be initialized as needed
    */
    public static GameEvent obtain(int t,boolean v) {
        GameEvent ev = GameEvent.obtain();
        ev.type = t;
        ev.bval = v;
        return ev;
    }


    /**
       Return a GameEvent instance to the pool so it can be reused later.
       Using obtain() / recycle() reduces the number of objects that have to
       be allocated, reducing the overhead of garbage collection.
    */
    public void recycle() {
        synchronized(sync) {
            this.next_avail = first_avail;
            first_avail = this;
        }
    }


    /**
       Pre-allocate a number of GameEvent instances.  This is used to create a pool of
       GameEvent objects that can be reused throughout the running of a GameActivity
    */
    public static void allocate(int num) {
        for (int i=0; i<num; ++i) {
            GameEvent ev = new GameEvent();
            ev.next_avail = first_avail;
            first_avail = ev;
        }
    }


}