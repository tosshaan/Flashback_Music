package JUnitTests;

/**
 * Created by Cory Liang on 3/11/2018.
 */
import android.support.test.rule.ActivityTestRule;

import com.google.api.services.people.v1.model.Person;

import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import comf.example.tydia.cse_110_team_project_team_15_1.FriendAsync;
import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;

import static comf.example.tydia.cse_110_team_project_team_15_1.MainActivity.friendsList;
import static junit.framework.Assert.assertEquals;

public class FriendsTester {
    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);
    //test only using testgmail account.
    //Username tbate219@gmail.com
    //Password tombate12345
    @Before
    public void setup() {
        friendsList = new ArrayList<Person>();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //friendsList should hold the right person objects
    @Test
    public void testEmails() {
        assertEquals("gmosbruc@ucsd.edu",friendsList.get(0).getEmailAddresses().get(0).getValue());
        assertEquals("chl550@ucsd.edu",friendsList.get(1).getEmailAddresses().get(0).getValue());
        assertEquals("wcl011@ucsd.edu",friendsList.get(2).getEmailAddresses().get(0).getValue());
    }
}
