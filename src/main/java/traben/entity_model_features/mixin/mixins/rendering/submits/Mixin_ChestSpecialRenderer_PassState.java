package traben.entity_model_features.mixin.mixins.rendering.submits;

//#if MC >=12109
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(ChestSpecialRenderer.class)
public class Mixin_ChestSpecialRenderer_PassState {

    @Inject(method = "submit", at = @At(value = "HEAD"))
    private static void emf$dummyState(ItemDisplayContext itemDisplayContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, boolean bl, int k, CallbackInfo ci) {
        var state = (EMFEntityRenderState) ETFEntityRenderState.forEntity(
                // TODO do we really need the actual chest type here? this is just so inventory anims can play
                (ETFEntity) new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState()));
        EMFAnimationEntityContext.setCurrentEntityIteration(state);
        EMFManager.getInstance().awaitingState = state;
    }

    @Inject(method = "submit", at = @At(value = "TAIL"))
    private static void emf$reset(CallbackInfo ci) {
        EMFManager.getInstance().awaitingState = null;
    }

}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class Mixin_ChestSpecialRenderer_PassState { }
//#endif