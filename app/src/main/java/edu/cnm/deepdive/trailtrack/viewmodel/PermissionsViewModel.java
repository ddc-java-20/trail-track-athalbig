package edu.cnm.deepdive.trailtrack.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

@HiltViewModel
public class PermissionsViewModel extends ViewModel {

  private final MutableLiveData<Map<String, Boolean>> permissionsStatus;

  @Inject
  public PermissionsViewModel() {
    permissionsStatus = new MutableLiveData<>(new HashMap<>());
  }

  public LiveData<Map<String, Boolean>> getPermissionsStatus() {
    return permissionsStatus;
  }

  public void updatePermissionsStatus(Map<String, Boolean> permissionsStatus) {
    Map<String, Boolean> permissions = this.permissionsStatus.getValue();
    //noinspection DataFlowIssue
    permissions.putAll(permissionsStatus);
    this.permissionsStatus.setValue(permissions);
  }

}
