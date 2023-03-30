package traben.entity_model_features.mixin.rewrite;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_model_features.utils.EMFManager;

@Mixin(FeatureRenderer.class)
public class Mixin_2_FeatureRenderer {

    @Inject(method = "render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static <T extends LivingEntity> void emf$setAngles(EntityModel<T> contextModel, EntityModel<T> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
        String modelName;
        if (entity instanceof DrownedEntity) {
            modelName = "drowned_outer";
        } else if (entity instanceof SheepEntity) {
            modelName = "sheep_wool";
        } else if (entity instanceof StrayEntity) {
            modelName = "stray_outer";
        } else if (entity instanceof CatEntity) {
            modelName = "cat_collar";
        } else  if (entity instanceof TropicalFishEntity fish) {
            if (fish.getShape() == 0) {
                modelName = "tropical_fish_pattern_a";
            } else {
                modelName = "tropical_fish_pattern_b";
            }
        } else {
            modelName = null;
        }
        if (modelName != null)
            EMFManager.getInstance().setAnglesOnParts(modelName, entity, limbAngle, limbDistance, age, headYaw, headPitch);
    }
}
