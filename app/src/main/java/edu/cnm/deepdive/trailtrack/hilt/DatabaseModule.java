package edu.cnm.deepdive.trailtrack.hilt;

import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.dao.TrackDao;
import edu.cnm.deepdive.trailtrack.model.dao.UserDao;
import edu.cnm.deepdive.trailtrack.service.PinDatabase;
import edu.cnm.deepdive.trailtrack.service.Preloader;
import javax.inject.Singleton;

/**
 * A Hilt module for providing database and DAO dependencies.
 * This module configures bindings in the Hilt dependency injection
 * framework to supply a singleton instance of the {@link PinDatabase}
 * and its associated DAOs.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

  /**
   * Creates and provides a singleton instance of the {@link PinDatabase} configured with the
   * specified application context and preloader callback. The created database includes preloading
   * functionality to initialize data on creation.
   *
   * @param context the application {@link Context} used to create the database instance.
   * @param callback the {@link Preloader} responsible for performing actions when the database is created.
   * @return a singleton instance of {@link PinDatabase}.
   */
  @Provides
  @Singleton
  PinDatabase provideDatabase(@ApplicationContext Context context, Preloader callback) {
    return Room.databaseBuilder(context,
            PinDatabase.class, PinDatabase.getDatabaseName())
        .addCallback(callback)
        .build();
  }

  /**
   * Provides an instance of {@link PinDao} for accessing and manipulating the "pins" table within the
   * {@link PinDatabase}.
   *
   * @param database the instance of {@link PinDatabase} from which the {@link PinDao} is obtained.
   * @return a singleton instance of {@link PinDao}.
   */
  @Provides
  @Singleton
  PinDao providePinDao(PinDatabase database) {
    return database.getPinDao();
  }

  /**
   * Provides an instance of {@link UserDao} for accessing and manipulating the "user" table within the
   * {@link PinDatabase}.
   *
   * @param pinDatabase the {@link PinDatabase} instance used to obtain the {@link UserDao}.
   * @return a singleton instance of {@link UserDao}.
   */
  @Provides
  @Singleton
  UserDao provideUserDao(PinDatabase pinDatabase) {
    return pinDatabase.getUserDao();
  }

  /**
   * Provides an instance of {@link TrackDao} for accessing and manipulating the "track" table
   * within the {@link PinDatabase}.
   *
   * @param pinDatabase the {@link PinDatabase} instance used to obtain the {@link TrackDao}.
   * @return a singleton instance of {@link TrackDao}.
   */
  @Provides
  @Singleton
  TrackDao provideTrackDao(PinDatabase pinDatabase) {
    return pinDatabase.getTrackDao();
  }
}
