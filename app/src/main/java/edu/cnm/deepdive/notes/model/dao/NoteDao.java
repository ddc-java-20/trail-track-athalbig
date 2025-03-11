package edu.cnm.deepdive.notes.model.dao;

import static androidx.lifecycle.Transformations.map;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import edu.cnm.deepdive.notes.model.entity.Note;
import edu.cnm.deepdive.notes.model.pojo.NoteWithUser;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
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

  @Update
  Completable update(Note note);

  @Update
  Completable update(Collection<Note> notes);

  @Update
  Completable update(Note... note);

  @Delete
  Completable delete(Note note);

  @Delete
  Completable delete(Collection<Note> notes);

  @Delete
  Completable delete(Note... note);

  @Query("SELECT * FROM note WHERE note_id = :id")
  LiveData<Note> selectById(long id);

  @Query("SELECT * FROM note ORDER BY created_on ASC")
  LiveData<List<Note>> selectByCreatedOnAsc();

  @Query("SELECT * FROM note ORDER BY created_on DESC")
  LiveData<List<Note>> selectByCreatedOnDesc();

  @Query("SELECT * FROM note WHERE created_on >= :rangeStart AND created_on < :rangeEnd  ORDER BY created_on ASC")
  LiveData<List<Note>> selectWhereCreatedOnInRangeByCreatedOnAsc(Instant rangeStart, Instant rangeEnd);

  @Query("SELECT * FROM note ORDER BY title ASC")
  LiveData<List<Note>> selectByTitleAsc();

  @Query("SELECT * FROM note ORDER BY title DESC")
  LiveData<List<Note>> selectByTitleDesc();

  @Transaction
  @Query("SELECT * FROM note WHERE title LIKE :filter ORDER BY title ASC")
  LiveData<List<NoteWithUser>> selectWhereTitleLikeByTitleAsc(String filter);

  @Query("SELECT * FROM note WHERE user_id = :userId ORDER BY created_on ASC")
  LiveData<List<Note>> selectByUserId(long userId);



}
