/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.shared.simulation;

public class SimulationTestUtils {

    public static String formatMillis(long millis) {
        if (millis == 0L) {
            return "0";
        }
        StringBuilder millisString = new StringBuilder();
        long leftover = millis;
        if (leftover >= 86400000L) {
            millisString.append(leftover / 86400000L).append("d ");
            leftover %= 86400000L;
        }
        if (leftover >= 3600000L) {
            millisString.append(leftover / 3600000L).append("h ");
            leftover %= 3600000L;
        }
        if (leftover >= 60000L) {
            millisString.append(leftover / 60000L).append("m ");
            leftover %= 60000L;
        }
        if (leftover >= 1000L) {
            millisString.append(leftover / 1000L).append("s ");
            leftover %= 1000L;
        }
        if (leftover >= 1L) {
            millisString.append(leftover).append("ms ");
        }
        return millisString.deleteCharAt(millisString.length() - 1).toString();
    }

    private SimulationTestUtils() {
    }

}
