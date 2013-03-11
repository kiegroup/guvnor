package org.kie.guvnor.jcr2vfsmigration;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.kie.commons.java.nio.fs.jgit.JGitFileSystemProvider;
import org.kie.guvnor.jcr2vfsmigration.vfs.IOServiceFactory;

import java.io.File;
import java.io.IOException;


public class MigrationAppTest {

    @Test
    public void migrateExample() throws IOException {
        migrate("migrationExample");
    }

    private void verifyResult() {
        //testDTXLS.xls
        //testSrpingContext.springContext
        //testFunction.function
        //testChangeSet.changeset
        //testWorkItem.wid
        //? testServiceConfig.serviceConfig
        //? testRuleTemplate.template: use DRL instead?
        //? testWorkingSets.workingset

    }

    private void migrate(String datasetName) throws IOException {
        File testBaseDir = new File("target/test/" + datasetName);
        if (testBaseDir.exists()) {
            FileUtils.deleteDirectory(testBaseDir);
        }
        testBaseDir.mkdirs();
        testBaseDir = testBaseDir.getCanonicalFile();

        File outputVfsRepository = new File(testBaseDir, "outputVfs");
        
        //Hack: Force JGitFileSystemProvider to reload git root dir due to JUnit class loader problem
        System.setProperty("org.kie.nio.git.dir", outputVfsRepository.getCanonicalPath());
        JGitFileSystemProvider.loadConfig();
        //Hack: Force to create a new FileSystem
        IOServiceFactory.DEFAULT_MIGRATION_FILE_SYSTEM = "guvnor-jcr2vfs-migration-another";
        
        Jcr2VfsMigrationApp.main(
                "-i", getClass().getResource(datasetName + ".jcr").getFile(),
                "-o", outputVfsRepository.getCanonicalPath());
    }
}
