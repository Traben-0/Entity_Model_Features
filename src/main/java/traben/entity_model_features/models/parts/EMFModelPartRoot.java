package traben.entity_model_features.models.parts;


import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.math.MathComponent;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_model_features.utils.EMFDirectoryHandler;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.utils.EMFResourceCaching;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.features.property_reading.PropertiesRandomProvider;

import java.util.*;
import java.util.function.Supplier;

import static traben.entity_model_features.EMFManager.getJemDataWithDirectory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import traben.entity_texture_features.utils.ETFLruCache;


public class EMFModelPartRoot extends EMFModelPartVanilla {

    public final EMFModel_ID modelName;
    public final ModelPart vanillaRoot;
    public final @NotNull ETFLruCache.UUIDInteger entitySuffixMap = new ETFLruCache.UUIDInteger();
    private final Map<String, EMFModelPartVanilla> allVanillaParts;
    private final Map<Integer, ModelPart> vanillaFormatModelPartOfEachState = new HashMap<>();
    public EMFDirectoryHandler directoryContext;
    public ETFApi.ETFVariantSuffixProvider variantTester = null;
    public boolean containsCustomModel = false;
    public boolean containsCustomAnims = false;
    private long lastMobCountAnimatedOn = 0;
    private boolean hasRemovedTopLevelJemTextureFromChildren = false;
    private boolean hasArmItemOverrides = false;

    // construct vanilla root
    public EMFModelPartRoot(EMFModel_ID mobNameForFileAndMap,
                            EMFDirectoryHandler directoryContext,
                            ModelPart vanillaRoot,
                            Collection<String> optifinePartNames,
                            Map<String, EMFModelPartVanilla> mapForCreatedParts) {
        // create vanilla root model object
        super("root", vanillaRoot, optifinePartNames, mapForCreatedParts);
        allVanillaParts = mapForCreatedParts;
        allVanillaParts.putIfAbsent(name, this);

        this.modelName = mobNameForFileAndMap;
        this.directoryContext = directoryContext;

        this.vanillaRoot = vanillaRoot;

        // init the first time runnable into all vanilla children
        receiveOneTimeRunnable(this::registerModelRunnableWithEntityTypeContext);
    }


    @Override
    protected float[] debugBoxColor() {
        return new float[]{1f, 0, 0};
    }

    public Collection<EMFModelPartVanilla> getAllVanillaPartsEMF() {
        return allVanillaParts.values();
    }

    private void registerModelRunnableWithEntityTypeContext() {
        var entity = EMFAnimationEntityContext.getEmfState();
        if (entity != null) { // await a valid entity
            String type = entity.typeString();
            var config = EMF.config().getConfig();

            // register models to entity type for debug print
            if (config.debugOnRightClick) {
                EMFManager.getInstance().rootPartsPerEntityTypeForDebug
                        .computeIfAbsent(type, k -> new HashSet<>())
                        .add(this);
            }

            // register variant runnable
            EMFManager.getInstance().rootPartsPerEntityTypeForVariation
                    .computeIfAbsent(type, k -> new HashSet<>())
                    .add(this);

            if (variantTester != null && config.logModelCreationData) {
                EMFUtils.log("Registered new variating model for: " + type);
            }

            // now set the runnable to null so it only runs once
            this.receiveOneTimeRunnable(null);
        }
    }

    public void doVariantCheck() {
        var emfState = EMFAnimationEntityContext.getEmfState();
        if(this.variantTester == null || emfState == null) {
            this.setVariantStateTo(1);
            return;
        }

        UUID id = emfState.uuid();
        int finalSuffix = entitySuffixMap.get(id);

        if (finalSuffix != -1) {
            checkIfShouldExpireEntity(id);
        } else {
            finalSuffix = Math.max(1, variantTester.getSuffixForETFEntity(emfState));
            entitySuffixMap.put(id, finalSuffix);
        }
        EMFManager.getInstance().lastModelSuffixOfEntity.put(id, finalSuffix);
        setVariantStateTo(finalSuffix);
    }

