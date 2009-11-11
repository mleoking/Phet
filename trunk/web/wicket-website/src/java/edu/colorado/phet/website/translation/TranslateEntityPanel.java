package edu.colorado.phet.website.translation;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedLabel;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.test.TestTranslateString;
import edu.colorado.phet.website.translation.entities.TranslationEntity;
import edu.colorado.phet.website.util.*;

public class TranslateEntityPanel extends PhetPanel {

    private TranslationEntity entity;
    private int translationId;
    private PreviewHolder panel;
    private Map<String, IModel> stringModelMap = new HashMap<String, IModel>();
    private TranslationEditPage page;

    public TranslateEntityPanel( String id, final PageContext context, final TranslationEditPage page, final TranslationEntity entity, final int translationId, final Locale testLocale ) {
        super( id, context );
        this.entity = entity;
        this.translationId = translationId;
        this.page = page;

        final PageContext externalContext = new PageContext( "/translation/" + String.valueOf( translationId ) + "/", "", testLocale, (PhetRequestCycle) getRequestCycle() );

        setOutputMarkupId( true );

        add( new Label( "translation-id", String.valueOf( translationId ) ) );

        panel = new PreviewHolder( "panel", externalContext, entity );
        panel.setOutputMarkupId( true );
        add( panel );

        if ( entity.getMinDisplaySize() <= 225 ) {
            add( new AttributeAppender( "class", new Model( "preview-small" ), " " ) );
        }
        else if ( entity.getMinDisplaySize() <= 525 ) {
            add( new AttributeAppender( "class", new Model( "preview-medium" ), " " ) );
        }

        ListView stringList = new ListView( "translation-string-list", entity.getStrings() ) {
            protected void populateItem( final ListItem item ) {
                final TranslationEntityString tString = (TranslationEntityString) item.getModel().getObject();

                item.setOutputMarkupId( true );
                String markupId = "id_string_" + tString.getKey().replaceAll( "\\.", "_" );
                item.setMarkupId( markupId );

                String initString = getLocalizer().getString( tString.getKey(), TranslateEntityPanel.this );
                initString = initString.replaceAll( "<br/>", "\n" );
                final Model model = new Model( initString );
                stringModelMap.put( tString.getKey(), model );

                if ( tString.getNotes() == null ) {
                    Label label = new Label( "translation-string-notes", "UNSEEN" );
                    label.setVisible( false );
                    item.add( label );
                }
                else {
                    Label label = new Label( "translation-string-notes", tString.getNotes() );
                    item.add( label );
                }

                item.add( new Label( "translation-string-key", tString.getKey() ) );

                if ( testLocale.equals( PhetWicketApplication.getDefaultLocale() ) ) {
                    item.add( new InvisibleComponent( "translation-string-english" ) );
                }
                else {
                    item.add( new LocalizedLabel( "translation-string-english", PhetWicketApplication.getDefaultLocale(), new ResourceModel( tString.getKey() ) ) );
                }

                final AjaxEditableMultiLineLabel editableLabel = new AjaxEditableMultiLineLabel( "translation-string-value", model ) {
                    @Override
                    protected void onSubmit( AjaxRequestTarget target ) {
                        super.onSubmit( target );
                        int status = StringUtils.stringStatus( getHibernateSession(), tString.getKey(), translationId );
                        StringUtils.setString( getHibernateSession(), tString.getKey(), (String) model.getObject(), translationId );
                        //target.addComponent( TranslateEntityPanel.this );
                        target.addComponent( panel );
                        target.addComponent( item );
                        //page.getEntityListPanel().updateEntity( entity );
                        if ( status == StringUtils.STRING_OUT_OF_DATE ) {
                            entity.getOutOfDateMap().put( translationId, entity.getOutOfDateMap().get( translationId ) - 1 );
                        }
                        else if ( status == StringUtils.STRING_UNTRANSLATED ) {
                            entity.getUntranslatedMap().put( translationId, entity.getUntranslatedMap().get( translationId ) - 1 );
                        }
                        target.addComponent( page.getEntityListPanel() );
                        add( new AttributeModifier( "class", new Model( "string-value" ) ) );
                    }
                };
                editableLabel.setCols( 80 );
                if ( !isStringSet( tString.getKey() ) ) {
                    editableLabel.add( new AttributeAppender( "class", true, new Model( "not-translated" ), " " ) );
                }
                else if ( !isStringUpToDate( tString.getKey() ) ) {
                    editableLabel.add( new AttributeAppender( "class", true, new Model( "string-out-of-date" ), " " ) );
                }
                item.add( editableLabel );

                if ( ( (PhetSession) getSession() ).getUser().isTeamMember() ) {
                    //item.add( new InvisibleComponent( "translate-auto" ) );

                    // TODO: remove after development
                    item.add( new AjaxLink( "translate-auto" ) {
                        public void onClick( AjaxRequestTarget target ) {
                            String value = TestTranslateString.translate( (String) model.getObject(), "en", testLocale.getLanguage() );
                            int status = StringUtils.stringStatus( getHibernateSession(), tString.getKey(), translationId );
                            if ( value != null ) {
                                StringUtils.setString( getHibernateSession(), tString.getKey(), value, translationId );
                            }
                            target.addComponent( panel );
                            target.addComponent( item );
                            // TODO: consolidate with above functions
                            //page.getEntityListPanel().updateEntity( entity );
                            if ( value != null && status == StringUtils.STRING_OUT_OF_DATE ) {
                                entity.getOutOfDateMap().put( translationId, entity.getOutOfDateMap().get( translationId ) - 1 );
                            }
                            else if ( value != null && status == StringUtils.STRING_UNTRANSLATED ) {
                                entity.getUntranslatedMap().put( translationId, entity.getUntranslatedMap().get( translationId ) - 1 );
                            }
                            target.addComponent( page.getEntityListPanel() );
                            editableLabel.add( new AttributeModifier( "class", new Model( "string-value" ) ) );
                            model.setObject( value );
                        }
                    } );

                }
                else {
                    item.add( new InvisibleComponent( "translate-auto" ) );
                }
            }
        };
        add( stringList );


        Link popupLink = IndexPage.createLink( "translation-popup", externalContext );
        popupLink.setPopupSettings( new PopupSettings( PopupSettings.LOCATION_BAR | PopupSettings.MENU_BAR | PopupSettings.RESIZABLE
                                                       | PopupSettings.SCROLLBARS | PopupSettings.STATUS_BAR | PopupSettings.TOOL_BAR ) );
        add( popupLink );
    }

