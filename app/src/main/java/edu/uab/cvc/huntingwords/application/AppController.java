package edu.uab.cvc.huntingwords.application;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import edu.uab.cvc.huntingwords.application.builder.AppComponent;
import edu.uab.cvc.huntingwords.application.builder.AppContextModule;
import edu.uab.cvc.huntingwords.application.builder.DaggerAppComponent;
import timber.log.BuildConfig;
import timber.log.Timber;
import org.acra.*;
import org.acra.annotation.*;

/**
 * Created by ygharsallah on 30/03/2017.
 */


public class AppController extends Application {


    private static AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        initialiseLogger();
        initAppComponent();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

    }

    @AcraCore(buildConfigClass = BuildConfig.class)
    @AcraMailSender(mailTo = "reports_word_hunter@gmail.com")
    public class MyApplication extends Application {
        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(base);

            // The following line triggers the initialization of ACRA
            ACRA.init(this);
        }
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
