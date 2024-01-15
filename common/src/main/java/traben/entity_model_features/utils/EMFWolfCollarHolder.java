package traben.entity_model_features.utils;

import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;

public interface EMFWolfCollarHolder<T extends WolfEntity> {

    default boolean emf$hasCollarModel(){
        return emf$getCollarModel() != null;
    }

    WolfEntityModel<T> emf$getCollarModel();
    void emf$setCollarModel(WolfEntityModel<T> model);
}
