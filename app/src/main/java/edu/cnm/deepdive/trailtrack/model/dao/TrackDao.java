package edu.cnm.deepdive.trailtrack.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TrackDao {

  @Insert
  Single<Long> insert(Track track);

  default Single<Track> insertAndReturnTrack(Track track) {
    return insert(track)
        .map((id) -> {
          track.setId(id);
          return track;
        });
  }

  @Update
  Completable update(Track track);

  @Delete
  Completable delete(Track track);

  @Query("SELECT * FROM user WHERE user_id = :id")
  LiveData<Track> select(long id);



}
