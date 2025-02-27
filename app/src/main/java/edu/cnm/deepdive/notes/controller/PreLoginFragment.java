package edu.cnm.deepdive.notes.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.notes.R;
import edu.cnm.deepdive.notes.viewmodel.LoginViewModel;

@AndroidEntryPoint
public class PreLoginFragment extends Fragment {

  private LoginViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_pre_login, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity())
        .get(LoginViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
    viewModel
        .getAccount()
        .observe(owner, (account) -> {
          if (account != null) {
            // TODO: 2/27/25 navigate to HomeFragment.
          }
        });
    viewModel
        .getRefreshThrowable()
        .observe(owner, (throwable) -> {
          if (throwable != null) {
            // TODO: 2/27/25 Navigate to login fragment.
          }
        });
    viewModel.refresh();
  }

}
