/* $Id$
 *
 * ProGuard -- shrinking, optimization, and obfuscation of Java class files.
 *
 * Copyright (c) 2002-2005 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.obfuscate;

import proguard.classfile.*;
import proguard.classfile.visitor.ClassFileVisitor;

import java.util.Map;

/**
 * This ClassFileVisitor clears a given map whenever it visits a class file.
 *
 * @author Eric Lafortune
 */
public class MapCleaner implements ClassFileVisitor
{
    private Map map;


    /**
     * Creates a new MapCleaner.
     * @param map the map to be cleared.
     */
    public MapCleaner(Map map)
    {
        this.map = map;
    }


    // Implementations for ClassFileVisitor.

    public void visitProgramClassFile(ProgramClassFile programClassFile)
    {
        map.clear();
    }


    public void visitLibraryClassFile(LibraryClassFile libraryClassFile)
    {
        map.clear();
    }
}
