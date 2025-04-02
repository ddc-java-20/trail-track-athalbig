package edu.cnm.deepdive.trailtrack.service;

import androidx.core.content.FileProvider;
import edu.cnm.deepdive.trailtrack.R;

/**
 * A specialized implementation of {@link FileProvider} for managing access to image files within
 * the app. This class extends the functionality of {@code FileProvider} by providing file path
 * mappings defined in the `provider_paths` resource.
 *
 * This class is primarily used to securely grant access to image files stored in the app's private
 * storage to other components or applications, for example, when sharing images via intents.
 *
 * Note: This class uses the {@code R.xml.provider_paths} file to define URI mappings for secure
 * file sharing.
 */
public class ImageFileProvider extends FileProvider {

  public ImageFileProvider() {
    super(R.xml.provider_paths); // Initialize with our provider-path mapping
  }

}
