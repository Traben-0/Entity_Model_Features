package traben.entity_model_features.models.animation.state;

import org.jetbrains.annotations.Nullable;

public class EMFSubmitData {

    public EMFEntityRenderState backupState = null;

    public int modelVariant = -1;

    public EMFBipedPose bipedPose = null;


    /**
     * These fields are used as a sort of "thread local" for passing data easily into new submit instances
     */
    public static EMFEntityRenderState AWAITING_backupState = null;
    public static EMFBipedPose AWAITING_bipedPose = null;

    //#if MC >= 1.21.9
    @Nullable
    public static EMFSubmitData from(net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<?> modelSubmit) {
        //noinspection ConstantValue
        return ((Object) modelSubmit) instanceof EMFSubmitExtension emf
                ? ((EMFSubmitExtension) (Object) modelSubmit).emf$getData()
                : null;
    }
    //#endif

}
