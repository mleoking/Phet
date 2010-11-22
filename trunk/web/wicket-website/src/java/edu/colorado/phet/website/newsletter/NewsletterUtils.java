package edu.colorado.phet.website.newsletter;

import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.util.EmailUtils;

public class NewsletterUtils {

    private static final Logger logger = Logger.getLogger( NewsletterUtils.class );

    public static boolean sendConfirmSubscriptionEmail( String emailAddress, String confirmationKey ) {
        String confirmLink = EmailUtils.makeUrlAbsolute( SubscribeLandingPage.getLinker( confirmationKey ).getDefaultRawUrl() );

        String body = "<p>Thank you for subscribing to the PhET Newsletter!</p><p>Please click on this link:</p><p><a href=\"" + confirmLink + "\">" + confirmLink + "</a></p><p>or copy and paste the text into your browser in order to confirm your subscription. If you believe you have received this message in error, please <a href=\"mailto:" + WebsiteConstants.HELP_EMAIL + "\">let us know</a>.</p><p>" +
                      "<br/>Thanks, the PhET team</p>";
        String subject = "Please confirm your PhET subscription";
        try {
            EmailUtils.GeneralEmailBuilder message = new EmailUtils.GeneralEmailBuilder( subject, WebsiteConstants.HELP_EMAIL );
            message.setBody( body );
            message.addRecipient( emailAddress );
            message.addReplyTo( WebsiteConstants.HELP_EMAIL );
            return EmailUtils.sendMessage( message );
        }
        catch ( MessagingException e ) {
            logger.warn( "message send error: ", e );
            return false;
        }
    }

    public static void sendNewsletter( PhetUser user ) { // TODO: improve signature

    }

    public static void sendNewsletterWelcomeEmail( PhetUser user ) {

    }

    /**
     * Send when the user visits the UnsubscribeLandingPage
     *
     * @param user User
     */
    public static void sendUnsubscribedEmail( PhetUser user ) {

    }

}
