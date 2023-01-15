package traben.entity_model_features.mixin.renderers.features;


import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_model_features.EMFData;


@Mixin(SheepWoolFeatureRenderer.class)
public abstract class MixinSheepWoolFeatureRenderer<T extends LivingEntity> {

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/SheepWoolFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V"),
            index = 1)
    private EntityModel<T> injected(EntityModel<T> par1) {
        EMFData emfData = EMFData.getInstance();
        int typeHash = par1.hashCode(); // livingEntity.getType().hashCode();

        if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
            String entityTypeName = "sheep_wool";
            emfData.createEMFModel(entityTypeName, typeHash, par1);
        }
        if (emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
            if (emfData.JEMPATH_CustomModel.get(typeHash) != null) {
                return (EntityModel<T>) emfData.JEMPATH_CustomModel.get(typeHash);
            }
        }
        return par1;
    }
}
