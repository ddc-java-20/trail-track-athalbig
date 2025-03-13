package edu.cnm.deepdive.trailTrack.viewmodel;

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
import edu.cnm.deepdive.trailTrack.model.entity.Pin;
import edu.cnm.deepdive.trailTrack.service.NoteRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class NoteViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final NoteRepository noteRepository;
  private final MutableLiveData<Long> noteId;
  private final LiveData<Pin> note;
  private final MutableLiveData<Uri> captureUri;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  private Uri pendingCaptureUri;

  @Inject
  NoteViewModel(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
    noteId = new MutableLiveData<>();
    note = Transformations.switchMap(noteId, noteRepository::get);
    captureUri = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public void saveNote(Pin pin) {
    // 2/12/25 Reset our throwable LiveData.
    throwable.setValue(null); // Will be invoked from controller on UI thread.
    // 2/12/25 Invoke save method of repository to get the machine for saving.
    // 2/12/25 Invoke the subscribe method on the machine, to start it and attach consumers.
    //   There will be 2 consumers: one for a successful result (a Pin) and one for an unsuccesful
    //   result (a Throwable). the first puts the Pin into a LiveData bucket; the second invokes
    //   a helper method.
    noteRepository
        .save(pin)
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
    this.noteId.setValue(noteId);
  }

  public void delete(Pin pin) {
    throwable.setValue(null);
    noteRepository
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

  public LiveData<Long> getNoteId() {
    return noteId;
  }

  public LiveData<Pin> getNote() {
    return note;
  }

  public LiveData<List<Pin>> getNotes() {
    return noteRepository.getAll();
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
    Log.e(NoteViewModel.class.getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
