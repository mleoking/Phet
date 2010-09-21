/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.editors.swing.widget;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

public class ValuePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public ValueSpinner spinner;

    private JLabel plabel, slabel;
    public static Font labelFont = new Font("Arial", Font.BOLD, 13);

    public ValuePanel(String prefix, String suffix, float min, float max,
            float step) {
        add(plabel = createLabel(prefix));
        add(spinner = new ValueSpinner(min, max, step));
        add(slabel = createLabel(suffix));
    }

    public ValuePanel(String prefix, String suffix, int min, int max, int step) {
        add(plabel = createLabel(prefix));
        add(spinner = new ValueSpinner(min, max, step));
        add(slabel = createLabel(suffix));
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        plabel.setEnabled(enabled);
        spinner.setEnabled(enabled);
        slabel.setEnabled(enabled);
    }

    public void setValue(float value) {
        spinner.setValue(Float.valueOf(value));
    }

    public void setValue(int value) {
        spinner.setValue(Integer.valueOf(value));
    }

    public float getFloatValue() {
        return ((Number) spinner.getValue()).floatValue();
    }

    public int getIntValue() {
        return ((Number) spinner.getValue()).intValue();
    }

    public void addChangeListener(ChangeListener l) {
        spinner.addChangeListener(l);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        return label;
    }
}
