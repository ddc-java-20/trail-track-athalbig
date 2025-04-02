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
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.service.PinRepository;
import edu.cnm.deepdive.trailtrack.service.UserRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

/**
 * PinViewModel serves as the ViewModel for managing UI-related data for pins and tracks in a
 * lifecycle-aware manner. It facilitates interaction between the UI and the repositories, primarily
 * {@code PinRepository} and {@code UserRepository}, to handle operations such as saving, fetching,
 * and deleting pins and their related data. Additionally, it handles image capture URIs and manages
 * asynchronous operations with composite disposables.
 * <p>
 * This ViewModel is constructed using dependency injection (Hilt) and extends {@code ViewModel},
 * implementing {@code DefaultLifecycleObserver} to cleanly manage lifecycle-related tasks.
 */
@HiltViewModel
public class PinViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final PinRepository pinRepository;
  private final UserRepository userRepository;
  private final MutableLiveData<Long> pinId;
  private final MutableLiveData<Track> track;
  private final LiveData<Pin> pin;
  private final MutableLiveData<User> user;
  private final MutableLiveData<Uri> captureUri;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  private Uri pendingCaptureUri;

  /**
   * Constructs a new instance of the {@code PinViewModel} class. This ViewModel acts as a mediator
   * between the {@code PinRepository} and {@code UserRepository}, managing LiveData and
   * dependencies to facilitate the interaction between the UI layer and the underlying data
   * sources. It also handles operations such as pin fetching, saving, and managing related UI
   * state.
   *
   * @param pinRepository  The {@code PinRepository} instance used for performing data operations
   *                       related to pins and tracks. Must not be null.
   * @param userRepository The {@code UserRepository} instance used for fetching or managing
   *                       user-specific data. Must not be null.
   */
  @Inject
  PinViewModel(PinRepository pinRepository, UserRepository userRepository) {
    this.pinRepository = pinRepository;
    this.userRepository = userRepository;
    pinId = new MutableLiveData<>();
    pin = Transformations.switchMap(pinId, pinRepository::get);
    track = new MutableLiveData<>();
    user = new MutableLiveData<>();
    captureUri = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  /**
   * Saves a given {@code Pin} to the database by associating it with the current user and the track
   * currently being processed. This method resets the throwable state before performing the save
   * operation and handles completion, errors, and pending states.
   *
   * @param pin The {@code Pin} object to be saved. It must contain valid data and will be updated
   *            internally with the ID of the current user and the associated track.
   */
  public void savePin(Pin pin) {
    // 2/12/25 Reset our throwable LiveData.
    throwable.setValue(null); // Will be invoked from controller on UI thread.
    userRepository
        .getCurrentUser()
        .map((user) -> {
          this.user.postValue(user);
          pin.setUserId(user.getId());
          //noinspection DataFlowIssue
          pin.setTrackId(track.getValue().getId());
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

  /**
   * Initiates the process of fetching a Pin identified by the specified pin ID. This method resets
   * the throwable state to null before setting the given pin ID, which triggers the relevant
   * data-fetching and LiveData state updates.
   *
   * @param pinId The unique identifier of the Pin to be fetched. Must be a non-negative long value.
   */
  public void fetch(long pinId) {
    throwable.setValue(null);
    // TODO: 2/18/25 Consider this.pin.setValue(null)
    this.pinId.setValue(pinId);
  }

  /**
   * Deletes the specified {@code Pin} entity from the repository. This method clears the current
   * throwable state, performs a removal operation on the repository, and manages completion,
   * error handling, and pending states through reactive streams.
   *
   * @param pin The {@code Pin} object to be deleted. It must be a valid, existing entity
   *            in the repository.
   */
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

  /**
   * Confirms the capture operation by updating the capture URI state based on the operation's success
   * and resetting pending state. If successful, the pending capture URI is assigned to the capture
   * URI; otherwise, the capture URI is cleared.
   *
   * @param success Indicates whether the capture operation was successful. If true, the capture URI
   *                is updated to the pending capture URI. If false, the capture URI is set to null.
   */
  public void confirmCapture(boolean success) {
    captureUri.setValue(success ? pendingCaptureUri : null);
    pendingCaptureUri = null;
  }

  /**
   * Clears the current capture URI by setting its value to null. This method is typically used
   * to reset or remove any information about the URI associated with a capture operation,
   * ensuring that subsequent operations do not mistakenly use outdated or irrelevant data.
   */
  public void clearCaptureUri() {
    captureUri.setValue(null);
  }

  /**
   * Retrieves a {@code LiveData} object representing the ID of the current pin.
   *
   * @return A {@code LiveData<Long>} instance that provides the current pin ID. The value
   *         may be null if no pin is currently set or available.
   */
  public LiveData<Long> getPinId() {
    return pinId;
  }

  /**
   * Retrieves a {@code LiveData} object representing the current {@code Pin} being managed by the ViewModel.
   *
   * @return A {@code LiveData<Pin>} instance that provides the current pin. The value may be null if no pin
   *         is currently set or available in the repository.
   */
  public LiveData<Pin> getPin() {
    return pin;
  }

  /**
   * Retrieves a {@code LiveData} object representing the current {@code Track} being managed by the ViewModel.
   *
   * @return A {@code LiveData<Track>} instance that provides the current track. The value may be null if
   *         no track is currently set or available in the repository.
   */
  public LiveData<Track> getTrack() {
    return track;
  }

  public void setTrack(Track track) {
    if (!Objects.equals(this.track.getValue(), track)) {
      this.track.setValue(track);
    }
  }

  public LiveData<List<Pin>> getPins() {
    return Transformations.switchMap(track, pinRepository::getAllForTrack);
  }

  public LiveData<List<Track>> getTracks() {
    return pinRepository.getAllTracks();
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

  /**
   * Fetches the currently authenticated user and updates the associated LiveData asynchronously.
   * Clears any previously posted throwable state before initiating the operation. Uses the
   * {@code UserRepository} to retrieve the user entity. If the fetch is successful, the user data
   * is posted to the LiveData. In case of an error, the throwable state is updated. Handles
   * pending state to indicate the operation progress.
   *
   * This method operates on the following streams:
   * - Success: Posts the fetched user to the associated LiveData.
   * - Error: Posts the encountered exception to the throwable state.
   * - Completion: Updates the pending state to reflect operation completion.
   */
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

  /**
   * Posts the specified {@code Throwable} to the ViewModel's throwable state, making it available
   * to observe for error handling. This method also logs the throwable message and stack trace for
   * debugging purposes.
   *
   * @param throwable The {@code Throwable} instance representing an error or exception encountered
   *                  during ViewModel operations. Must not be null.
   */
  private void postThrowable(Throwable throwable) {
    Log.e(edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel.class.getSimpleName(),
        throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
