package edu.cnm.deepdive.trailtrack.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

/**
 * ViewModel class for managing permissions status. This class is designed
 * to maintain and update permissions states as a central repository for UI
 * controllers that may need to observe or update the status of multiple
 * permissions. It interacts with the UI layer to propagate changes and
 * ensures that the state persists across configuration changes.
 *
 * This class uses dependency injection provided by Hilt and leverages
 * LiveData to expose its state in a lifecycle-aware manner.
 *
 * Key responsibilities:
 * - Maintain the status of permissions, represented as a map where key is
 *   the permission name and value is a boolean indicating its granted status.
 * - Provide a LiveData object to expose permissions status to observers.
 * - Allow updates to the permission status, triggering updates to observers
 *   when changes occur.
 *
 * Dependencies, lifecycle management, and architecture principles are at
 * the core of the design of this implementation.
 */
@HiltViewModel
public class PermissionsViewModel extends ViewModel {

  private final MutableLiveData<Map<String, Boolean>> permissionsStatus;

  /**
   * Constructor for the PermissionsViewModel class. This constructor initializes the
   * {@code permissionsStatus} as a {@link MutableLiveData} instance containing an empty map.
   * The {@code permissionsStatus} is used to represent the current status of app permissions,
   * with the permission name as the key and the granted status (true/false) as the corresponding value.
   *
   * This constructor is annotated with {@link Inject} to allow for dependency injection via Hilt,
   * ensuring proper instantiation and lifecycle management within the dependency injection framework.
   */
  @Inject
  public PermissionsViewModel() {
    permissionsStatus = new MutableLiveData<>(new HashMap<>());
  }

  /**
   * Retrieves the current permissions status, represented as a {@link LiveData} object containing a map
   * with permission names as keys and their granted statuses (true/false) as values. This allows
   * lifecycle-aware observers to monitor changes in the permissions status.
   *
   * @return a {@link LiveData} object wrapping a map where each key is a permission name
   *         and the corresponding value is a {@code boolean} indicating whether the permission
   *         is granted ({@code true}) or not ({@code false}).
   */
  public LiveData<Map<String, Boolean>> getPermissionsStatus() {
    return permissionsStatus;
  }

  /**
   * Updates the current permissions status by merging the provided map of permission names and
   * their granted statuses into the existing permissions status map. The updated map is then
   * set as the value of the {@code permissionsStatus} LiveData, allowing observers to be
   * notified of the changes.
   *
   * @param permissionsStatus a map where each key is a permission name and the corresponding
   *                          value is a {@code boolean} indicating whether the permission is
   *                          granted ({@code true}) or not ({@code false}).
   */
  public void updatePermissionsStatus(Map<String, Boolean> permissionsStatus) {
    Map<String, Boolean> permissions = this.permissionsStatus.getValue();
    //noinspection DataFlowIssue
    permissions.putAll(permissionsStatus);
    this.permissionsStatus.setValue(permissions);
  }

}
