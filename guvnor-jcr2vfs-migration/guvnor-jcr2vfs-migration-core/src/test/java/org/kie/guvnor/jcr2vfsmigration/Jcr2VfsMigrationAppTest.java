package org.kie.guvnor.jcr2vfsmigration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class Jcr2VfsMigrationAppTest {

    @Test
    public void migrate() throws IOException {
        File testBaseDir = new File("target/test/" + getClass().getSimpleName());
        if (testBaseDir.exists()) {
            FileUtils.deleteDirectory(testBaseDir);
        }
        testBaseDir.mkdirs();
        testBaseDir = testBaseDir.getCanonicalFile();
        File inputJcrRepository = new File(testBaseDir, "inputJcr");
        inputJcrRepository.mkdir();
        unzip(getClass().getResource("mortgageExampleJcr.zip"), inputJcrRepository);
        File outputVfsRepository = new File(testBaseDir, "outputVfs");

        Jcr2VfsMigrationApp.main(
                "-i", inputJcrRepository.getCanonicalPath(),
                "-o", outputVfsRepository.getCanonicalPath());

    }

    private void unzip(URL resource, File outputDir) throws IOException {
        assertNotNull(resource);
        File file = new File(outputDir, resource.getFile().replaceAll(".*/", ""));
        copyAndClose(resource.openStream(), new FileOutputStream(file));
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File entryDestination = new File(outputDir,  entry.getName());
            entryDestination.getParentFile().mkdirs();
            if (entryDestination.isDirectory()) {
                entryDestination.mkdir();
            } else {
                copyAndClose(zipFile.getInputStream(entry), new FileOutputStream(entryDestination));
            }
        }
    }

    private void copyAndClose(InputStream in, OutputStream out) throws IOException {
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

}
