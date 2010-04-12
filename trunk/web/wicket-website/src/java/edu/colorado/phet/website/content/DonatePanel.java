package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.content.contribution.ContributePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class DonatePanel extends PhetPanel {
    public DonatePanel( String id, PageContext context ) {
        super( id, context );

//        add( new LocalizedText( "contribute-main", "contribute.main", new Object[]{
//                "http://www.cufund.org/giving-opportunities/fund-description/?id=3685"
//        } ) );
//
//        add( new LocalizedText( "contribute-thanks", "contribute.thanks", new Object[]{
//                AboutSponsorsPanel.getLinker().getHref( context, getPhetCycle() ),
//                "href=\"http://www.royalinteractive.com/\""
//        } ) );
    }

    public static String getKey() {
        return "donate";
    }

    public static String getUrl() {
        return "donate";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, DonatePanel.class ) ) {
                    return "http://phet.colorado.edu/contribute/donate.php";
                }
                else {
                    return super.getRawUrl( context, cycle );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}