    public void checkIfShouldExpireEntity(UUID id) {
        if (this.variantTester.entityCanUpdate(id)) {
            switch (EMF.config().getConfig().modelUpdateFrequency) {
                case Never -> {}
                case Instant -> this.entitySuffixMap.remove(id);
                default -> {
                    int delay = EMF.config().getConfig().modelUpdateFrequency.getDelay();
                    int time = (int) (EMFAnimationEntityContext.getTime() % delay);
                    if (time == Math.abs(id.hashCode()) % delay) {
                        this.entitySuffixMap.remove(id);
                    }
                }
            }
        }
    }

    //root only
    public void addVariantOfJem(EMFJemData jemData, int variant) {
        if (EMF.config().getConfig().logModelCreationData)
            EMFUtils.log(" > " + jemData.getMobModelIDInfo().getfileName() + ", constructing variant #" + variant);

        if (jemData.hasAttachments) this.hasArmItemOverrides = true;

        Map<String, EMFModelPartCustom> newEmfParts = new HashMap<>();
        for (EMFPartData part : jemData.models) {
            if (part.part != null) {
                String idUnique = EMFUtils.getIdUnique(newEmfParts.keySet(), part.id);
                newEmfParts.put(idUnique, new EMFModelPartCustom(part, variant, part.part, idUnique));
            }
        }
        var rootTextureOverride = jemData.getCustomTexture();

        for (Map.Entry<String, EMFModelPartVanilla> vanillaEntry : allVanillaParts.entrySet()) {
            EMFModelPartVanilla thisPart = vanillaEntry.getValue();
            EMFModelState vanillaState = thisPart.allKnownStateVariants.get(0).copy();
            thisPart.setFromState(vanillaState);

            thisPart.textureOverride = rootTextureOverride;

            Map<String, ModelPart> children = new HashMap<>(thisPart.vanillaChildren);
            for (Map.Entry<String, EMFModelPartCustom> newPartEntry : newEmfParts.entrySet()) {
                EMFModelPartCustom newPart = newPartEntry.getValue();
                if (vanillaEntry.getKey().equals(newPart.partToBeAttached)) {
                    if (EMF.config().getConfig().logModelCreationData)
                        EMFUtils.log(" > > > EMF custom part attached: " + newPartEntry.getKey());
                    if (!newPart.attach) {
                        thisPart.cubes = List.of();
                        thisPart.children.values().forEach((part) -> {
                            if (part instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified)
                                vanilla.setHideInTheseStates(variant);
                        });
                    }
                    children.put(EMFUtils.getIdUnique(thisPart.children.keySet(), newPartEntry.getKey()), newPart);
                }
            }
            thisPart.children = children;
            thisPart.allKnownStateVariants.put(variant, thisPart.getCurrentState());

        }
        allKnownStateVariants.putIfAbsent(variant, allKnownStateVariants.get(0).copy());
    }

