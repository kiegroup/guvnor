package org.drools.guvnor.client.rpc;
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
 * Returned by the builder.
 * @author Michael Neale
 */
public class BuilderResult
    implements
    IsSerializable {


    public String assetFormat;
    public String assetName;
    public String uuid;
    public String message;

    public String toString() {
        return "Asset: " + assetName + "." + assetFormat + "\n" + //NON-NLS
               "Message: " + message + "\n" +   //NON-NLS
               "UUID: " + uuid; //NON-NLS
    }

}