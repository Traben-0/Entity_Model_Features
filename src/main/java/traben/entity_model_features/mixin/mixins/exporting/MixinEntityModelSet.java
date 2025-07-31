package traben.entity_model_features.mixin.mixins.exporting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.IEMFUnmodifiedLayerRootGetter;

import java.util.Map;

@Mixin(value = EntityModelSet.class, priority = 1001)
public class MixinEntityModelSet implements IEMFUnmodifiedLayerRootGetter {
    //#if MC >= 12104
    @Unique
    private Map<ModelLayerLocation, LayerDefinition> emf$unmodifiedRoots = ImmutableMap.of();

    @Inject(method = "vanilla", at = @At(value = "RETURN"))
    private static void emf$unModifiedRoots(final CallbackInfoReturnable<EntityModelSet> cir) {
        EMF.tempDisableModelModifications = true;
        ((IEMFUnmodifiedLayerRootGetter)cir.getReturnValue()).emf$setUnmodifiedRoots(ImmutableMap.copyOf(LayerDefinitions.createRoots())) ;
        EMF.tempDisableModelModifications = false;
    }

    @Override
    public Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots() {
        return emf$unmodifiedRoots;
    }

    @Override
    public void emf$setUnmodifiedRoots(final Map<ModelLayerLocation, LayerDefinition> roots) {
        emf$unmodifiedRoots = roots;
    }

    //#elseif MC >= 12102
    //$$ @Unique
    //$$ private Map<ModelLayerLocation, LayerDefinition> emf$unmodifiedRoots = ImmutableMap.of();
    //$$
    //$$ @Inject(method = "onResourceManagerReload", at = @At(value = "TAIL"))
    //$$ private void emf$unModifiedRoots(final CallbackInfo ci) {
    //$$     EMF.tempDisableModelModifications = true;
    //$$     this.emf$unmodifiedRoots = ImmutableMap.copyOf(LayerDefinitions.createRoots());
    //$$     EMF.tempDisableModelModifications = false;
    //$$ }
    //$$
    //$$ @Override
    //$$ public Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots() {
    //$$     return emf$unmodifiedRoots;
    //$$ }
    //$$
    //#else
    //$$ @Shadow
    //$$ public Map<ModelLayerLocation, LayerDefinition> roots;
    //$$
    //$$ @Override
    //$$ public Map<ModelLayerLocation, LayerDefinition> emf$getUnmodifiedRoots() {
    //$$     return roots;
    //$$ }
    //#endif
}