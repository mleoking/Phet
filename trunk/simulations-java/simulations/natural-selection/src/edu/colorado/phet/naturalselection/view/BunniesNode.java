package edu.colorado.phet.naturalselection.view;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;

public class BunniesNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    private ArrayList bunnyNodes;

    public BunniesNode() {
        bunnyNodes = new ArrayList();

    }


    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void reset() {
        Iterator iter = bunnyNodes.iterator();
        while ( iter.hasNext() ) {
            BunnyNode bunnyNode = (BunnyNode) iter.next();
            removeChild( bunnyNode );
        }
        bunnyNodes.clear();
    }

    public void onNewBunny( Bunny bunny ) {
        BunnyNode bunnyNode = new BunnyNode( bunny.getColorGenotype().getPhenotype(), bunny.getTeethGenotype().getPhenotype(), bunny.getTailGenotype().getPhenotype() );
        bunnyNode.setOffset( ( NaturalSelectionDefaults.VIEW_SIZE.getWidth() - NaturalSelectionConstants.SCALED_BUNNY_WIDTH ) * Math.random(), ( NaturalSelectionDefaults.VIEW_SIZE.getHeight() - NaturalSelectionConstants.SCALED_BUNNY_HEIGHT ) * Math.random() );
        addChild( bunnyNode );
        bunny.addListener( bunnyNode );
        bunnyNodes.add( bunnyNode );
    }
}
