package jee.mif.model.user;

/**
 * Created by Tadas.
 */
public enum AuthenticationType {
    REGISTRATION("email-register-account"),
    PASSWORD_RESET("email-password-reset");

    private String emailTemplateName;

    AuthenticationType(String emailTemplateName) {
        this.emailTemplateName = emailTemplateName;
    }

    public String getEmailTemplateName() {
        return emailTemplateName;
    }
}
