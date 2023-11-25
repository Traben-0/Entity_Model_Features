package traben.entity_model_features.models;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationHelper;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.utils.OptifineMobNameForFileAndEMFMapId;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.utils.EntityIntLRU;

import java.util.*;

import static traben.entity_model_features.utils.EMFManager.getJemDataWithDirectory;


@Environment(value = EnvType.CLIENT)
public class EMFModelPartRoot extends EMFModelPartVanilla {

    public final OptifineMobNameForFileAndEMFMapId modelName;
    public final ModelPart vanillaRoot;
    public final @NotNull EntityIntLRU entitySuffixMap = new EntityIntLRU();
    private final Map<String, EMFModelPartVanilla> allVanillaParts;
    private final Int2ObjectOpenHashMap<ModelPart> vanillaFormatModelPartOfEachState = new Int2ObjectOpenHashMap<>();
    public EMFManager.CemDirectoryApplier variantDirectoryApplier;
    //    private long lastFrameVariatedOn = -1;
    public ETFApi.ETFVariantSuffixProvider variantTester = null;
    public boolean containsCustomModel = false;
    private long lastMobCountAnimatedOn = 0;
    private boolean removedJemTexture = false;

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

        this.vanillaRoot = vanillaRoot;

    }

    public void doVariantCheck() {
        //variant tester not null and is valid
        EMFAnimationHelper.thisModelVariates = true;
        int finalSuffix;
        UUID id = EMFAnimationHelper.getEMFEntity().etf$getUuid();
        int knownSuffix = entitySuffixMap.getInt(id);
        if (knownSuffix != -1) {
            checkIfShouldExpireEntity(id);
            finalSuffix = knownSuffix;
        } else {
            int newSuffix;
            newSuffix = this.variantTester.getSuffixForETFEntity(EMFAnimationHelper.getEMFEntity());
            if (newSuffix == 0) {//DONT ALLOW 0 IN EMF
                this.entitySuffixMap.put(id, 1);
                finalSuffix = 1;
            } else {
                this.entitySuffixMap.put(id, newSuffix);
                finalSuffix = newSuffix;
            }
        }
        setVariantStateTo(finalSuffix);
    }

//    private boolean hasRegisteredVariator = false;

//    @Override
//    public void setVariantStateTo(int newVariant) {
//        if (currentModelVariant != newVariant) {
//            super.setVariantStateTo(newVariant);//always does contain it now
//        }
//    }

    public void checkIfShouldExpireEntity(UUID id) {
        if (this.variantTester.entityCanUpdate(id)) {
            switch (EMFConfig.getConfig().modelUpdateFrequency) {
                case Never -> {
                }
                case Instant -> this.entitySuffixMap.removeInt(id);
                default -> {
                    int delay = EMFConfig.getConfig().modelUpdateFrequency.getDelay();
                    int time = (int) (EMFAnimationHelper.getTime() % delay);
                    if (time == Math.abs(id.hashCode()) % delay) {
                        this.entitySuffixMap.removeInt(id);
                    }
                }
            }
        }
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

    public void discoverAndInitVariants() {
        //get random properties and init variants
        String thisDirectoryFileName = variantDirectoryApplier.getThisDirectoryOfFilename(modelName.getfileName());
        Identifier propertyID = new Identifier(thisDirectoryFileName + ".properties");
        if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
            //todo same or higher pack check
            variantTester = ETFApi.getVariantSupplierOrNull(propertyID, new Identifier(thisDirectoryFileName + ".jem"), "models");
            if (variantTester != null) {
                IntOpenHashSet allModelVariants = variantTester.getAllSuffixes();
                allModelVariants.remove(1);
                allModelVariants.remove(0);
                if (!allModelVariants.isEmpty()) {
                    //init all variants
                    for (int variant : allModelVariants) {
                        setVariantStateTo(1);

                        String jemNameVariant = variantDirectoryApplier.getThisDirectoryOfFilename(modelName.getfileName() + variant + ".jem");
                        if (EMFConfig.getConfig().logModelCreationData)
                            System.out.println(" > incorporating variant jem file: " + jemNameVariant);
                        EMFJemData jemDataVariant = getJemDataWithDirectory(jemNameVariant, modelName);
                        if (jemDataVariant != null) {
                            addVariantOfJem(jemDataVariant, variant);
                            setVariantStateTo(variant);
                            EMFManager.getInstance().setupAnimationsFromJemToModel(jemDataVariant, this, variant);
                            containsCustomModel = true;
                        } else {
                            //make this variant map to 1
                            allKnownStateVariants.put(variant, allKnownStateVariants.get(1));
                            System.out.println(" > invalid jem variant file: " + jemNameVariant);
                        }

                    }
                    receiveOneTimeOnlyRunnable(() -> {
                        String type = EMFAnimationHelper.getEMFEntity().emf$getTypeString();
                        if (EMFConfig.getConfig().logModelCreationData)
                            EMFUtils.EMFModMessage("Registered new variating model for: " + type);

                        Set<Runnable> variators = EMFManager.getInstance().rootPartsPerEntityTypeForVariation.get(type);
                        if (variators == null) {
                            Set<Runnable> newVariators = new HashSet<>();
                            EMFManager.getInstance().rootPartsPerEntityTypeForVariation.put(type, newVariators);
                            newVariators.add(this::doVariantCheck);
                        } else {
                            variators.add(this::doVariantCheck);
                        }

                        //now set the runnable to null so it only runs once
                        this.receiveOneTimeOnlyRunnable(null);
                    });
                } else {
                    EMFUtils.EMFModWarn("non variating properties found for: " + propertyID);
                    variantTester = null;
                    variantDirectoryApplier = null;
                }
            } else {
                EMFUtils.EMFModWarn("null properties found for: " + propertyID);
                variantDirectoryApplier = null;
            }
        } else {
            EMFUtils.EMFModWarn("no properties or variants found for found for: " + thisDirectoryFileName + ".jem");
            variantDirectoryApplier = null;
        }
    }

    public void setVariant1ToVanilla0() {
        allKnownStateVariants.put(1, allKnownStateVariants.get(0));
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
            if (!vanillaFormatModelPartOfEachState.containsKey(currentModelVariant)) {
                vanillaFormatModelPartOfEachState.put(currentModelVariant, getVanillaModelPartsOfCurrentState());
            }
            ModelPart vanillaFormat = vanillaFormatModelPartOfEachState.get(currentModelVariant);
            if (vanillaFormat != null) {
                vanillaFormat.render(matrixStack, vertexConsumer, light, overlay, 1, 1, 1, 1);
            }
        }
    }

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

    public void removeJemOverrideTextureForModelSupplyingItAnotherWay() {
        if (removedJemTexture) return;
        removedJemTexture = true;

        if (textureOverride != null) {
            allVanillaParts.values().forEach((emf) -> {
                if (emf.textureOverride.equals(textureOverride)) emf.textureOverride = null;
            });
        }
    }


    @Override
    public String toString() {
        return "[EMF_root of " + modelName.getfileName() + "]";
    }
}
