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

import java.util.*;

import static traben.entity_model_features.EMFClient.EYES_FEATURE_LIGHT_VALUE;

public abstract class EMFModelPart extends ModelPart {
    public Identifier textureOverride;
//    protected BufferBuilder MODIFIED_RENDER_BUFFER = null;


    public EMFModelPart(List<Cuboid> cuboids, Map<String, ModelPart> children) {
        super(cuboids, children);
    }


    void renderWithTextureOverride(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {

        if (textureOverride == null || EMFAnimationHelper.getEMFEntity() == null) {
            //normal vertex consumer
            renderToVanillaSuper(matrices, vertices, light, overlay, red, green, blue, alpha);
        } else if (light != EYES_FEATURE_LIGHT_VALUE // this is only the case for EyesFeatureRenderer
                && !ETFRenderContext.isIsInSpecialRenderOverlayPhase()) { //do not allow new etf emissive rendering here

            RenderLayer originalLayer = ETFRenderContext.getCurrentRenderLayer();
            RenderLayer layerModified = RenderLayer.getEntityTranslucent(textureOverride);
            VertexConsumer newConsumer = ETFRenderContext.processVertexConsumer(ETFRenderContext.getCurrentProvider(), layerModified);

            renderToVanillaSuper(matrices, newConsumer, light, overlay, red, green, blue, alpha);

            ETFRenderContext.startSpecialRenderOverlayPhase();
            etf$renderEmissive(matrices, overlay, red, green, blue, alpha);
            etf$renderEnchanted(matrices, light, overlay, red, green, blue, alpha);
            ETFRenderContext.endSpecialRenderOverlayPhase();

            //reset render settings
            ETFRenderContext.processVertexConsumer(ETFRenderContext.getCurrentProvider(), originalLayer);
        }
        //else cancel out render
    }

    void renderToVanillaSuper(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    //stop trying to optimize my code so it doesn't work sodium :P
    @Override
    // overrides to circumvent sodium optimizations that mess with custom uv quad creation and swapping out //todo better way??
    protected void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        //this is a copy of the vanilla renderCuboids() method
        for (Cuboid cuboid : ((ModelPartAccessor) this).getCuboids()) {
            cuboid.renderCuboid(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    public String simplePrintChildren(int depth){
        StringBuilder mapper = new StringBuilder();
        mapper.append("\n  | ");
        mapper.append("- ".repeat(Math.max(0, depth)));
        mapper.append(this.toStringShort());
        for (ModelPart child:
             getChildrenEMF().values()) {
            if(child instanceof EMFModelPart emf){
                mapper.append(emf.simplePrintChildren(depth+1));
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

    public Map<String, ModelPart> getChildrenEMF() {
        return ((ModelPartAccessor) this).getChildren();
    }

//    private static int indent = 0;
    public ModelPart getVanillaModelPartsOfCurrentState(){
//        indent++;
        Map<String, ModelPart> children = new HashMap<>();
        for (Map.Entry<String, ModelPart> child :
                getChildrenEMF().entrySet()) {
            if (child.getValue() instanceof EMFModelPart emf) {
                children.put(child.getKey(), emf.getVanillaModelPartsOfCurrentState());
            }
        }
//        indent--;
//        for (int i = 0; i < indent; i++) {
//            System.out.print("\\ ");
//        }
//        System.out.print("made: "+ this + "\n");
        List<Cuboid> cubes;
        if(((ModelPartAccessor) this).getCuboids().isEmpty()){
            cubes = List.of(new Cuboid(0,0,0,0,0,0,0,0,0,0,0,false,0,0, Set.of()));
        }else{
            cubes = ((ModelPartAccessor) this).getCuboids();
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

    //todo copy of etf rewrite emissive rendering code
    private void etf$renderEmissive(MatrixStack matrices, int overlay, float red, float green, float blue, float alpha) {
        Identifier emissive = ETFRenderContext.getCurrentETFTexture().getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {

            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();

            boolean textureIsAllowedBrightRender = ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT
                    && ETFRenderContext.getCurrentEntity().etf$canBeBright();// && !ETFRenderContext.getCurrentETFTexture().isPatched_CurrentlyOnlyArmor();

            VertexConsumer emissiveConsumer = ETFRenderContext.getCurrentProvider().getBuffer(
                    textureIsAllowedBrightRender ?
                            RenderLayer.getBeaconBeam(emissive, true) :
                            ETFRenderContext.getCurrentEntity().etf$isBlockEntity() ?
                                    RenderLayer.getEntityTranslucentCull(emissive) :
                                    RenderLayer.getEntityTranslucent(emissive));


            if (wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            renderToVanillaSuper(matrices, emissiveConsumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, red, green, blue, alpha);

        }
    }

    //todo copy of etf enchanted render code
    private void etf$renderEnchanted(MatrixStack matrices, int light, int overlay, float red, float green, float blue, float alpha) {
        //attempt enchanted render
        Identifier enchanted = ETFRenderContext.getCurrentETFTexture().getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(ETFRenderContext.getCurrentProvider(), RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
            if (wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            renderToVanillaSuper(matrices, enchantedVertex, light, overlay, red, green, blue, alpha);
        }
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
