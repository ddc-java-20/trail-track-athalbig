package edu.cnm.deepdive.trailtrack.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.trailtrack.R;
import edu.cnm.deepdive.trailtrack.adapter.PinsAdapter;
import edu.cnm.deepdive.trailtrack.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.trailtrack.model.entity.Pin;
import edu.cnm.deepdive.trailtrack.viewmodel.LoginViewModel;
import edu.cnm.deepdive.trailtrack.viewmodel.PinViewModel;
import java.util.List;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements MenuProvider {

  private static final String TAG = HomeFragment.class.getSimpleName();

  private FragmentHomeBinding binding;
  private LoginViewModel loginViewModel;
  private PinViewModel pinViewModel;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
binding.newPin.setOnClickListener((v) -> Navigation.findNavController(binding.getRoot())
    .navigate(HomeFragmentDirections.openEditFragment()));
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
    loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    loginViewModel
        .getAccount()
        .observe(lifecycleOwner, (account) -> {
          if (account == null) {
            Navigation.findNavController(binding.getRoot())
                .navigate(HomeFragmentDirections.navigateToPreLoginFragment());
          }
        });
    requireActivity().addMenuProvider(this, getViewLifecycleOwner(), State.RESUMED);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
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

  @Override
  public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
    menuInflater.inflate(R.menu.pin_actions, menu);
  }

  @Override
  public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
    boolean handled = true;
    if (menuItem.getItemId() == R.id.sign_out) {
      loginViewModel.signOut();
    } else {
      handled = false;
    }
    return handled;
  }
}