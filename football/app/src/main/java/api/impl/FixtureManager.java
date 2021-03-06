package api.impl;

import com.example.igiagante.football.FixtureFragment;

import java.util.ArrayList;

import api.OnDataLoad;
import api.OnDataReady;
import api.Provider;
import model.Match;
import model.Season;
import model.SoccerSeason;

/**
 * @author igiagante on 25/11/15.
 */
public class FixtureManager implements OnDataReady {

    private ArrayList<OnDataLoad> listeners;
    private ArrayList<Season> mSeasons;

    public FixtureManager() {
        listeners = new ArrayList<>();
        mSeasons = new ArrayList<>();
    }

    public void obtainSeasons(Provider provider) {
        provider.getFixtures();
    }

    public void registerListener(OnDataLoad listener) {
        listeners.add(listener);
        //load matches for the new active listener
        if (mSeasons != null && !mSeasons.isEmpty()) {
            loadDataIntoListener();
        }
    }

    public void unregisterListener(OnDataLoad listener) {
        listeners.remove(listener);
    }

    @Override
    public void onDataReady(SoccerSeason soccerSeason) {

        mSeasons.addAll(soccerSeason.getSeasons());
        //data is ready for the fragments
        loadDataIntoListener();
    }

    private void loadDataIntoListener() {
        if (listeners != null && !listeners.isEmpty()) {
            //data is ready for the fragments
            for (OnDataLoad listener : listeners) {
                listener.onDataLoading(true);
                FixtureFragment fixtureFragment = (FixtureFragment) listener;
                String date = fixtureFragment.getDate();
                listener.onDataLoadedSuccess(getMatchesForOneDate(date));
                listener.onDataLoading(false);
            }
        }
    }

    public ArrayList<Season> getMatchesForOneDate(String date) {

        ArrayList<Season> partSeasons = new ArrayList<>();
        ArrayList<Match> partMatches = new ArrayList<>();

        if(!partSeasons.isEmpty()) {
            partSeasons.clear();
        }

        if (mSeasons != null && !mSeasons.isEmpty()) {
            for (Season season : mSeasons) {
                for (Match match : season.getMatches()) {
                    if (match.getDate().equals(date)) {
                        partMatches.add(match);
                    }
                }

                //add only those matches which have this date
                ArrayList<Match> matches = new ArrayList<>();
                matches.addAll(partMatches);

                //Copy the season with filtered matches
                Season seasonFiltered = copySeason(season);
                seasonFiltered.setMatches(matches);

                //add season with matches
                partSeasons.add(seasonFiltered);

                //clear partMatches
                partMatches.clear();
            }
        }
        return partSeasons;
    }

    private Season copySeason(Season mSeason) {
        Season season = new Season();
        season.setId(mSeason.getId());
        season.setCaption(mSeason.getCaption());
        season.setLeague(mSeason.getLeague());
        season.setYear(mSeason.getYear());
        return  season;
    }

    public ArrayList<Season> getSeasons() {
        return mSeasons;
    }
}