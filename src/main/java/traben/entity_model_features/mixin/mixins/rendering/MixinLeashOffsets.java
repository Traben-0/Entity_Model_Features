package traben.entity_model_features.mixin.mixins.rendering;
//#if MC < 1.21
//$$ import net.minecraft.client.renderer.entity.EntityRenderer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public abstract class MixinLeashOffsets { }
//#else
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import traben.entity_model_features.utils.EMFEntity;

import java.util.Map;


@Mixin(EntityRenderer.class)
public class MixinLeashOffsets {

    //#if MC >= 1.21.2
    private static final String METHOD = "extractRenderState";
    //#else
    //$$ private static final String METHOD = "renderLeash";
    //#endif

    //#if MC >= 1.21.2
    @ModifyExpressionValue(method = METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;getLeashHolder()Lnet/minecraft/world/entity/Entity;"))
    private Entity getLeashHolder(Entity original, @Share("holder") LocalRef<Entity> roper) {
        roper.set(original);
        return original;
    }
    //#endif

    //#if MC >= 12106
    @ModifyExpressionValue(method = METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;getLeashOffset(F)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 emf$leash(Vec3 original, @Local net.minecraft.world.entity.Leashable leashable) {
    //#else
    //$$ @ModifyExpressionValue(method = METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getLeashOffset(F)Lnet/minecraft/world/phys/Vec3;"))
    //$$ private Vec3 emf$leash(Vec3 original, @Local(ordinal = 0, argsOnly = true) Entity leashable) {
    //#endif
        return leashModify(original, "", ((EMFEntity) leashable).emf$getVariableMap(), false);
    }

    @ModifyExpressionValue(method = METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getRopeHoldPosition(F)Lnet/minecraft/world/phys/Vec3;"))
    //#if MC >= 1.21.2
    private Vec3 emf$leashHold(Vec3 original, @Share("holder") LocalRef<Entity> roper) {
        return leashModify(original, "", ((EMFEntity) roper.get()).emf$getVariableMap(), true);
    }
    //#else
    //$$ private Vec3 emf$leashHold(Vec3 original, @Local(ordinal = 1, argsOnly = true) Entity roper) {
    //$$     return leashModify(original, "", ((EMFEntity) roper).emf$getVariableMap(), true);
    //$$ }
    //#endif

    @Unique
    private Vec3 leashModify(Vec3 original, String variant, Map<String, Float> variable, boolean holder) {
        String type = holder ? "_holder" : "";
        var modifiedX = variable.getOrDefault("render.leash" + type + "_offset_x" + variant, 0f);
        var modifiedY = variable.getOrDefault("render.leash" + type + "_offset_y" + variant, 0f);
        var modifiedZ = variable.getOrDefault("render.leash" + type + "_offset_z" + variant, 0f);
        if (modifiedX != 0 || modifiedY != 0 || modifiedZ != 0) {
            foundQuadLeashHolderOffsets = true;
            return original.add(modifiedX, modifiedY, modifiedZ);
        }
        return original;
    }

    @Unique private boolean foundQuadLeashHolderOffsets = false;

    //#if MC >= 12106
    @ModifyExpressionValue(method = METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;getQuadLeashOffsets()[Lnet/minecraft/world/phys/Vec3;"))
    private Vec3[] emf$leashQuad(Vec3[] original, @Local net.minecraft.world.entity.Leashable leashable) {
        return quad(original, (EMFEntity) leashable, false);
    }

    @ModifyExpressionValue(method = METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getQuadLeashHolderOffsets()[Lnet/minecraft/world/phys/Vec3;"))
    private Vec3[] emf$leashQuadHolder(Vec3[] original, @Share("holder") LocalRef<Entity> roper) {
        return quad(original, (EMFEntity) roper.get(), true);
    }

    @Unique
    private Vec3 @NotNull [] quad(Vec3[] original, EMFEntity leashable, boolean holder) {
        foundQuadLeashHolderOffsets = false;
        var map = leashable.emf$getVariableMap();
        original[0] = leashModify(original[0], "_1", map, holder);
        original[1] = leashModify(original[1], "_2", map, holder);
        original[2] = leashModify(original[2], "_3", map, holder);
        original[3] = leashModify(original[3], "_4", map, holder);
        if (!foundQuadLeashHolderOffsets) {
            var og = original[0];
            original[0] = leashModify(og, "", map, holder);
            if (!og.equals(original[0])) {
                original[1] = leashModify(original[1], "", map, holder);
                original[2] = leashModify(original[2], "", map, holder);
                original[3] = leashModify(original[3], "", map, holder);
            }
        }
        return original;
    }
    //#endif
}
//#endif
