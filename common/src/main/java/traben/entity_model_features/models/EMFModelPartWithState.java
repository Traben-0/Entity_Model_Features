package traben.entity_model_features.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.resources.ResourceLocation;

public abstract class EMFModelPartWithState extends EMFModelPart {
    public final Int2ObjectOpenHashMap<EMFModelState> allKnownStateVariants = new Int2ObjectOpenHashMap<>() {
        @Override
        public EMFModelState get(final int k) {
            if (!containsKey(k)) {
                EMFUtils.logWarn("EMFModelState variant with key " + k + " does not exist in part [" + toStringShort() + "], returning copy of 0");
                put(k, EMFModelState.copy(get(0)));
            }
            return super.get(k);
        }
    };

    public int currentModelVariant = 0;
    Map<String, ModelPart> vanillaChildren = new HashMap<>();
    Runnable startOfRenderRunnable = null;
    Animator tryAnimate = new Animator();

    public EMFModelPartWithState(List<Cube> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }

    void receiveOneTimeRunnable(Runnable run) {
        startOfRenderRunnable = run;
        children.values().forEach((child) -> {
            if (child instanceof EMFModelPartWithState emf) {
                emf.receiveOneTimeRunnable(run);
            }
        });
    }


    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay,#if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {

        if (startOfRenderRunnable != null) {
            startOfRenderRunnable.run();
        }

        if (tryAnimate != null && !EMFAnimationEntityContext.isEntityAnimPaused()) {
            tryAnimate.run();
        }
        renderWithTextureOverride(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);

    }


    EMFModelState getCurrentState() {
        return new EMFModelState(
                getInitialPose(),
                cubes,
                children,
                xScale, yScale, zScale,
                visible, skipDraw,
                textureOverride, tryAnimate
        );
    }

    EMFModelState getStateOf(ModelPart modelPart) {
        if (modelPart instanceof EMFModelPartWithState emf) {
            return new EMFModelState(
                    modelPart.getInitialPose(),
                    modelPart.cubes,
                    modelPart.children,
                    modelPart.xScale, modelPart.yScale, modelPart.zScale,
                    modelPart.visible, modelPart.skipDraw,
                    emf.textureOverride, emf.tryAnimate
            );
        }
        return new EMFModelState(
                modelPart.getInitialPose(),
                modelPart.cubes,
                new HashMap<>(),
                modelPart.xScale, modelPart.yScale, modelPart.zScale,
                modelPart.visible, modelPart.skipDraw,
                null, new Animator()
        );
    }

    void setFromState(EMFModelState newState) {
        setInitialPose(newState.defaultTransform());
        loadPose(getInitialPose());
        cubes = newState.cuboids();

        children = newState.variantChildren();

        xScale = newState.xScale();
        yScale = newState.yScale();
        zScale = newState.zScale();
        visible = newState.visible();
        skipDraw = newState.hidden();
        textureOverride = newState.texture();
        tryAnimate = newState.animation();
    }

    protected void resetState(){
        setFromState(allKnownStateVariants.get(currentModelVariant));
    }


    public void setVariantStateTo(int newVariant) {
        if (currentModelVariant != newVariant) {
            setFromState(allKnownStateVariants.get(newVariant));
            currentModelVariant = newVariant;
            for (ModelPart part :
                    children.values()) {
                if (part instanceof EMFModelPartWithState p3)
                    p3.setVariantStateTo(newVariant);
            }
        }
    }

    public record EMFModelState(
            PartPose defaultTransform,
            // ModelTransform currentTransform,
            List<Cube> cuboids,
            Map<String, ModelPart> variantChildren,
            float xScale,
            float yScale,
            float zScale,
            boolean visible,
            boolean hidden,
            ResourceLocation texture,
            Animator animation
    ) {

        public static EMFModelState copy(EMFModelState copyFrom) {
            PartPose trans = copyFrom.defaultTransform();
            Animator animator = new Animator();
            animator.setAnimation(copyFrom.animation().getAnimation());
            return new EMFModelState(
                    PartPose.offsetAndRotation(trans.x, trans.y, trans.z, trans.xRot, trans.yRot, trans.zRot),
                    new ArrayList<>(copyFrom.cuboids()),
                    new HashMap<>(copyFrom.variantChildren()),
                    copyFrom.xScale(),
                    copyFrom.yScale(),
                    copyFrom.zScale(),
                    copyFrom.visible(),
                    copyFrom.hidden(),
                    copyFrom.texture(),
                    animator
            );
        }

    }

}
