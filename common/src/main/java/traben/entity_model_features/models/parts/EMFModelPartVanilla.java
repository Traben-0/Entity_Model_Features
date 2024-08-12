package traben.entity_model_features.models.parts;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.EMF;
import traben.entity_model_features.utils.EMFUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.*;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartVanilla extends EMFModelPartWithState {

    final String name;
    final boolean isOptiFinePartSpecified;
    final Set<Integer> hideInTheseStates = new HashSet<>();

    public EMFModelPartVanilla(String name,
                               ModelPart vanillaPart,
                               Collection<String> optifinePartNames,
                               Map<String, EMFModelPartVanilla> allVanillaParts) {
        //create vanilla root model object
        super(new ArrayList<>(), new HashMap<>());
        this.name = name;

        if (EMF.config().getConfig().logModelCreationData) EMFUtils.log(" > EMF vanilla part made: " + name);

        isOptiFinePartSpecified = optifinePartNames.contains(name);

        setFromState(getStateOf(vanillaPart));

        for (Map.Entry<String, ModelPart> child :
                vanillaPart.children.entrySet()) {

            EMFModelPartVanilla vanilla = new EMFModelPartVanilla(child.getKey(), child.getValue(), optifinePartNames, allVanillaParts);
            children.put(child.getKey(), vanilla);
            allVanillaParts.put(child.getKey(), vanilla);
        }
        vanillaChildren = this.children;
        allKnownStateVariants.put(0, getCurrentState());

    }

    @Override
    protected float[] debugBoxColor() {
        return new float[]{0, 1f, 0};
    }

    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, #if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {
        //ignore non optifine specified parts when not vanilla variant
        if (!hideInTheseStates.contains(currentModelVariant))
            super.render(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
    }

    public void setHideInTheseStates(int variant) {
        hideInTheseStates.add(variant);
        children.values().forEach((part) -> {
            if (part instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified)
                vanilla.setHideInTheseStates(variant);
        });
    }

    public void receiveRootAnimationRunnable(int variant, Runnable run) {
        allKnownStateVariants.get(variant).animation().setAnimation(run);
    }

    @Override
    public String toString() {
        return "[vanilla part " + name + "], cubes =" + cubes.size() + ", children = " + children.size();
    }

    @Override
    public String toStringShort() {
        return "[vanilla part " + name + "]";
    }
}