    public void discoverAndInitVariants(String fallbackPropertiesName) {
        boolean printing = EMF.config().getConfig().logModelCreationData;

        //get random properties and init variants
        String thisDirectoryFileName =  directoryContext.getRelativeDirectoryLocationNoValidation(directoryContext.rawFileName);
        ResourceLocation propertyID = directoryContext.getRelativeFilePossiblyEMFOverridden(directoryContext.rawFileName + ".properties");

        if(printing) EMFUtils.log(" > checking properties file: " + propertyID + " for: " + thisDirectoryFileName + ".jem");

        var resourceManager = Minecraft.getInstance().getResourceManager();
        boolean exists = EMFResourceCaching.resourceExists(resourceManager, propertyID);

        //try fallback properties
        if (!exists && EMF.config().getConfig().allowOptifineFallbackProperties){
            ResourceLocation fallbackPropertiesID = directoryContext.getRelativeFilePossiblyEMFOverridden(fallbackPropertiesName + ".properties");
            if(printing) EMFUtils.log(" > checking fallback .properties file: " + fallbackPropertiesID + " for: " + thisDirectoryFileName + ".jem");
            exists = EMFResourceCaching.resourceExists(resourceManager, fallbackPropertiesID);
            if(exists){
                propertyID = fallbackPropertiesID;
            }
        }

        if (exists) {
            if(printing) EMFUtils.log(" > found properties file: " + propertyID + " for: " + thisDirectoryFileName + ".jem");
            variantTester = ETFApi.getVariantSupplierOrNull(propertyID, EMFUtils.res(thisDirectoryFileName + ".jem"), "models");

            if (variantTester instanceof PropertiesRandomProvider propertiesRandomProvider) {
                propertiesRandomProvider.setOnMeetsRuleHook((entity, rule) -> {
                    if (rule == null) {
                        EMFManager.getInstance().lastModelRuleOfEntity.remove(entity.uuid());
                    } else {
                        EMFManager.getInstance().lastModelRuleOfEntity.put(entity.uuid(), rule.ruleNumber);
                    }
                });
            }

            if (variantTester != null) {
                Set<Integer> allModelVariants = variantTester.getAllSuffixes();
                allModelVariants.remove(1);
                allModelVariants.remove(0);
                if (!allModelVariants.isEmpty()) {
                    //init all variants
                    for (int variant : allModelVariants) {
                        setVariantStateTo(1);
                        //String jemNameVariant = directoryContext.getRelativeFilePossiblyEMFOverridden( directoryContext.rawFileName + variant + ".jem");
                        EMFDirectoryHandler variantDirectoryContext = EMFDirectoryHandler.getDirectoryManagerOrNull(EMF.config().getConfig().logModelCreationData, directoryContext.namespace, directoryContext.rawFileName,  variant + ".jem");
                        boolean canUseVariant = directoryContext.validForThisBase(variantDirectoryContext);//null checks

                        if (printing) EMFUtils.log(" > incorporating variant jem file: " + directoryContext.namespace + ":"+ directoryContext.rawFileName + variant + ".jem");

                        //noinspection DataFlowIssue
                        EMFJemData jemDataVariant = canUseVariant ? getJemDataWithDirectory(variantDirectoryContext, modelName) : null;

                        if (jemDataVariant != null) {
                            addVariantOfJem(jemDataVariant, variant);
                            setVariantStateTo(variant);
                            EMFManager.getInstance().setupAnimationsFromJemToModel(jemDataVariant, this, variant);
                            containsCustomModel = true;
                        } else {
                            //make this variant map to 1
                            allKnownStateVariants.put(variant, allKnownStateVariants.get(1));
                            if (printing) EMFUtils.log(" > invalid jem variant file: " + directoryContext.namespace + ":"+ directoryContext.rawFileName + variant + ".jem");
                        }

                    }
                } else {
                    if (printing) EMFUtils.logWarn("properties with only 1 variant found: " + propertyID + ".");
                }
            } else {
                if (printing) EMFUtils.logWarn("null properties found for: " + propertyID);
                directoryContext = null;
            }
        } else {
            if (printing) EMFUtils.logWarn("no properties or variants found for found for: [" + thisDirectoryFileName + ".jem]");
            directoryContext = null;
        }
    }


    public void setVariant1ToVanilla0() {
        allKnownStateVariants.put(1, allKnownStateVariants.get(0));
        allVanillaParts.forEach((k, child) -> child.allKnownStateVariants.put(1, child.allKnownStateVariants.get(0)));
    }


