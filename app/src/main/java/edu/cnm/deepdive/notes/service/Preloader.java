package edu.cnm.deepdive.notes.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.google.gson.Gson;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.model.dao.NoteDao;
import edu.cnm.deepdive.notes.model.entity.Note;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import javax.inject.Inject;
import javax.inject.Provider;

public class Preloader extends RoomDatabase.Callback {


  private final Context context;
  private final Provider<NoteDao> noteDaoProvider;
  private final Gson gson;

  @Inject
  Preloader(@ApplicationContext Context context, Provider<NoteDao> noteDaoProvider, Gson gson) {
    this.context = context;
    this.noteDaoProvider = noteDaoProvider;
    this.gson = gson;
  }

  @Override
  public void onCreate(@NonNull SupportSQLiteDatabase db) {
    super.onCreate(db);

    NoteDao noteDao = noteDaoProvider.get();

    //Need an input stream
    try (
        InputStream input = context.getResources().openRawResource(R.raw.preload);
        Reader reader = new InputStreamReader(input)
    ) {
      Note[] notes = gson.fromJson(reader, Note[].class);
      noteDao
          .insert(notes)
          .subscribeOn(Schedulers.io())
          .subscribe();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
