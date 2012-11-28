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

package org.kie.guvnor.projecteditor.backend.server;

public class KProjectLoader {

    public static String load() {
        return "<kproject kProjectPath=\"src/main/resources/\" kBasesPath=\"src/kbases\">\n" +
                "  <groupArtifactVersion>\n" +
                "    <groupId>org.test</groupId>\n" +
                "    <artifactId>fol4</artifactId>\n" +
                "    <version>0.1</version>\n" +
                "  </groupArtifactVersion>\n" +
                "  <kbases>\n" +
                "    <kbase name=\"fol4.test1.KBase1\" equalsBehavior=\"EQUALITY\" eventProcessingMode=\"STREAM\">\n" +
                "      <ksessions>\n" +
                "        <ksession name=\"fol4.test1.KSession2\" type=\"stateful\" clockType=\"pseudo\"/>\n" +
                "        <ksession name=\"fol4.test1.KSession1\" type=\"stateless\" clockType=\"realtime\"/>\n" +
                "      </ksessions>\n" +
                "    </kbase>\n" +
                "    <kbase name=\"fol4.test3.KBase3\" equalsBehavior=\"IDENTITY\" eventProcessingMode=\"CLOUD\">\n" +
                "      <ksessions>\n" +
                "        <ksession name=\"fol4.test3.KSession4\" type=\"stateless\" clockType=\"pseudo\"/>\n" +
                "      </ksessions>\n" +
                "    </kbase>\n" +
                "    <kbase name=\"fol4.test2.KBase2\" equalsBehavior=\"IDENTITY\" eventProcessingMode=\"CLOUD\">\n" +
                "      <ksessions>\n" +
                "        <ksession name=\"fol4.test2.KSession3\" type=\"stateful\" clockType=\"pseudo\"/>\n" +
                "      </ksessions>\n" +
                "    </kbase>\n" +
                "  </kbases>\n" +
                "</kproject>";
    }
}
