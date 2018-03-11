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


public class PeopleServiceFactory {

    public static PeopleService setUp(Context context, String serverAuthCode) throws IOException {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory factory = JacksonFactory.getDefaultInstance();

        String redirectUrl = "";
        String clientID = "781790350902-i1j0re1i0i8rc22mhugerv5p6okadnj9.apps.googleusercontent.com";
        String clientSecret = "Z7DcQmFsIX1DyyDwpBQQsS9Y";

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(transport, factory, clientID, clientSecret, serverAuthCode, redirectUrl).execute();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(clientID, clientSecret)
                .setTransport(transport)
                .setJsonFactory(factory)
                .build();

        credential.setFromTokenResponse(tokenResponse);

        return new PeopleService.Builder(transport, factory, credential)
                .setApplicationName("Vibe Music")
                .build();
    }
}
