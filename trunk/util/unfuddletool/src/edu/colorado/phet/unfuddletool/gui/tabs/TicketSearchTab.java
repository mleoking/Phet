package edu.colorado.phet.unfuddletool.gui.tabs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.gui.TicketDisplayPane;
import edu.colorado.phet.unfuddletool.gui.TicketTable;
import edu.colorado.phet.unfuddletool.gui.TicketTableModel;
import edu.colorado.phet.unfuddletool.util.TicketLoader;
import edu.colorado.phet.unfuddletool.util.TicketSearch;

public class TicketSearchTab extends JSplitPane {

    private static TicketTableModel model;

    public TicketTable ticketTable;
    public TicketDisplayPane ticketTableDisplay;
    public TicketDisplayPane ticketTableHeader;

    public TicketSearchTab() {
        // set up the model
        model = new TicketTableModel();
        //TicketHandler.getTicketHandler().addTicketAddListener( model );

        ticketTable = new TicketTable( model );
        JScrollPane ticketTableScrollPane = new JScrollPane( ticketTable );
        ticketTable.setFillsViewportHeight( true );
        ticketTableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );
        ticketTableDisplay = new TicketDisplayPane();
        ticketTableDisplay.setText( "Testing" );
        JScrollPane tableAreaScrollPane = new JScrollPane( ticketTableDisplay );
        tableAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        ticketTableHeader = new TicketDisplayPane();
        ticketTableHeader.setText( "Ticket Header" );
        final JSplitPane rightSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, ticketTableHeader, tableAreaScrollPane );
        rightSplitPane.setOneTouchExpandable( true );
        ticketTable.ticketSelectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if ( !event.getValueIsAdjusting() ) {
                    int[] indices = ticketTable.getSelectedRows();
                    if ( indices.length == 1 ) {
                        int index = ticketTable.convertRowIndexToModel( indices[0] );
                        Ticket ticket = model.getTicketAt( index );
                        ticketTableDisplay.setText( ticket.getHTMLComments() );
                        ticketTableHeader.setText( ticket.getHTMLHeader() );
                        rightSplitPane.setDividerLocation( -1 );
                    }
                }
            }
        } );


        JButton searchButton = new JButton( "Search" );
        final JTextField searchField = new JTextField( "" );
        searchField.setEditable( true );

        JPanel topLeftPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        topLeftPanel.add( searchField, c );
        c.gridx = 1;
        c.weightx = 0.0;
        topLeftPanel.add( searchButton, c );

        JPanel leftPanel = new JPanel( new BorderLayout() );
        leftPanel.add( topLeftPanel, BorderLayout.NORTH );
        leftPanel.add( ticketTableScrollPane, BorderLayout.CENTER );

        setLeftComponent( leftPanel );
        setRightComponent( rightSplitPane );


        searchButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.clear();
                List<Integer> ticketList = TicketSearch.getTicketSearchIDs( searchField.getText() );
                System.out.println( "Report found " + ticketList.size() + " tickets" );
                //model.addTicketIDList( ticketList );
                new TicketLoader( ticketList, model ).start();
            }
        } );

    }
}