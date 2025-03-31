package edu.cnm.deepdive.trailtrack.viewmodel;


import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.trailtrack.service.LocationService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javax.inject.Inject;

@HiltViewModel
public class LocationViewModel extends ViewModel implements DefaultLifecycleObserver {

  private final LocationService locationService;
  // TODO: 3/31/25 Add location, somehow.
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;


  @Inject
  LocationViewModel(LocationService locationService, MutableLiveData<Throwable> throwable) {
    this.locationService = locationService;
    this.throwable = throwable;
    this.pending = new CompositeDisposable();
  }

  public LocationService getLocationService() {
    return locationService.getLocation();
  }

}