package edu.cnm.deepdive.trailtrack.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.MapsPinsNavGraphDirections;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.adapter.PinsAdapter;
import edu.cnm.deepdive.trailtrack.databinding.FragmentPinsBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.model.entity.Track;
import edu.cnm.deepdive.trailtrack.viewmodel.LocationViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.List;

@AndroidEntryPoint
public class PinsFragment extends Fragment implements OnItemSelectedListener {

  private FragmentPinsBinding binding;
  private PinViewModel pinViewModel;
  private static final String TAG = PinsFragment.class.getSimpleName();
  private List<Track> tracks;
  private Track track;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    binding = FragmentPinsBinding.inflate(inflater, container, false);
    binding.newPin.setOnClickListener((v) -> Navigation.findNavController(binding.getRoot())
        .navigate(MapsPinsNavGraphDirections.editPin()));
    binding.tracks.setOnItemSelectedListener(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    pinViewModel = new ViewModelProvider(requireActivity()).get(PinViewModel.class);
    pinViewModel
        .getTracks()
        .observe(lifecycleOwner, (tracks) -> {
          this.tracks = tracks;
          ArrayAdapter<Track> adapter =
              new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, tracks);
          binding.tracks.setAdapter(adapter);
          selectCurrentTrack();
        });
    pinViewModel.getTrack().observe(lifecycleOwner, (track) -> {
      this.track = track;
      selectCurrentTrack();
    });
    pinViewModel
        .getPins()
        .observe(lifecycleOwner, this::handlePins);
  }

  private void selectCurrentTrack() {
    if (track != null && tracks != null) {
      int position = tracks.indexOf(track);
      binding.tracks.setSelection(position);
    }
  }

  private void handlePins(List<Pin> pins) {
    PinsAdapter adapter;
    adapter = new PinsAdapter(requireContext(), pins, (v, pin, position) -> {

      PopupMenu popup = new PopupMenu(requireContext(), v);
      Menu menu = popup.getMenu();
      popup.getMenuInflater().inflate(R.menu.pin_options, menu);
      menu.findItem(R.id.edit_pin).setOnMenuItemClickListener(item -> {
        Log.d(TAG, String.format("onMenuItemClick: item=%s", item));
        Navigation.findNavController(binding.getRoot())
            .navigate(MapsPinsNavGraphDirections.editPin().setPinId(pin.getId()));
        return true;
      });
      menu
          .findItem(R.id.delete_pin)
          .setOnMenuItemClickListener((item) -> {
            Log.d(TAG, String.format("onMenuItemClick: item=%s", item));
            pinViewModel.delete(pin);
//            adapter.notifyItemRemoved(position);
            return true;
          });
      popup.show();
      return true;
    });
    binding.pins.setAdapter(adapter);
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