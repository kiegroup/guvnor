/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.metadata.attribute;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.DiscussionRecord;

import static org.uberfire.commons.validation.Preconditions.*;

/**
 *
 */
public final class DiscussionAttributesUtil {

    private DiscussionAttributesUtil() {
    }

    public static Map<String, Object> cleanup( final Map<String, Object> _attrs ) {
        final Map<String, Object> attrs = new HashMap<String, Object>( _attrs );

        for ( final String key : _attrs.keySet() ) {
            if ( key.startsWith( DiscussionView.TIMESTAMP ) || key.startsWith( DiscussionView.AUTHOR ) || key.startsWith( DiscussionView.NOTE ) ) {
                attrs.put( key, null );
            }
        }

        return attrs;
    }

    public static Map<String, Object> toMap( final DiscussionAttributes attrs,
                                             final String... attributes ) {
        return new HashMap<String, Object>() {{
            for ( final String attribute : attributes ) {
                checkNotEmpty( "attribute", attribute );

                if ( attribute.equals( "*" ) || attribute.equals( DiscussionView.DISCUSS ) ) {
                    for ( int i = 0; i < attrs.discussion().size(); i++ ) {
                        final DiscussionRecord record = attrs.discussion().get( i );
                        put( buildAttrName( DiscussionView.TIMESTAMP, i ), record.getTimestamp() );
                        put( buildAttrName( DiscussionView.AUTHOR, i ), record.getAuthor() );
                        put( buildAttrName( DiscussionView.NOTE, i ), record.getNote() );
                    }
                }
                if ( attribute.equals( "*" ) ) {
                    break;
                }
            }
        }};
    }

    private static String buildAttrName( final String title,
                                         final int i ) {
        return title + "[" + i + "]";
    }

}
