package edu.colorado.phet.wickettest.panels;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.components.StaticImage;
import edu.colorado.phet.wickettest.data.Keyword;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import static edu.colorado.phet.wickettest.util.HtmlUtils.encode;
import edu.colorado.phet.wickettest.util.PageContext;

public class SimulationMainPanel extends PhetPanel {

    public SimulationMainPanel( String id, LocalizedSimulation simulation, final PageContext context ) {
        super( id, context );

        String simulationVersionString = simulation.getSimulation().getProject().getVersionString();

        add( new Label( "simulation-main-title", simulation.getTitle() ) );

        PhetLink link = new PhetLink( "simulation-main-link-run-main", simulation.getRunUrl() );
        // TODO: localize
        link.add( new StaticImage( "simulation-main-screenshot", simulation.getSimulation().getImageUrl(), null, new StringResourceModel( "simulationMainPanel.screenshot.alt", this, null, new String[]{encode( simulation.getTitle() )} ) ) );
        add( link );

        add( new Label( "simulation-main-description", simulation.getDescription() ) );
        add( new Label( "simulationMainPanel.version", new StringResourceModel( "simulationMainPanel.version", this, null, new String[]{simulationVersionString} ) ) );
        add( new Label( "simulationMainPanel.kilobytes", new StringResourceModel( "simulationMainPanel.kilobytes", this, null, new Object[]{simulation.getSimulation().getKilobytes()} ) ) );


        List<LocalizedSimulation> simulations = HibernateUtils.getLocalizedSimulationsMatching( getHibernateSession(), null, simulation.getSimulation().getName(), null );
        HibernateUtils.orderSimulations( simulations, context.getLocale() );

        List<IModel> models = new LinkedList<IModel>();

        // TODO: improve model?
        for ( final LocalizedSimulation sim : simulations ) {
            if ( !sim.getLocale().equals( simulation.getLocale() ) ) {
                models.add( new IModel() {
                    public Object getObject() {
                        return sim;
                    }

                    public void setObject( Object o ) {

                    }

                    public void detach() {

                    }
                } );
            }
        }

        // TODO: allow localization of locale display names
        ListView simulationList = new ListView( "simulation-main-translation-list", models ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation simulation = (LocalizedSimulation) ( ( (IModel) ( item.getModel().getObject() ) ).getObject() );
                Locale simLocale = simulation.getLocale();
                PhetLink link = new PhetLink( "simulation-main-translation-link", simulation.getRunUrl() );
                link.add( new Label( "simulation-main-translation-locale-name", simLocale.getDisplayName( context.getLocale() ) ) );
                item.add( link );
                item.add( new Label( "simulation-main-translation-locale-translated-name", simLocale.getDisplayName( simLocale ) ) );
                item.add( new Label( "simulation-main-translation-title", simulation.getTitle() ) );
            }
        };
        add( simulationList );

        List<Keyword> keywords = new LinkedList<Keyword>();

        Transaction tx = null;
        try {
            Session session = getHibernateSession();
            tx = session.beginTransaction();

            Simulation sim = (Simulation) session.load( Simulation.class, simulation.getSimulation().getId() );
            System.out.println( "Simulation keywords for " + sim.getName() );
            for ( Object o : sim.getKeywords() ) {
                Keyword keyword = (Keyword) o;
                keywords.add( keyword );
                System.out.println( keyword.getKey() );
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }

        ListView keywordList = new ListView( "keyword-list", keywords ) {
            protected void populateItem( ListItem item ) {
                Keyword keyword = (Keyword) item.getModel().getObject();
                Link link = new StatelessLink( "keyword-link" ) {
                    public void onClick() {
                        // TODO: fill in keyword links!
                    }
                };
                link.add( new Label( "keyword-label", new ResourceModel( keyword.getKey() ) ) );
                item.add( link );
            }
        };
        add( keywordList );
        if ( keywords.isEmpty() ) {
            keywordList.setVisible( false );
        }

        // so we don't emit an empty <table></table> that isn't XHTML Strict compatible
        if ( models.isEmpty() ) {
            simulationList.setVisible( false );
        }

        add( HeaderContributor.forCss( "/css/simulation-main-v1.css" ) );
    }

}