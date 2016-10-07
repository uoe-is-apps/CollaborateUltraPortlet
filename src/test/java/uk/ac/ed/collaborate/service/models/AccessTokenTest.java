package uk.ac.ed.collaborate.service.models;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by v1mburg3 on 22/06/2016.
 */
public class AccessTokenTest {
    @Test
    public void getExpiryDate_noExpiresInSet_returnsObjectCreationDate() {
        Date currentDate = DateUtils.round(new Date(), Calendar.SECOND);

        AccessToken accessToken = new AccessToken();

        assertThat("Dates are equal to nearest second",
                DateUtils.round(accessToken.getExpiryDate(), Calendar.SECOND),
                equalTo(currentDate));
    }

    @Test
    public void getExpiryDate_expiresInSet_returnsOffsetDate() {
        // 30 day expiry is default for the API
        int expiresInSeconds = 30 * 24 * 60 * 60;
        Date currentDate = DateUtils.round(new Date(), Calendar.SECOND);

        AccessToken accessToken = new AccessToken();
        accessToken.setExpiresInSeconds(expiresInSeconds);

        assertThat("Expiry date is offset by correct number of seconds",
                DateUtils.round(accessToken.getExpiryDate(), Calendar.SECOND),
                equalTo(DateUtils.addSeconds(currentDate, expiresInSeconds)));
    }

    @Test
    public void isNotExpired_noExpiresInSet_returnsFalse() {
        AccessToken accessToken = new AccessToken();

        assertThat("Token with no expiry offset is immediately expired", accessToken.isNotExpired(), is(false));
    }

    @Test
    public void isNotExpired_expiresInSet_returnsTrue() {
        // 30 day expiry is default for the API
        int expiresInSeconds = 30 * 24 * 60 * 60;

        AccessToken accessToken = new AccessToken();
        accessToken.setExpiresInSeconds(expiresInSeconds);

        assertThat("Token with a future expiry time is not expired", accessToken.isNotExpired(), is(true));
    }

    @Test
    public void isNotExpired_expiresInPassed_returnsFalse() {
        // simulate a passed expiry time by setting a negative value
        int expiresInSeconds = -1;

        AccessToken accessToken = new AccessToken();
        accessToken.setExpiresInSeconds(expiresInSeconds);

        assertThat("Token with a past expiry time is expired", accessToken.isNotExpired(), is(false));
    }
}
