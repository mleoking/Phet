/************************************************
 Copyright 2004,2005 Markus Gebhard, Jeff Chapman

 This file is part of BrowserLauncher2.

 BrowserLauncher2 is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 BrowserLauncher2 is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with BrowserLauncher2; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 ************************************************/
// $Id$
package edu.stanford.ejalbert.launching.windows;

import net.sf.wraplog.AbstractLogger;

/**
 * @author Markus Gebhard
 */
public class WindowsNtBrowserLaunching
        extends DefaultWindowsBrowserLaunching {
    private static final String browserCommand = "cmd.exe";

    public WindowsNtBrowserLaunching( AbstractLogger logger ) {
        super( logger );
        logger.info( "WindowsNT launcher" );
    }

    /**
     * Command args for Windows NT type.
     *
     * @param protocol  String
     * @param urlString String
     * @return String[]
     */
    protected String[] getCommandArgs( String protocol,
                                       String urlString ) {
        // no differences based on protocol
        return new String[]{
                browserCommand,
                FIRST_WINDOWS_PARAMETER,
                SECOND_WINDOWS_PARAMETER,
                THIRD_WINDOWS_PARAMETER,
                '"' + urlString + '"'};
    }

    /**
     * Command args for Windows NT type.
     *
     * @param protocol    String
     * @param browserName String
     * @param urlString   String
     * @return String[]
     */
    protected String[] getCommandArgs( String protocol,
                                       String browserName,
                                       String urlString ) {
        // no differences based on protocol
        return new String[]{
                browserCommand,
                FIRST_WINDOWS_PARAMETER,
                SECOND_WINDOWS_PARAMETER,
                browserName,
                '"' + urlString + '"'};
    }
}
