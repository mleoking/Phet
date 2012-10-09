// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.circuitconstructionkit.controls;


import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Has an expand and collapse button.
 */

public class AdvancedPanel extends VerticalLayoutPanel {

    private JButton showButton;
    private JButton hideButton;
    private VerticalLayoutPanel controls;

    /**
     * Create an AdvancedPanel with text Advanced >> and Hide <<.
     */
    public AdvancedPanel() {
        this("Advanced >>", "Hide <<");
    }

    /**
     * Create an AdvancedPanel with specified expansion and collapse button labels.
     *
     * @param show
     * @param hide
     */
    public AdvancedPanel(String show, String hide) {
        controls = new VerticalLayoutPanel();
        controls.setFillNone();

        showButton = new JButton(show);
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAdvanced();
            }
        });

        hideButton = new JButton(hide);
        hideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hideAdvanced();
            }
        });
        setFillNone();
        add(showButton);
        add(hideButton);
        setFillHorizontal();
        add(controls);

        controls.setVisible(false);
        hideButton.setVisible(false);
    }

    /**
     * Add another control to the expanded Advanced Panel.
     *
     * @param control
     */
    public void addControl(JComponent control) {
        controls.add(control);
    }

    /**
     * Add another control to the expanded Advanced Panel, to span the width.
     *
     * @param control
     */
    public void addControlFullWidth(JComponent control) {
        controls.addFullWidth(control);
    }

    private void showAdvanced() {
        showButton.setVisible(false);
        controls.setVisible(true);
        hideButton.setVisible(true);
        validateAll();
        for (int i = 0; i < listeners.size(); i++) {
            Listener listener = (Listener) listeners.get(i);
            listener.advancedPanelShown(this);
        }
        validateAll();
        validate();
    }

    private void validateAll() {
        invalidate();
        controls.invalidate();
        if (getParent() != null) {
            getParent().invalidate();
            if (getParent().getParent() != null) {
                getParent().getParent().invalidate();
                getParent().getParent().validate();
            }
        }
        Window parent = SwingUtilities.getWindowAncestor(this);
        parent.invalidate();
        parent.validate();
        parent.repaint();
    }

    private void hideAdvanced() {
        hideButton.setVisible(false);
        controls.setVisible(false);
        showButton.setVisible(true);
        validateAll();
        for (int i = 0; i < listeners.size(); i++) {
            Listener listener = (Listener) listeners.get(i);
            listener.advancedPanelHidden(this);
        }
        validateAll();
        validate();
    }

    ArrayList listeners = new ArrayList();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public interface Listener {
        void advancedPanelHidden(AdvancedPanel advancedPanel);

        void advancedPanelShown(AdvancedPanel advancedPanel);
    }

    public VerticalLayoutPanel getControls() {
        return controls;
    }
}