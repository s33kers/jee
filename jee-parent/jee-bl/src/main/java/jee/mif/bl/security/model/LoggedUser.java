package jee.mif.bl.security.model;

import jee.mif.model.user.RoleType;

/**
 * Created by Tadas.
 */
public class LoggedUser {

    private Long id;
    private String principalEmail;
    private String name;
    private String surname;
    private RoleType roleType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipalEmail() {
        return principalEmail;
    }

    public void setPrincipalEmail(String principalEmail) {
        this.principalEmail = principalEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
