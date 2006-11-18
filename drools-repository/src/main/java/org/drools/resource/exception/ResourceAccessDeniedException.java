/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.drools.resource.exception;

/**
 * @author jwilliams
 *
 */
public class ResourceAccessDeniedException extends Exception {

    private String url;
    private String username;
    private String password;

    public ResourceAccessDeniedException(String url,
                                         String username,
                                         String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getMessage() {
        return "You do not have the right access priveleges for this resource: " + url + "\nwith username=" + username + " and password=" + password;
    }
}
