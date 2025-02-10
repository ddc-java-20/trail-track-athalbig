package edu.cnm.deepdive.notes.model.dao;

import static androidx.lifecycle.Transformations.map;

import androidx.room.Dao;
import androidx.room.Insert;
import edu.cnm.deepdive.notes.model.entity.Note;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;

@Dao
public interface NoteDao {

  @Insert
  Single<Long> insert(Note note);

  default Single<Note> insertAndReturn(Note note) {
    return insert(note)
    .map((id) -> {
      note.setId(id);
      return note;
    });
  }

  @Insert
  Single<List<Long>> insert(Collection<Note> notes);

  @Insert
  Single<List<Long>> insert(Note... notes);
}
