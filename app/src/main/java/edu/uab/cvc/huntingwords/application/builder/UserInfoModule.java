package edu.uab.cvc.huntingwords.application.builder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.uab.cvc.huntingwords.models.UserInformation;

/**
 * Created by carlosb on 18/04/18.
 */

@Module
public class UserInfoModule {
    @Singleton
    @Provides
    public UserInformation getUserInformation() {
        return new UserInformation ();
    }
}
