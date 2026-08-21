package traben.entity_model_features.mixin.mixins.rendering;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_texture_features.features.state.ETFState;

//#if MC < 12102
//$$ import net.minecraft.world.entity.Entity;
//$$ import traben.entity_texture_features.features.state.ETFEntityRenderState;
//$$ import traben.entity_texture_features.utils.ETFEntity;
//#else
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//#endif

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

    private static final String RENDER_ETF =
            //#if MC >= 12109
            "submit"
            //#elseif MC >= 12105
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            //#elseif MC >= 12102
            //$$ "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V"
            //#else
            //$$ "render"
            //#endif
            ;


    @Inject(method = RENDER_ETF, at = @At(value = "RETURN"))
    //#if MC >= 12102
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState> void emf$endOfRender(
            final CallbackInfo ci
    //#if MC >= 12105
            , @Local(argsOnly = true) S state
    ) {
        EMFEntityRenderState emfState = EMFEntityRenderState.from(state);
    //#else
    //$$ ) {
    //$$ EMFEntityRenderState emfState = EMFState.state();
    //#endif
        // todo likely extremely broken in 1.21.9
        if (EMFState.announceModels) {
            EMFState.anounceModels(emfState);
        }
    }
    //#else
    //$$ private <E extends Entity> void emf$endOfRender( CallbackInfo ci, @Local(argsOnly = true) E entity) {
    //$$     if (EMFState.announceModels) {
    //$$         EMFState.anounceModels(EMFState.state());
    //$$     }
    //$$ }
    //$$
    //#endif

    //region shadow modification

    private static final String SHADOW_RENDER_ETF =
            //#if MC >= 12109
            "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShadow(Lcom/mojang/blaze3d/vertex/PoseStack;FLjava/util/List;)V"
            //#elseif MC >= 12105
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FLnet/minecraft/world/level/LevelReader;F)V"
            //#elseif MC >= 12102
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;FFLnet/minecraft/world/level/LevelReader;F)V"
            //#else
            //$$ "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"
            //#endif
            ;

    //#if MC >= 1.21.9
    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            shift = At.Shift.AFTER))
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState> void
    postSubmitTweaks(CallbackInfo ci, @Local EntityRenderer entityRenderer, @Local(argsOnly = true) S ogState) {

        var state = EMFEntityRenderState.from(ogState);
        if (state == null) return;

        ETFState.stackVerify(state);

        if (state.needsToModifyShadow()) {
            var x = ogState.x;
            var z = ogState.z;
            boolean useShadowX = !Float.isNaN(state.shadowX());
            boolean useShadowZ = !Float.isNaN(state.shadowZ());
            if (useShadowX) ogState.x += state.shadowX();
            if (useShadowZ) ogState.z += state.shadowZ();
            // Size and opacity are set from MixinEntityRenderer

            // Recalculate shadow that has been modified, this now needs to run before the actual shadow call so that its
            // surrounding condition check can pass or fail based on the state values
            //noinspection unchecked
            entityRenderer.extractShadow(ogState, Minecraft.getInstance(), EMFState.state().world());

            if (useShadowX) ogState.x = x;
            if (useShadowZ) ogState.z = z;
        }
    }

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF))
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
    void preShadow(CallbackInfo ci, @Local PoseStack poseStack, @Local(argsOnly = true) S ogState) {
        var state = EMFEntityRenderState.from(ogState);
        if (state == null || (Float.isNaN(state.shadowX()) && Float.isNaN(state.shadowZ()))) return;

        poseStack.translate(Float.isNaN(state.shadowX()) ? 0 : state.shadowX(), 0, Float.isNaN(state.shadowZ()) ? 0 : state.shadowZ());
    }

    @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.AFTER))
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
    void postShadow(CallbackInfo ci, @Local PoseStack poseStack, @Local(argsOnly = true) S ogState) {
        var state = EMFEntityRenderState.from(ogState);
        if (state == null || (Float.isNaN(state.shadowX()) && Float.isNaN(state.shadowZ()))) return;

        poseStack.translate(Float.isNaN(state.shadowX()) ? 0 : -state.shadowX(), 0, Float.isNaN(state.shadowZ()) ? 0 : -state.shadowZ());
    }
    //#elseif MC >= 1.21.2
    //$$
    //$$
    //$$ @ModifyExpressionValue(method = RENDER_ETF, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowRadius(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)F"))
    //$$ private float radius(float original) {
    //$$     var state = EMFState.state();
    //$$     if (state != null && !Float.isNaN(state.shadowSize())) {
    //$$         return state.shadowSize();
    //$$     }
    //$$     return original;
    //$$ }
    //$$
    //$$ @ModifyExpressionValue(method = RENDER_ETF, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowStrength(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)F"))
    //$$ private float strength(float original) {
    //$$     var state = EMFState.state();
    //$$     if (state != null && !Float.isNaN(state.shadowOpacity())) {
    //$$         return state.shadowOpacity();
    //$$     }
    //$$     return original;
    //$$ }
    //$$
    //$$ @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF))
    //$$ private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
    //$$ void preShadow(CallbackInfo ci, @Local PoseStack poseStack, @Local(argsOnly = true) S ogState) {
    //$$     var state = EMFEntityRenderState.from(ogState);
    //$$     if (state == null || !state.needsToModifyShadow()) return;
    //$$
    //$$     ETFState.stackVerify(state);
    //$$
    //$$     if (!Float.isNaN(state.shadowX())) ogState.x += state.shadowX();
    //$$     if (!Float.isNaN(state.shadowZ())) ogState.z += state.shadowZ();
    //$$
    //$$     poseStack.translate(Float.isNaN(state.shadowX()) ? 0 : state.shadowX(), 0, Float.isNaN(state.shadowZ()) ? 0 : state.shadowZ());
    //$$ }
    //$$
    //$$ @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.AFTER))
    //$$ private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
    //$$ void postShadow(CallbackInfo ci, @Local PoseStack poseStack, @Local(argsOnly = true) S ogState) {
    //$$     var state = EMFEntityRenderState.from(ogState);
    //$$     if (state == null || !state.needsToModifyShadow()) return;
    //$$
    //$$     if (!Float.isNaN(state.shadowX())) ogState.x -= state.shadowX();
    //$$     if (!Float.isNaN(state.shadowZ())) ogState.z -= state.shadowZ();
    //$$
    //$$     poseStack.translate(Float.isNaN(state.shadowX()) ? 0 : -state.shadowX(), 0, Float.isNaN(state.shadowZ()) ? 0 : -state.shadowZ());
    //$$ }
    //#else
    //$$
         //#if MC >= 1.21
         //$$ @ModifyExpressionValue(method = RENDER_ETF, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getShadowRadius(Lnet/minecraft/world/entity/Entity;)F"))
         //#else
         //$$ @ModifyExpressionValue(method = RENDER_ETF, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;shadowRadius:F", opcode = Opcodes.GETFIELD))
         //#endif
    //$$ private float radius(float original) {
    //$$     var state = EMFState.state();
    //$$     if (state != null && !Float.isNaN(state.shadowSize())) {
    //$$         return state.shadowSize();
    //$$     }
    //$$     return original;
    //$$ }
    //$$
    //$$ @ModifyExpressionValue(method = RENDER_ETF, at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;shadowStrength:F", opcode = Opcodes.GETFIELD))
    //$$ private float strength(float original) {
    //$$     var state = EMFState.state();
    //$$     if (state != null && !Float.isNaN(state.shadowOpacity())) {
    //$$         return state.shadowOpacity();
    //$$     }
    //$$     return original;
    //$$ }
    //$$
    //$$ @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF))
    //$$ private void preShadow(CallbackInfo ci, @Local PoseStack poseStack, @Local(argsOnly = true) Entity entity) {
    //$$     var state = EMFState.state();
    //$$     if (state == null || !state.needsToModifyShadow()) return;
    //$$
    //$$     entity.setPos(
    //$$             entity.position().x + (Float.isNaN(state.shadowX()) ? 0 : state.shadowX()),
    //$$             entity.position().y,
    //$$             entity.position().z + (Float.isNaN(state.shadowZ()) ? 0 : state.shadowZ())
    //$$     );
    //$$
    //$$     poseStack.translate(Float.isNaN(state.shadowX()) ? 0 : state.shadowX(), 0, Float.isNaN(state.shadowZ()) ? 0 : state.shadowZ());
    //$$ }
    //$$
    //$$ @Inject(method = RENDER_ETF, at = @At(value = "INVOKE", target = SHADOW_RENDER_ETF, shift = At.Shift.AFTER))
    //$$ private void postShadow(CallbackInfo ci, @Local PoseStack poseStack, @Local(argsOnly = true) Entity entity) {
    //$$     var state =EMFState.state();
    //$$     if (state == null || !state.needsToModifyShadow()) return;
    //$$
    //$$     entity.setPos(
    //$$             entity.position().x + (Float.isNaN(state.shadowX()) ? 0 : -state.shadowX()),
    //$$             entity.position().y,
    //$$             entity.position().z + (Float.isNaN(state.shadowZ()) ? 0 : -state.shadowZ())
    //$$     );
    //$$
    //$$     poseStack.translate(Float.isNaN(state.shadowX()) ? 0 : -state.shadowX(), 0, Float.isNaN(state.shadowZ()) ? 0 : -state.shadowZ());
    //$$ }
    //#endif

    //endregion

    //region flame modification

    //#if MC >= 1.21.2 && MC < 1.21.9
    //$$ @ModifyExpressionValue(method = "renderFlame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;boundingBoxWidth:F", opcode = Opcodes.GETFIELD))
    //$$ private float width(float original, @Local EntityRenderState vanilla, @Local PoseStack pose) {
    //$$     var state = EMFEntityRenderState.from(vanilla);
    //$$     if (state == null) return original;
    //$$
    //$$     if (state.needsToModifyFire()) {
    //$$         pose.translate(
    //$$                 Float.isNaN(state.fireX()) ? 0 : state.fireX(),
    //$$                 Float.isNaN(state.fireY()) ? 0 : state.fireY(),
    //$$                 Float.isNaN(state.fireZ()) ? 0 : state.fireZ()
    //$$         );
    //$$
    //$$         if (!Float.isNaN(state.fireScale())) {
    //$$             return state.fireScale();
    //$$         }
    //$$     }
    //$$     return original;
    //$$ }
    //$$
    //$$ @ModifyExpressionValue(method = "renderFlame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;boundingBoxHeight:F", opcode = Opcodes.GETFIELD))
    //$$ private float height(float original, @Local EntityRenderState vanilla) {
    //$$     var state = EMFEntityRenderState.from(vanilla);
    //$$     if (state != null && !Float.isNaN(state.fireHeight())) {
    //$$         return state.fireHeight();
    //$$     }
    //$$     return original;
    //$$ }
    //#elseif MC < 1.21.2
    //$$ @ModifyExpressionValue(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBbWidth()F"))
    //$$ private float width(float original, @Local PoseStack pose) {
    //$$     var state = EMFState.state();
    //$$     if (state == null) return original;
    //$$
    //$$     if (state.needsToModifyFire()) {
    //$$         pose.translate(
    //$$                 Float.isNaN(state.fireX()) ? 0 : state.fireX(),
    //$$                 Float.isNaN(state.fireY()) ? 0 : state.fireY(),
    //$$                 Float.isNaN(state.fireZ()) ? 0 : state.fireZ()
    //$$         );
    //$$
    //$$         if (!Float.isNaN(state.fireScale())) {
    //$$             return state.fireScale();
    //$$         }
    //$$     }
    //$$     return original;
    //$$ }
    //$$
    //$$ @ModifyExpressionValue(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getBbHeight()F"))
    //$$ private float height(float original) {
    //$$     var state = EMFState.state();
    //$$     if (state != null && !Float.isNaN(state.fireHeight())) {
    //$$         return state.fireHeight();
    //$$     }
    //$$     return original;
    //$$ }
    //#endif

    //endregion
}
