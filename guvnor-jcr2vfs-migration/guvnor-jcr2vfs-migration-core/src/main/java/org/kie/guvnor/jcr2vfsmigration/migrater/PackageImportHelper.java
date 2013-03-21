package org.kie.guvnor.jcr2vfsmigration.migrater;

import javax.inject.Inject;

import org.drools.guvnor.models.commons.backend.packages.PackageNameParser;
import org.drools.guvnor.models.commons.backend.packages.PackageNameWriter;
import org.drools.guvnor.models.commons.shared.packages.HasPackageName;
import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;

public class PackageImportHelper {    
    @Inject
    private ProjectService projectService;
    
    //TODO: duplicate the DRLTextEditorServiceImpl. But we can not move this helper method to a common place. as this depends on ProjectService.
    //Check if the DRL contains a Package declaration, appending one if it does not exist
    public String assertPackageName( final String drl,
                                     final Path resource ) {
        final String existingPackageName = PackageNameParser.parsePackageName( drl );
        if ( !"".equals( existingPackageName ) ) {
            return drl;
        }

        final String requiredPackageName = projectService.resolvePackageName( resource );
        System.out.println("===========: requiredPackageName:" + requiredPackageName);
        final HasPackageName mockHasPackageName = new HasPackageName() {

            @Override
            public String getPackageName() {
                return requiredPackageName;
            }

            @Override
            public void setPackageName( final String packageName ) {
                //Nothing to do here
            }
        };
        final StringBuilder sb = new StringBuilder();
        PackageNameWriter.write( sb,
                                 mockHasPackageName );
        
        System.out.println("===========: PackageNameWriter:" + sb.toString());
        sb.append( drl );
        return sb.toString();
    }

}
