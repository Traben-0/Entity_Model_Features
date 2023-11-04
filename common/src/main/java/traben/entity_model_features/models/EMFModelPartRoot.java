package traben.entity_model_features.models;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;
import traben.entity_texture_features.ETFApi;

import java.util.*;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartRoot extends EMFModelPartVanilla {

    public final OptifineMobNameForFileAndEMFMapId modelName;
    public final ModelPart vanillaRoot;
    private final Map<String, EMFModelPartVanilla> allVanillaParts;
    private final Int2ObjectOpenHashMap<ModelPart> vanillaFormatModelPartOfEachState = new Int2ObjectOpenHashMap<>();
    public EMFManager.CemDirectoryApplier variantDirectoryApplier;
    public ETFApi.ETFRandomTexturePropertyInstance variantTester = null;
    private long lastFrameVariatedOn = -1;

    //construct vanilla root
    public EMFModelPartRoot(OptifineMobNameForFileAndEMFMapId mobNameForFileAndMap,
                            EMFManager.CemDirectoryApplier variantDirectoryApplier,
                            ModelPart vanillaRoot,
                            Collection<String> optifinePartNames,
                            Map<String, EMFModelPartVanilla> mapForCreatedParts) {
        //create vanilla root model object
        super("EMF_root", vanillaRoot, optifinePartNames, mapForCreatedParts);
        allVanillaParts = mapForCreatedParts;
        allVanillaParts.put(name, this);

        this.modelName = mobNameForFileAndMap;
        this.variantDirectoryApplier = variantDirectoryApplier;

//        Map<String,ModelPart> children = getChildrenEMF();
//        for (Map.Entry<String,ModelPart> child:
//             ((ModelPartAccessor) vanillaRoot).getChildren().entrySet()) {
//            EMFModelPartVanilla vanilla =new EMFModelPartVanilla(child.getKey(),child.getValue(),optifinePartNameMap.values(), allVanillaParts);
//            children.put(child.getKey(), vanilla);
//            allVanillaParts.put(child.getKey(), vanilla);
//        }

        receiveStartOfRenderRunnable((emfModelPart) -> {
            if (this.lastFrameVariatedOn != EMFManager.getInstance().entityRenderCount) {
                this.lastFrameVariatedOn = EMFManager.getInstance().entityRenderCount;
                EMFManager.getInstance().doVariantCheckFor(this);
            }
//            if(this.getTextureOverride() != null && emfModelPart.textureOverride == null){
//                emfModelPart.textureOverride = this.getTextureOverride();
//                emfModelPart.needsToNullifyCustomTexture = true;
//            }
        });


        this.vanillaRoot = vanillaRoot;

        //vanillaChildren = getChildrenEMF();
        // allKnownStateVariants.put(0,getCurrentState());
    }




    //root only
    public void addVariantOfJem(EMFJemData jemData, int variant) {
        if (EMFConfig.getConfig().logModelCreationData)
            System.out.println(" > " + jemData.mobModelIDInfo.getfileName() + ", constructing variant #" + variant);

        Map<String, EMFModelPartCustom> newEmfParts = new HashMap<>();
        for (EMFPartData part :
                jemData.models) {
            if (part.part != null) {
                String idUnique = EMFUtils.getIdUnique(newEmfParts.keySet(), part.id);
                newEmfParts.put(idUnique, new EMFModelPartCustom(part, variant, part.part, idUnique));
            }
        }
        for (Map.Entry<String, EMFModelPartVanilla> vanillaEntry :
                allVanillaParts.entrySet()) {
            String thisPartName = vanillaEntry.getKey();
            EMFModelPartVanilla thisPart = vanillaEntry.getValue();
            EMFModelState vanillaState = EMFModelState.copy(thisPart.allKnownStateVariants.get(0));
            thisPart.setFromState(vanillaState);
            if (thisPart instanceof EMFModelPartRoot root && !((ModelPartAccessor) root).getCuboids().isEmpty()) {
                root.textureOverride = jemData.customTexture;
            }
            Map<String, ModelPart> children = new HashMap<>();
            for (Map.Entry<String, EMFModelPartCustom> newPartEntry :
                    newEmfParts.entrySet()) {
                EMFModelPartCustom newPart = newPartEntry.getValue();
                if (thisPartName.equals(newPart.partToBeAttached)) {
                    if (EMFConfig.getConfig().logModelCreationData)
                        System.out.println(" > > > EMF custom part attached: " + newPartEntry.getKey());
                    if (!newPart.attach) {
                        ((ModelPartAccessor) thisPart).setCuboids(List.of());
                        thisPart.getChildrenEMF().values().forEach((part) -> {
                            if (part instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified)
                                vanilla.setHideInTheseStates(variant);
                        });
                    }
                    children.put(EMFUtils.getIdUnique(thisPart.getChildrenEMF().keySet(), newPartEntry.getKey()), newPart);
                }
            }
            children.putAll(thisPart.vanillaChildren);
            ((ModelPartAccessor) thisPart).setChildren(children);
            thisPart.allKnownStateVariants.put(variant, thisPart.getCurrentState());

        }
        if (!allKnownStateVariants.containsKey(variant))
            allKnownStateVariants.put(variant, EMFModelState.copy(allKnownStateVariants.get(0)));

    }

    @Override
    public void setVariantStateTo(int newVariantState) {
        if (currentModelVariantState != newVariantState) {
            if (newVariantState == 1 && !allKnownStateVariants.containsKey(1)) {
                //EMFAnimationHelper.setRuleIndex(0); already is
                super.setVariantStateTo(0);
            } else {
               // EMFAnimationHelper.setRuleIndex(newVariantState);
                super.setVariantStateTo(newVariantState);
            }
        }
    }

    public void tryRenderVanillaRootNormally(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
        if (vanillaRoot != null) {
            matrixStack.push();
            if (EMFConfig.getConfig().vanillaModelHologramRenderMode == EMFConfig.VanillaModelRenderMode.Positon_offset) {
                matrixStack.translate(1, 0, 0);
            }
            vanillaRoot.render(matrixStack, vertexConsumer, light, overlay, 1, 0.5f, 0.5f, 0.5f);
            matrixStack.pop();
        }
    }

    public void tryRenderVanillaFormatRoot(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
        if (EMFConfig.getConfig().attemptPhysicsModPatch_2 == EMFConfig.PhysicsModCompatChoice.VANILLA) {
            if (vanillaRoot != null) {
                vanillaRoot.render(matrixStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
            }
        } else {
            if (!vanillaFormatModelPartOfEachState.containsKey(currentModelVariantState)) {
                vanillaFormatModelPartOfEachState.put(currentModelVariantState, getVanillaModelPartsOfCurrentState());
            }
            ModelPart vanillaFormat = vanillaFormatModelPartOfEachState.get(currentModelVariantState);
            if (vanillaFormat != null) {
                vanillaFormat.render(matrixStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
            }
        }
    }


    private long lastMobCountAnimatedOn = 0;

    public void receiveAnimations(int variant, Object2ObjectLinkedOpenHashMap<String, Object2ObjectLinkedOpenHashMap<String, EMFAnimation>> orderedAnimationsByPartName) {
        LinkedList<EMFAnimation> animationList = new LinkedList<>();
        if (orderedAnimationsByPartName.size() > 0) {
            allVanillaParts.values().forEach((emf) -> {
                if (orderedAnimationsByPartName.containsKey(emf.name)) {
                    Object2ObjectLinkedOpenHashMap<String, EMFAnimation> anims = orderedAnimationsByPartName.get(emf.name);
                    if (anims != null && !anims.isEmpty()) {
                        anims.forEach((key, anim) -> animationList.add(anim));
                    }
                }
            });
        }
        if (animationList.size() > 0) {
            Runnable run = () -> {
                if (lastMobCountAnimatedOn != EMFManager.getInstance().entityRenderCount) {
                    lastMobCountAnimatedOn = EMFManager.getInstance().entityRenderCount;
                    animationList.forEach((EMFAnimation::calculateAndSet));
                }
            };
            allVanillaParts.values().forEach((emf) -> emf.receiveRootAnimationRunnable(variant, run));
        }
    }
    private boolean removedJemTexture = false;
    public void removeJemOverrideTextureForModelSupplyingItAnotherWay(){
        if(removedJemTexture) return;
        removedJemTexture = true;

        if(textureOverride != null){
            allVanillaParts.values().forEach((emf) -> {
                if(emf.textureOverride.equals(textureOverride)) emf.textureOverride = null;
            });
        }
    }


    @Override
    public String toString() {
        return "[EMF_root of " + modelName.getfileName() + "]";
    }
}
