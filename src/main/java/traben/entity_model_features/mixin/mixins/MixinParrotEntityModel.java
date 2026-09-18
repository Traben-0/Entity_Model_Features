package traben.entity_model_features.mixin.mixins;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.state.EMFState;

import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import traben.entity_model_features.utils.EMFUtils;

@Mixin(ParrotOnShoulderLayer.class)
public class MixinParrotEntityModel {

    @Unique
    private static final ModelLayerLocation emf$parrot_shoulder =
            new ModelLayerLocation(EMFUtils.res("minecraft", "parrot"), "shoulder");

    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/EntityModelSet;bakeLayer(Lnet/minecraft/client/model/geom/ModelLayerLocation;)Lnet/minecraft/client/model/geom/ModelPart;"))
    private ModelPart emf$injectParrotShoulderLayer(ModelPart original) {
        if (EMF.testForForgeLoadingError()) return original;

        return EMFManager.getInstance().injectIntoModelRootGetter(emf$parrot_shoulder, ParrotModel.createBodyLayer().bakeRoot());
    }


    //#if MC >= 12109
    private static final String RENDER_METHOD = "submitOnShoulder";
    //#elseif MC > 1.21.2
    //$$ private static final String RENDER_METHOD = "renderOnShoulder";
    //#else
    //$$ private static final String RENDER_METHOD = "method_17958";
    //#endif

    @Inject(method = RENDER_METHOD, at = @At("HEAD"))
    private void emf$parrot1(final CallbackInfo ci, @Local(argsOnly = true) boolean isLeft) {
        EMFState.isInShoulderMethod = true;
        EMFState.isLeftShoulder = isLeft;
    }

    @Inject(method = RENDER_METHOD, at = @At("TAIL"))
    private void emf$parrot2(final CallbackInfo ci) {
        EMFState.isInShoulderMethod = false;
    }
}




