package edu.cnm.deepdive.trailtrack.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.adapter.PinsAdapter;
import edu.cnm.deepdive.trailtrack.databinding.FragmentPinsBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.List;

public class PinsFragment extends Fragment {

  private FragmentPinsBinding binding;
  private PinViewModel pinViewModel;
  private static final String TAG = PinsFragment.class.getSimpleName();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    binding = FragmentPinsBinding.inflate(inflater, container, false);
    binding.newPin.setOnClickListener((v) -> Navigation.findNavController(binding.getRoot())
        .navigate(HomeFragmentDirections.openEditFragment()));
    // TODO: 3/27/25 Fix this binding issue. New pin is now in the bottom fragment.
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    pinViewModel = new ViewModelProvider(requireActivity()).get(PinViewModel.class);
    pinViewModel
        .getPins()
        .observe(lifecycleOwner, this::handlePins);
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
            .navigate(HomeFragmentDirections.openEditFragment().setPinId(pin.getId()));
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


}