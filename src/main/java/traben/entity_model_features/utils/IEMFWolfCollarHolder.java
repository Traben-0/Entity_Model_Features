package traben.entity_model_features.utils;

import net.minecraft.client.model.WolfModel;
//#if MC>=12105
import net.minecraft.world.entity.animal.wolf.Wolf;
//#else
//$$ import net.minecraft.world.entity.animal.Wolf;
//#endif

public interface
//#if MC >= 12102
    IEMFWolfCollarHolder
//#else
//$$     IEMFWolfCollarHolder<T extends Wolf>
//#endif
{

    default boolean emf$hasCollarModel() {
        return emf$getCollarModel() != null;
    }

    //#if MC >= 12102
    WolfModel
    //#else
    //$$ WolfModel<T>
    //#endif
        emf$getCollarModel();

    void emf$setCollarModel(
            //#if MC >= 12102
            WolfModel
            //#else
            //$$ WolfModel<T>
            //#endif
            model);


}
