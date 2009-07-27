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
