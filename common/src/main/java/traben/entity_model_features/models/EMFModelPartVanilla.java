package traben.entity_model_features.models;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;

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
            System.out.println(" > EMF vanilla part made: " + name);

        isOptiFinePartSpecified = optifinePartNames.contains(name);
        rootType = name.equals("root") || name.equals("EMF_root");


        EMFModelState state = getStateOf(vanillaPart);
        setFromState(state);
        Map<String, ModelPart> children = getChildrenEMF();
        for (Map.Entry<String, ModelPart> child :
                ((ModelPartAccessor) vanillaPart).getChildren().entrySet()) {


            EMFModelPartVanilla vanilla = new EMFModelPartVanilla(child.getKey(), child.getValue(), optifinePartNames, allVanillaParts);
            children.put(child.getKey(), vanilla);
            allVanillaParts.put(child.getKey(), vanilla);
        }
        vanillaChildren = getChildrenEMF();
        allKnownStateVariants.put(0, getCurrentState());

    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //ignore non optifine specified parts when not vanilla variant
        if (!hideInTheseStates.contains(currentModelVariantState))
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    ModelPart getVanillaModelPartsOfCurrentState() {
        Map<String, ModelPart> children = new HashMap<>();
        for (Map.Entry<String, ModelPart> child :
                getChildrenEMF().entrySet()) {
            if (child.getValue() instanceof EMFModelPart emf) {
                children.put(child.getKey(), emf.getVanillaModelPartsOfCurrentState());
            }
        }

        ModelPart part = new ModelPart(((ModelPartAccessor) this).getCuboids(), children);
        part.setTransform(getDefaultTransform());
        part.pitch = pitch;
        part.roll = roll;
        part.yaw = yaw;
        part.pivotZ = pivotZ;
        part.pivotY = pivotY;
        part.pivotX = pivotX;
//        part.xScale = xScale;
//        part.yScale = yScale;
//        part.zScale = zScale;

        return part;
    }


    public void setHideInTheseStates(int variant) {
        hideInTheseStates.add(variant);
        getChildrenEMF().values().forEach((part) -> {
            if (part instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified)
                vanilla.setHideInTheseStates(variant);
        });
    }


    public void receiveRootAnimationRunnable(int variant, Runnable run) {
//        Runnable run = () -> {
//            if (this.lastMobCountAnimatedOn != EMFManager.getInstance().entityRenderCount) {
//                this.lastMobCountAnimatedOn = EMFManager.getInstance().entityRenderCount;
//                animList.forEach((EMFAnimation::calculateAndSet));
//            }else{
//                animList.forEach((EMFAnimation::getLastAndSet));
//            }
//        };
        allKnownStateVariants.get(variant).animation().setAnimation(run);
    }

    @Override
    public String toString() {
        return "[vanilla part " + name + "]";
    }
}
