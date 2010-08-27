package chum.util.gl;

import chum.util.Log;


/**
   Trace OpenGL ES calls. All GL calls get logged via the chum.util.Log.d()
   method. Also supports an optional callback to be invoked on every call.
 */
abstract class TraceGL {

    /** The StringBuilder for composing messages */
    protected StringBuilder sb = new StringBuilder();
    
    /** The tag for logging */
    protected String tag = Log.default_tag;

    /** The callback */
    protected Callback callback;

    /** Whether to pass calls through to the real GL instance */
    public boolean pass = true;
    

    public void setCategory(String tag) {
        this.tag = tag;
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public void trace(String call, Object ... args) {
        sb.delete(0,sb.length());
        sb.append(call);
        sb.append('(');
        for(int i=0; i<args.length; ++i) {
            if ( i > 0 ) sb.append(',');
            sb.append(args[i]);
        }
        sb.append(')');
        
        String msg = sb.toString();
        Log.d(tag, msg);

        if (callback != null)
            callback.trace(this,msg,call,args);
    }

    
    public interface Callback {
        public void trace(TraceGL gl, String msg, String call, Object ... args);
    }

}