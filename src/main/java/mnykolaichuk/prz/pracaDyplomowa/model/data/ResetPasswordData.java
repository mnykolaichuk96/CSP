package mnykolaichuk.prz.pracaDyplomowa.model.data;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.FieldMatch;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidPassword;

@FieldMatch.List({
        @FieldMatch(first = "password", second = "matchingPassword", message = "{mnykolaichuk.prz.MatchPassword.message}")
})
public class ResetPasswordData {

    private String username;
    private String token;

    @ValidPassword
    private String password;

    @ValidPassword
    private String matchingPassword;

    public ResetPasswordData() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
