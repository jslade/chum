package chum.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
   Helper class that manages storing and retreiving data for the various
   ExceptionHandlers.

   The crash reports are stored in files in the private area for the
   current app.  The methods here do not care about the content of the
   file.  These are just utilities to handle naming, creating,
   retreiving, and deleting the files.

   The CrashReport class also provides some helper methods for adding some
   of the standard info the would typically go into a crash report, such
   as the application name and version, and a stack trace.  These methods
   can be used when composing the crash report, but they are optional.
*/
public class CrashReport
{
    /** Create a new instance */
    public static CrashReport create(Context context) {
        CrashReport newReport = new CrashReport(context,null);
        return newReport;
    }


    /** Find all available reports */
    public static CrashReport[] find(Context context) {
        File crashDir = getCrashDir(context);
        File[] files = crashDir.listFiles();
        
        CrashReport[] reports = new CrashReport[files.length];
        for( int i=0; i<files.length; ++i ) {
            reports[i] = new CrashReport(context, files[i]);
        }

        return reports;
    }


    /** Clean out any existing reports */
    public static void deleteAll(Context context) {
        File crashDir = getCrashDir(context);
        File[] files = crashDir.listFiles();
        for( int i=0; i<files.length; ++i ) {
            files[i].delete();
        }
    }


    /** Get the directory where this app's crash reports are stored */
    public static File getCrashDir(Context context) {
        return context.getDir("chum_crash_reports", Context.MODE_PRIVATE);
    }




    /** The Context */
    private Context context;

    /** The on-disk File storing this report (if any) */
    public File file;

    /** The content of the crash report (if any) */
    public String contents;

    /** Indicates an error */
    public java.io.IOException ioError;


    private CrashReport(Context context,File file) {
        this.context = context;
        this.file = file;

        if ( context != null && file != null )
            load();
    }
        

    /** Write the crash report contents to the target file */
    public void save(String contents) {
        this.contents = contents;
        save();
    }


    /** Write the crash report contents to the target file */
    public void save() {
        if ( this.contents == null ) return;
        FileWriter output = openForWriting();
        if ( output != null ) {
            try {
                Log.e("CrashReport.save:\n"+this.contents);
                output.write(this.contents);
                output.close();
            } catch(java.io.IOException e) {
                // Silently ignore IO exceptions...
                ioError = e;
            }
        }
    }


    /** Load the crash report contents from the target file */
    public void load() {
        FileReader input = openForReading();
        if ( input == null ) return;
        try { 
            BufferedReader reader = new BufferedReader(input);
            String line;
            contents = "";
            while ( (line = reader.readLine()) != null ) {
                contents += line + "\n";
            }
        } catch(java.io.IOException e) {
            // Silently ignore IO exceptions
            ioError = e;
        }
    }
        
    
    /** Delete the CrashReport from the storage */
    public void delete() {
        if ( file == null ) return;
        file.delete();
    }


    /** Creates a FileWriter ready to store the contents of the
        CrashReport */
    private FileWriter openForWriting() {
        pickName();
        try {
            Log.d("Open CrashReport for writing: "+file);
            return new FileWriter(file);
        } catch(java.io.IOException e) {
            // Silently ignore IO exceptions
            ioError = e;
            return null;
        }
    }


    private FileReader openForReading() {
        Log.d("Open CrashReport for reading: "+file+" ("+file.length()+" bytes)");
        if ( file == null ) return null;
        try {
            return new FileReader(file);
        } catch(java.io.IOException e) {
            // Silently ignore IO exceptions
            ioError = e;
            return null;
        }
    }

    
    private void pickName() {
        if ( file != null ) return;

        File dir = getCrashDir(context);
        String name = "crash-"+System.currentTimeMillis();
        file = new File(dir, name);
    }




    /* ------------------------------------------------------------
     * Helper methods for adding info to the report
     * ------------------------------------------------------------ */

    private transient PackageManager pm = null;
    private transient PackageInfo pi = null ;
    private transient ApplicationInfo ai = null;


    private boolean getApplicationInfo() {
        if ( pm != null && 
             pi != null &&
             ai != null ) return true;

        try {
            pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),0);
            ai = pi.applicationInfo;
            return true;
        } catch(PackageManager.NameNotFoundException nnfe) {
            return false;
        }
    }


    public String getApplicationLabel() {
        if ( getApplicationInfo() ) {
            CharSequence label = pm.getApplicationLabel(ai);
            if ( label != null ) 
                return label.toString();
        }
        return "(unknown)";
    }


    public String getVersionName() {
        if ( getApplicationInfo() )
            return pi.versionName;
        else
            return "(unknown)";
    }

    
    public int getVersionCode() {
        if ( getApplicationInfo() )
            return pi.versionCode;
        else
            return 0;
    }


    public String getStackTrace(Throwable e) {
        String trace = "";

        Writer stack = new StringWriter();
        PrintWriter writer = new PrintWriter(stack);
        e.printStackTrace(writer);
        trace = stack.toString();

        Throwable cause = e.getCause();;
        while ( cause != null ) {
            trace += "\n";
            trace += "Cause:\n";
            cause.printStackTrace(writer);
            trace += stack.toString();
        }
         
        writer.close();

        return trace;
    }

}