    public boolean isStringUpToDate( final String key ) {
        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                TranslatedString standard = (TranslatedString) session.createQuery( "select ts from TranslatedString as ts, Translation as t where ts.translation = t and t.visible = true and t.locale = :locale and ts.key = :key" )
                        .setLocale( "locale", PhetWicketApplication.getDefaultLocale() )
                        .setString( "key", key )
                        .uniqueResult();
                TranslatedString current = (TranslatedString) session.createQuery( "select ts from TranslatedString as ts, Translation as t where ts.translation = t and t.id = :id and ts.key = :key" )
                        .setInteger( "id", translationId )
                        .setString( "key", key )
                        .uniqueResult();
                return current.getUpdatedAt().compareTo( standard.getUpdatedAt() ) >= 0;
            }
        } );
        return success;
    }

    public boolean isStringSet( String key ) {
        Session session = getHibernateSession();
        Transaction tx = null;
        List results = null;
        try {
            tx = session.beginTransaction();

            Query query = session.createQuery( "select ts from TranslatedString as ts, Translation as t where (t.id = :id AND ts.translation = t AND ts.key = :key)" );
            query.setInteger( "id", translationId );
            query.setString( "key", key );
            results = query.list();

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

        if ( results == null || results.isEmpty() ) {
            return false;
        }
        return true;
    }

    @Override
    public String getVariation() {
        return new Integer( translationId ).toString();
    }

    private Long renderStart;

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        renderStart = System.currentTimeMillis();
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        System.out.println( "TranslateEntityPanel Render: " + ( System.currentTimeMillis() - renderStart ) + " ms" );
    }

}
