/**
 * Copyright 2010 JBoss Inc
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

package org.drools.repository;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Artifact {   
	private String name;
	private String description;
	private Map<String, Object> metadata;	
	private String content;	
	private boolean isBinary;
	private InputStream binaryContent;
	private Calendar lastModified;
	private String srcLink;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
    /**
     * If this asset contains binary data, this is how you return it.
     * Otherwise it will return null.
     */
    public InputStream getBinaryContentAttachment() {
    	if(isBinary) {
    		return binaryContent;
    	}
    	
    	return null;
    }

	public void setContent(String content) {
		this.content = content;
		isBinary = false;
	}
	public void setBinaryContent(InputStream content) {
		this.binaryContent = content;
		isBinary = true;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
	public boolean isBinary() {
		return isBinary;
	}
	public void setBinary(boolean isBinary) {
		this.isBinary = isBinary;
	}
	
    /**
     * Nicely formats the information contained by the node that this object encapsulates
     */
    public String toString() {
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append( "Content of artifact named '" + this.getName() + "':\n" );
            returnString.append( "Content: " + this.getContent() + "\n" );
            returnString.append( "------\n" );

            returnString.append( "Description: " + this.getDescription() + "\n" );
            returnString.append( "------\n" );

            returnString.append( "Meta data: " );
            
            Map<String, Object> metadata = this.getMetadata();            

	    	for (Iterator<Map.Entry<String, Object>> iterator = metadata.entrySet().iterator(); iterator.hasNext();) {
	    		Map.Entry<String, Object> en = iterator.next();
				String key = en.getKey();
				Object value = en.getValue();
				returnString.append(key + ": " + value + "\n" );
			}

            returnString.append( "--------------\n" );
            return returnString.toString();
        } catch ( Exception e ) {
            throw new RulesRepositoryException( e );
        }
    }
	public Calendar getLastModified() {
		return lastModified;
	}
	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}
	public String getSrcLink() {
		return srcLink;
	}
	public void setSrcLink(String srcLink) {
		//has a srclink means this is a binary content
		this.srcLink = srcLink;
		isBinary = true;
	}	
}
