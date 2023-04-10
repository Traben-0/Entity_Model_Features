package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.EMFModelPart3;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationVariableSuppliers;
import traben.entity_model_features.models.animation.EMFDefaultModelVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;

public class EMFManager {//singleton for data holding and resetting needs


    private static final Object2ObjectOpenHashMap<String, String> map_MultiMobVariantMap = new Object2ObjectOpenHashMap<>() {{
        put("cat2", "cat_collar");
        put("wither_skeleton2", "wither_skeleton_inner_armor");
        put("wither_skeleton3", "wither_skeleton_outer_armor");
        put("zombie2", "zombie_inner_armor");
        put("zombie3", "zombie_outer_armor");
        put("skeleton2", "skeleton_inner_armor");
        put("skeleton3", "skeleton_outer_armor");
        put("zombified_piglin2", "zombified_piglin_inner_armor");
        put("zombified_piglin3", "zombified_piglin_outer_armor");
        put("piglin2", "piglin_inner_armor");
        put("piglin3", "piglin_outer_armor");
        put("piglin_brute2", "piglin_brute_inner_armor");
        put("piglin_brute3", "piglin_brute_outer_armor");
        put("armor_stand2", "armor_stand_inner_armor");
        put("armor_stand3", "armor_stand_outer_armor");
        put("zombie_villager2", "zombie_villager_inner_armor");
        put("zombie_villager3", "zombie_villager_outer_armor");
        put("giant2", "giant_inner_armor");
        put("giant3", "giant_outer_armor");
        put("player2", "player_inner_armor");
        put("player3", "player_outer_armor");
        put("drowned2", "drowned_inner_armor");
        put("drowned3", "drowned_outer_armor");
        put("drowned4", "drowned_outer");
        put("stray2", "stray_inner_armor");
        put("stray3", "stray_outer_armor");
        put("stray4", "stray_outer");
        put("shulker2", "shulker_bullet");
        put("husk2", "husk_inner_armor");
        put("husk3", "husk_outer_armor");
        put("player_slim2", "player_slim_inner_armor");
        put("player_slim3", "player_slim_outer_armor");
        put("creeper2", "creeper_charge");
        put("pig2", "pig_saddle");
        put("strider2", "strider_saddle");
        put("sheep2", "sheep_wool");
        put("slime2", "slime_outer");

        put("parrot2", "parrot_?shoulder2?");//todo
        put("parrot3", "parrot_?shoulder3?");//todo

    }};
    private static EMFManager self = null;
    private final Object2ObjectOpenHashMap<String, EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<String, EMFJemData>();
    private final Object2IntOpenHashMap<String> cache_AmountOfMobNameAlreadyDone = new Object2IntOpenHashMap<String>();
    private final Object2ObjectOpenHashMap<String, EMFAnimationExecutor> cache_EntityNameToAnimationExecutable = new Object2ObjectOpenHashMap<String, EMFAnimationExecutor>();
    private final Object2ObjectOpenHashMap<String, EMFModelPart3> cache_JemNameToCannonModelRoot = new Object2ObjectOpenHashMap<String, EMFModelPart3>();
    private final Object2ObjectOpenHashMap<String, ModelPart> cache_JemNameToVanillaModelRoot = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, Identifier> cache_JemNameToTextureOverride = new Object2ObjectOpenHashMap<>();
    private final Object2BooleanOpenHashMap<String> cache_JemNameDoesHaveVariants = new Object2BooleanOpenHashMap<>() {{
        defaultReturnValue(false);
    }};
    private final Object2BooleanOpenHashMap<UUID> cache_UUIDDoUpdating = new Object2BooleanOpenHashMap<>() {{
        defaultReturnValue(true);
    }};
    private final Object2IntOpenHashMap<UUIDAndMobTypeKey> cache_UUIDAndTypeToCurrentVariantInt = new Object2IntOpenHashMap<UUIDAndMobTypeKey>() {{
        defaultReturnValue(0);
    }};
    private final Object2LongOpenHashMap<UUIDAndMobTypeKey> cache_UUIDAndTypeToLastVariantCheckTime = new Object2LongOpenHashMap<UUIDAndMobTypeKey>() {{
        defaultReturnValue(0);
    }};
    public Object2ObjectOpenHashMap<String, EMFPropertyTester> cache_mobJemNameToPropertyTester = new Object2ObjectOpenHashMap<>();
    @NotNull
    public Runnable deferPlayerSetAngles = () -> {
    };
    private boolean isETFPresentAndValid = false;

