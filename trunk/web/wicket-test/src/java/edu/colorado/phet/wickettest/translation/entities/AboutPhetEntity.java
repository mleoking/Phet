package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.about.AboutMainPanel;
import edu.colorado.phet.wickettest.content.about.AboutSourceCodePanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class AboutPhetEntity extends TranslationEntity {
    public AboutPhetEntity() {
        addString( "about.p1", "The text surrounded with <a {0}> and </a> will be linked to the main PhET site, and the text surrounded with <a {1}> and </a> will be linked to the research page on the PhET site." );
        addString( "about.p2" );
        addString( "about.p3", "The text surrounded with <a {0}> and </a> will be linked to a page about PhET's rating system." );
        addString( "about.p4", "The text surrounded with <a {0}> and </a> will be linked to the main PhET site, and the text surrounded with <a {1}> and </a> will be linked to a page about Java technical support, and text surrounded with <a {2}> and </a> will be linked to a page about Flash technical support." );

        addString( "about.source-code.header" );
        addString( "about.source-code.location" );
        addString( "about.source-code.flash-simulations" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutMainPanel( id, context );
            }
        }, "About (main)" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutSourceCodePanel( id, context );
            }
        }, "About (source code)" );
    }

    public String getDisplayName() {
        return "About";
    }
}
