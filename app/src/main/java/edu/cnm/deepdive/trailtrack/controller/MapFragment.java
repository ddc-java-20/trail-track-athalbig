package edu.cnm.deepdive.trailtrack.controller;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.databinding.FragmentMapBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.viewmodel.LocationViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PermissionsViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.List;

@AndroidEntryPoint
public class MapFragment extends Fragment implements OnMapReadyCallback, OnItemSelectedListener {

  private PermissionsViewModel permissionsViewModel;
  private LocationViewModel locationViewModel;
  private PinViewModel pinViewModel;
  private FragmentMapBinding binding;
  private boolean currentLocationEnabled;
  private GoogleMap map;
  private List<Track> tracks;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentMapBinding.inflate(inflater, container, false);
    binding.mainMap.onCreate(savedInstanceState);
    binding.mainMap.getMapAsync(this);
    binding.tracks.setOnItemSelectedListener(this);
    // TODO: 3/31/25 Attach listeners to UI widgets
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    FragmentActivity activity = requireActivity();
    ViewModelProvider provider = new ViewModelProvider(activity);
    LifecycleOwner owner = getViewLifecycleOwner();

    locationViewModel = provider.get(LocationViewModel.class);
    locationViewModel.getLocation()
        .observe(owner, location -> {
          CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
          map.moveCamera(cameraUpdate);
        });
    getLifecycle().addObserver(locationViewModel);
    permissionsViewModel = provider.get(PermissionsViewModel.class);
    permissionsViewModel
        .getPermissionsStatus()
        .observe(owner, (permissions) -> {
          if (Boolean.TRUE.equals(permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false))) {
            locationViewModel.startService();
            currentLocationEnabled = true;
          } else {
            currentLocationEnabled = false;
          }
        });
    pinViewModel = new ViewModelProvider(requireActivity()).get(PinViewModel.class);
    pinViewModel
        .getTracks()
        .observe(owner, (tracks) -> {
          this.tracks = tracks;
          ArrayAdapter<Track> adapter =
              new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tracks);
          binding.tracks.setAdapter(adapter);
        });
    pinViewModel.getTrack().observe(owner, (track) -> {
      int position = tracks.indexOf(track);
      binding.tracks.setSelection(position);
    });
    pinViewModel
        .getPins()
        .observe(owner, pins ->
            handlePins(pins));
  }

  private void handlePins(List<Pin> pins) {
    // TODO: 4/1/25 Iterate over pins, create marker for each, and add it to map (googleMap Map field)
  }

  @Override
  public void onStart() {
    super.onStart();
    binding.mainMap.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    binding.mainMap.onResume();
  }

  @Override
  public void onPause() {
    binding.mainMap.onPause();
    super.onPause();
  }

  @Override
  public void onStop() {
    binding.mainMap.onStop();
    super.onStop();
  }

  @Override
  public void onDestroyView() {
    binding.mainMap.onDestroy();
    super.onDestroyView();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    binding.mainMap.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onLowMemory() {
    binding.mainMap.onLowMemory();
    super.onLowMemory();
  }

  /**
   * Manipulates the map once available. This callback is triggered when the map is ready to be
   * used. This is where we can add markers or lines, add listeners or move the camera. In this
   * case, we just add a marker near Sydney, Australia. If Google Play services is not installed on
   * the device, the user will be prompted to install it inside the SupportMapFragment. This method
   * will only be triggered once the user has installed Google Play services and returned to the
   * app.
   */

  @SuppressLint("MissingPermission")
  @Override
  public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    if (currentLocationEnabled) {
      googleMap.setMyLocationEnabled(true);
    }
//    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int position, long generatedId) {
    pinViewModel.setTrack((Track) adapterView.getItemAtPosition(position));
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {
//No action needed, this doesn't happen
  }
}
