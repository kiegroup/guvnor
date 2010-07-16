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

package org.jboss.drools.guvnor.importgenerator.example.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserVO implements Serializable/* Principal */{
  private String name;
  private Set<PermissionVO> permissions = new HashSet<PermissionVO>();

  public UserVO(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Set<PermissionVO> getPermissions() {
    return permissions;
  }

  public void setPermissions(Set<PermissionVO> permissions) {
    this.permissions = permissions;
  }

  public void setName(String name) {
    this.name = name;
  }

}
