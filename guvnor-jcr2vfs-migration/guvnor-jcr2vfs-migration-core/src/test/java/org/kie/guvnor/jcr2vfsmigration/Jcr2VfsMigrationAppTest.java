package org.kie.guvnor.jcr2vfsmigration;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class Jcr2VfsMigrationAppTest {

    @Test
    public void migrate() throws IOException {
        File testBaseDir = new File("target/test/" + getClass().getSimpleName());
        testBaseDir.mkdirs();
        testBaseDir = testBaseDir.getCanonicalFile();
        File inputJcrRepository = new File(testBaseDir, "inputJcr");
        File outputVfsRepository = new File(testBaseDir, "outputVfs");

        Jcr2VfsMigrationApp.main(
                "-i", inputJcrRepository.getCanonicalPath(),
                "-o", outputVfsRepository.getCanonicalPath());

    }

}
