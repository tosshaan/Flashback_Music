package comf.example.tydia.cse_110_team_project_team_15_1;

/**
 * Created by Cadu on 11-Mar-18.
 */

public interface playerSubject {
    public void notifyObservers();
    public void regObserver(Observer obs);
    public void delObserver(Observer obs);
}
