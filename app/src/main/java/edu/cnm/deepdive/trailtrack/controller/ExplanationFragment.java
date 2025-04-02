package edu.cnm.deepdive.trailtrack.controller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import edu.cnm.deepdive.trailtrack.R;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ExplanationFragment is a subclass of DialogFragment that displays an alert dialog
 * explaining the reason for requesting specific permissions. The message is dynamically
 * constructed based on the permissions passed to the fragment.
 *
 * The permissions to be explained are provided as arguments to this fragment and are
 * extracted during initialization. These permissions are then converted to corresponding
 * string resource names, and the associated text resources are fetched and compiled
 * into a readable explanation message for the user.
 *
 * A parent fragment must implement the OnDismissListener interface to handle the dismissal
 * of the dialog. This callback ensures that the parent fragment is notified when the dialog
 * is dismissed.
 *
 * Key Features:
 * - Dynamically generates explanatory messages for permissions.
 * - Provides an AlertDialog to present the explanation.
 * - Notifies the parent fragment when the dialog is dismissed via a callback interface.
 *
 * The fragment requires the parent context to provide permission explanation string resources
 * in the format `<permission_name>_explanation`.
 */
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
    Resources resources = getResources();
    String packageName = requireActivity().getPackageName();
    @SuppressLint("DiscouragedApi") String message = Arrays.stream(permissionsToExplain)
        .map(permission -> permission.substring(permission.lastIndexOf('.') + 1))
        .map(String::toLowerCase)
        .map(permission -> permission.replace('.', '_'))
        .map(permission -> permission + "_explanation")
        .mapToInt(permission -> resources.getIdentifier(permission, "string", packageName))
        .mapToObj(resources::getString)
        .filter(string -> !string.isEmpty())
        .collect(Collectors.joining("\n"));

    OnDismissListener listener =
        (OnDismissListener) getParentFragment().getParentFragment();

    return new AlertDialog.Builder(requireContext())
        .setTitle(R.string.permissions_explanation_title)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setMessage(message)
        .setNeutralButton(android.R.string.ok, (dialog, which) -> {
          listener.onDismiss(); // Tell activity we are done
        })
        .create();
  }

  /**
   * Interface definition for a callback to be invoked when a dismiss event occurs.
   *
   * Implement this interface to handle the dismissal of a dialog or similar component.
   */
  public interface OnDismissListener {

    void onDismiss();

  }
}
