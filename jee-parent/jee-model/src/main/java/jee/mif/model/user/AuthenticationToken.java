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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Tadas
 */

@Entity
@Table(name = "AUTHENTICATION_TOKEN")
public class AuthenticationToken implements Serializable {
    private static final long serialVersionUID = -500036154675604254L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private JeeUser user;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "AUTHENTICATION_TOKEN")
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "TOKEN_TYPE")
    private AuthenticationType tokenType;

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

    public JeeUser getUser() {
        return user;
    }

    public void setUser(JeeUser user) {
        this.user = user;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthenticationType getTokenType() {
        return tokenType;
    }

    public void setTokenType(AuthenticationType tokenType) {
        this.tokenType = tokenType;
    }
}
