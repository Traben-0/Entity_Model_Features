package traben.entity_model_features.utils;

import net.minecraft.client.model.WolfModel;
#if MC>=MC_21_5
import net.minecraft.world.entity.animal.wolf.Wolf;
#else
import net.minecraft.world.entity.animal.Wolf;
#endif

public interface #if MC > MC_21 IEMFWolfCollarHolder #else IEMFWolfCollarHolder<T extends Wolf> #endif{

    default boolean emf$hasCollarModel(boolean baby) {
        return emf$getCollarModel(baby) != null;
    }

    #if MC > MC_21 WolfModel #else WolfModel<T> #endif emf$getCollarModel(boolean baby);

    void emf$setCollarModel(#if MC > MC_21 WolfModel #else WolfModel<T> #endif model, boolean baby);


}
