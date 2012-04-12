/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.maven.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileDownloadUtil {

    private FileDownloadUtil() {
    }

    private final static int BUFFER_SIZE = 1024;
    private static final Logger log = LoggerFactory.getLogger(FileDownloadUtil.class);

    public static File downloadFile(String downloadURL, final String fullPathToLocalFile) {
        OutputStream outStream = null;
        final URLConnection uCon;

        InputStream is = null;
        try {
            final URL url;
            final byte[] buf;
            int byteRead = 0;
            //url = new URL(downloadURL);
            url = new URL("file", null, -1, downloadURL);
            uCon = url.openConnection();
            is = uCon.getInputStream();

            outStream = new BufferedOutputStream(new FileOutputStream(fullPathToLocalFile));
            buf = new byte[BUFFER_SIZE];
            while ((byteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, byteRead);
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        final File resultFile = new File(fullPathToLocalFile);

        if (!resultFile.exists()) {
            return null;
        }

        return resultFile;
    }

}
