package edu.cnm.deepdive.trailtrack.hilt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt module for providing a Gson instance as a dependency. This module configures bindings in
 * the Hilt dependency injection framework to supply a singleton instance of {@link Gson}.
 */
@Module
@InstallIn(SingletonComponent.class)
public class GsonModule {

  /**
   * Provides a singleton instance of {@link Gson}, configured using a {@link GsonBuilder} to exclude
   * fields with certain modifiers.
   *
   * @return a singleton instance of {@link Gson}.
   */
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
