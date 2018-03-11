package comf.example.tydia.cse_110_team_project_team_15_1;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

import org.mortbay.jetty.servlet.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cory Liang on 3/11/2018.
 */

public class FriendAsync extends AsyncTask<String, Void, List<String>> {

    private static Context context;
    private AsyncObserver listener;

    public FriendAsync(AsyncObserver list){
        listener = list;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... parameters) {

        List<String> friendsIDs = new ArrayList<>();

        try {
            PeopleService peopleGetter = PeopleServiceFactory.setUp( parameters[0]);
            Person profile = peopleGetter.people().get("people/me")
                    .setRequestMaskIncludeField("person.names,person.emailAddresses")
                    .execute();

            MainActivity.myPerson = profile;

            Log.d("myPerson", profile.getEmailAddresses().get(0).getValue());

            ListConnectionsResponse peopleList = peopleGetter.people().connections()
                    .list("people/me")
                    .setPageSize(100)
                    .setRequestMaskIncludeField("person.names,person.emailAddresses")
                    .execute();
            List<Person> friends = peopleList.getConnections();

            if (friends == null) {
                System.out.println("I HAVE NO FRIENDS");
            }

            for (Person p : friends) {
                List<EmailAddress> grahamPls = p.getEmailAddresses();
                MainActivity.friendsList.add(p);
                friendsIDs.add(grahamPls.get(0).getValue());
                Person person = peopleGetter.people().get(p.getResourceName()).setPersonFields("emailAddresses").execute();
                Log.d("Friend ID Added: ", grahamPls.get(0).getValue());
                System.out.println(person.getEmailAddresses().get(0).getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Running Async Task");
        return friendsIDs;
    }

    @Override
    protected void onPostExecute(List<String> r){
        super.onPostExecute(r);
        listener.callback();
    }

}