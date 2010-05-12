package edu.colorado.phet.website.content.search;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.SearchUtils;

public class SearchResultsPanel extends PhetPanel {
    public SearchResultsPanel( String id, final PageContext context, String query ) {
        super( id, context );

        List<LocalizedSimulation> lsims = new LinkedList<LocalizedSimulation>();
        List<Contribution> contributions = new LinkedList<Contribution>();

        if ( query != null ) {
            lsims = SearchUtils.simulationSearch( getHibernateSession(), query, context.getLocale() );
            contributions = SearchUtils.contributionSearch( getHibernateSession(), query, context.getLocale() );
        }

        add( new ListView( "sims", lsims ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                Link link = SimulationPage.getLinker( lsim ).getLink( "sim-link", context, getPhetCycle() );
                item.add( link );
                link.add( new Label( "sim-title", lsim.getTitle() ) );
            }
        } );
        add( new ListView( "contribs", contributions ) {
            protected void populateItem( ListItem item ) {
                Contribution contribution = (Contribution) item.getModel().getObject();
                Link link = ContributionPage.getLinker( contribution ).getLink( "contrib-link", context, getPhetCycle() );
                item.add( link );
                link.add( new Label( "contrib-title", contribution.getTitle() ) );
            }
        } );

        if ( query != null ) {
            add( new LocalizedText( "search-query", "search.query", new Object[]{lsims.size() + contributions.size(), query} ) );
        }
        else {
            add( new InvisibleComponent( "search-query" ) );
        }

    }
}