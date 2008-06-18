package org.drools.brms.client.rpc;
/*
 * Copyright 2005 JBoss Inc
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



import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains the results returned to populate a table/grid.
 * This will be enhanced to provide pagination data shortly.
 * @author Michael Neale
 */
public class TableDataResult
    implements
    IsSerializable {

    public TableDataRow[] data;
    public long total;
    public boolean hasNext;

}