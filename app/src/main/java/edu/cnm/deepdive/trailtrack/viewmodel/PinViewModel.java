package edu.cnm.deepdive.trailtrack.viewmodel;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.service.PinRepository;
import edu.cnm.deepdive.trailtrack.service.UserRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class PinViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PinRepository pinRepository;
  private final UserRepository userRepository;
  private final MutableLiveData<Long> pinId;
  private final LiveData<Pin> pin;
  private final MutableLiveData<User> user;
  private final MutableLiveData<Uri> captureUri;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  private Uri pendingCaptureUri;

  @Inject
  PinViewModel(PinRepository pinRepository, UserRepository userRepository) {
    this.pinRepository = pinRepository;
    this.userRepository = userRepository;
    pinId = new MutableLiveData<>();
    pin = Transformations.switchMap(pinId, pinRepository::get);
    user = new MutableLiveData<>();
    captureUri = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public void savePin(Pin pin) {
    // 2/12/25 Reset our throwable LiveData.
    throwable.setValue(null); // Will be invoked from controller on UI thread.
    userRepository
        .getCurrentUser()
        .map((user) -> {
          this.user.postValue(user);
          pin.setUserId(user.getId());
          return pin;
        })
        .flatMap(pinRepository::save)
        .ignoreElement()
        .subscribe(
            () -> {
            },
            this::postThrowable,
            pending
        );
  }

  public void fetch(long noteId) {
    throwable.setValue(null);
    // TODO: 2/18/25 Consider this.note.setValue(null)
    this.pinId.setValue(noteId);
  }

  public void delete(Pin pin) {
    throwable.setValue(null);
    pinRepository
        .remove(pin)
        .subscribe(
            () -> {
            },
            this::postThrowable,
            pending
        );
  }

  public void confirmCapture(boolean success) {
    captureUri.setValue(success ? pendingCaptureUri : null);
    pendingCaptureUri = null;
  }

  public void clearCaptureUri() {
    captureUri.setValue(null);
  }

  public LiveData<Long> getPinId() {
    return pinId;
  }

  public LiveData<Pin> getPin() {
    return pin;
  }

  public LiveData<List<Pin>> getPins() {
    fetchCurrentUser();
    return Transformations.switchMap(user, pinRepository::getAllForUser);
  }

  public LiveData<Uri> getCaptureUri() {
    return captureUri;
  }

  public void setPendingCaptureUri(Uri pendingCaptureUri) {
    this.pendingCaptureUri = pendingCaptureUri;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    pending.clear();
    DefaultLifecycleObserver.super.onStop(owner);
  }

  private void fetchCurrentUser() {
    throwable.setValue(null);
    userRepository
        .getCurrentUser()
        .subscribe(
            user::postValue,
            this::postThrowable,
            pending
        );
  }

  private void postThrowable(Throwable throwable) {
    Log.e(edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel.class.getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
