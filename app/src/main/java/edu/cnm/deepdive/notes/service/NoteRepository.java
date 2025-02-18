package edu.cnm.deepdive.notes.service;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.notes.model.dao.NoteDao;
import edu.cnm.deepdive.notes.model.entity.Note;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NoteRepository {

  private final NoteDao noteDao;
  private final Scheduler scheduler;

  @Inject
  NoteRepository(NoteDao noteDao) {
    this.noteDao = noteDao;
    scheduler = Schedulers.io();
  }

  public Single<Note> save(Note note) {
    return (note.getId() != 0)
      ? Completable.fromAction(() -> note.setModifiedOn(Instant.now()))
        .andThen(noteDao.update(note))
        .toSingle(() -> note)
        .subscribeOn(scheduler)
      : noteDao
        .insertAndReturn(note)
        .subscribeOn(scheduler);
  }

  public LiveData<Note> get(long id) {
    return noteDao.selectById(id);
  }

  public Completable remove(Note note) {
    return noteDao
        .delete(note)
        .subscribeOn(scheduler);
  }

  public LiveData<List<Note>> getAll() {
    return noteDao.selectByCreatedOnAsc();
  }

}
