package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EMFModelPartWithState extends EMFModelPart {
    public final Int2ObjectOpenHashMap<EMFModelState> allKnownStateVariants = new Int2ObjectOpenHashMap<>();
    public int currentModelVariant = 0;
    Map<String, ModelPart> vanillaChildren = new HashMap<>();
    Runnable startOfRenderRunnable = null;
    Animator tryAnimate = new Animator();

    public EMFModelPartWithState(List<Cuboid> cuboids, Map<String, ModelPart> children) {
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
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        if (startOfRenderRunnable != null) {
            startOfRenderRunnable.run();
        }

        if (tryAnimate != null) {
            tryAnimate.run();
        }
        renderWithTextureOverride(matrices, vertices, light, overlay, red, green, blue, alpha);

    }



    EMFModelState getCurrentState() {
        return new EMFModelState(
                getDefaultTransform(),
                cuboids,
                children,
                xScale, yScale, zScale,
                visible, hidden,
                textureOverride, tryAnimate
        );
    }

    EMFModelState getStateOf(ModelPart modelPart) {
        if (modelPart instanceof EMFModelPartWithState emf) {
            return new EMFModelState(
                    modelPart.getDefaultTransform(),
                    modelPart.cuboids,
                    modelPart.children,
                    modelPart.xScale, modelPart.yScale, modelPart.zScale,
                    modelPart.visible, modelPart.hidden,
                    emf.textureOverride, emf.tryAnimate
            );
        }
        return new EMFModelState(
                modelPart.getDefaultTransform(),
                modelPart.cuboids,
                new HashMap<>(),
                modelPart.xScale, modelPart.yScale, modelPart.zScale,
                modelPart.visible, modelPart.hidden,
                null, new Animator()
        );
    }

    void setFromState(EMFModelState newState) {
        setDefaultTransform(newState.defaultTransform());
        setTransform(getDefaultTransform());
        cuboids = newState.cuboids();

        children = newState.variantChildren();

        xScale = newState.xScale();
        yScale = newState.yScale();
        zScale = newState.zScale();
        visible = newState.visible();
        hidden = newState.hidden();
        textureOverride = newState.texture();
        tryAnimate = newState.animation();
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
            ModelTransform defaultTransform,
            // ModelTransform currentTransform,
            List<Cuboid> cuboids,
            Map<String, ModelPart> variantChildren,
            float xScale,
            float yScale,
            float zScale,
            boolean visible,
            boolean hidden,
            Identifier texture,
            Animator animation
    ) {

        public static EMFModelState copy(EMFModelState copyFrom) {
            ModelTransform trans = copyFrom.defaultTransform();
            Animator animator = new Animator();
            animator.setAnimation(copyFrom.animation().getAnimation());
            return new EMFModelState(
                    ModelTransform.of(trans.pivotX, trans.pivotY, trans.pivotZ, trans.pitch, trans.yaw, trans.roll),
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
