package comf.example.tydia.cse_110_team_project_team_15_1;

import android.util.Log;

import com.google.api.services.people.v1.model.Person;

import org.mortbay.jetty.Main;

import java.util.Random;

/**
 * Created by Graham on 3/11/2018.
 */

public class GoogleHelper {

    private static String[] adjList = {"Anonymous", "Evasive", "Large", "Small", "Cool",
                                        "Baller", "Joyous", "Happy", "Sad", "Ecstatic",
                                        "Introspective", "Introverted", "Extroverted", "Smiling", "Winking",
                                        "Pompous", "Adventurous", "Singing", "Dancing", "Typing",
                                        "Coding", "Winking", "Bitter", "Angry", "Brave",
                                        "Proud", "Comfortable", "Intelligent", "Smart", "Clever",
                                        "Famous", "Lazy", "Busy", "Careful", "Noisy",
                                        "Nosey", "Loud", "Quite", "Tiny", "Massive",
                                        "Red", "Blue", "Green", "Maroon", "Cyan",
                                        "Purple", "Yellow", "Orange", "Teal", "Tan"};

    private static String[] nounList = {"Tiger", "Lion", "Otter", "Panda", "Eel",
                                        "Giraffe", "Rhino", "Baboon", "Badger", "Bass",
                                        "Bat", "Jellyfish", "Anaconda", "Monkey", "Cat",
                                        "Dog", "Ant", "Buffalo", "Beaver", "Bird",
                                        "Iguana", "Mouse", "Rat", "Dragon", "Kangaroo",
                                        "Lemur", "Shark", "Ocelot", "Narwhal", "Owl",
                                        "Python", "Quokka", "Quail", "Snake", "Snail",
                                        "Slug", "Salmon", "Unicorn", "Viper", "Crane",
                                        "Wasp", "Worm", "Yak", "Zebra", "Penguin",
                                        "Phoenix", "Python", "Possum", "Hamster", "Bear"};

    /*
     * Returns the name that needs to be displayed on the screen, whether friend, you, or anonymous
     */
    public static String getDisplayName(String id){
        Log.d("Song Username Info", id);

        String email = parseForEmail(id);

        if(MainActivity.myPerson.getEmailAddresses().get(0).equals(email)){
            return "You";
        }

        Person foundFriend = getFriend(email);
        if(foundFriend != null){
            return getFriendsName(foundFriend);
        }
        else{
            return getAnonName(id);
        }
    }

    /*
     * Returns the Person object of the friend on your friends list with a matching email
     * Return null if the email is not someone on your friends list
     */
    public static Person getFriend(String email){
        Person retVal = null;
        for(Person p : MainActivity.friendsList){
            if(p.getEmailAddresses().get(0).getValue().equals(email)){
                retVal = p;
                break;
            }
        }
        return retVal;
    }

    /*
     * Fetches just the email portion of the user ID stored in firebase, scrapping the anon name
     */
    public static String parseForEmail(String id){
        int index = id.lastIndexOf("!");
        if(index != -1){
            return id.substring(0, index);
        }
        else{
            Log.d("GoogleHelper", "Error getting email");
            return "";
        }
    }

    /*
     * Returns the name of your friend from the person object of your friends list
     */
    private static String getFriendsName(Person p){
        return p.getNames().get(0).getDisplayName();
    }

    /*
     * Gets the anonymous name of someone who is not on your friends list
     */
    private static String getAnonName(String id){
        int index = id.indexOf("!");
        if(index != -1){
            return id.substring(index);
        }
        else{
            Log.d("GoogleHelper", "Error getting anon name of other user");
            return "Anon Name problem found";
        }
    }

    /*
     * Generates a full username for the current user in format "email!anonymousName"
     */
    public static String generateUserName(String email){
        return email + "!" + generateAnonName();
    }

    /*
     * Generates the anonymous portion of the users unique ID
     */
    private static String generateAnonName(){
        Random ran = new Random();
        int adjective = ran.nextInt(50);
        Log.d("Generating Name Adjective: ", adjList[adjective]);
        int noun = ran.nextInt(50);
        Log.d("Generating Name Noun: ", nounList[noun]);
        return adjList[adjective] + nounList[noun];
    }
}
