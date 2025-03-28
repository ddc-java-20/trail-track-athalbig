package edu.cnm.deepdive.trailtrack.controller;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import edu.cnm.deepdive.trailtrack.R;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ExplanationFragment extends DialogFragment {

  private String[] permissionsToExplain;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ExplanationFragmentArgs args = ExplanationFragmentArgs.fromBundle(getArguments());
    permissionsToExplain = args.getPermissionsToExplain();
  }

  // TODO: 3/28/25 figure out how to convert strings into permissions to explain
  //  into the names of string resources, so I can look those up and provide regular text.

  // TODO: 3/28/25 Can try getIdentifier in Resources class. 3 params context (requirecontexT) and ...
  //  name of resource, type of resource, package of the resource context.getpackagename

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    return new AlertDialog.Builder(requireContext())
        .setTitle(R.string.permissions_explanation_title)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setMessage(Arrays.stream(permissionsToExplain).collect(Collectors.joining("\n")))
        .setNeutralButton(android.R.string.ok, (dialog, which) -> {
          ((OnDismissListener) requireActivity()).onDismiss(); // Tell activity we are done
        })
        .create();
  }

  public interface OnDismissListener {

    void onDismiss();

  }
}
