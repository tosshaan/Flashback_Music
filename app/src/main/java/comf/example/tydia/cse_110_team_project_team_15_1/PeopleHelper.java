package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;

import java.io.IOException;

/**
 * Created by Cory Liang on 3/10/2018.
 */


public class PeopleHelper    {
    private static final String APPLICATION_NAME = "Peoples App";


    public static PeopleService setUp(Context context, String serverAuthCode) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Redirect URL for web based applications.
        // Can be empty too.
        String redirectUrl = "";
        String clientID = "781790350902-i1j0re1i0i8rc22mhugerv5p6okadnj9.apps.googleusercontent.com";
        String clientSecret = "Z7DcQmFsIX1DyyDwpBQQsS9Y";

        // Exchange auth code for access token
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport,
                jsonFactory,
                clientID,
                clientSecret,
                serverAuthCode,
                redirectUrl).execute();

        // Then, create a GoogleCredential object using the tokens from GoogleTokenResponse
        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(clientID, clientSecret)
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .build();

        credential.setFromTokenResponse(tokenResponse);

        // credential can then be used to access Google services
        return new PeopleService.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
