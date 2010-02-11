package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.ContributePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class ContributeEntity extends TranslationEntity {
    public ContributeEntity() {
        addString( "contribute.financialContributions" );
        addString( "contribute.main" );
        addString( "contribute.thanks" );

        addString( "teacherIdeas.title" );

        addString( "contribution.level.k5" );
        addString( "contribution.level.middleSchool" );
        addString( "contribution.level.highSchool" );
        addString( "contribution.level.undergraduateIntro" );
        addString( "contribution.level.undergraduateAdvanced" );
        addString( "contribution.level.graduate" );
        addString( "contribution.level.other" );
        addString( "contribution.level.k5.abbrev" );
        addString( "contribution.level.middleSchool.abbrev" );
        addString( "contribution.level.highSchool.abbrev" );
        addString( "contribution.level.undergraduateIntro.abbrev" );
        addString( "contribution.level.undergraduateAdvanced.abbrev" );
        addString( "contribution.level.graduate.abbrev" );
        addString( "contribution.level.other.abbrev" );
        addString( "contribution.subject.astronomy" );
        addString( "contribution.subject.biology" );
        addString( "contribution.subject.chemistry" );
        addString( "contribution.subject.earthScience" );
        addString( "contribution.subject.mathematics" );
        addString( "contribution.subject.physics" );
        addString( "contribution.subject.other" );
        addString( "contribution.subject.astronomy.abbrev" );
        addString( "contribution.subject.biology.abbrev" );
        addString( "contribution.subject.chemistry.abbrev" );
        addString( "contribution.subject.earthScience.abbrev" );
        addString( "contribution.subject.mathematics.abbrev" );
        addString( "contribution.subject.physics.abbrev" );
        addString( "contribution.subject.other.abbrev" );
        addString( "contribution.type.lab" );
        addString( "contribution.type.homework" );
        addString( "contribution.type.conceptQuestions" );
        addString( "contribution.type.demonstration" );
        addString( "contribution.type.other" );
        addString( "contribution.type.lab.abbrev" );
        addString( "contribution.type.homework.abbrev" );
        addString( "contribution.type.conceptQuestions.abbrev" );
        addString( "contribution.type.demonstration.abbrev" );
        addString( "contribution.type.other.abbrev" );
        addString( "contribution.duration" );

        addString( "contribution.answers.yes" );
        addString( "contribution.answers.no" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ContributePanel( id, context );
            }
        }, "Contribute" );
    }

    public String getDisplayName() {
        return "Contribute";
    }
}