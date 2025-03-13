package edu.cnm.deepdive.trailTrack.hilt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class GsonModule {

  @Provides
  @Singleton
  Gson provideGson() {
    return new GsonBuilder()
        .excludeFieldsWithModifiers()
        .create();
    // 2/17/25 Create a GsonBuilder, and invoke methods to configure it and build an instance
    //  of Gson.
  }
}
