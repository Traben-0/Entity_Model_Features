package traben.entity_model_features.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFManager;

@Mixin(Player.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "interactOn", at = @At("HEAD"))
    private void emf$injected(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (EMF.config().getConfig().debugOnRightClick && ((LivingEntity) (Object) this).level().isClientSide()) {
            EMFManager.getInstance().entityForDebugPrint = entity.getUUID();
        }
    }

}