    private EMFManager() {
        if (EMFVersionDifferenceManager.isThisModLoaded("entity_texture_features")) {
            isETFPresentAndValid = EMFVersionDifferenceManager.isETFValidAPI();
        } else {
            isETFPresentAndValid = false;
        }
    }

    public static EMFManager getInstance() {
        if (self == null) self = new EMFManager();
        return self;
    }

    public static void resetInstance() {
        self = new EMFManager();
    }

    public static ModelPart traverseRootForChildOrNull(ModelPart root, String nameOfModelToFind) {
        if (root.hasChild(nameOfModelToFind))
            return root.getChild(nameOfModelToFind);
        for (ModelPart part :
                ((ModelPartAccessor) root).getChildren().values()) {
            ModelPart found = traverseRootForChildOrNull(part, nameOfModelToFind);
            if (found != null)
                return found;
        }
        return null;
    }

    private static String getTypeName(Entity entity) {
        String forReturn = Registries.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:", "");
//        if (entity instanceof PlayerEntity plyr && plyr.thin ((PlayerEntityModelAccessor) plyr).isThinArms()) {
//            forReturn = entityTypeBaseName + "_slim";
//        } else
        if (entity instanceof PufferfishEntity puffer) {
            forReturn = "puffer_fish_" + switch (puffer.getPuffState()) {
                case 0 -> "small";
                case 1 -> "medium";
                default -> "big";
            };
        } else if (entity instanceof TropicalFishEntity fish) {
            forReturn =  (fish.getVariant().getSize() == TropicalFishEntity.Size.LARGE ? "tropical_fish_b" : "tropical_fish_a");
//        } else if (entity instanceof LlamaEntity llama) {
//            forReturn = llama.isTrader() ? "trader_llama" : "llama";
        }
        return forReturn;
    }

