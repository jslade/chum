package chum.util;

import android.content.Context;


/**
   The base class for exception handlers.  DefaultExceptionHandler
   instances are used to handle uncaught exceptions, via the
   Thread.UncaughtExceptionHandler interface -- in other words,
   application crashes.

   The idea is generally to capture some info about the exception, and
   notify both the user and the developer.  The real focus is on notifying
   the developer, however, so real crash data can be gathered from in
   the wild.
   
*/
public class DefaultExceptionHandler
    implements Thread.UncaughtExceptionHandler
{
    /** Save the previous exception handler for proper chaining */
    private Thread.UncaughtExceptionHandler priorUEH;


    public DefaultExceptionHandler() {
        priorUEH = Thread.getDefaultUncaughtExceptionHandler();
    }


    public void uncaughtException(Thread t, Throwable e) {
        Log.e("%s uncaughtException() %s", this, e);
        Throwable propagate = handleException(t,e);
        if ( propagate != null )
            propagateException(t,propagate);
    }


    /**
       Handle an exception. 

       The default behavior is just to propagate the original.
       Subclasses should redefine this method to capture the
       appropriate level of detail, and to somehow dispatch the
       crash report to the developer's remote logging facility.

       In many cases, the dispatching portion should be separated from
       the actual exception handling, so that dispatching can be done
       at a later time (e.g. send pending crash reports when the app
       is started again).  The dispatch() method is intended to be
       kept separate for just that purpose.

       @return The exception to propagate to the original uncaught
       exception handler (which by default will force-close the app).
       Should return null if no exception should be propagated.
    */
    protected Throwable handleException(Thread t, Throwable e) {
        return e;
    }


    /**
       Pass the given exception on to the prior uncaught exception
       handler in the chain (if there was one)
    */
    protected void propagateException(Thread t, Throwable e) {
        if ( priorUEH != null )
            priorUEH.uncaughtException(t,e);
    }


    /**
       Dispatch any pending exceptions that have been previously
       handled.

       This is specifically intended for the case where an exception
       is handled and logged, but can't be immediately dispatched
       (i.e. because the dispatch method, such as sending email, won't
       work from a crashed app).

       The dispatch() method is automatically called early on during
       the initialization of a GameActivity instance.  The idea is to
       dispatch exceptions from the last time the app was opened
    */
    public void dispatch(Context context) {
        
    }
    

  }

