package edu.cnm.deepdive.trailtrack.controller;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import edu.cnm.deepdive.trailtrack.R;

public class ExplanationFragment extends DialogFragment {

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return new AlertDialog.Builder(requireContext())
        .setTitle(R.string.camera_permission_title)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setMessage(R.string.camera_permission_explanation)
        .setNeutralButton(android.R.string.ok, (dialog, which) -> {
          ((OnDismissListener) requireActivity()).onDismiss(); // Tell activity we are done
        })
        .create();
  }

  public interface OnDismissListener {

    void onDismiss();

  }
}
