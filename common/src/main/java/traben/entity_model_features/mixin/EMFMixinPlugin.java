package traben.entity_model_features.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import traben.entity_model_features.EMFVersionDifferenceManager;

import java.util.List;
import java.util.Set;

public class EMFMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // this mixin is optional but allows emf to skip extraneous calculations and also capture modifications to
        // the model animation variables.
        // pollen is known to conflict with this mixin
        if (mixinClassName.endsWith("MixinLivingEntityRenderer_ValueCapturing")) {
            return !EMFVersionDifferenceManager.isThisModLoaded("pollen");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
