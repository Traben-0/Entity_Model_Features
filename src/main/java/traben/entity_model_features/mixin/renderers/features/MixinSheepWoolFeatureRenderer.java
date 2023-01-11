package traben.entity_model_features.mixin.renderers.features;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMF_CustomModel;


@Mixin(SheepWoolFeatureRenderer.class)
public abstract class MixinSheepWoolFeatureRenderer {

    @ModifyArg(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/SheepWoolFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V"),
            index = 1)
    private EntityModel<?> injected(EntityModel<?> par1) {
        EMFData emfData = EMFData.getInstance();
        int typeHash = par1.hashCode(); // livingEntity.getType().hashCode();

        if (!emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
            String entityTypeName = "sheep_wool";
            emfData.createEMFModel(entityTypeName, typeHash, par1);
        }
        if (emfData.JEMPATH_CustomModel.containsKey(typeHash)) {
            if (emfData.JEMPATH_CustomModel.get(typeHash) != null) {
                return emfData.JEMPATH_CustomModel.get(typeHash);
            }
        }
        return par1;
    }
}
