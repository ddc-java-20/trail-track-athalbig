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

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
//Telling hilt how to get an instance of the database
  @Provides
  @Singleton
  PinDatabase provideDatabase(@ApplicationContext Context context, Preloader callback) {
    return Room.databaseBuilder(context,
            PinDatabase.class, PinDatabase.getDatabaseName())
//        .addCallback(callback)
        .build();
  }

  @Provides
  @Singleton
  PinDao providePinDao(PinDatabase database) {
    return database.getPinDao();
  }

  @Provides
  @Singleton
  UserDao provideUserDao(PinDatabase pinDatabase) {
    return pinDatabase.getUserDao();
  }

  @Provides
  @Singleton
  TrackDao provideTrackDao(PinDatabase pinDatabase) { return pinDatabase.getTrackDao(); }


}
