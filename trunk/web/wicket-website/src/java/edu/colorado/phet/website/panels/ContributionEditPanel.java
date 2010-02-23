package edu.colorado.phet.website.panels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.panels.lists.LevelSetManager;
import edu.colorado.phet.website.panels.lists.SimSetManager;
import edu.colorado.phet.website.panels.lists.SubjectSetManager;
import edu.colorado.phet.website.panels.lists.TypeSetManager;
import edu.colorado.phet.website.util.PageContext;

public class ContributionEditPanel extends PhetPanel {

    private final Contribution contribution;
    private boolean creating;

    private PageContext context;

    private static Logger logger = Logger.getLogger( ContributionEditPanel.class.getName() );

    private SimSetManager simManager;
    private TypeSetManager typeManager;
    private LevelSetManager levelManager;
    private SubjectSetManager subjectManager;

    public ContributionEditPanel( String id, PageContext context, Contribution preContribution ) {
        super( id, context );

        this.context = context;

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        contribution = preContribution;

        creating = contribution.getId() == 0;

        // initialize selectors

        simManager = new SimSetManager( getHibernateSession(), getLocale() ) {
            @Override
            public Set getInitialSimulations( Session session ) {
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    return activeContribution.getSimulations();
                }
                else {
                    return new HashSet();
                }
            }
        };

        typeManager = new TypeSetManager( getHibernateSession(), getLocale() ) {
            @Override
            public Set getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionTypes = activeContribution.getTypes();
                    for ( Object o : contributionTypes ) {
                        set.add( ( (ContributionType) o ).getType() );
                    }
                }
                return set;
            }
        };

        levelManager = new LevelSetManager( getHibernateSession(), getLocale() ) {
            public Collection<Level> getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionLevels = activeContribution.getLevels();
                    for ( Object o : contributionLevels ) {
                        set.add( ( (ContributionLevel) o ).getLevel() );
                    }
                }
                return set;
            }
        };

        subjectManager = new SubjectSetManager( getHibernateSession(), getLocale() ) {
            public Collection<Subject> getInitialValues( Session session ) {
                HashSet set = new HashSet();
                if ( contribution.getId() != 0 ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    Set contributionSubjects = activeContribution.getSubjects();
                    for ( Object o : contributionSubjects ) {
                        set.add( ( (ContributionSubject) o ).getSubject() );
                    }
                }
                return set;
            }
        };

        add( new ContributionForm( "contributionform" ) );

        add( new FeedbackPanel( "feedback" ) );
    }

    public final class ContributionForm extends Form {

        private RequiredTextField authorsText;
        private RequiredTextField organizationText;
        private RequiredTextField emailText;
        private RequiredTextField titleText;
        private RequiredTextField keywordsText;

        private TextArea descriptionText;

        public ContributionForm( String id ) {
            super( id );

            authorsText = new RequiredTextField( "contribution.edit.authors", new Model( creating ? "" : contribution.getAuthors() ) );
            add( authorsText );

            organizationText = new RequiredTextField( "contribution.edit.organization", new Model( creating ? "" : contribution.getAuthorOrganization() ) );
            add( organizationText );

            emailText = new RequiredTextField( "contribution.edit.email", new Model( creating ? "" : contribution.getContactEmail() ) );
            add( emailText );

            titleText = new RequiredTextField( "contribution.edit.title", new Model( creating ? "" : contribution.getTitle() ) );
            add( titleText );

            keywordsText = new RequiredTextField( "contribution.edit.keywords", new Model( creating ? "" : contribution.getKeywords() ) );
            add( keywordsText );

            descriptionText = new TextArea( "contribution.edit.description", new Model( creating ? "" : contribution.getDescription() ) );
            add( descriptionText );

            add( simManager.getComponent( "simulations", context ) );
            add( typeManager.getComponent( "types", context ) );
            add( levelManager.getComponent( "levels", context ) );

            add( subjectManager.getComponent( "subjects", context ) );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            logger.debug( "Main submission" );

            logger.debug( "authors: " + authorsText.getModelObjectAsString() );

            for ( Simulation simulation : simManager.getSimulations() ) {
                logger.debug( "simulation: " + simulation.getName() );
            }

            for ( Type type : typeManager.getValues() ) {
                logger.debug( "type: " + type );
            }

            for ( Level level : levelManager.getValues() ) {
                logger.debug( "level: " + level );
            }

            for ( Subject subject : subjectManager.getValues() ) {
                logger.debug( "subject: " + subject );
            }

        }

        @Override
        protected void delegateSubmit( IFormSubmittingComponent submittingComponent ) {
            // we must override the delegate submit because we will have child ajax buttons to handle adding / removing
            // things like the simulations. they should NOT trigger a full form submission.

            // yes this is ugly, maybe nested forms could be used?

            logger.debug( "submit delegation from: " + submittingComponent );

            if ( submittingComponent == null ) {
                // submit like normal
                super.delegateSubmit( submittingComponent );
            }
            else {
                submittingComponent.onSubmit();
            }
        }
    }


}
