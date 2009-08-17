package edu.colorado.phet.wickettest.menu;

import java.util.List;
import java.util.Set;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.util.PageContext;

public class NavMenuList extends Panel {

    public NavMenuList( String id, final PageContext context, List<NavLocation> locations, final Set<NavLocation> currentLocations, final int level ) {
        super( id );

        ListView listView = new ListView( "items", locations ) {
            protected void populateItem( ListItem item ) {
                NavLocation location = (NavLocation) item.getModel().getObject();
                Link link = location.getLink( "link", context );

                Label label = new Label( "link-label", new ResourceModel( location.getLocalizationKey() ) );

                boolean open = false;

                if ( currentLocations != null ) {
                    for ( NavLocation currentLocation : currentLocations ) {
                        if ( !open ) {
                            open = currentLocation.isUnderLocation( location );
                        }
                        if ( currentLocation.getBaseKey().equals( location.getKey() ) || currentLocation.getKey().equals( location.getKey() ) ) {
                            label.add( new AttributeAppender( "class", new Model( "selected" ), " " ) );
                            break;
                        }
                    }
                }

                link.add( label );

                item.add( link );

                if ( location.getChildren().isEmpty() || !open ) {
                    Label placeholder = new Label( "children", "BOO" );
                    placeholder.setVisible( false );
                    item.add( placeholder );
                }
                else {
                    item.add( new NavMenuList( "children", context, location.getChildren(), currentLocations, level + 1 ) );
                }

                link.add( new AttributeAppender( "class", new Model( "nav" + level ), " " ) );

            }
        };
        add( listView );

        add( HeaderContributor.forCss( "/css/navmenu-v1.css" ) );
    }
}
