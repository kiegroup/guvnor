package org.kie.guvnor.jcr2vfsmigration;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class MigrationAppTest {

    @Test
    public void migrateExample() throws IOException {
        migrate("migrationExample");
    }

    private void verifyResult() {

    }

    private void migrate(String datasetName) throws IOException {
        File testBaseDir = new File("target/test/" + datasetName);
        if (testBaseDir.exists()) {
            FileUtils.deleteDirectory(testBaseDir);
        }
        testBaseDir.mkdirs();
        testBaseDir = testBaseDir.getCanonicalFile();

        File outputVfsRepository = new File(testBaseDir, "outputVfs");

        Jcr2VfsMigrationApp.main(
                "-i", getClass().getResource(datasetName + ".jcr").getFile(),
                "-o", outputVfsRepository.getCanonicalPath());
    }
}
