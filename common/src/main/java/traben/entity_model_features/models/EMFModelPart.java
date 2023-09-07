package traben.entity_model_features.models;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.ETFApi;

import java.util.List;
import java.util.Map;

public abstract class EMFModelPart extends ModelPart {
    public Identifier textureOverride;
    protected BufferBuilder MODIFIED_RENDER_BUFFER = null;

    public EMFModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }

    void primaryRender(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        if (//!isTopLevelModelRoot &&
                textureOverride != null
                        && EMFConfig.getConfig().textureOverrideMode != EMFConfig.TextureOverrideMode.OFF
                        && light != LightmapTextureManager.MAX_LIGHT_COORDINATE+1 // this is only the case for EyesFeatureRenderer
                        && EMFAnimationHelper.getEMFEntity() != null) {

            Identifier texture;
            if(light == LightmapTextureManager.MAX_LIGHT_COORDINATE+2){
                //require emissive texture variant
                if(EMFAnimationHelper.getEMFEntity().entity() == null){
                    texture = null;//todo ETFApi.get(EMFAnimationHelper.getEMFEntity().getBlockEntity(),EMFAnimationHelper.getEMFEntity().getUuid(), textureOverride);
                }else{
                    texture = ETFApi.getCurrentETFEmissiveTextureOfEntityOrNull(EMFAnimationHelper.getEMFEntity().entity(), textureOverride);
                }

            }else{
                //otherwise normal texture
                if(EMFAnimationHelper.getEMFEntity().entity() == null) {
                    texture = ETFApi.getCurrentETFVariantTextureOfEntity(EMFAnimationHelper.getEMFEntity().getBlockEntity(), textureOverride,EMFAnimationHelper.getEMFEntity().getUuid());
                }else{
                    texture = ETFApi.getCurrentETFVariantTextureOfEntity(EMFAnimationHelper.getEMFEntity().entity(), textureOverride);
                }
            }
            //todo alternate layers other than translucent
            if (texture != null){
                if(EMFManager.getInstance().IS_IRIS_INSTALLED
                        && EMFConfig.getConfig().textureOverrideMode == EMFConfig.TextureOverrideMode.USE_IRIS_QUIRK_AND_DEFER_TO_EMF_CODE_OTHERWISE){
                    //this simple code seems to work with iris
                    VertexConsumerProvider vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
                    VertexConsumer newVertex = EMFAnimationHelper.getEMFEntity() == null ? null : vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(
                            EMFAnimationHelper.getEMFEntity().entity() == null?
                                    ETFApi.getCurrentETFVariantTextureOfEntity(EMFAnimationHelper.getEMFEntity().getBlockEntity(), textureOverride,EMFAnimationHelper.getEMFEntity().getUuid()):
                                    ETFApi.getCurrentETFVariantTextureOfEntity(EMFAnimationHelper.getEMFEntity().entity(), textureOverride)

                    ));
                    if (newVertex != null) {
                        renderToSuper(matrices, newVertex, light, overlay, red, green, blue, alpha);
                    }
                }else{
                    //this code works otherwise, and is entirely brute forced from what I could teach myself about buffers through experimentation
                    //todo I would appreciate feedback on this from someone who understands the buffer system better
                    try {
                        RenderLayer layer = RenderLayer.getEntityTranslucent(texture);
                        if (MODIFIED_RENDER_BUFFER == null)
                            MODIFIED_RENDER_BUFFER = new BufferBuilder(layer.getExpectedBufferSize());
                        MODIFIED_RENDER_BUFFER.begin(layer.getDrawMode(), layer.getVertexFormat());
                        renderToSuper(matrices, MODIFIED_RENDER_BUFFER, light, overlay, red, green, blue, alpha);
                        layer.draw(MODIFIED_RENDER_BUFFER, RenderSystem.getVertexSorting());
                        MODIFIED_RENDER_BUFFER.clear();
                    }catch (Exception ignored){}
                }
            }
        }else {
            //normal vertex consumer
            renderToSuper(matrices, vertices, light, overlay, red, green, blue, alpha);
        }

    }
    void renderToSuper(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha){
//        if (EMFConfig.getConfig().renderCustomModelsGreen) {
//            float flash = (float) Math.abs(Math.sin(System.currentTimeMillis() / 1000d));
//            super.render(matrices, vertices, light, overlay, flash, green, flash, alpha);
//        } else {
            super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
//        }
    }
    //stop trying to optimize my code so it doesn't work sodium :P
    @Override // overrides to circumvent sodium optimizations that mess with custom uv quad creation
    protected void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        //this is a copy of the vanilla renderCuboids() method
        for (Cuboid cuboid : ((ModelPartAccessor) this).getCuboids()) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }


    @Override
    public String toString() {
        return "generic emf part";
    }

    public Map<String, ModelPart> getChildrenEMF() {
        return ((ModelPartAccessor) this).getChildren();
    }
    public static class Animator implements Runnable {
        private Runnable animation = null;

        Animator() {

        }
        public Runnable getAnimation() {
            return animation;
        }

        public void setAnimation(Runnable animation) {
            this.animation = animation;
        }

        public void run() {
            if (animation != null) animation.run();
        }
    }

    abstract ModelPart getVanillaModelPartsOfCurrentState();
    public Object2ReferenceOpenHashMap<String, EMFModelPart> getAllChildPartsAsAnimationMap(String prefixableParents,int variantNum){
        Object2ReferenceOpenHashMap<String, EMFModelPart> list = new Object2ReferenceOpenHashMap<>();
        Map<String, ModelPart> children;
        if(this instanceof EMFModelPartWithState state){
            EMFModelPartWithState.EMFModelState s = state.allKnownStateVariants.get(variantNum);
            if(s!= null){
                children = s.variantChildren();
            }else{
                children = getChildrenEMF();
            }
        }else{
            children = getChildrenEMF();
        }
        for (ModelPart part :
                children.values()) {
            if (part instanceof EMFModelPart part3) {
                String thisKey;
                if (part instanceof EMFModelPartCustom partc) {
                    thisKey = partc.id;
                }else if (part instanceof EMFModelPartVanilla partv) {
                    thisKey = partv.name;
                }else{
                    thisKey = "NULL_KEY_NAME";
                }
                list.put(thisKey, part3);
                if (prefixableParents.isBlank()) {
                    list.putAll(part3.getAllChildPartsAsAnimationMap(thisKey,variantNum));
                } else {
                    list.put(prefixableParents +':'+ thisKey, part3);
                    list.putAll(part3.getAllChildPartsAsAnimationMap(prefixableParents +':'+thisKey,variantNum));
                }
            }

        }
        return list;
    }
}
