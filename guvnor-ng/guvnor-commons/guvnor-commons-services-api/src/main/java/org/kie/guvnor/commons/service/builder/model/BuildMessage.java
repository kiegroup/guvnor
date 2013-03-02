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

package org.kie.guvnor.commons.service.builder.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class BuildMessage {

    private long id;
    private Type type;
    private Level level;
    private Path path;
    private int line;
    private int column;
    private String text;
    private String artifactID;

    public void setId( long id ) {
        this.id = id;
    }

    public void setType( final Type type ) {
        this.type = type;
    }

    public void setLevel( final Level level ) {
        this.level = level;
    }

    public void setPath( final Path path ) {
        this.path = path;
    }

    public void setLine( final int line ) {
        this.line = line;
    }

    public void setColumn( final int column ) {
        this.column = column;
    }

    public void setText( final String text ) {
        this.text = text;
    }

    public void setArtifactID( final String artifactId ) {
        this.artifactID = artifactId;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Level getLevel() {
        return level;
    }

    public Path getPath() {
        return path;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getText() {
        return text;
    }

    public String getArtifactID() {
        return artifactID;
    }

    @Portable
    public static enum Level {
        ERROR, WARNING, INFO;
    }

    @Portable
    public static enum Type {
        BUILD_FULL, BUILD_INCREMENTAL_ADD, BUILD_INCREMENTAL_REMOVE;
    }
}