    public void tryRenderVanillaRootNormally(PoseStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
        if (vanillaRoot != null) {
            matrixStack.pushPose();
            if (EMF.config().getConfig().getVanillaHologramModeFor(EMFAnimationEntityContext.getEMFEntity()) == EMFConfig.VanillaModelRenderMode.OFFSET) {
                matrixStack.translate(1, 0, 0);
            }
            vanillaRoot.render(matrixStack, vertexConsumer, light, overlay
                    //#if MC >= 12100
                    //#else
                    //$$ , 1f, 1f, 1f, 1f
                    //#endif
            );
            matrixStack.popPose();
        }
    }

//    public void tryRenderVanillaFormatRoot(PoseStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
//        if (EMF.config().getConfig().getPhysicsModModeFor(EMFAnimationEntityContext.getEMFEntity()) == EMFConfig.PhysicsModCompatChoice.VANILLA) {
//            if (vanillaRoot != null) {
//                vanillaRoot.render(matrixStack, vertexConsumer, light, overlay #if MC >= MC_21  #else , 1f, 1f, 1f, 1f #endif);
//            }
//        } else {
//            ModelPart vanillaFormat = getVanillaFormatRoot();
//            if (vanillaFormat != null) {
//                vanillaFormat.render(matrixStack, vertexConsumer, light, overlay #if MC >= MC_21  #else , 1f, 1f, 1f, 1f #endif);
//            }
//        }
//    }

    public boolean hasAnimation(){
        return animationHolder.hasAnimation();
    }

    public void triggerManualAnimation(PoseStack pose) {
        if (hasAnimation()) animationHolder.run();
        checkArmOverrides(pose);
    }

    public void checkArmOverrides(PoseStack pose) {
        if (hasArmItemOverrides) processArmItemOverrides(pose);
    }

    public ModelPart getVanillaFormatRoot() {
        if (!vanillaFormatModelPartOfEachState.containsKey(currentModelVariant)) {
            vanillaFormatModelPartOfEachState.put(currentModelVariant, getVanillaModelPartsOfCurrentState());
        }
        return vanillaFormatModelPartOfEachState.get(currentModelVariant);
    }

    public void receiveAnimations(int variant, Collection<EMFAnimation> animationList, ASMVariableHandler asmVariableHandler, LinkedHashMap<String, EMFAnimation> emfAnimationVariables,
                                  HashMap<String, EMFModelPart> allPartByName) {
        final var finalList = new ArrayList<>(animationList);
        boolean doesASM = EMF.config().getConfig().asmMaths;
        boolean logsASM = doesASM && EMF.config().getConfig().logASM;
        final var finalAsmVarListSupplier = doesASM ? prepopulateVarStates(asmVariableHandler, emfAnimationVariables, allPartByName) : null;

        if (!finalList.isEmpty()) {
            containsCustomAnims = true;
            Runnable run = () -> {
                if (lastMobCountAnimatedOn != EMFManager.getInstance().entityRenderCount) {
                    lastMobCountAnimatedOn = EMFManager.getInstance().entityRenderCount;

                    if (EMFAnimationEntityContext.isFirstPersonHand && EMF.config().getConfig().preventFirstPersonHandAnimating) return;

                    var pausedParts = EMFAnimationEntityContext.getEntityPartsAnimPaused();

                    ASMVariableHandler.AnimVars vars = doesASM ? finalAsmVarListSupplier.get() : null;

                    if (logsASM) {
                        EMFUtils.log("Start ASM anim with vars:");
                        for (int i = 0; i < vars.floats().length; i++) {
                            EMFUtils.log(" - " + asmVariableHandler.getFloatVarList().get(i) + ": " + vars.floats()[i]);
                        }
                        for (int i = 0; i < vars.bools().length; i++) {
                            EMFUtils.log(" - " + asmVariableHandler.getBoolVarList().get(i) + ": " + vars.bools()[i]);
                        }
                    }

                    for (EMFAnimation emfAnimation : finalList) {
                        try {
                            if (pausedParts != null){
                                emfAnimation.calculateAndSetIfNotPaused(pausedParts, vars);
                            }else{
                                emfAnimation.calculateAndSet(vars);
                            }
                        } catch (Exception e) {
                            EMFUtils.logError("Error in animation expression [" + emfAnimation.animKey + "] for model [" + modelName.getfileName() + "] with expression [" + emfAnimation.expressionString + "].");
                            e.printStackTrace();
                            EMFUtils.logError("Disabling all animations for model: [" + modelName + "]");
                            allVanillaParts.values().forEach((emf) -> emf.receiveRootAnimationRunnable(variant, null));
                        }
                    }
                    if (logsASM) {
                        EMFUtils.log("End ASM anim with vars:");
                        for (int i = 0; i < vars.floats().length; i++) {
                            EMFUtils.log(" - " + asmVariableHandler.getFloatVarList().get(i) + ": " + vars.floats()[i]);
                        }
                        for (int i = 0; i < vars.bools().length; i++) {
                            EMFUtils.log(" - " + asmVariableHandler.getBoolVarList().get(i) + ": " + vars.bools()[i]);
                        }
                    }
                }
            };
            allVanillaParts.values().forEach((emf) -> emf.receiveRootAnimationRunnable(variant, run));
        }
    }

