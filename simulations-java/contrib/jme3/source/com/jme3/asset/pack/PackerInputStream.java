/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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

package com.jme3.asset.pack;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PackerInputStream extends FilterInputStream {

    private ProgressListener listener;

    public PackerInputStream(InputStream in, ProgressListener listener){
        super(in);
        this.listener = listener;
    }

    public int read(byte[] buf, int off, int len) throws IOException{
        int read = super.read(buf, off, len);
        if (read > 0)
            listener.onProgress(read);
        return read;
    }

    public int read(byte[] buf) throws IOException{
        int read = super.read(buf);
        if (read > 0)
            listener.onProgress(read);
        return read;
    }

    @Override
    public int read() throws IOException{
        int read = super.read();
        if (read != -1)
            listener.onProgress(1);
        return read;
    }

    public long skip(long bytes) throws IOException{
        long skipped = super.skip(bytes);
        if (skipped > 0)
            listener.onProgress((int)skipped);
        return skipped;
    }

}