    @Nullable
    public static EMFJemData getJemData(String pathOfJem) {
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        if (EMFManager.getInstance().cache_JemDataByFileName.containsKey(pathOfJem)) {
            return EMFManager.getInstance().cache_JemDataByFileName.get(pathOfJem);
        }
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem));
            if (res.isEmpty()) {
                if (EMFConfig.getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMFModMessage("jem failed " + pathOfJem + " does not exist", false);
                return null;
            }
            Resource jemResource = res.get();
            //File jemFile = new File(pathOfJem);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println("jem exists "+ jemFile.exists());
            //if (jemFile.exists()) {
            //FileReader fileReader = new FileReader(jemFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(jemResource.getInputStream()));

            EMFJemData jem = gson.fromJson(reader, EMFJemData.class);
            reader.close();
            jem.sendFileName(pathOfJem);
            jem.prepare();
            EMFManager.getInstance().cache_JemDataByFileName.put(pathOfJem, jem);
            return jem;
            //}
        } catch (InvalidIdentifierException | FileNotFoundException e) {
            if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFUtils.EMFModMessage("jem failed " + e, false);
        } catch (Exception e) {
            if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFUtils.EMFModMessage("jem failed " + e, false);
            e.printStackTrace();
        }
        return null;
    }
    private boolean traderLlamaHappened = false;
    public ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root) {

        boolean printing =  (EMFConfig.getConfig().printModelCreationInfoToLog);
//        if (layer == EntityModelLayers.SPIDER ||layer == EntityModelLayers.IRON_GOLEM ||layer == EntityModelLayers.ZOMBIE || layer == EntityModelLayers.COW || layer == EntityModelLayers.SHEEP || layer == EntityModelLayers.VILLAGER) {
//            System.out.println("ran zomb and sheep");
        String mobModelName = layer.getId().getPath();
        if (mobModelName.contains("pufferfish"))
            mobModelName = mobModelName.replace("pufferfish", "puffer_fish");

        switch (mobModelName){
            case "tropical_fish_large" ->{
                if("pattern".equals(layer.getName())){
                    mobModelName = "tropical_fish_pattern_b";
                }else{
                    mobModelName = "tropical_fish_b";
                }
            }
            case "tropical_fish_small" ->{
                if("pattern".equals(layer.getName())){
                    mobModelName = "tropical_fish_pattern_a";
                }else{
                    mobModelName = "tropical_fish_a";
                }
            }
            case "trader_llama" ->traderLlamaHappened = true;
            case "llama" ->{
                if("main".equals(layer.getName())){
                    traderLlamaHappened = false;
                }else{
                    mobModelName = traderLlamaHappened ? "trader_llama_decor" : "llama_decor";
                }
            }
            default -> {
                if (cache_AmountOfMobNameAlreadyDone.containsKey(mobModelName)) {
                    int amount = cache_AmountOfMobNameAlreadyDone.getInt(mobModelName);
                    amount++;
                    cache_AmountOfMobNameAlreadyDone.put(mobModelName, amount);
                    //System.out.println("higherCount: "+ mobModelName+amount);
                    mobModelName = map_MultiMobVariantMap.getOrDefault(mobModelName + amount, mobModelName + amount);
                } else {
                    EMFManager.getInstance().cache_AmountOfMobNameAlreadyDone.put(mobModelName, 1);
                }
            }
        }
        if (printing) System.out.println(" > EMF try to find a model for: " + mobModelName);

        //add simple namespace logic
        String nameSpace = layer.getId().getNamespace();
        if(!"minecraft".equals(nameSpace)){
            mobModelName = "modded/"+nameSpace+"/"+mobModelName;
        }

       //System.out.println("foundlayer: "+ layer.getId() +" _ "+ layer.getName() +", returned: "+mobModelName);
        ///jem name is final and correct from here

        if (printing) System.out.println(" >> EMF trying to find: optifine/cem/" + mobModelName + ".jem");
        String jemName = "optifine/cem/" + mobModelName + ".jem";//todo mod namespaces
        EMFJemData jemData = getJemData(jemName);
        if (jemData != null) {
            if (!EMFOptiFineMappings2.getMapOf(mobModelName).isEmpty()) {
                EMFModelPart3 part = getEMFRootModelFromJem(jemData, root);
                cache_JemNameToCannonModelRoot.put(mobModelName, part);
                cache_JemNameToVanillaModelRoot.put(mobModelName, root);
                if (MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("optifine/cem/" + mobModelName + ".properties")).isPresent())
                    cache_JemNameDoesHaveVariants.put(mobModelName, true);

                if (part.textureOverride != null)
                    cache_JemNameToTextureOverride.put(mobModelName, part.textureOverride);

                return part;
            } else {
                //not a cem mob
                if (printing) System.out.println(" >> no EMF mapping found");//todo modded mob handling
                EMFUtils.EMFModWarn("EMF Beta does not have the code to read unknown model [" + jemName+ "] yet, soon though." );
            }
        } else {
            //no mob .jem

            if (printing) System.out.println(" >> EMF mob does not have a .jem file");
        }

        if (printing) System.out.println(" > Vanilla model used for: " + mobModelName);
        return root;
    }

    private EMFModelPart3 getEMFRootModelFromJem(EMFJemData jemData, ModelPart vanillaRoot) {
        return getEMFRootModelFromJem(jemData, vanillaRoot, 0);
    }

    private EMFModelPart3 getEMFRootModelFromJem(EMFJemData jemData, ModelPart vanillaRoot, int variantNumber) {
        Map<String, ModelPart> rootChildren = new HashMap<>();

        boolean printing = EMFConfig.getConfig().printModelCreationInfoToLog;

        for (EMFPartData partData :
                jemData.models) {
            if (partData != null && partData.part != null) {

                ModelPart oldPart = traverseRootForChildOrNull(vanillaRoot, partData.part);
//                ModelPart oldPart = vanillaRoot.hasChild("root") ?
//                        ((ModelPartAccessor) vanillaRoot.getChild("root")).getChildren().getOrDefault(partData.part, null)
//                        :
//                        ((ModelPartAccessor) vanillaRoot).getChildren().getOrDefault(partData.part, null);

                EMFModelPart3 newPart = new EMFModelPart3(partData, variantNumber);
                if (oldPart != null  && !"tadpole".equals(jemData.mobName)) {//TODO this can't be right with the tadpole but i have nothing else to compare with
                    newPart.applyDefaultModelRotates(oldPart.getDefaultTransform());
                }
                if (printing) System.out.println(" >>> EMF part made: " + partData.toString(false));
//                if ("piglin".equals(jemData.mobName)) {
//                    System.out.println(" >>>>>>>>> piglin part made: " + partData.toString(false));
//                }
                rootChildren.put(partData.part, newPart);

            } else {
                //part is not mapped to a vanilla part
                System.out.println("no part definition");
            }
        }
        //have iterated over all parts in jem and made them


        EMFModelPart3 emfRootModelPart = new EMFModelPart3(new ArrayList<ModelPart.Cuboid>(), rootChildren, variantNumber, jemData);
        //try
        //todo pretty sure we must match root transforms because of fucking frogs, maybe?
        //emfRootModelPart.pivotY = 24;
        //todo check all were mapped correctly before return
        if (printing) System.out.println(" > EMF model returned");

        //emfRootModelPart.assertChildrenAndCuboids();

        setupAnimationsFromJemToModel(jemData, emfRootModelPart);

        // check for if root is expected below the top level modelpart
        // as in some single part entity models
        if (vanillaRoot.hasChild("root") && !emfRootModelPart.hasChild("root")) {
            ModelPart subRoot = vanillaRoot.getChild("root");
//            if (subRoot.pivotX != 0 ||
//                    subRoot.pivotY != 0 ||
//                    subRoot.pivotZ != 0 ||
//                    subRoot.pitch != 0 ||
//                    subRoot.yaw != 0 ||
//                    subRoot.roll != 0 ||
//                    subRoot.xScale != 0 ||
//                    subRoot.yScale != 0 ||
//                    subRoot.zScale != 0
//
//            ) {
//                //this covers things like frogs who pivot their root for some reason
//                emfRootModelPart.setTransform(subRoot.getTransform());
//                emfRootModelPart.setDefaultTransform(subRoot.getDefaultTransform());
//            }

            emfRootModelPart = new EMFModelPart3(new ArrayList<ModelPart.Cuboid>(), Map.of("root", emfRootModelPart), variantNumber, jemData);
        }
        if(EMFConfig.getConfig().attemptToCopyVanillaModelIntoMissingModelPart)
            emfRootModelPart.mergeInVanillaWhereRequired(vanillaRoot);
        return emfRootModelPart;
    }

    private void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPart3 emfRootModelPart) {
        ///////SETUP ANIMATION EXECUTABLES////////////////

        boolean printing =   EMFConfig.getConfig().printModelCreationInfoToLog;

        Object2ObjectOpenHashMap<String, EMFModelPart3> allPartByName = new Object2ObjectOpenHashMap<>();
        allPartByName.put("root", emfRootModelPart);
        allPartByName.putAll(emfRootModelPart.getAllChildPartsAsMap());

        Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimations = new Object2ObjectLinkedOpenHashMap<>();

        final EMFAnimationVariableSuppliers variableSuppliers = new EMFAnimationVariableSuppliers();
        if (printing) {
            System.out.println(" > finalAnimationsForModel =");
            jemData.finalAnimationsForModel.forEach((key, expression) -> {
                System.out.println(" >> " + key + " = " + expression);
            });
        }
        jemData.finalAnimationsForModel.forEach((animKey, animationExpression) -> {

            if (EMFConfig.getConfig().printModelCreationInfoToLog)
                EMFUtils.EMFModMessage("parsing animation value: [" + animKey + "]");
            String modelId = animKey.split("\\.")[0];
            String modelVariable = animKey.split("\\.")[1];

            EMFDefaultModelVariable thisVariable = EMFDefaultModelVariable.get(modelVariable);

            EMFModelPart3 thisPart = allPartByName.get(modelId);
            EMFAnimation thisCalculator = null;

            if (thisPart != null) {
                thisCalculator =
                        new EMFAnimation(
                                thisPart,
                                thisVariable,
                                animKey,
                                animationExpression,
                                jemData.fileName,
                                variableSuppliers);
            } else {
                //not a custom model or vanilla must be a custom variable
                thisCalculator = new EMFAnimation(
                        null,
                        null,
                        animKey,
                        animationExpression,
                        jemData.fileName,
                        variableSuppliers);
            }
            emfAnimations.put(animKey, thisCalculator);
        });
        LinkedList<EMFAnimation> orderedAnimations = new LinkedList<>();
        //System.out.println("> anims: " + emfAnimations);
        emfAnimations.forEach((key, anim) -> {
            //System.out.println(">> anim key: " + key);
            if (anim != null) {
                //System.out.println(">> anim: " + anim.expressionString);
                anim.initExpression(emfAnimations, allPartByName);
                //System.out.println(">>> valid: " + anim.isValid());
                if (anim.isValid())
                    orderedAnimations.add(anim);
                else
                    EMFUtils.EMFModWarn("animations was invalid: " + anim.animKey + " = " + anim.expressionString);
            }
        });

        EMFAnimationExecutor executor = new EMFAnimationExecutor(variableSuppliers, orderedAnimations);

        cache_EntityNameToAnimationExecutable.put(jemData.mobName, executor);
        ///////////////////////////
    }

    public void setAnglesOnParts(String modelName, Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        //todo modify for multi model names
        //todo modify puffer name with model state


        boolean print = false;//new Random().nextInt(500)==1;
        if (print) System.out.println("mobName = " + modelName);
        int suffix = cache_UUIDAndTypeToCurrentVariantInt.getInt(new UUIDAndMobTypeKey(entity.getUuid(), entity.getType()));
        if (suffix > 1) modelName = modelName + suffix;
        if (cache_EntityNameToAnimationExecutable.containsKey(modelName)) {
            cache_EntityNameToAnimationExecutable.get(modelName).executeAnimations(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, print);
        }
    }

    public void setAnglesOnParts(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        String mobName = getTypeName(entity);
        setAnglesOnParts(mobName, entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
//        if(mobName.contains("llama") && new Random().nextInt(100)==1)
//            System.out.println("animating: "+mobName);
    }

    public void doVariantCheckFor(Entity entity) {
        String mobName = getTypeName(entity);
        if (cache_JemNameDoesHaveVariants.getBoolean(mobName) && cache_UUIDDoUpdating.getBoolean(entity.getUuid())) {
            UUIDAndMobTypeKey key = new UUIDAndMobTypeKey(entity.getUuid(), entity.getType());
            if (cache_UUIDAndTypeToLastVariantCheckTime.getLong(key) + 1500 < System.currentTimeMillis()) {
                if (isETFPresentAndValid) {
                    if (!cache_mobJemNameToPropertyTester.containsKey(mobName)) {
                        Identifier propertyID = new Identifier("optifine/cem/" + mobName + ".properties");
                        if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
                            EMFPropertyTester emfTester = EMFVersionDifferenceManager.getAllValidPropertyObjects(propertyID);
                            cache_mobJemNameToPropertyTester.put(mobName, emfTester);
                        } else {
                            EMFUtils.EMFModWarn("no property" + propertyID.toString());
                            cache_JemNameDoesHaveVariants.put(mobName, false);
                            return;
                        }
                    }
                    EMFPropertyTester emfProperty = cache_mobJemNameToPropertyTester.get(mobName);
                    if (emfProperty != null) {
                        int suffix = emfProperty.getSuffixOfEntity(entity, cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
                        EMFModelPart3 cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                        if (suffix > 1) { // ignore 0 & 1
                            //System.out.println(" > apply model variant: "+suffix +", to "+mobName);
                            if (!cannonicalRoot.allKnownStateVariants.containsKey(suffix)) {

                                String jemName = "optifine/cem/" + mobName + suffix + ".jem";//todo mod namespaces
                                System.out.println(" >> first time load of : " + jemName);
                                EMFJemData jemData = getJemData(jemName);
                                if (jemData != null) {
                                    ModelPart vanillaRoot = cache_JemNameToVanillaModelRoot.get(mobName);
                                    if (vanillaRoot != null) {
                                        EMFModelPart3 variantRoot = getEMFRootModelFromJem(jemData, vanillaRoot, suffix);
                                        cannonicalRoot.mergePartVariant(suffix, variantRoot);
                                        setupAnimationsFromJemToModel(jemData, cannonicalRoot);
                                    }
                                } else {
                                    System.out.println("invalid jem: " + jemName);
                                }
                            }
                            cannonicalRoot.setVariantStateTo(suffix);
                            cache_UUIDAndTypeToCurrentVariantInt.put(key, suffix);
                        } else {
                            cannonicalRoot.setVariantStateTo(0);
                            cache_UUIDAndTypeToCurrentVariantInt.put(key, 0);
                        }

                    }
                }
                cache_UUIDAndTypeToLastVariantCheckTime.put(key, System.currentTimeMillis());
            }else{
                EMFModelPart3 cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                cannonicalRoot.setVariantStateTo(cache_UUIDAndTypeToCurrentVariantInt.getInt(key));
            }
        }
    }




    public interface EMFPropertyTester {
        int getSuffixOfEntity(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom);
    }

    private record UUIDAndMobTypeKey(UUID uuid, EntityType<?> entityType) {
    }

    public static class EMFAnimationExecutor {

        private final EMFAnimationVariableSuppliers variableSuppliers;
        private final LinkedList<EMFAnimation> orderedAnimations;

        EMFAnimationExecutor(EMFAnimationVariableSuppliers variableSuppliers, LinkedList<EMFAnimation> orderedAnimations) {
            this.variableSuppliers = variableSuppliers;
            this.orderedAnimations = orderedAnimations;
        }

        public void executeAnimations(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, boolean print) {
//            if(entity instanceof PiglinEntity){
//                System.out.println("ran animations for: "+entity.getType());
//                System.out.println("animations length =" + orderedAnimations.size());
//                System.out.println("animations =" + orderedAnimations);
//            }
            //constrain head yaw amount
            if(headYaw >= 360){
                headYaw %= 360;
            }else if(headYaw <= -360){
                headYaw %= -360;
            }

            variableSuppliers.entity = entity;
            variableSuppliers.limbAngle = limbAngle;
            variableSuppliers.limbDistance = limbDistance;
            variableSuppliers.headYaw = headYaw;
            variableSuppliers.headPitch = headPitch;
            variableSuppliers.tickDelta = MinecraftClient.getInstance().getTickDelta();

            variableSuppliers.animationProgress = alterAnimationProgress(animationProgress);

            for (EMFAnimation animation :
                    orderedAnimations) {
                animation.calculateAndSet(variableSuppliers.entity);
            }
        }

        private float alterAnimationProgress(float animationProgress) {
            if (variableSuppliers.entity == null)
                return animationProgress;
            // if(new Random().nextInt(100)==1 && currentEntity.world != null) System.out.println((System.currentTimeMillis()/50d+tickDelta));
            return variableSuppliers.entity.age + variableSuppliers.tickDelta;//(System.currentTimeMillis()/50d+ tickDelta);
        }
    }
}
