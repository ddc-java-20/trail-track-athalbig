package edu.cnm.deepdive.notes.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditFragment extends BottomSheetDialogFragment {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // TODO: 2/18/25 Read any input arguments.
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    // TODO: 2/18/25 Inflate layout and construct & return dialog containing layout.
    return super.onCreateDialog(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // TODO: 2/18/25 Return root element of layout.
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // TODO: 2/18/25 Connect to viewmodel(s) and observe LiveData.
  }

  @Override
  public void onDestroyView() {
    // TODO: 2/18/25 Set binding reference(s) to null.
    super.onDestroyView();
  }
}
