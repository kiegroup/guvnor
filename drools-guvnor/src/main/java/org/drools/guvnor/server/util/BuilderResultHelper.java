/**
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.server.builder.ContentAssemblyError;
import org.drools.guvnor.server.builder.ContentPackageAssembler;
/**
 * 
 * @author Jari Timonen
 *
 */
public class BuilderResultHelper {
    public BuilderResultLine[] generateBuilderResults(ContentPackageAssembler asm) {
        BuilderResultLine[] result = new BuilderResultLine[asm.getErrors().size()];
        for ( int i = 0; i < result.length; i++ ) {
            ContentAssemblyError err = asm.getErrors().get( i );
            BuilderResultLine res = new BuilderResultLine();
            res.assetName = err.getName();
            res.assetFormat = err.getFormat();
            res.message = err.getErrorReport();
            res.uuid = err.getUUID();
            result[i] = res;
        }
        return result;
    }

}
