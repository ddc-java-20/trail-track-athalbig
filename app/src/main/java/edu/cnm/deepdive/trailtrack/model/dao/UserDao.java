package edu.cnm.deepdive.trailtrack.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import edu.cnm.deepdive.trailtrack.model.entity.User;
import edu.cnm.deepdive.trailtrack.model.pojo.UserWithPins;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

  @Insert
  Single<Long> insert(User user);

  default Single<User> insertAndReturn(User user) {
    return insert(user)
        .map((id) -> {
          user.setId(id);
          return user;
        });
  }

  @Update
  Completable update(User user);

  @Delete
  Completable delete(User... users);

  @Query("SELECT * FROM user WHERE user_id = :id")
  LiveData<User> select(long id);

  @Transaction
  @Query("SELECT * FROM user WHERE user_id = :id")
  LiveData<UserWithPins> selectWithNotes(long id);

  @Query("SELECT * FROM user WHERE oauth_key = :oauthkey")
  Maybe<User> select(String oauthkey);

}
