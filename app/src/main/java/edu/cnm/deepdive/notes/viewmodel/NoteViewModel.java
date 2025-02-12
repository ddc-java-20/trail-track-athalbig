package edu.cnm.deepdive.notes.viewmodel;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.notes.model.entity.Note;
import edu.cnm.deepdive.notes.service.NoteRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;

@HiltViewModel
public class NoteViewModel extends ViewModel {

  private final NoteRepository noteRepository;
  private final MutableLiveData<Note> note;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  @Inject
  NoteViewModel(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
    note = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public void saveNote(Note note) {
    // 2/12/25 Reset our throwable LiveData.
    throwable.setValue(null); // Will be invoked from controller on UI thread.
    // 2/12/25 Invoke save method of repository to get the machine for saving.
    // 2/12/25 Invoke the subscribe method on the machine, to start it and attach consumers.
    //   There will be 2 consumers: one for a successful result (a Note) and one for an unsuccesful
    //   result (a Throwable). the first puts the Note into a LiveData bucket; the second invokes
    //   a helper method.
    noteRepository
        .save(note)
        .subscribe(
            this.note::postValue,
            this::postThrowable,
            pending
        );
  }

  public void fetch(long noteId) {

  }

  private void postThrowable(Throwable throwable) {
    Log.e(NoteViewModel.class.getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
