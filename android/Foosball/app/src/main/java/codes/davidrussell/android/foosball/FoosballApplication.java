package codes.davidrussell.android.foosball;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class FoosballApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "4vFefzZizmLjr0JufPGih1Daa85YNl2etUwj58do", "wEpHFNLP0MzF9O9yiBerJnYSqSQ2EmLu5d1p7Gtd");
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }

}
