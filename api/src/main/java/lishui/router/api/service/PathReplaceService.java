package lishui.lib.router.api.service;

import android.net.Uri;

import lishui.lib.router.api.template.IProvider;

/**
 * Preprocess your path
 */
public interface PathReplaceService extends IProvider {

    /**
     * For normal path.
     *
     * @param path raw path
     */
    String forString(String path);

    /**
     * For uri type.
     *
     * @param uri raw uri
     */
    Uri forUri(Uri uri);
}
