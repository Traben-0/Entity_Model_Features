package traben.entity_model_features.mixin.mixins.accessor;

//#if MC >= 12100
import net.minecraft.client.DeltaTracker;
//#endif

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {
    //#if MC >= 12102
    @Accessor
    DeltaTracker.Timer getDeltaTracker();
    //#elseif MC == 12100 || MC == 12101
    //$$ @Accessor
    //$$ DeltaTracker.Timer getTimer();
    //#else
    //$$ @Accessor
    //$$ float getPausePartialTick();
    //#endif
}
