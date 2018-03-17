package comf.example.tydia.cse_110_team_project_team_15_1;

/**
 * Created by tosshaan on 3/16/2018.
 */

public interface downloadSubject {
    public void notifyDownDone();
    public void regDownObs(downloadObserver obs);
    public void delDownObs(downloadObserver obs);
}
