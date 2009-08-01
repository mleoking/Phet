package edu.colorado.phet.wickettest.translation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public abstract class TranslationEntity implements Serializable {
    private List<TranslationEntityString> strings = new LinkedList<TranslationEntityString>();
    private List<PhetPanelPreview> previews = new LinkedList<PhetPanelPreview>();

    protected void addString( String key ) {
        strings.add( new TranslationEntityString( key ) );
    }

    protected void addString( String key, String notes ) {
        strings.add( new TranslationEntityString( key, notes ) );
    }

    protected void addPreview( final PhetPanelFactory factory, final String name ) {
        previews.add( new PhetPanelPreview() {
            public String getName() {
                return name;
            }

            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return factory.getNewPanel( id, context, requestCycle );
            }
        } );
    }

    public boolean hasPreviews() {
        return !previews.isEmpty();
    }

    public List<PhetPanelPreview> getPreviews() {
        return previews;
    }

    public List<TranslationEntityString> getStrings() {
        return strings;
    }
}
