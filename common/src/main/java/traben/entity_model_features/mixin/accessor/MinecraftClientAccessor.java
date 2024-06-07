package traben.entity_model_features.mixin.accessor;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {

    #if MC >= MC_21
    @Accessor
    DeltaTracker.Timer getTimer();
    #else
    @Accessor
    float getPausePartialTick();
    #endif
}
