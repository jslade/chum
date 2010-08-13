package chum.util;

/**
   Compatibility layer for android.util.Log(), primarily to allow classes
   that use Log methods to run outside of Android environment.  It also
   provides some enhanced convenience features, however
*/
public class Log {
    
    static public String default_tag = "Chum";

    static private boolean _android = false;
    static private boolean _init = false;


    static public void puts(String msg) {
        puts(default_tag,msg);
    }

    static public void puts(String fmt, Object ... args) {
        String msg = String.format(fmt,args);

        if ( !_init ) _init();
        if ( _android ) {
            android.util.Log.i(default_tag,msg);
        } else {
            System.out.println(msg);
        }
    }

    static public void puts(String tag,String fmt, Object ... args) {
        String msg = String.format(fmt,args);

        if ( !_init ) _init();
        if ( _android ) {
            android.util.Log.i(tag,msg);
        } else {
            System.out.printf("%s| %s\n", tag, msg);
        }
    }
    


    static public void d(String msg) {
        d(default_tag,msg);
    }

    static public void d(String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        d(default_tag,msg);
    }

    static public void d(String tag,String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        d(tag,msg);
    }

    static public void d(String tag,String msg) {
        if ( !_init ) _init();
        if ( _android ) {
            android.util.Log.d(tag,msg);
        } else {
            System.out.printf("%s| %s\n", tag, msg);
        }
    }
    


    static public void w(String msg) {
        w(default_tag,msg);
    }

    static public void w(String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        w(default_tag,msg);
    }

    static public void w(String tag,String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        w(tag,msg);
    }

    static public void w(String tag,String msg) {
        if ( !_init ) _init();
        if ( _android ) {
            android.util.Log.w(tag,msg);
        } else {
            System.out.printf("%s| %s\n", tag, msg);
        }
    }
    


    static public void i(String msg) {
        i(default_tag,msg);
    }

    static public void i(String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        i(default_tag,msg);
    }

    static public void i(String tag,String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        i(tag,msg);
    }

    static public void i(String tag,String msg) {
        if ( !_init ) _init();
        if ( _android ) {
            android.util.Log.i(tag,msg);
        } else {
            System.out.printf("%s| %s\n", tag, msg);
        }
    }
    


    static public void e(String msg) {
        e(default_tag,msg);
    }

    static public void e(String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        e(default_tag,msg);
    }

    static public void e(String tag,String fmt, Object ... args) {
        String msg = String.format(fmt,args);
        e(tag,msg);
    }

    static public void e(String tag,String msg) {
        if ( !_init ) _init();
        if ( _android ) {
            android.util.Log.e(tag,msg);
        } else {
            System.out.printf("%s| %s\n", tag, msg);
        }
    }
    


    private static void _init() {
        _init = false;
        _android = false;
        try {
            android.util.Log.isLoggable("x",android.util.Log.ASSERT);
            _android = true;
        } catch( NoClassDefFoundError e ) {}
    }
}

