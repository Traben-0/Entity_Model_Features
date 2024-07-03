package traben.entity_model_features.models;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.world.level.block.entity.BlockEntity;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import static traben.entity_model_features.EMF.EYES_FEATURE_LIGHT_VALUE;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class EMFModelPart extends ModelPart {
    public ResourceLocation textureOverride;
    //    protected BufferBuilder MODIFIED_RENDER_BUFFER = null;
    protected long lastTextureOverride = -1L;



    public EMFModelPart(List<Cube> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);

        // re assert children and cuboids as modifiable
        // required for sodium post 0.5.4
        // this should not cause issues as emf does not allow these model parts to pass through sodium's unique renderer
        this.cubes = new ObjectArrayList<>(cuboids);
        this.children = new Object2ObjectOpenHashMap<>(children);
    }

    void renderWithTextureOverride(PoseStack matrices, VertexConsumer vertices, int light, int overlay, #if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {

        if (textureOverride == null
                || lastTextureOverride == EMFManager.getInstance().entityRenderCount) {//prevents texture overrides carrying over into feature renderers that reuse the base model
            //normal vertex consumer
            renderLikeETF(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
        } else if (light != EYES_FEATURE_LIGHT_VALUE // this is only the case for EyesFeatureRenderer
                && !ETFRenderContext.isIsInSpecialRenderOverlayPhase() //do not allow new etf emissive rendering here
                //&& vertices instanceof ETFVertexConsumer etfVertexConsumer
        ) { //can restore to previous render layer

            // check if need to skip due to being in iris shadow pass
            // fixed weird bug with certain texture overrides rendering in first person as though from the sun's POV
            // downside is incorrect shadows for some model parts :/
            //todo triple check it is only block entities, I so far cannot recreate the bug for regular mobs
            if ((EMFAnimationEntityContext.getEMFEntity() != null && EMFAnimationEntityContext.getEMFEntity().etf$isBlockEntity())
                    && ETF.IRIS_DETECTED && IrisShadowPassDetection.getInstance().inShadowPass()) {
                //skip texture override
                renderLikeETF(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                return;
            }

            if (vertices instanceof ETFVertexConsumer etfVertexConsumer) {
                // if the texture override is the same as the current texture, render as normal
                var etfTextureTest = etfVertexConsumer.etf$getETFTexture();
                if (etfTextureTest != null && etfTextureTest.thisIdentifier.equals(textureOverride)) {
                    renderLikeETF(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                    return;
                }

                RenderType originalLayer = etfVertexConsumer.etf$getRenderLayer();
                if (originalLayer == null) return;

                MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
                if (provider == null) return;
                renderTextureOverrideWithoutReset(provider, matrices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);

                //reset render settings
                provider.getBuffer(originalLayer);
            }else{
                //could be a sprite originally, if so lets ignore trying to reset the texture at all to its original
                MultiBufferSource provider = Minecraft.getInstance().renderBuffers().bufferSource();
                renderTextureOverrideWithoutReset(provider, matrices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
            }
        }
        //else cancel out render
    }

    private void renderTextureOverrideWithoutReset(MultiBufferSource provider, PoseStack matrices, int light, int overlay, #if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif){

        lastTextureOverride = EMFManager.getInstance().entityRenderCount;
        RenderType layerModified = EMFAnimationEntityContext.getLayerFromRecentFactoryOrETFOverrideOrTranslucent(textureOverride);
        VertexConsumer newConsumer = provider.getBuffer(layerModified);

        renderLikeVanilla(matrices, newConsumer, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);

        if (newConsumer instanceof ETFVertexConsumer newETFConsumer) {
            ETFTexture etfTexture = newETFConsumer.etf$getETFTexture();
            if (etfTexture == null) return;
            ETFUtils2.RenderMethodForOverlay renderMethodForOverlay = (prov, ligh) -> renderLikeVanilla(matrices, prov, ligh, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
            ETFUtils2.renderEmissive(etfTexture, provider, renderMethodForOverlay);
            ETFUtils2.renderEnchanted(etfTexture, provider, light, renderMethodForOverlay);
        }
    }

    //required for sodium 0.5.4+
    void renderLikeVanilla(PoseStack matrices, VertexConsumer vertices, int light, int overlay, #if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {

        if (this.visible) {
            if (!cubes.isEmpty() || !children.isEmpty()) {
                matrices.pushPose();
                this.translateAndRotate(matrices);
                if (!this.skipDraw) {
                    this.compile(matrices.last(), vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                }

                for (ModelPart modelPart : children.values()) {
                    modelPart.render(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                }

                matrices.popPose();
            }
        }
    }
//todo 1.21 or 1.20.6?
    #if MC >= MC_20_6
    private VertexConsumer testForBuildingException(VertexConsumer vertices) {
        BufferBuilder testBuilding;
        if (vertices instanceof BufferBuilder) {
            testBuilding = (BufferBuilder) vertices;
        } else if (vertices instanceof SpriteCoordinateExpander sprite && sprite.delegate instanceof BufferBuilder) {
            testBuilding = (BufferBuilder) sprite.delegate;
        }else if (vertices instanceof VertexMultiConsumer.Double dub && dub.second instanceof BufferBuilder) {
            testBuilding = (BufferBuilder) dub.second;
        }else{
            //exit early if not a buffer builder
            return vertices;
        }


        if (testBuilding != null && !testBuilding.building){
            if (testBuilding instanceof ETFVertexConsumer etf
                    && etf.etf$getRenderLayer() != null
                    && etf.etf$getProvider() != null){
                boolean allowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
                ETFRenderContext.preventRenderLayerTextureModify();

                vertices = etf.etf$getProvider().getBuffer(etf.etf$getRenderLayer());

                if (allowed) ETFRenderContext.allowRenderLayerTextureModify();
            }else {
                return null;
            }
        }
        return vertices;
    }
    #endif


    //mimics etf model part mixins which can no longer be relied on due to sodium 0.5.5
    void renderLikeETF(PoseStack matrices, VertexConsumer vertices, int light, int overlay, #if MC >= MC_21 final int k #else float red, float green, float blue, float alpha #endif) {

        //todo 1.21 or 1.20.6?
        #if MC >= MC_20_6
        vertices = testForBuildingException(vertices);
        if (vertices == null) return;
        #endif

        //etf ModelPartMixin copy
        ETFRenderContext.incrementCurrentModelPartDepth();

        renderLikeVanilla(matrices, vertices, light, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);

        //etf ModelPartMixin copy
        if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
        } else {
            //top level model so try special rendering
            if (ETFRenderContext.isCurrentlyRenderingEntity()
                    && vertices instanceof ETFVertexConsumer etfVertexConsumer) {
                ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
                //is etf texture not null and does it special render?
                if (texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                    MultiBufferSource provider = etfVertexConsumer.etf$getProvider();
                    //very important this is captured before doing the special renders as they can potentially modify
                    //the same ETFVertexConsumer down stream
                    RenderType layer = etfVertexConsumer.etf$getRenderLayer();
                    //are these render required objects valid?
                    if (provider != null && layer != null) {
                        //attempt special renders as eager OR checks
                        ETFUtils2.RenderMethodForOverlay renderMethodForOverlay = (prov, ligh) -> renderLikeVanilla(matrices, prov, ligh, overlay, #if MC >= MC_21 k #else red, green, blue, alpha #endif);
                        if (ETFUtils2.renderEmissive(texture, provider, renderMethodForOverlay) |
                                ETFUtils2.renderEnchanted(texture, provider, light, renderMethodForOverlay)) {
                            //reset render layer stuff behind the scenes if special renders occurred
                            //this will also return ETFVertexConsumer held data to normal if the same ETFVertexConsumer
                            //was previously affected by a special render
                            provider.getBuffer(layer);
                        }
                    }
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }

    public void renderBoxes(PoseStack matrices, VertexConsumer vertices) {
        if (this.visible) {
            if (!cubes.isEmpty() || !children.isEmpty()) {
                matrices.pushPose();
                this.translateAndRotate(matrices);
                if (!this.skipDraw) {
                    for (Cube cuboid : cubes) {
                        AABB box = new AABB(cuboid.minX / 16, cuboid.minY / 16, cuboid.minZ / 16, cuboid.maxX / 16, cuboid.maxY / 16, cuboid.maxZ / 16);
                        LevelRenderer.renderLineBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
                for (ModelPart modelPart : children.values()) {
                    if (modelPart instanceof EMFModelPart emf)
                        emf.renderBoxes(matrices, vertices);
                }
                matrices.popPose();
            }
        }
    }

    public void renderBoxesNoChildren(PoseStack matrices, VertexConsumer vertices, float alpha) {
        if (this.visible) {
            if (!cubes.isEmpty() || !children.isEmpty()) {
                matrices.pushPose();
                this.translateAndRotate(matrices);
                if (!this.skipDraw) {
                    for (Cube cuboid : cubes) {
                        AABB box = new AABB(cuboid.minX / 16, cuboid.minY / 16, cuboid.minZ / 16, cuboid.maxX / 16, cuboid.maxY / 16, cuboid.maxZ / 16);
                        LevelRenderer.renderLineBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, alpha);
                    }
                }
                matrices.popPose();
            }
        }
    }
    //required for sodium pre 0.5.4
    // overrides to circumvent sodium optimizations that mess with custom uv quad creation and swapping out cuboids
    @Override
    public void compile(final PoseStack.Pose pose, final VertexConsumer vertexConsumer, final int i, final int j, #if MC >= MC_21 int k #else float red, float green, float blue, float alpha #endif) {
        //this is a copy of the vanilla renderCuboids() method
        #if MC >= MC_21
        try {
        #endif
            for (Cube cuboid : cubes) {
                cuboid.compile(pose, vertexConsumer, i, j, #if MC >= MC_21 k #else red, green, blue, alpha #endif );
            }
        #if MC >= MC_21
        } catch (IllegalStateException e) {
            EMFUtils.logWarn("IllegalStateException caught in EMF model part");
        }
        #endif
    }

    public String simplePrintChildren(int depth) {
        StringBuilder mapper = new StringBuilder();
        mapper.append("\n  | ");
        mapper.append("- ".repeat(Math.max(0, depth)));
        mapper.append(this.toStringShort());
        for (ModelPart child :
                children.values()) {
            if (child instanceof EMFModelPart emf) {
                mapper.append(emf.simplePrintChildren(depth + 1));
            }
        }
        return mapper.toString();
    }

    public String toStringShort() {
        return toString();
    }


    @Override
    public String toString() {
        return "generic emf part";
    }


    //    private static int indent = 0;
    public ModelPart getVanillaModelPartsOfCurrentState() {
//        indent++;
        Map<String, ModelPart> children = new HashMap<>();
        for (Map.Entry<String, ModelPart> child :
                this.children.entrySet()) {
            if (child.getValue() instanceof EMFModelPart emf) {
                children.put(child.getKey(), emf.getVanillaModelPartsOfCurrentState());
            }
        }

        List<Cube> finalCubes;
        if (cubes.isEmpty()) {
            finalCubes = List.of(new Cube(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0, Set.of()));
        } else {
            finalCubes = cubes;
        }

        ModelPart part = new ModelPart(finalCubes, children);
        part.setInitialPose(getInitialPose());
        part.xRot = xRot;
        part.zRot = zRot;
        part.yRot = yRot;
        part.z = z;
        part.y = y;
        part.x = x;
        part.xScale = xScale;
        part.yScale = yScale;
        part.zScale = zScale;

        return part;
    }

    public Object2ReferenceOpenHashMap<String, EMFModelPart> getAllChildPartsAsAnimationMap(String prefixableParents, int variantNum, Map<String, String> optifinePartNameMap) {
        if (this instanceof EMFModelPartRoot root)
            root.setVariantStateTo(variantNum);

        Object2ReferenceOpenHashMap<String, EMFModelPart> mapOfAll = new Object2ReferenceOpenHashMap<>();
        //Map<String, ModelPart> children = this.children;

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
                    //put if absent so the first part with that id is the one referenced
                    mapOfAll.putIfAbsent(thisKey, part3);
                    if (prefixableParents.isBlank()) {
                        mapOfAll.putAll(part3.getAllChildPartsAsAnimationMap(thisKey, variantNum, optifinePartNameMap));
                    } else {
                        mapOfAll.putIfAbsent(prefixableParents + ':' + thisKey, part3);
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
}
