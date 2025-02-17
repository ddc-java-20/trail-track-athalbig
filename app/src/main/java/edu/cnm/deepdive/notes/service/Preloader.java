package edu.cnm.deepdive.notes.service;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import edu.cnm.deepdive.notes.model.dao.NoteDao;
import edu.cnm.deepdive.notes.model.entity.Note;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Provider;

public class Preloader extends RoomDatabase.Callback {


  private final Provider<NoteDao> noteDaoProvider;

  @Inject
  Preloader(Provider<NoteDao> noteDaoProvider) {
    this.noteDaoProvider = noteDaoProvider;
  }

  @Override
  public void onCreate(@NonNull SupportSQLiteDatabase db) {
    super.onCreate(db);

    NoteDao noteDao = noteDaoProvider.get();

    Note note1 = new Note();
    note1.setTitle("Test Note 1");
    note1.setContent("Blah blah blah alskdj");

    Note note2 = new Note();
    note2.setTitle("Test Note 2");
    note2.setContent("WOAH NOTES DAWG THESE ARE NOTES SO NOTES");

    noteDao
        .insert(note1, note2)
        .subscribeOn(Schedulers.io())
        .subscribe();
  }
}
