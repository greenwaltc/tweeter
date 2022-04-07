package edu.byu.cs.tweeter.server.service.util;

import java.sql.Timestamp;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.dto.AuthTokenDTO;

public class AuthTokenUtils {

    private static final Long duration = Long.valueOf(((120 * 60) + 0) * 1000); // 2 hours in milliseconds

    public static AuthToken generateAuthToken() {
        String tokenValue = UUID.randomUUID().toString();
        String datetime = new Timestamp(System.currentTimeMillis()).toString();

        return new AuthToken(tokenValue, datetime);
    }

    public static AuthTokenDTO generateAuthToken(String alias) {
        AuthToken authToken = generateAuthToken();
        return new AuthTokenDTO(authToken, alias);
    }

    public static boolean verifyAuthToken(AuthToken authToken) {
        Timestamp checkTime = Timestamp.valueOf(authToken.getDatetime());
        checkTime.setTime(checkTime.getTime() + duration);

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        if (checkTime.compareTo(currentTime) > 0) { // the provided authToken time plus 2 hours is greater than the current time, so the authToken is valid
            return true;
        }
        return false;
    }
}
