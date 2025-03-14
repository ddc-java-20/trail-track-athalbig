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
import edu.cnm.deepdive.trailtrack.service.PinRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class PinViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PinRepository pinRepository;
  private final MutableLiveData<Long> pinId;
  private final LiveData<Pin> pin;
  private final MutableLiveData<Uri> captureUri;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  private Uri pendingCaptureUri;

  @Inject
  PinViewModel(PinRepository pinRepository) {
    this.pinRepository = pinRepository;
    pinId = new MutableLiveData<>();
    pin = Transformations.switchMap(pinId, pinRepository::get);
    captureUri = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public void savePin(Pin pin) {
    // 2/12/25 Reset our throwable LiveData.
    throwable.setValue(null); // Will be invoked from controller on UI thread.
    // 2/12/25 Invoke save method of repository to get the machine for saving.
    // 2/12/25 Invoke the subscribe method on the machine, to start it and attach consumers.
    //   There will be 2 consumers: one for a successful result (a Pin) and one for an unsuccesful
    //   result (a Throwable). the first puts the Pin into a LiveData bucket; the second invokes
    //   a helper method.
    pinRepository
        .save(pin)
        .ignoreElement()
        .subscribe(
            () -> {
            },
            this::postThrowable,
            pending
        );
  }

  public void fetch(long pinId) {
    throwable.setValue(null);
    // TODO: 2/18/25 Consider this.pin.setValue(null)
    this.pinId.setValue(pinId);
  }

  public void delete(Pin pin) {
    throwable.setValue(null);
    pinRepository
        .remove(pin)
        .subscribe(
            () -> {},
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
    return pinRepository.getAll();
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

  private void postThrowable(Throwable throwable) {
    Log.e(PinViewModel.class.getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
