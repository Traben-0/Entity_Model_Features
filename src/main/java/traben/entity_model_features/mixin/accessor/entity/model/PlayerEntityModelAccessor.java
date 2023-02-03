package traben.entity_model_features.mixin.accessor.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntityModel.class)
public interface PlayerEntityModelAccessor {
    @Mutable
    @Accessor
    void setLeftSleeve(ModelPart leftSleeve);

    @Mutable
    @Accessor
    void setRightSleeve(ModelPart rightSleeve);

    @Mutable
    @Accessor
    void setLeftPants(ModelPart leftPants);

    @Mutable
    @Accessor
    void setRightPants(ModelPart rightPants);

    @Mutable
    @Accessor
    void setJacket(ModelPart jacket);

    @Mutable
    @Accessor
    void setCloak(ModelPart cloak);

    @Mutable
    @Accessor
    void setEar(ModelPart ear);

    @Accessor
    boolean isThinArms();
}
