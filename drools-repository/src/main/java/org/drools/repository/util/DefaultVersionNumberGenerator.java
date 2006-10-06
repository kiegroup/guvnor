package org.drools.repository.util;

import org.drools.repository.VersionableItem;

public class DefaultVersionNumberGenerator
    implements
    VersionNumberGenerator {

    public String calculateNextVersion(String currentVersionLabel, VersionableItem asset) {
        if (currentVersionLabel == null || currentVersionLabel.trim().equals( "" )) {
            return "1";
        } 
        try {
            int current = Integer.parseInt( currentVersionLabel );
            return Integer.toString( ++current );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to calculate next version number for version: " + currentVersionLabel);
        }
    }

}
