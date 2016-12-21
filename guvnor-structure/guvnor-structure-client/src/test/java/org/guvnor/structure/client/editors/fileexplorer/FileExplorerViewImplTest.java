/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.core.client.tree.TreeItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FileExplorerViewImplTest {

    @Mock
    private FileExplorerPresenter presenter;

    private TreeItem item;

    private FileExplorerViewImpl view;

    @Before
    public void setUp() {
        view = new FileExplorerViewImpl();
        view.init( presenter );
    }

    @Test
    public void checkItemsAreNotLazyLoaded() {
        item = newTreeItem( new TreeItemData( TreeItem.Type.ITEM,
                                              "file",
                                              mock( Path.class ) ) );
        assertFalse( view.needsLoading( item ) );
    }

    @Test
    public void checkFoldersWithNoChildrenAreNotLazyLoaded() {
        item = newTreeItem( new TreeItemData( TreeItem.Type.FOLDER,
                                              "folder",
                                              mock( Path.class ) ) );
        assertFalse( view.needsLoading( item ) );
    }

    @Test
    public void checkFoldersWithExistingChildrenAreNotLazyLoaded() {
        item = newTreeItem( new TreeItemData( TreeItem.Type.FOLDER,
                                              "folder",
                                              mock( Path.class ) ),
                            new TreeItemData( TreeItem.Type.ITEM,
                                              "file1",
                                              mock( Path.class ) ),
                            new TreeItemData( TreeItem.Type.ITEM,
                                              "file2",
                                              mock( Path.class ) ) );
        assertFalse( view.needsLoading( item ) );
    }

    @Test
    public void checkFoldersWithLazyFlagAreLazyLoaded() {
        item = newTreeItem( new TreeItemData( TreeItem.Type.FOLDER,
                                              "folder",
                                              mock( Path.class ) ),
                            new TreeItemData( TreeItem.Type.ITEM,
                                              CommonConstants.INSTANCE.Loading(),
                                              mock( Path.class ) ) );
        assertTrue( view.needsLoading( item ) );
    }

    private class TreeItemData {

        TreeItem.Type type;
        String value;
        Path path;

        TreeItemData( final TreeItem.Type type,
                      final String value,
                      final Path path ) {
            this.type = type;
            this.value = value;
            this.path = path;
        }
    }

    private TreeItem newTreeItem( TreeItemData parent,
                                  TreeItemData... children ) {
        final List<TreeItem> cti = new ArrayList<>();

        final TreeItem item = new TreeItem( parent.type,
                                            parent.value ) {

            @Override
            public int getChildCount() {
                return cti.size();
            }

            @Override
            public TreeItem getChild( int i ) {
                return cti.get( i );
            }

            @Override
            public Iterable<TreeItem> getChildren() {
                return cti;
            }

            @Override
            protected TreeItem makeChild( final Type type,
                                          final String value ) {
                return new TreeItem( type,
                                     value ) {
                    @Override
                    public String getText() {
                        return value;
                    }
                };
            }
        };
        item.setUserObject( parent.path );

        Arrays.asList( children ).stream().forEach( ( c ) -> {
            final TreeItem ti = item.addItem( c.type,
                                              c.value );
            ti.setUserObject( c.path );
            cti.add( ti );
        } );

        return item;
    }

}
