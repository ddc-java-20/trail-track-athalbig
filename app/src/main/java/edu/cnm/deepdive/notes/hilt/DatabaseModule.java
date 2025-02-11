package edu.cnm.deepdive.notes.hilt;

import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.notes.model.dao.NoteDao;
import edu.cnm.deepdive.notes.service.NotesDatabase;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
//Telling hilt how to get an instance of the database
  @Provides
  @Singleton
  NotesDatabase provideDatabase(@ApplicationContext Context context) {
    return Room.databaseBuilder(context.getApplicationContext(),
            NotesDatabase.class, NotesDatabase.getDatabaseName())
        // TODO: 2/11/25 Attach callback for database preload.
        .build();
  }

  @Provides
  @Singleton
  NoteDao provideNoteDao(NotesDatabase database) {
    return database.getNoteDao();
  }
}
