package edu.uab.cvc.huntingwords.application.builder;

import dagger.Component;
import edu.uab.cvc.huntingwords.api.HeroApi;
import edu.uab.cvc.huntingwords.utils.rx.RxSchedulers;

/**
 * Created by ygharsallah on 30/03/2017.
 */
@AppScope
@Component(modules = {NetworkModule.class, AppContextModule.class, RxModule.class, HereosApiServiceModule.class})
public interface AppComponent {

    RxSchedulers rxSchedulers();
    HeroApi apiService();


}
