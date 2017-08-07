package co.touchlab.droidconandroid.shared.presenter;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.Log;

import com.google.j2objc.annotations.Weak;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import co.touchlab.droidconandroid.shared.interactors.SponsorsInteractor;
import co.touchlab.droidconandroid.shared.network.SponsorsResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SponsorsViewModel extends ViewModel
{
    private final SponsorsInteractor task;
    @Weak
    private       SponsorsHost       host;
    private CompositeDisposable disposables = new CompositeDisposable();
    private Observable<SponsorsResult> sponsorsResultObservable;

    private SponsorsViewModel(@NonNull SponsorsInteractor task)
    {
        this.task = task;
    }

    public void register(@NonNull SponsorsHost host)
    {
        this.host = host;
    }

    public void getSponsors(int type)
    {
        if(sponsorsResultObservable == null)
        {
            sponsorsResultObservable = task.getSponsors(type).cache();
        }

        disposables.add(sponsorsResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(host:: onSponsorsFound, e -> host.onError()));
    }

    public void unregister()
    {
        host = null;
        disposables.clear();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory
    {
        @Inject
        SponsorsInteractor task;

        public Factory()
        {

        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass)
        {
            //noinspection unchecked
            return (T) new SponsorsViewModel(task);
        }
    }
}
