package uk.ac.ed.collaborate.service.models;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by v1mburg3 on 01/06/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {
    @JsonProperty("expires_in")
    private long expiresInSeconds;

    @JsonProperty("access_token")
    private String accessToken;

    private Date tokenCreationDate;

    public AccessToken() {
        this.tokenCreationDate = new Date();
    }

    /**
     * @return Gets the time in seconds that the token is valid for after generation
     */
    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    /**
     * @param expiresInSeconds The time in seconds that the token is valid for after generation
     */
    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    /**
     * @return Gets the OAuth2 token that must be included in requests to the Collaborate API
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken The OAuth2 token that must be included in requests to the Collaborate API
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return Gets the date on which the access token expires and must be regenerated
     */
    public Date getExpiryDate() {
        return new DateTime(this.tokenCreationDate).plus(this.expiresInSeconds*1000).toDate();
    }

    /**
     * @return Gets a value indicating whether the token has passed its expiry date
     */
    public boolean isNotExpired() {
        return DateUtils.truncate(this.getExpiryDate(), Calendar.MINUTE)
                .after(DateUtils.truncate(new Date(), Calendar.MINUTE));
    }
}