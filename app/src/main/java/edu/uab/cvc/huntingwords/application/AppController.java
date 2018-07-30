package edu.uab.cvc.huntingwords.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import io.fabric.sdk.android.Fabric;
import edu.uab.cvc.huntingwords.application.builder.AppComponent;
import edu.uab.cvc.huntingwords.application.builder.AppContextModule;
import edu.uab.cvc.huntingwords.application.builder.DaggerAppComponent;
import timber.log.BuildConfig;
import timber.log.Timber;

/**
 * Created by ygharsallah on 30/03/2017.
 */


public class AppController extends Application {


    private static AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initialiseLogger();
        initAppComponent();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder().appContextModule(new AppContextModule(this)).build();
    }


    private void initialiseLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static AppComponent getComponent() {
        return appComponent;
    }

}
