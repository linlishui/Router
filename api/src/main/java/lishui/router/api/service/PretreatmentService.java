package lishui.lib.router.api.service;

import android.content.Context;

import lishui.lib.router.api.Postcard;
import lishui.lib.router.api.template.IProvider;


/**
 * Pretreatment service used for check if need navigation.
 */
public interface PretreatmentService extends IProvider {
    /**
     * Do something before navigation.
     *
     * @param context  context
     * @param postcard meta
     * @return if need navigation.
     */
    boolean onPretreatment(Context context, Postcard postcard);
}
