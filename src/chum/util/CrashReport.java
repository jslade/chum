package chum.util;

import android.content.Context;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


/**
   Helper class that manages storing and retreiving data for the various
   ExceptionHandlers.

   The crash reports are stored in files in the private area for the
   current app.  The methods here do not care about the content of the
   file.  These are just utilities to handle naming, creating,
   retreiving, and deleting the files.
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
        FileWriter output = openForWriting();
        if ( output != null ) {
            try {
                this.contents = contents;
                output.write(this.contents);
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
        
    
    public void delete() {
        if ( file == null ) return;
        file.delete();
    }


    private FileWriter openForWriting() {
        pickName();
        try {
            return new FileWriter(file);
        } catch(java.io.IOException e) {
            // Silently ignore IO exceptions
            ioError = e;
            return null;
        }
    }


    private FileReader openForReading() {
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
}
