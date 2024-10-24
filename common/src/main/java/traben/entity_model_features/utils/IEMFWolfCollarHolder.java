package traben.entity_model_features.utils;

import net.minecraft.client.model.WolfModel;
import net.minecraft.world.entity.animal.Wolf;

public interface #if MC > MC_21 IEMFWolfCollarHolder #else IEMFWolfCollarHolder<T extends Wolf> #endif{

    default boolean emf$hasCollarModel() {
        return emf$getCollarModel() != null;
    }

    #if MC > MC_21 WolfModel #else WolfModel<T> #endif emf$getCollarModel();

    void emf$setCollarModel(#if MC > MC_21 WolfModel #else WolfModel<T> #endif model);


}
