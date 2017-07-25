package co.touchlab.droidconandroid.shared.interactors;

import com.birbit.android.jobqueue.JobManager;

import java.sql.SQLException;

import co.touchlab.droidconandroid.CrashReport;
import co.touchlab.droidconandroid.DroidconApplication;
import co.touchlab.droidconandroid.shared.data.DatabaseHelper;
import co.touchlab.droidconandroid.shared.presenter.ConferenceDataHelper;
import co.touchlab.droidconandroid.shared.presenter.ConferenceDayHolder;
import co.touchlab.droidconandroid.shared.tasks.persisted.RefreshScheduleJob;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class RefreshScheduleInteractor
{
    private final JobManager     jobManager;
    private final DatabaseHelper databaseHelper;
    private BehaviorSubject<ConferenceDayHolder[]> conferenceDataSubject = BehaviorSubject.create();

    public RefreshScheduleInteractor(JobManager jobManager, DatabaseHelper databaseHelper)
    {
        this.databaseHelper = databaseHelper;
        this.jobManager = jobManager;
    }

    public Observable<ConferenceDayHolder[]> getDataStream()
    {
        return conferenceDataSubject.hide();
    }

    public void refreshFromDatabase(boolean allEvents)
    {
        try
        {
            ConferenceDayHolder[] newData = ConferenceDataHelper.listDays(databaseHelper,
                    allEvents);
            conferenceDataSubject.onNext(newData);
        }
        catch(SQLException e)
        {
            CrashReport.logException(e);
            refreshFromServer();
        }
    }

    public void refreshFromServer()
    {
        jobManager.addJobInBackground(new RefreshScheduleJob());
    }
}