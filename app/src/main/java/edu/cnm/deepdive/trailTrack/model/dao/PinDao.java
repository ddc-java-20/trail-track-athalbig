package edu.cnm.deepdive.trailTrack.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.trailTrack.model.entity.Pin;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Dao
public interface PinDao {

  @Insert
  Single<Long> insert(Pin pin);

  default Single<Pin> insertAndReturn(Pin pin) {
    return insert(pin)
    .map((id) -> {
      pin.setId(id);
      return pin;
    });
  }

  @Insert
  Single<List<Long>> insert(Collection<Pin> pins);

  @Insert
  Single<List<Long>> insert(Pin... pins);

  @Update
  Completable update(Pin pin);

  @Update
  Completable update(Collection<Pin> pins);

  @Update
  Completable update(Pin... pin);

  @Delete
  Completable delete(Pin pin);

  @Delete
  Completable delete(Collection<Pin> pins);

  @Delete
  Completable delete(Pin... pin);

  @Query("SELECT * FROM Pin WHERE note_id = :id")
  LiveData<Pin> selectById(long id);

  @Query("SELECT * FROM Pin ORDER BY created_on ASC")
  LiveData<List<Pin>> selectByCreatedOnAsc();

  @Query("SELECT * FROM Pin ORDER BY created_on DESC")
  LiveData<List<Pin>> selectByCreatedOnDesc();

  @Query("SELECT * FROM Pin WHERE created_on >= :rangeStart AND created_on < :rangeEnd  ORDER BY created_on ASC")
  LiveData<List<Pin>> selectWhereCreatedOnInRangeByCreatedOnAsc(Instant rangeStart, Instant rangeEnd);

  @Query("SELECT * FROM Pin ORDER BY title ASC")
  LiveData<List<Pin>> selectByTitleAsc();

  @Query("SELECT * FROM Pin ORDER BY title DESC")
  LiveData<List<Pin>> selectByTitleDesc();

  @Query("SELECT * FROM Pin WHERE title LIKE :filter ORDER BY title ASC")
  LiveData<List<Pin>> selectWhereTitleLikeByTitleAsc(String filter);



}
