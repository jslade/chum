package chum.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class CrashReportMailerActivity extends Activity {
    private static final int SEND = 1;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);

        //Log.d("CrashReportMailerActivity onCreate()");

        Intent startIntent = getIntent();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { 
                                startIntent.getStringExtra(Intent.EXTRA_EMAIL)
                            });
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                            startIntent.getStringExtra(Intent.EXTRA_SUBJECT));
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                            startIntent.getStringExtra(Intent.EXTRA_TEXT));
        
        startActivityForResult(Intent.createChooser(sendIntent,"Title:"),
                               SEND);
    }
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {     
        super.onActivityResult(requestCode, resultCode, data); 
        finish();
    }
}

