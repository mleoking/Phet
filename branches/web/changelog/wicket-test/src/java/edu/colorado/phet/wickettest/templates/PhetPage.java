package edu.colorado.phet.wickettest.templates;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.components.StaticImage;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.menu.NavMenu;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;
import edu.colorado.phet.wickettest.util.PhetSession;

public abstract class PhetPage extends WebPage {

    private Locale myLocale;
    private String prefix;
    private String path;
    private String variation;

    public PhetPage( PageParameters parameters ) {
        this( parameters, true );
    }

    public PhetPage( PageParameters parameters, boolean addTemplateBindings ) {

        if ( parameters.get( "locale" ) != null ) {
            myLocale = (Locale) parameters.get( "locale" );
        }
        else {
            // try again with localeString, but use english as default
            myLocale = LocaleUtils.stringToLocale( parameters.getString( "localeString", "en" ) );
        }

        if ( parameters.getString( "prefixString" ) != null ) {
            prefix = parameters.getString( "prefixString" );
        }
        else {
            prefix = "/" + LocaleUtils.localeToString( myLocale ) + "/";
        }

        path = parameters.getString( "path" );

        // should usually default to null
        variation = parameters.getString( "variation" );

        Session wicketSession = getSession();
        wicketSession.setLocale( myLocale );


        System.out.println( "Loading " + this.getClass().getCanonicalName() + " with Locale: " + LocaleUtils.localeToString( myLocale ) );
        System.out.println( "prefix of this page is: " + prefix );
        System.out.println( "path of this page is: " + path );
        System.out.println( "Session id is: " + wicketSession.getId() );

        for ( Object o : parameters.keySet() ) {
            System.out.println( "[" + o.toString() + "] = " + parameters.get( o ).toString() );
        }

        // visual display
        if ( addTemplateBindings ) {
            // TODO: refactor static images to a single location, so paths / names can be quickly changed
            Link link = IndexPage.createLink( "page-header-home-link", getPageContext() );
            add( link );
            link.add( new StaticImage( "page-header-logo-image", "/images/phet-logo.gif", null ) );
            add( new StaticImage( "page-header-title-image", "/images/logo-title.jpg", null ) );

            add( HeaderContributor.forCss( "/css/phetpage-v1.css" ) );

            if ( prefix.startsWith( "/translation" ) && getVariation() != null ) {
                org.hibernate.Session session = getHibernateSession();
                Translation translation = null;
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    translation = (Translation) session.load( Translation.class, Integer.valueOf( getVariation() ) );

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
                add( new Label( "translation-preview-notification", "This is a preview for translation #" + getVariation() +
                                                                    " of " + translation.getLocale().getDisplayName() +
                                                                    " (" + LocaleUtils.localeToString( translation.getLocale() ) +
                                                                    ")" ) );
            }
            else {
                Label label = new Label( "translation-preview-notification", "UNSEEN2" );
                label.setVisible( false );
                add( label );
            }

            // TODO: change sign out link to a mini panel with options (one of which will be to sign out)
            final PhetSession psession = PhetSession.get();
            if ( psession != null && psession.isSignedIn() ) {
                add( new Link( "sign-out" ) {
                    public void onClick() {
                        psession.invalidate();
                        setResponsePage( IndexPage.class );
                    }
                } );
            }
            else {
                Label label = new Label( "sign-out", "INVISIBLE" );
                label.setVisible( false );
                add( label );
            }
        }

        System.out.println( "request cycle is a : " + getRequestCycle().getClass().getSimpleName() );
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public String getMyPrefix() {
        return prefix;
    }

    public String getMyPath() {
        if ( path == null ) {
            return "";
        }
        return path;
    }

    public PageContext getPageContext() {
        return new PageContext( getMyPrefix(), getMyPath(), getMyLocale() );
    }

    @Override
    public Locale getLocale() {
        return myLocale;
    }

    public void addTitle( String title ) {
        add( new Label( "page-title", title ) );
    }

    public void addTitle( IModel title ) {
        add( new Label( "page-title", title ) );
    }

    public org.hibernate.Session getHibernateSession() {
        return ( (PhetRequestCycle) getRequestCycle() ).getHibernateSession();
    }

    public NavMenu getNavMenu() {
        return ( (WicketApplication) getApplication() ).getMenu();
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        System.out.println( "Debug: page stateless = " + isPageStateless() );
    }

    @Override
    protected void onDetach() {
        System.out.println( "Detaching page" );
        super.onDetach();
    }

    @Override
    public String getVariation() {
        return variation;
    }
}