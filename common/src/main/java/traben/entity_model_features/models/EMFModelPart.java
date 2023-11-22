package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;

import java.util.List;
import java.util.Map;

import static traben.entity_model_features.EMFClient.EYES_FEATURE_LIGHT_VALUE;

public abstract class EMFModelPart extends ModelPart {
    public Identifier textureOverride;
//    protected BufferBuilder MODIFIED_RENDER_BUFFER = null;

    public EMFModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }



//    @Nullable
//    private Identifier getTextureOverrideViaETF(int renderLight) {
//        if (renderLight == ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE) {
//            //require emissive texture variant
//            if (EMFAnimationHelper.getEMFEntity().entity() != null) {
//                return ETFApi.getCurrentETFEmissiveTextureOfEntityOrNull(EMFAnimationHelper.getEMFEntity().entity(), textureOverride);
//            } else if (EMFAnimationHelper.getEMFEntity().getBlockEntity() != null) {
//                //todo api version needed with emf format uuid support
//                ETFBlockEntityWrapper block = new ETFBlockEntityWrapper(EMFAnimationHelper.getEMFEntity().getBlockEntity(), EMFAnimationHelper.getEMFEntity().getUuid());
//                ETFTexture etfTexture = ETFManager.getInstance().getETFTexture(textureOverride, block, ETFManager.TextureSource.BLOCK_ENTITY, ETFClientCommon.ETFConfigData.removePixelsUnderEmissiveBlockEntity);
//                if(etfTexture != null) {
//                    Identifier emissive = etfTexture.getEmissiveIdentifierOfCurrentState();
//                    if(emissive != null)
//                        return emissive;
//                }
//            }
//            //assert null if no emissive exists as we are in an emissive only render
//            return null;
//        } else {
//            //otherwise normal texture
//            if (EMFAnimationHelper.getEMFEntity().entity() != null) {
//                return ETFApi.getCurrentETFVariantTextureOfEntity(EMFAnimationHelper.getEMFEntity().entity(), textureOverride);
//            } else if (EMFAnimationHelper.getEMFEntity().getBlockEntity() != null) {
//                return ETFApi.getCurrentETFVariantTextureOfBlockEntity(EMFAnimationHelper.getEMFEntity().getBlockEntity(), textureOverride,EMFAnimationHelper.getEMFEntity().getUuid());
//            }
//            return textureOverride;
//        }
//    }

    void primaryRender(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        if (textureOverride != null
                && light != EYES_FEATURE_LIGHT_VALUE // this is only the case for EyesFeatureRenderer
                && light != ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE //do not allow new etf emissive rendering here
                && EMFAnimationHelper.getEMFEntity() != null
        ) {
            RenderLayer originalLayer = ETFRenderContext.getCurrentRenderLayer();
            RenderLayer layerModified = RenderLayer.getEntityTranslucent(textureOverride);
            VertexConsumer newConsumer = ETFRenderContext.processVertexConsumer(ETFRenderContext.getCurrentProvider(),layerModified);

            renderToSuper(matrices, newConsumer, light, overlay, red, green, blue, alpha);
            etf$renderEmissive(matrices, overlay, red, green, blue, alpha) ;
            etf$renderEnchanted(matrices, light, overlay, red, green, blue, alpha);

            ETFRenderContext.processVertexConsumer(ETFRenderContext.getCurrentProvider(),originalLayer);


//                //todo I would appreciate feedback on this from someone who understands the buffer system better
//                //this is just my best guess from brute forcing it
//                try {
//                    RenderLayer layer = RenderLayer.getEntityTranslucent(textureOverride);
////                    if (MODIFIED_RENDER_BUFFER == null)
////                        MODIFIED_RENDER_BUFFER = new BufferBuilder(layer.getExpectedBufferSize());
////                    MODIFIED_RENDER_BUFFER.begin(layer.getDrawMode(), layer.getVertexFormat());
////                    renderToSuper(matrices, MODIFIED_RENDER_BUFFER, light, overlay, red, green, blue, alpha);
////                    layer.draw(MODIFIED_RENDER_BUFFER, RenderSystem.getVertexSorting());
////                    MODIFIED_RENDER_BUFFER.clear();
//                } catch (Exception ignored) {
//                }

        } else {
            //normal vertex consumer
            renderToSuper(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
    }

    void renderToSuper(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
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

    abstract ModelPart getVanillaModelPartsOfCurrentState();

    public Object2ReferenceOpenHashMap<String, EMFModelPart> getAllChildPartsAsAnimationMap(String prefixableParents, int variantNum, Map<String, String> optifinePartNameMap) {
        if (this instanceof EMFModelPartRoot root)
            root.setVariantStateTo(variantNum);

        Object2ReferenceOpenHashMap<String, EMFModelPart> mapOfAll = new Object2ReferenceOpenHashMap<>();
        Map<String, ModelPart> children = getChildrenEMF();

        for (ModelPart part :
                children.values()) {
            if (part instanceof EMFModelPart part3) {
                String thisKey;
                boolean addThis;
                if (part instanceof EMFModelPartCustom partc) {
                    thisKey = partc.id;
                    addThis = true;
                } else if (part instanceof EMFModelPartVanilla partv) {
                    thisKey = partv.name;
                    addThis = partv.isOptiFinePartSpecified;
                } else {
                    thisKey = "NULL_KEY_NAME";
                    addThis = false;
                }
                for (Map.Entry<String, String> entry :
                        optifinePartNameMap.entrySet()) {
                    if (entry.getValue().equals(thisKey)) {
                        thisKey = entry.getKey();
                        break;
                    }
                }
                if (addThis) {
                    mapOfAll.put(thisKey, part3);
                    if (prefixableParents.isBlank()) {
                        mapOfAll.putAll(part3.getAllChildPartsAsAnimationMap(thisKey, variantNum, optifinePartNameMap));
                    } else {
                        mapOfAll.put(prefixableParents + ':' + thisKey, part3);
                        mapOfAll.putAll(part3.getAllChildPartsAsAnimationMap(prefixableParents + ':' + thisKey, variantNum, optifinePartNameMap));
                    }
                } else {
                    mapOfAll.putAll(part3.getAllChildPartsAsAnimationMap(prefixableParents, variantNum, optifinePartNameMap));
                }

            }

        }
        return mapOfAll;
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

    //todo copy of etf rewrite emissive rendering code
    private void etf$renderEmissive(MatrixStack matrices, int overlay, float red, float green, float blue, float alpha) {
        Identifier emissive = ETFRenderContext.getCurrentETFTexture().getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {

            ETFRenderContext.preventRenderLayerTextureModify();

            boolean textureIsAllowedBrightRender = ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT
                    && ETFRenderContext.getCurrentEntity().etf$canBeBright();// && !ETFRenderContext.getCurrentETFTexture().isPatched_CurrentlyOnlyArmor();

            VertexConsumer emissiveConsumer = ETFRenderContext.getCurrentProvider().getBuffer(
                    textureIsAllowedBrightRender ?
                            RenderLayer.getBeaconBeam(emissive, true) :
                            ETFRenderContext.getCurrentEntity().etf$isBlockEntity() ?
                                    RenderLayer.getEntityTranslucentCull(emissive) :
                                    RenderLayer.getEntityTranslucent(emissive));


            ETFRenderContext.allowRenderLayerTextureModify();

            renderToSuper(matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, red, green, blue, alpha);

        }
    }
    //todo copy of etf enchanted render code
    private void etf$renderEnchanted(MatrixStack matrices, int light, int overlay, float red, float green, float blue, float alpha) {
        //attempt enchanted render
        Identifier enchanted = ETFRenderContext.getCurrentETFTexture().getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(ETFRenderContext.getCurrentProvider(), RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
            ETFRenderContext.allowRenderLayerTextureModify();

            renderToSuper(matrices, enchantedVertex, light, overlay, red, green, blue, alpha);
        }
    }
}
