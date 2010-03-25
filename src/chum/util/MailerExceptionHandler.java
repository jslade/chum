package chum.util;

import chum.cfg.Config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Date;


/**
   A default exception handler that seends exception info via email.

   To send the email, the ACTION_SEND intent is used.  This gives the
   user a chance to see exactly what is being sent, and to possibly
   modify the crash report before it is sent.
*/
public class MailerExceptionHandler extends DefaultExceptionHandler 
{
    /** The context, needed to start the ACTION_SEND intent */
    protected Context context;

    /** The address to which mail messages are sent */
    protected String default_address;
    
    /** The subject line for mail messages */
    protected String default_subject;

    /** The header / explanation text to go at the
        beginning of mail messages */
    protected String default_intro;



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
        default_intro = cfg.get("exception_intro").toString();
    }


    /**
       Handle the exception by sending an email with data about the
       crash to the developer
    */
    @Override
    protected Throwable handleException(Thread t, Throwable e) {
        CrashReport report = createCrashReport(t,e);
        dispatchReport(report);
        return e;
    }


    /**
       Create the CrashReport for the given crash info.

    */
    protected CrashReport createCrashReport(Thread t, Throwable e) {
        CrashReport report = CrashReport.create(context);
        String contents = "";

        contents += "Crash Report collected on: " + (new Date()) + "\n";

        contents += "Package: " + context.getPackageName() + "\n";
        contents += "App: " + report.getApplicationLabel() + "\n";
        contents += "VersionName: " + report.getVersionName() + "\n";
        contents += "VersionCode: " + report.getVersionCode() + "\n";
        contents += "\n";

        contents += "Context: " + context + "\n";
        contents += "Thread: " + t + "\n";

        String appSpecific = createAppSpecificCrashReport(t,e);
        if ( appSpecific != null &&
             !appSpecific.equals("") ) {
            contents += "\n" + appSpecific + "\n\n";
        }

        contents += "Error:\n";
        contents += report.getStackTrace(e);
        

        report.contents = contents;
        return report;
    }

    
    protected String createAppSpecificCrashReport(Thread t, Throwable e) {
        return null;
    }


    protected void dispatchReport(CrashReport report) {
        // Unfortunately, mails cannot be sent from an app that
        // has crashed.  So instead, have to save the email for
        // future dispatch
        // sendEmail(email);
        report.save();
    }
        

    /**
       Dispatch a set of CrashReports that were previously generated.

       All reports are combined together into a single big email, 
       then sent
    */
    protected void dispatchReports(Context context, CrashReport[] reports) {
        Log.i("MailerExceptionHandler dispatching %d crash reports",
              reports.length);
        this.context = context; // just in case?
        
        Email email = new Email();
        email.address = default_address;
        email.subject = default_subject;
        email.body = default_intro + "\n\n";

        composeEmailAddress(context,reports,email);
        composeEmailSubject(context,reports,email);
        composeEmailIntro(context,reports,email);

        if ( reports.length > 1 ) {
            email.body += "*** " + reports.length +
                " combined crash reports\n";
        }
        
        for (int i=0; i<reports.length; ++i) {
            if ( reports.length > 1 )
                email.body += "\n----------------------------------------\n";
            email.body += reports[i].contents;
        }

        sendEmail(email);
    }


    protected void composeEmailAddress(Context context, CrashReport[] reports,
                                       Email email) {
    }


    protected void composeEmailSubject(Context context, CrashReport[] reports,
                                       Email email) {
        email.subject = default_subject + ": " + 
            reports[0].getApplicationLabel() +
            " (" + reports[0].getVersionName() + ")";
    }



    protected void composeEmailIntro(Context context, CrashReport[] reports,
                                     Email email) {

    }


    protected void sendEmail(Email email) {
        Log.i("MailerExceptionHandler sending email to %s", email.address);
        Log.d("Subject: "+email.subject);
        Log.d(email.body);

        Intent sendIntent = new Intent(context,
                                       CrashReportMailerActivity.class);
        sendIntent.putExtra(Intent.EXTRA_EMAIL, email.address);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, email.subject);
        sendIntent.putExtra(Intent.EXTRA_TEXT, email.body);
        context.startActivity(sendIntent);
    }


    static class Email {
        public Email() {}
        String address;
        String subject;
        String body;
    }

}



