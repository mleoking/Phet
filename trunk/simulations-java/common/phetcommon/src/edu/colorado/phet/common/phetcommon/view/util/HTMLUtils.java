package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;



public class HTMLUtils {
    
    private static final Font DEFAULT_FONT = new PhetFont();
    private static final String DEFAULT_CSS = "<head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>";
    
    public static String createStyledHTMLFromFragment( String htmlFragment ) {
        return createStyledHTMLFromFragment( htmlFragment, DEFAULT_FONT, DEFAULT_CSS );
    }
    
    public static String createStyledHTMLFromFragment( String htmlFragment, Font font ) {
        return createStyledHTMLFromFragment( htmlFragment, font, DEFAULT_CSS );
    }
    
    public static String createStyledHTMLFromFragment( String htmlFragment, Font font, String css ) {
        String html = "<html>" + css + htmlFragment + "</html>";
        html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
        return html;
    }
    
    public static class InteractiveHTMLPane extends JEditorPane {
        public InteractiveHTMLPane( String html ) {
            setEditorKit( new HTMLEditorKit() );
            setText( html );
            setEditable( false );
            setBackground( new JPanel().getBackground() );
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        PhetServiceManager.showWebPage( e.getURL() );
                    }
                }
            } );
        }
    }
}
