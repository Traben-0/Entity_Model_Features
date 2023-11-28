package traben.entity_model_features.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFManager;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "interact", at = @At("HEAD"))
    private void emf$injected(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (EMFConfig.getConfig().debugOnRightClick && ((LivingEntity) (Object) this).getWorld().isClient()) {
            EMFManager.getInstance().entityForDebugPrint = entity.getUuid();
        }
    }

}