    ;


    private Supplier<ASMVariableHandler.AnimVars> prepopulateVarStates(
            ASMVariableHandler asmVariableHandler,
            LinkedHashMap<String, EMFAnimation> emfAnimationVariables,
            HashMap<String, EMFModelPart> allPartByName
    ) {
        var dummyInstance = new AnimSetupContext("prepopulateVarStates()", emfAnimationVariables,allPartByName);
        dummyInstance.animKey = "prepopulateVarStates()";

        List<@Nullable MathComponent> floats = new ArrayList<>();
        List<@Nullable MathComponent> bools = new ArrayList<>();
        for (String varName : asmVariableHandler.getFloatVarList()) {

            if (!asmVariableHandler.isReadVarName(varName)) {
                floats.add(null);
                continue;
                // Skip var names that aren't read, as they won't be used by the calculation, but we couldn't know that
                // until the end so all the ASM var indexing already includes them in the index counts
            }
            var variable = VariableRegistry.getInstance().getVariable(varName, false, dummyInstance);
            floats.add(variable);
        }
        for (String varName : asmVariableHandler.getBoolVarList()) {

            if (!asmVariableHandler.isReadVarName(varName)) {
                bools.add(null);
                continue;
                // Skip var names that aren't read, as they won't be used by the calculation, but we couldn't know that
                // until the end so all the ASM var indexing already includes them in the index counts
            }
            var variable = VariableRegistry.getInstance().getVariable(varName, false, dummyInstance);
            bools.add(variable);
        }

        final int size = floats.size();
        final int sizeBool = bools.size();
        return ()-> {
            float[] vars = new float[size];
            for (int i = 0; i < size; i++) {
                MathComponent variable = floats.get(i);
                if (variable != null) {
                    float result = variable.getResult();

                    vars[i] = Float.isInfinite(result)
                            ? result > 0 ? 1 : 0
                            : result;
                }
            }
            boolean[] varsBool  = new boolean[sizeBool];
            for (int i = 0; i < sizeBool ; i++) {
                MathComponent variable = bools.get(i);
                if (variable != null) {
                    float result = variable.getResult();

                    varsBool[i] = MathValue.toBoolean(result);
                }
            }
            return new ASMVariableHandler.AnimVars(vars, varsBool);
        };

    }


    /**
     * Gets top level jem texture.
     * also removes this texture from the child part overrides if this is the first time it's called.
     * This allows optimizing the amount of times the texture is overridden for living entities
     *
     * @return the top level jem texture
     */
    public ResourceLocation getTopLevelJemTexture() {
        if (hasRemovedTopLevelJemTextureFromChildren)
            return jemLevelOverride;
        hasRemovedTopLevelJemTextureFromChildren = true;
        jemLevelOverride = textureOverride;
        if (textureOverride != null) {
            allVanillaParts.values().forEach((emf) -> {
                if (emf.textureOverride.equals(textureOverride)) emf.textureOverride = null;
            });
        }
        return jemLevelOverride;
    }

    private ResourceLocation jemLevelOverride = null;

    public void resetVanillaPartsToDefaults(){
        this.resetState();
        allVanillaParts.values().forEach(EMFModelPartWithState::resetState);
    }


    @Override
    public String toString() {
        return "[EMF root part of " + modelName.getfileName() + "]";
    }

    @Override
    public String toStringShort() {
        return "[EMF root part of " + modelName.getfileName() + "]";
    }


}
