package chum.util;

import chum.cfg.Config;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;


/**
   A default exception handler that seends exception info via email.

   To send the email, the XXX intent is used.  This gives the user
   a chance to see exactly what is being sent, and to possibly modify
   the crash report before it is sent.
*/
public class MailerExceptionHandler extends DefaultExceptionHandler 
{
    /** The context, needed to start the ACTION_SEND intent */
    protected Context context;

    /** The address to which mail messages are sent */
    protected String default_address;
    
    /** The subject line for mail messages */
    protected String default_subject;



    /**

     */
    public MailerExceptionHandler(Context context) {
        super();
        this.context = context;
    }


    /**
       Load configuration from the Config instance.  This determines
       details such as the address to which the crash reports should
       be sent.
    */
    public void loadConfig(Config cfg) {
        default_address = cfg.get("exception_email").toString();
        default_subject = cfg.get("exception_subject").toString();
    }


    /**
       Handle the exception by sending an email with data about the
       crash to the developer
    */
    @Override
    protected Throwable handleException(Thread t, Throwable e) {
        Email email = composeEmail(t,e);
        sendEmail(email);
        return e;
    }

    
    protected Email composeEmail(Thread t, Throwable e) {
        Email email = new Email();
        email.address = default_address;
        email.subject = default_subject;
        email.body = "Test";
        return email;
    }


    protected void sendEmail(Email email) {
        Log.d("Send email to: "+email.address);
        Log.d("      subject: "+email.subject);
        Log.d(email.body);

        Intent sendIntent = new Intent(context,EmailActivity.class);
        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { 
                                email.address
                            });
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, email.subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, email.body);
        Log.d("sendEmail() A");
        context.startActivity(sendIntent);
        Log.d("sendEmail() B");
    }


    static class Email {
        public Email() {}
        String address;
        String subject;
        String body;
    }


    static class EmailActivity extends Activity {
        private static final int SEND = 1;

        @Override
        public void onCreate(Bundle saved) {
            Log.d("sendEmail() onCreate()");

            Intent sendIntent = (Intent)(getIntent().clone());
            sendIntent.setAction(Intent.ACTION_SEND);
            Log.d("sendEmail() created intent: "+sendIntent);
            
            startActivityForResult(sendIntent,SEND);
            Log.d("sendEmail() started activity");
        }

        @Override 
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent data) {     
            super.onActivityResult(requestCode, resultCode, data); 
            finish();
        }
    }

}

