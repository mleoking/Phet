package edu.colorado.phet.wickettest.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.menu.NavLocation;
import edu.colorado.phet.wickettest.panels.SideNavMenu;
import edu.colorado.phet.wickettest.panels.SponsorsPanel;
import edu.colorado.phet.wickettest.panels.TranslationLinksPanel;

public abstract class PhetMenuPage extends PhetPage {
    public PhetMenuPage( PageParameters parameters ) {
        super( parameters, true );

        add( new SponsorsPanel( "sponsors-panel", getPageContext() ) );
        add( new TranslationLinksPanel( "translation-links", getPageContext() ) );

    }

    public void initializeLocation( NavLocation currentLocation ) {
        Set<NavLocation> currentLocations = new HashSet<NavLocation>();
        currentLocations.add( currentLocation );
        initializeLocationWithSet( currentLocations );
    }

    public void initializeLocationWithSet( Set<NavLocation> currentLocations ) {
        add( new SideNavMenu( "side-navigation", getPageContext(), currentLocations ) );
    }
}
