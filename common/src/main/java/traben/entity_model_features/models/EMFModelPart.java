package traben.entity_model_features.models;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static traben.entity_model_features.EMF.EYES_FEATURE_LIGHT_VALUE;

public abstract class EMFModelPart extends ModelPart {
    public Identifier textureOverride;
    //    protected BufferBuilder MODIFIED_RENDER_BUFFER = null;
    protected long lastTextureOverride = -1L;


    public EMFModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);

        // re assert children and cuboids as modifiable
        // required for sodium post 0.5.4
        // this should not cause issues as emf does not allow these model parts to pass through sodium's unique renderer
        this.cuboids = new ObjectArrayList<>(cuboids);
        this.children = new Object2ObjectOpenHashMap<>(children);
    }

    void renderWithTextureOverride(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (textureOverride == null
                || lastTextureOverride == EMFManager.getInstance().entityRenderCount) {//prevents texture overrides carrying over into feature renderers that reuse the base model
            //normal vertex consumer
            renderLikeETF(matrices, vertices, light, overlay, red, green, blue, alpha);
        } else if (light != EYES_FEATURE_LIGHT_VALUE // this is only the case for EyesFeatureRenderer
                && !ETFRenderContext.isIsInSpecialRenderOverlayPhase() //do not allow new etf emissive rendering here
                && vertices instanceof ETFVertexConsumer etfVertexConsumer) { //can restore to previous render layer

            // if the texture override is the same as the current texture, render as normal
            var etfTextureTest = etfVertexConsumer.etf$getETFTexture();
            if (etfTextureTest != null && etfTextureTest.thisIdentifier.equals(textureOverride)) {
                renderLikeETF(matrices, vertices, light, overlay, red, green, blue, alpha);
                return;
            }

            VertexConsumerProvider provider = etfVertexConsumer.etf$getProvider();
            if (provider == null) return;

            RenderLayer originalLayer = etfVertexConsumer.etf$getRenderLayer();
            if (originalLayer == null) return;


            lastTextureOverride = EMFManager.getInstance().entityRenderCount;


            RenderLayer layerModified = EMFAnimationEntityContext.getLayerFromRecentFactoryOrETFOverrideOrTranslucent(textureOverride);
            VertexConsumer newConsumer = provider.getBuffer(layerModified);

            renderLikeVanilla(matrices, newConsumer, light, overlay, red, green, blue, alpha);

            if (newConsumer instanceof ETFVertexConsumer newETFConsumer) {
                ETFTexture etfTexture = newETFConsumer.etf$getETFTexture();
                if (etfTexture == null) return;
                ETFUtils2.RenderMethodForOverlay renderMethodForOverlay = (prov, ligh) -> renderLikeVanilla(matrices, prov, ligh, overlay, red, green, blue, alpha);
                ETFUtils2.renderEmissive(etfTexture, provider, renderMethodForOverlay);
                ETFUtils2.renderEnchanted(etfTexture, provider, light, renderMethodForOverlay);
            }


            //reset render settings
            provider.getBuffer(originalLayer);
        }
        //else cancel out render
    }

    //required for sodium 0.5.4+
    void renderLikeVanilla(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.visible) {
            if (!cuboids.isEmpty() || !children.isEmpty()) {
                matrices.push();
                this.rotate(matrices);
                if (!this.hidden) {
                    this.renderCuboids(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
                }

                for (ModelPart modelPart : children.values()) {
                    modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }

                matrices.pop();
            }
        }
    }

    //mimics etf model part mixins which can no longer be relied on due to sodium 0.5.5
    void renderLikeETF(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        //etf ModelPartMixin copy
        ETFRenderContext.incrementCurrentModelPartDepth();

        renderLikeVanilla(matrices, vertices, light, overlay, red, green, blue, alpha);

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
                    VertexConsumerProvider provider = etfVertexConsumer.etf$getProvider();
                    //very important this is captured before doing the special renders as they can potentially modify
                    //the same ETFVertexConsumer down stream
                    RenderLayer layer = etfVertexConsumer.etf$getRenderLayer();
                    //are these render required objects valid?
                    if (provider != null && layer != null) {
                        //attempt special renders as eager OR checks
                        ETFUtils2.RenderMethodForOverlay renderMethodForOverlay = (prov, ligh) -> renderLikeVanilla(matrices, prov, ligh, overlay, red, green, blue, alpha);
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

    public void renderBoxes(MatrixStack matrices, VertexConsumer vertices) {
        if (this.visible) {
            if (!cuboids.isEmpty() || !children.isEmpty()) {
                matrices.push();
                this.rotate(matrices);
                if (!this.hidden) {
                    for (Cuboid cuboid : cuboids) {
                        Box box = new Box(cuboid.minX / 16, cuboid.minY / 16, cuboid.minZ / 16, cuboid.maxX / 16, cuboid.maxY / 16, cuboid.maxZ / 16);
                        WorldRenderer.drawBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
                for (ModelPart modelPart : children.values()) {
                    if (modelPart instanceof EMFModelPart emf)
                        emf.renderBoxes(matrices, vertices);
                }
                matrices.pop();
            }
        }
    }

    public void renderBoxesNoChildren(MatrixStack matrices, VertexConsumer vertices, float alpha) {
        if (this.visible) {
            if (!cuboids.isEmpty() || !children.isEmpty()) {
                matrices.push();
                this.rotate(matrices);
                if (!this.hidden) {
                    for (Cuboid cuboid : cuboids) {
                        Box box = new Box(cuboid.minX / 16, cuboid.minY / 16, cuboid.minZ / 16, cuboid.maxX / 16, cuboid.maxY / 16, cuboid.maxZ / 16);
                        WorldRenderer.drawBox(matrices, vertices, box, 1.0F, 1.0F, 1.0F, alpha);
                    }
                }
                matrices.pop();
            }
        }
    }

    //required for sodium pre 0.5.4
    @Override
    // overrides to circumvent sodium optimizations that mess with custom uv quad creation and swapping out cuboids
    protected void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        //this is a copy of the vanilla renderCuboids() method
        for (Cuboid cuboid : cuboids) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
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

        List<Cuboid> cubes;
        if (cuboids.isEmpty()) {
            cubes = List.of(new Cuboid(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0, Set.of()));
        } else {
            cubes = cuboids;
        }

        ModelPart part = new ModelPart(cubes, children);
        part.setDefaultTransform(getDefaultTransform());
        part.pitch = pitch;
        part.roll = roll;
        part.yaw = yaw;
        part.pivotZ = pivotZ;
        part.pivotY = pivotY;
        part.pivotX = pivotX;
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
}
