package edu.colorado.phet.unfuddletool.gui;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import edu.colorado.phet.unfuddletool.TicketHandler;
import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.util.DateUtils;

public class TicketTableModel extends AbstractTableModel implements Ticket.TicketListener, TicketHandler.TicketAddListener {

    public static final int INDEX_LAST_MODIFIED = 0;
    public static final int INDEX_SUMMARY = 2;
    public static final int INDEX_NUMBER = 1;
    public static final int INDEX_COMPONENT = 3;

    private List<Ticket> tickets;

    private List<TicketTableDisplay> displays;

    public TicketTableModel() {
        tickets = new LinkedList<Ticket>();
        displays = new LinkedList<TicketTableDisplay>();

    }


    public void addTicket( Ticket ticket ) {
        ticket.addListener( this );

        if ( tickets.size() == 0 ) {
            tickets.add( ticket );
            fireTableDataChanged();
            return;
        }

        ListIterator<Ticket> iter = tickets.listIterator();
        Date ticketDate = ticket.lastUpdateTime();

        while ( iter.hasNext() ) {
            Ticket otherTicket = iter.next();

            // if ticket is more recent than the one in the list, insert it before
            if ( ticketDate.compareTo( otherTicket.lastUpdateTime() ) > 0 ) {
                iter.previous();
                iter.add( ticket );
                fireTableRowsInserted( iter.previousIndex(), iter.previousIndex() );
                return;
            }
        }

        // all other tickets are more recent, insert at the end
        iter.add( ticket );
        fireTableRowsInserted( iter.previousIndex(), iter.previousIndex() );
    }

    public void removeTicket( Ticket ticket ) {
        ticket.removeListener( this );

        int index = tickets.indexOf( ticket );
        tickets.remove( index );
        fireTableRowsDeleted( index, index );
    }

    public void changeTicket( Ticket ticket ) {
        int oldIndex = getTicketIndex( ticket );
        LinkedList<TicketTableDisplay> changedDisplays = new LinkedList<TicketTableDisplay>();
        Iterator<TicketTableDisplay> firstIter = displays.iterator();
        while ( firstIter.hasNext() ) {
            TicketTableDisplay display = firstIter.next();
            if ( display.getSelectedIndex() == oldIndex ) {
                changedDisplays.add( display );
            }
        }

        removeTicket( ticket );
        addTicket( ticket );

        int newIndex = getTicketIndex( ticket );

        Iterator<TicketTableDisplay> secondIter = changedDisplays.iterator();
        while ( secondIter.hasNext() ) {
            System.out.println( "Updating display" );
            TicketTableDisplay display = secondIter.next();
            display.clearSelection();
            display.setSelectedIndex( newIndex );
        }
    }

    public Ticket getTicketAt( int index ) {
        return tickets.get( index );
    }

    public int getTicketIndex( Ticket ticket ) {
        ListIterator<Ticket> iter = tickets.listIterator();

        while ( iter.hasNext() ) {
            Ticket listedTicket = iter.next();
            if ( ticket == listedTicket ) {
                return iter.previousIndex();
            }
        }

        return -1;
    }

    public int getRowCount() {
        return tickets.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName( int column ) {
        switch( column ) {
            case INDEX_LAST_MODIFIED:
                return "Last Modified";
            case INDEX_SUMMARY:
                return "Summary";
            case INDEX_NUMBER:
                return "Number";
            case INDEX_COMPONENT:
                return "Component";
        }

        return "Unknown";
    }

    public Class getColumnClass( int column ) {
        switch( column ) {
            case INDEX_SUMMARY:
                return Ticket.class;
        }

        return Object.class;
    }

    public Object getValueAt( int i, int j ) {
        Ticket ticket = tickets.get( i );

        switch( j ) {
            case INDEX_LAST_MODIFIED:
                return DateUtils.compactDate( ticket.lastUpdateTime() );
            case INDEX_SUMMARY:
                //return ticket.getSummary();
                return ticket;
            case INDEX_NUMBER:
                return "#" + ticket.getNumber();
            case INDEX_COMPONENT:
                return ticket.getComponentName();
        }

        return null;
    }

    public void onTicketUpdate( Ticket ticket ) {
        changeTicket( ticket );
    }

    public void onTicketAdded( Ticket ticket ) {
        addTicket( ticket );
    }


    public void addDisplay( TicketTableDisplay display ) {
        displays.add( display );
    }

    public void removeDisplay( TicketTableDisplay display ) {
        displays.remove( display );
    }

    public interface TicketTableDisplay {
        public int getSelectedIndex();

        public void setSelectedIndex( int index );

        public void clearSelection();
    }
}
