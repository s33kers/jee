package jee.mif.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Created by Tadas.
 */
@Entity
@Table(name = "JEE_USER")
public class JeeUser implements Serializable {
    private static final long serialVersionUID = 5700284157223611710L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Column(name="PRINCIPAL_EMAIL", nullable = false, unique = true)
    private String principalEmail;

    @Column(name="NAME")
    private String name;

    @Column(name="SURNAME")
    private String surname;

    @Column(name="HASHED_PASSWORD")
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE")
    private RoleType role;

    @Column(name = "ACTIVATED")
    private Boolean activated = Boolean.FALSE;

    @Column(name = "BLOCKED")
    private Boolean blocked = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return "JeeUser{" +
                "id=" + id +
                ", version=" + version +
                ", principalEmail='" + principalEmail + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", role=" + role +
                ", activated=" + activated +
                ", blocked=" + blocked +
                '}';
    }
}
