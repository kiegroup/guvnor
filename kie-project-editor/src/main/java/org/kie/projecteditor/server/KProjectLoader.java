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

package org.kie.projecteditor.server;

import com.thoughtworks.xstream.XStream;
import org.drools.kproject.KProject;

public class KProjectLoader {

    public static KProject load() {
        return (KProject) new XStream().fromXML(getText());
    }

    private static String getText() {
        return "<org.drools.kproject.KProjectImpl>\n" +
                "  <groupArtifactVersion>\n" +
                "    <groupId>org.test</groupId>\n" +
                "    <artifactId>fol4</artifactId>\n" +
                "    <version>0.1</version>\n" +
                "  </groupArtifactVersion>\n" +
                "  <kProjectPath>src/main/resources/</kProjectPath>\n" +
                "  <kBasesPath>src/kbases</kBasesPath>\n" +
                "  <kBases>\n" +
                "    <entry>\n" +
                "      <string>fol4.test1.KBase1</string>\n" +
                "      <org.drools.kproject.KBaseImpl>\n" +
                "        <namespace>fol4.test1</namespace>\n" +
                "        <name>KBase1</name>\n" +
                "        <includes/>\n" +
                "        <files class=\"java.util.Arrays$ArrayList\">\n" +
                "          <a class=\"string-array\">\n" +
                "            <string>fol4/test1/rule1.drl</string>\n" +
                "            <string>fol4/test1/rule2.drl</string>\n" +
                "          </a>\n" +
                "        </files>\n" +
                "        <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "          <a class=\"string-array\">\n" +
                "            <string>@ApplicationScoped; @Inject</string>\n" +
                "          </a>\n" +
                "        </annotations>\n" +
                "        <equalsBehavior>EQUALITY</equalsBehavior>\n" +
                "        <eventProcessingMode>STREAM</eventProcessingMode>\n" +
                "        <kSessions>\n" +
                "          <entry>\n" +
                "            <string>fol4.test1.KSession2</string>\n" +
                "            <org.drools.kproject.KSessionImpl>\n" +
                "              <namespace>fol4.test1</namespace>\n" +
                "              <name>KSession2</name>\n" +
                "              <type>stateful</type>\n" +
                "              <clockType>\n" +
                "                <clockType>pseudo</clockType>\n" +
                "              </clockType>\n" +
                "              <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "                <a class=\"string-array\">\n" +
                "                  <string>@ApplicationScoped; @Inject</string>\n" +
                "                </a>\n" +
                "              </annotations>\n" +
                "              <kBase reference=\"../../../..\"/>\n" +
                "            </org.drools.kproject.KSessionImpl>\n" +
                "          </entry>\n" +
                "          <entry>\n" +
                "            <string>fol4.test1.KSession1</string>\n" +
                "            <org.drools.kproject.KSessionImpl>\n" +
                "              <namespace>fol4.test1</namespace>\n" +
                "              <name>KSession1</name>\n" +
                "              <type>stateless</type>\n" +
                "              <clockType>\n" +
                "                <clockType>realtime</clockType>\n" +
                "              </clockType>\n" +
                "              <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "                <a class=\"string-array\">\n" +
                "                  <string>@ApplicationScoped; @Inject</string>\n" +
                "                </a>\n" +
                "              </annotations>\n" +
                "              <kBase reference=\"../../../..\"/>\n" +
                "            </org.drools.kproject.KSessionImpl>\n" +
                "          </entry>\n" +
                "        </kSessions>\n" +
                "        <kProject reference=\"../../../..\"/>\n" +
                "      </org.drools.kproject.KBaseImpl>\n" +
                "    </entry>\n" +
                "    <entry>\n" +
                "      <string>fol4.test3.KBase3</string>\n" +
                "      <org.drools.kproject.KBaseImpl>\n" +
                "        <namespace>fol4.test3</namespace>\n" +
                "        <name>KBase3</name>\n" +
                "        <includes>\n" +
                "          <string>fol4.test1.KBase1</string>\n" +
                "          <string>fol4.test2.KBase2</string>\n" +
                "        </includes>\n" +
                "        <files class=\"java.util.Arrays$ArrayList\">\n" +
                "          <a class=\"string-array\"/>\n" +
                "        </files>\n" +
                "        <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "          <a class=\"string-array\">\n" +
                "            <string>@ApplicationScoped</string>\n" +
                "          </a>\n" +
                "        </annotations>\n" +
                "        <equalsBehavior>IDENTITY</equalsBehavior>\n" +
                "        <eventProcessingMode>CLOUD</eventProcessingMode>\n" +
                "        <kSessions>\n" +
                "          <entry>\n" +
                "            <string>fol4.test3.KSession4</string>\n" +
                "            <org.drools.kproject.KSessionImpl>\n" +
                "              <namespace>fol4.test3</namespace>\n" +
                "              <name>KSession4</name>\n" +
                "              <type>stateless</type>\n" +
                "              <clockType>\n" +
                "                <clockType>pseudo</clockType>\n" +
                "              </clockType>\n" +
                "              <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "                <a class=\"string-array\">\n" +
                "                  <string>@ApplicationScoped</string>\n" +
                "                </a>\n" +
                "              </annotations>\n" +
                "              <kBase reference=\"../../../..\"/>\n" +
                "            </org.drools.kproject.KSessionImpl>\n" +
                "          </entry>\n" +
                "        </kSessions>\n" +
                "        <kProject reference=\"../../../..\"/>\n" +
                "      </org.drools.kproject.KBaseImpl>\n" +
                "    </entry>\n" +
                "    <entry>\n" +
                "      <string>fol4.test2.KBase2</string>\n" +
                "      <org.drools.kproject.KBaseImpl>\n" +
                "        <namespace>fol4.test2</namespace>\n" +
                "        <name>KBase2</name>\n" +
                "        <includes/>\n" +
                "        <files class=\"java.util.Arrays$ArrayList\">\n" +
                "          <a class=\"string-array\">\n" +
                "            <string>fol4/test2/rule1.drl</string>\n" +
                "            <string>fol4/test2/rule2.drl</string>\n" +
                "          </a>\n" +
                "        </files>\n" +
                "        <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "          <a class=\"string-array\">\n" +
                "            <string>@ApplicationScoped</string>\n" +
                "          </a>\n" +
                "        </annotations>\n" +
                "        <equalsBehavior>IDENTITY</equalsBehavior>\n" +
                "        <eventProcessingMode>CLOUD</eventProcessingMode>\n" +
                "        <kSessions>\n" +
                "          <entry>\n" +
                "            <string>fol4.test2.KSession3</string>\n" +
                "            <org.drools.kproject.KSessionImpl>\n" +
                "              <namespace>fol4.test2</namespace>\n" +
                "              <name>KSession3</name>\n" +
                "              <type>stateful</type>\n" +
                "              <clockType>\n" +
                "                <clockType>pseudo</clockType>\n" +
                "              </clockType>\n" +
                "              <annotations class=\"java.util.Arrays$ArrayList\">\n" +
                "                <a class=\"string-array\">\n" +
                "                  <string>@ApplicationScoped</string>\n" +
                "                </a>\n" +
                "              </annotations>\n" +
                "              <kBase reference=\"../../../..\"/>\n" +
                "            </org.drools.kproject.KSessionImpl>\n" +
                "          </entry>\n" +
                "        </kSessions>\n" +
                "        <kProject reference=\"../../../..\"/>\n" +
                "      </org.drools.kproject.KBaseImpl>\n" +
                "    </entry>\n" +
                "  </kBases>\n" +
                "</org.drools.kproject.KProjectImpl>";
    }
}
