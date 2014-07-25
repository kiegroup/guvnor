package org.guvnor.shared;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GeneralSettings {

    private String name;
    private boolean enabled;
    private boolean socialLogin;
    private boolean userRegistration;
    private boolean resetPassword;
    private boolean verifyEmail;
    private boolean userAccountManagement;
    private boolean cookieLoginAllowed;
    private boolean requireSSL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSocialLogin() {
        return socialLogin;
    }

    public void setSocialLogin(boolean socialLogin) {
        this.socialLogin = socialLogin;
    }

    public boolean isUserRegistration() {
        return userRegistration;
    }

    public void setUserRegistration(boolean userRegistration) {
        this.userRegistration = userRegistration;
    }

    public boolean isResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public boolean isVerifyEmail() {
        return verifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        this.verifyEmail = verifyEmail;
    }

    public boolean isUserAccountManagement() {
        return userAccountManagement;
    }

    public void setUserAccountManagement(boolean userAccountManagement) {
        this.userAccountManagement = userAccountManagement;
    }

    public boolean isCookieLoginAllowed() {
        return cookieLoginAllowed;
    }

    public void setCookieLoginAllowed(boolean cookieLoginAllowed) {
        this.cookieLoginAllowed = cookieLoginAllowed;
    }

    public boolean isRequireSSL() {
        return requireSSL;
    }

    public void setRequireSSL(boolean requireSSL) {
        this.requireSSL = requireSSL;
    }
}
