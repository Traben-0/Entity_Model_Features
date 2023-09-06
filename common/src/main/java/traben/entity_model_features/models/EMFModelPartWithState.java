package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EMFModelPartWithState extends EMFModelPart{
    public EMFModelPartWithState(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }

    Map<String, ModelPart> vanillaChildren = new HashMap<>();


    public int currentModelVariantState = 0;
    public boolean isValidToRenderInThisState = true;
    public final Int2ObjectOpenHashMap<EMFModelState> allKnownStateVariants = new Int2ObjectOpenHashMap<>();

    void receiveRootVariationRunnable(Runnable run){
        variantCheck = run;
        getChildrenEMF().values().forEach((child)->{
            if(child instanceof EMFModelPartWithState emf){
                emf.receiveRootVariationRunnable(run);
            }
        });
    }
    Runnable variantCheck = null;

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        if(variantCheck != null){
            variantCheck.run();
        }

        //assertChildrenAndCuboids();
        //if(new Random().nextInt(100)==1) System.out.println("rendered");
        if (isValidToRenderInThisState) {
            if(tryAnimate != null){
                tryAnimate.run();
            }
            primaryRender(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }

    EMFModelState getCurrentState() {
        return new EMFModelState(
                getDefaultTransform(),
                ((ModelPartAccessor) this).getCuboids(),
                getChildrenEMF(),
                xScale, yScale, zScale,
                visible, hidden,
                textureOverride, tryAnimate
        );
    }

    EMFModelState getStateOf(ModelPart modelPart) {
        if(modelPart instanceof EMFModelPartWithState emf){
            return new EMFModelState(
                    modelPart.getDefaultTransform(),
                    ((ModelPartAccessor) modelPart).getCuboids(),
                    emf.getChildrenEMF(),
                    modelPart.xScale, modelPart.yScale, modelPart.zScale,
                    modelPart.visible, modelPart.hidden,
                    emf.textureOverride, emf.tryAnimate
            );
        }
        return new EMFModelState(
                modelPart.getDefaultTransform(),
                ((ModelPartAccessor) modelPart).getCuboids(),
                new HashMap<>(),
                modelPart.xScale, modelPart.yScale, modelPart.zScale,
                modelPart.visible, modelPart.hidden,
                null, new Animator()
        );
    }


    void setFromState(EMFModelState newState) {
        setDefaultTransform(newState.defaultTransform());
        setTransform(getDefaultTransform());
        ((ModelPartAccessor) this).setCuboids(newState.cuboids());

        ((ModelPartAccessor) this).setChildren(newState.variantChildren());

        xScale = newState.xScale();
        yScale = newState.yScale();
        zScale = newState.zScale();
        visible = newState.visible();
        hidden = newState.hidden();
        textureOverride = newState.texture();
        tryAnimate =newState.animation();
    }


    void setFromStateVariant(EMFModelState newState, @Nullable EMFModelState oldState) {
        ModelTransform oldDefault = getDefaultTransform();
        setDefaultTransform(newState.defaultTransform());
        setTransformOnlyChangingDefaults(getDefaultTransform(),oldDefault);
        ((ModelPartAccessor) this).setCuboids(newState.cuboids());
        ((ModelPartAccessor) this).setChildren(newState.variantChildren());
        if(oldState == null || xScale == oldState.xScale()) xScale = newState.xScale();
        if(oldState == null || yScale == oldState.yScale()) yScale = newState.yScale();
        if(oldState == null || zScale == oldState.zScale()) zScale = newState.zScale();
        if(oldState == null || visible == oldState.visible()) visible = newState.visible();
        if(oldState == null || hidden == oldState.hidden()) hidden = newState.hidden();
//        xScale = newState.xScale();
//        yScale = newState.yScale();
//        zScale = newState.zScale();
//        visible = newState.visible();
//        hidden = newState.hidden();
        textureOverride = newState.texture();
        tryAnimate = newState.animation();
    }

    public void setTransformOnlyChangingDefaults(ModelTransform newDefault,ModelTransform oldDefault) {
        if(this.pivotX == oldDefault.pivotX) this.pivotX = newDefault.pivotX;
        if(this.pivotY == oldDefault.pivotY) this.pivotY = newDefault.pivotY;
        if(this.pivotZ == oldDefault.pivotX) this.pivotZ = newDefault.pivotZ;
        if(this.pitch == oldDefault.pitch) this.pitch = newDefault.pitch;
        if(this.yaw == oldDefault.yaw) this.yaw = newDefault.yaw;
        if(this.roll == oldDefault.roll) this.roll = newDefault.roll;
    }

    public void setVariantStateTo(int newVariantState) {
        if (currentModelVariantState != newVariantState) {
            currentModelVariantState = newVariantState;
            if (allKnownStateVariants.containsKey(newVariantState)) {
                setFromStateVariant(allKnownStateVariants.get(newVariantState),allKnownStateVariants.get(currentModelVariantState));
                isValidToRenderInThisState = true;
            } else {
                isValidToRenderInThisState = false;
            }
            for (ModelPart part :
                    getChildrenEMF().values()) {
                if (part instanceof EMFModelPartWithState p3)
                    p3.setVariantStateTo(newVariantState);
            }
        }

    }



    record EMFModelState(
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

        public static EMFModelState copy(EMFModelState copyFrom){
            ModelTransform trans = copyFrom.defaultTransform();
            Animator animator = new Animator();
            animator.setAnimation(copyFrom.animation().getAnimation());
            return new EMFModelState(
                    ModelTransform.of(trans.pivotX, trans.pivotY, trans.pivotZ, trans.pitch, trans.yaw, trans.roll) ,
                    new ArrayList<>(copyFrom.cuboids()),
                    new HashMap<>(copyFrom.variantChildren()) ,
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
    Animator tryAnimate = new Animator();

}
