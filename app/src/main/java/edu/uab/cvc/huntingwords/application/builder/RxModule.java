package edu.uab.cvc.huntingwords.application.builder;

import dagger.Module;
import dagger.Provides;
import edu.uab.cvc.huntingwords.utils.rx.AppRxSchedulers;
import edu.uab.cvc.huntingwords.utils.rx.RxSchedulers;

/**
 * Created by yassinegharsallah on 31/03/2017.
 */

@Module
public class RxModule {

    @Provides
    RxSchedulers provideRxSchedulers() {
        return new AppRxSchedulers();
    }
}
