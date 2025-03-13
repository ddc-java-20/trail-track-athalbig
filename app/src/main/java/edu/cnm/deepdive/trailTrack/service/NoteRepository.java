package edu.cnm.deepdive.trailTrack.service;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.trailTrack.model.dao.PinDao;
import edu.cnm.deepdive.trailTrack.model.entity.Pin;
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

  private final PinDao pinDao;
  private final Scheduler scheduler;

  @Inject
  NoteRepository(PinDao pinDao) {
    this.pinDao = pinDao;
    scheduler = Schedulers.io();
  }

  public Single<Pin> save(Pin pin) {
    return (pin.getId() != 0)
      ? Completable.fromAction(() -> pin.setModifiedOn(Instant.now()))
        .andThen(pinDao.update(pin))
        .toSingle(() -> pin)
        .subscribeOn(scheduler)
      : pinDao
        .insertAndReturn(pin)
        .subscribeOn(scheduler);
  }

  public LiveData<Pin> get(long id) {
    return pinDao.selectById(id);
  }

  public Completable remove(Pin pin) {
    return pinDao
        .delete(pin)
        .subscribeOn(scheduler);
  }

  public LiveData<List<Pin>> getAll() {
    return pinDao.selectByCreatedOnAsc();
  }

}
