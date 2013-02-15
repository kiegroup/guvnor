/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.commons.service.source;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.Path;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public abstract class DRLBaseSourceService
        extends BaseSourceService<String> {

    protected DRLBaseSourceService() {
        super( "/src/main/resources" );
    }

    @Override
    public SourceContext getSource( final Path path ) {

        String drl = getSource(path, getIOService().readAllString( path ));

        final ByteArrayInputStream is = new ByteArrayInputStream( drl.getBytes() );
        final BufferedInputStream bis = new BufferedInputStream( is );
        final SourceContext context = new SourceContext( bis,
                                                         stripProjectPrefix( path ) );
        return context;
    }

    @Override
    public String getSource(Path path, String drl) {
        String packageDeclaration = returnPackageDeclaration(path);

        if ( !drl.contains( packageDeclaration ) ) {
            drl = packageDeclaration + "\n" + drl;
        }

        //Hack for empty byte streams not handled by the underlying KieBuilder
        if ( drl.isEmpty() ) {
            drl = " ";
        }

        return drl;
    }

    abstract protected IOService getIOService();
}
