package traben.entity_model_features.models.parts;


import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.EMF;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.utils.EMFUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.*;
import java.util.function.Consumer;


public class EMFModelPartVanilla extends EMFModelPartWithState {

    final String name;
    final boolean isOptiFinePartSpecified;
    final Set<Integer> hideInTheseStates = new HashSet<>();

    //#if MC >= 12109
    public boolean isPlayerArm = false;
    //#endif

    public EMFModelPartVanilla(String name,
                               ModelPart vanillaPart,
                               Collection<String> optifinePartNames,
                               Map<String, EMFModelPartVanilla> allVanillaParts,
                               EMFModelPartRoot root
    ) {
        //create vanilla root model object
        super(new ArrayList<>(), new HashMap<>(), root);
        this.name = name;

        if (EMF.config().getConfig().logModelCreationData) EMFUtils.log(" > EMF vanilla part made: " + name);

        isOptiFinePartSpecified = optifinePartNames.contains(name);

        setFromState(getStateOf(vanillaPart));

        for (Map.Entry<String, ModelPart> child :
                vanillaPart.children.entrySet()) {

            EMFModelPartVanilla vanilla = new EMFModelPartVanilla(child.getKey(), child.getValue(), optifinePartNames, allVanillaParts, getRoot());
            children.put(child.getKey(), vanilla);
            allVanillaParts.put(child.getKey(), vanilla);
        }
        vanillaChildren = this.children;
        allKnownStateVariants.put(0, getCurrentState());

    }

    public ModelPart[] getAllEMFCustomChildren() {
        return children.values().stream()
                .filter(part -> part instanceof EMFModelPartCustom)
                .toArray(ModelPart[]::new);
    }

    @Override
    public void translateAndRotate(PoseStack poseStack) {
        if (EMFState.isInHandItemLayerTransform && EMFState.isInLeftHand != null) {
            // Held item rendering runs this to translate for hand positioning, but we might have attachment points set in any arbitrary part at any depth
            // To account for this on the first call to this method during that layer call we will run that stack through
            // the root's positioner
            var root = getRoot();
            boolean leftArm = EMFState.isInLeftHand == Boolean.TRUE;
            if ((leftArm && root.hasLeftArmItemOverrides.contains(currentModelVariant))
                    || (!leftArm && root.hasRightArmItemOverrides.contains(currentModelVariant))) {
                // Already done the once only transform
                if (EMFState.hasDoneArmOverride == Boolean.TRUE) return;
                // Already failed the once only transform
                if (EMFState.hasDoneArmOverride == Boolean.FALSE) {
                    super.translateAndRotate(poseStack);
                    return;
                }

                Consumer<PoseStack> positioner = leftArm
                        ? root.leftArmPositioners.get(currentModelVariant)
                        : root.rightArmPositioners.get(currentModelVariant);

                if (positioner != null) {
                    EMFState.isInHandItemLayerTransform = false; // Very likely to recurse into this method
                    positioner.accept(poseStack);
                    EMFState.isInHandItemLayerTransform = true;
                    EMFState.hasDoneArmOverride = Boolean.TRUE;
                } else {
                    EMFState.hasDoneArmOverride = Boolean.FALSE;
                }
                return;
            }
        }

        // Normal or aborted call
        super.translateAndRotate(poseStack);
    }

    @Override
    protected float[] debugBoxColor() {
        return new float[]{0, 1f, 0};
    }

    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay,
                       //#if MC >= 12100
                       final int k
                       //#else
                       //$$ float red, float green, float blue, float alpha
                       //#endif
    ) {
        //ignore non optifine specified parts when not vanilla variant
        if (!hideInTheseStates.contains(currentModelVariant)){
                super.render(matrices, vertices, light, overlay,
                        //#if MC >= 12100
                        k
                        //#else
                        //$$ red, green, blue, alpha
                        //#endif
                );
        }
    }

    public void setHideInTheseStates(int variant) {
        hideInTheseStates.add(variant);
        children.values().forEach((part) -> {
            if (part instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified)
                vanilla.setHideInTheseStates(variant);
        });
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
