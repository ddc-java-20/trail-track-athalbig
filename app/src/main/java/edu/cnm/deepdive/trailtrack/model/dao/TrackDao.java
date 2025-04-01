package edu.cnm.deepdive.trailtrack.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.model.pojo.TrackWithPins;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;

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

  @Insert
  Single<List<Long>> insert(Collection<?extends Track> tracks);

  @Insert
  Single<List<Long>> insert(Track... tracks);

  @Update
  Completable update(Track track);

  @Delete
  Completable delete(Track track);

  @Transaction
  @Query("SELECT * FROM track WHERE user_id = :id ORDER BY name ASC")
  LiveData<List<TrackWithPins>> selectByUser(long id);

  @Transaction
  @Query("SELECT * FROM track ORDER BY name ASC")
  LiveData<List<Track>> selectAll();

}
