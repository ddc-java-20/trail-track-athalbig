package edu.cnm.deepdive.trailtrack.service;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PinRepository {

  private final PinDao pinDao;
  private final Scheduler scheduler;
  private final GoogleSignInService googleSignInService;

  @Inject
  PinRepository(PinDao pinDao, GoogleSignInService googleSignInService) {
    this.pinDao = pinDao;
    this.googleSignInService = googleSignInService;
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

  public LiveData<List<Pin>> getAll(User user) {
    return pinDao.selectByCreatedOnAsc(user.getId());
  }

  public LiveData<List<Pin>> getAllForUser(User user) {
    return pinDao.selectByUserId(user.getId());
  }

}
