package chum.cfg;

import chum.util.DefaultExceptionHandler;
import chum.util.MailerExceptionHandler;

import android.content.Context;

import java.util.HashMap;


/**
   

*/
public class Config {


    /** The global Config instance */
    private static Config globalConfig;


    public static void setConfig(Config cfg) {
        globalConfig = cfg;
    }


    public static Config getConfig(Context context) {
        if ( globalConfig == null ) {
            globalConfig = new Config(context);
            globalConfig.loadSettings();
        }
        return globalConfig;
    }


    // ------------------------------------------------------------


    private Context context;

    private HashMap<String,Object> map = new HashMap<String,Object>();


    public Config(Context context) {
        this.context = context;
    }


    public Context getContext() {
        return context;
    }


    public void loadSettings() {
        // todo: Load settings from a standard xml resource file.
        put("exception_handler","mailer");
        put("exception_email","chum-exceptions@crazyheadgames.com");
        put("exception_subject","Application error");
    }


    public void put(String key, Object val) {
        map.put(key,val);
    }


    public Object get(String key) {
        return map.get(key);
    }



    /**
       Return the default ExceptioHandler to deal with any
       uncaught exceptions (prevent or at least log / record
       force-close occurrences)
    */
    public DefaultExceptionHandler defaultExceptionHandler() {
        String handlerName = get("exception_handler").toString();

        if ( handlerName.equals("mailer") ) {
            MailerExceptionHandler mailer =
                new MailerExceptionHandler(context);
            mailer.loadConfig(this);
            return mailer;
        }

        else {
            return new DefaultExceptionHandler();
        }
    }
}
