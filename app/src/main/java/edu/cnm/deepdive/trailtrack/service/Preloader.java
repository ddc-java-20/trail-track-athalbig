package edu.cnm.deepdive.trailtrack.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.google.gson.Gson;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.model.dao.PinDao;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.inject.Inject;
import javax.inject.Provider;

public class Preloader extends RoomDatabase.Callback {


  private final Context context;
  private final Provider<PinDao> pinDaoProvider;
  private final Gson gson;

  @Inject
  Preloader(@ApplicationContext Context context, Provider<PinDao> pinDaoProvider, Gson gson) {
    this.context = context;
    this.pinDaoProvider = pinDaoProvider;
    this.gson = gson;
  }

  @Override
  public void onCreate(@NonNull SupportSQLiteDatabase db) {
    super.onCreate(db);

    PinDao pinDao = pinDaoProvider.get();

    //Need an input stream
    try (
        InputStream input = context.getResources().openRawResource(R.raw.preload);
        Reader reader = new InputStreamReader(input)
    ) {
      Pin[] pins = gson.fromJson(reader, Pin[].class);
      pinDao
          .insert(pins)
          .subscribeOn(Schedulers.io())
          .subscribe();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
