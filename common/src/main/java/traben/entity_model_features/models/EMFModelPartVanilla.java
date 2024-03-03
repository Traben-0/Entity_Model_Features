package traben.entity_model_features.models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFUtils;

import java.util.*;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartVanilla extends EMFModelPartWithState {


    final String name;
    //construct single vanilla
    final boolean isOptiFinePartSpecified;
    final boolean rootType;

    final Set<Integer> hideInTheseStates = new HashSet<>();


    public EMFModelPartVanilla(String name,
                               ModelPart vanillaPart,
                               Collection<String> optifinePartNames,
                               Map<String, EMFModelPartVanilla> allVanillaParts) {
        //create vanilla root model object
        super(new ArrayList<>(), new HashMap<>());
        this.name = name;

        if (EMFConfig.getConfig().logModelCreationData)
            EMFUtils.log(" > EMF vanilla part made: " + name);

        isOptiFinePartSpecified = optifinePartNames.contains(name);
        rootType = name.equals("root") || name.equals("EMF_root");


        EMFModelState state = getStateOf(vanillaPart);
        setFromState(state);
        //Map<String, ModelPart> children = this.children;
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
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //ignore non optifine specified parts when not vanilla variant
        if (!hideInTheseStates.contains(currentModelVariant))
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
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
        return "[vanilla part " + name + "], cubes =" + cuboids.size() + ", children = " + children.size();
    }

    @Override
    public String toStringShort() {
        return "[vanilla part " + name + "]";
    }
}
