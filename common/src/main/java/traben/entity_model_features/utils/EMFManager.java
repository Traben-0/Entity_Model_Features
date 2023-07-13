package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFVersionDifferenceManager;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.mixin.accessor.MinecraftClientAccessor;
import traben.entity_model_features.mixin.accessor.ModelPartAccessor;
import traben.entity_model_features.models.EMFModelPartMutable;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationVariableSuppliers;
import traben.entity_model_features.models.animation.EMFDefaultModelVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;
import traben.entity_texture_features.ETFApi;
import traben.entity_texture_features.config.ETFConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;

public class EMFManager {//singleton for data holding and resetting needs


    private static final Object2ObjectOpenHashMap<String, String> map_MultiMobVariantMap = new Object2ObjectOpenHashMap<>() {{
//        put("cat_b", "cat_collar");
//        put("wither_skeleton_b", "wither_skeleton_inner_armor");
//        put("wither_skeleton_c", "wither_skeleton_outer_armor");
//        put("zombie_b", "zombie_inner_armor");
//        put("zombie_c", "zombie_outer_armor");
//        put("skeleton_b", "skeleton_inner_armor");
//        put("skeleton_c", "skeleton_outer_armor");
//        put("zombified_piglin_b", "zombified_piglin_inner_armor");
//        put("zombified_piglin_c", "zombified_piglin_outer_armor");
//        put("piglin_b", "piglin_inner_armor");
//        put("piglin_c", "piglin_outer_armor");
//        put("piglin_brute_b", "piglin_brute_inner_armor");
//        put("piglin_brute_c", "piglin_brute_outer_armor");
//        put("armor_stand_b", "armor_stand_inner_armor");
//        put("armor_stand_c", "armor_stand_outer_armor");
//        put("zombie_villager_b", "zombie_villager_inner_armor");
//        put("zombie_villager_c", "zombie_villager_outer_armor");
//        put("giant_b", "giant_inner_armor");
//        put("giant_c", "giant_outer_armor");
//        put("player_b", "player_inner_armor");
//        put("player_c", "player_outer_armor");
//        put("drowned_b", "drowned_inner_armor");
//        put("drowned_c", "drowned_outer_armor");
//        put("drowned_d", "drowned_outer");
//        put("stray_b", "stray_inner_armor");
//        put("stray_c", "stray_outer_armor");
//        put("stray_d", "stray_outer");
        put("shulker_b", "shulker_box");//todo this entire map appears redundant now, follow up!
//        put("husk_b", "husk_inner_armor");
//        put("husk_c", "husk_outer_armor");
//        put("player_slim_b", "player_slim_inner_armor");
//        put("player_slim_c", "player_slim_outer_armor");
//        put("creeper_b", "creeper_charge");
//        put("pig_b", "pig_saddle");
//        put("strider_b", "strider_saddle");
//        put("sheep_b", "sheep_wool");
//        put("slime_b", "slime_outer");
//
//        put("parrot_b", "parrot");//todo shoulder parrots
//        put("parrot_c", "parrot");//todo

    }};

    public final boolean physicsModInstalled;
    private static EMFManager self = null;
    private final Object2ObjectOpenHashMap<String, EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<>();
    private final Object2IntOpenHashMap<String> cache_AmountOfMobNameAlreadyDone = new Object2IntOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, EMFAnimationExecutor> cache_EntityNameToAnimationExecutable = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, EMFModelPartMutable> cache_JemNameToCannonModelRoot = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, ModelPart> cache_JemNameToVanillaModelRoot = new Object2ObjectOpenHashMap<>();
    private final Object2BooleanOpenHashMap<String> cache_JemNameDoesHaveVariants = new Object2BooleanOpenHashMap<>() {{
        defaultReturnValue(false);
    }};
    private final Object2BooleanOpenHashMap<UUID> cache_UUIDDoUpdating = new Object2BooleanOpenHashMap<>() {{
        defaultReturnValue(true);
    }};
    private final Object2IntOpenHashMap<UUIDAndMobTypeKey> cache_UUIDAndTypeToCurrentVariantInt = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(0);
    }};
//    private final Object2LongOpenHashMap<UUIDAndMobTypeKey> cache_UUIDAndTypeToLastVariantCheckTime = new Object2LongOpenHashMap<>() {{
//        defaultReturnValue(0);
//    }};
    public final Object2ObjectOpenHashMap<String, ETFApi.ETFRandomTexturePropertyInstance> cache_mobJemNameToPropertyTester = new Object2ObjectOpenHashMap<>();



    public final boolean irisInstalled;
    private EMFManager() {
        physicsModInstalled = EMFVersionDifferenceManager.isThisModLoaded("physicsmod");
        irisInstalled = EMFVersionDifferenceManager.isThisModLoaded("iris") || EMFVersionDifferenceManager.isThisModLoaded("oculus");
    }

    public static EMFManager getInstance() {
        if (self == null) self = new EMFManager();
        return self;
    }

    public static void resetInstance() {
        EMFUtils.EMFModMessage("Clearing EMF data.");
        EMFOptiFinePartNameMappings.UNKNOWN_MODEL_MAP_CACHE.clear();
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

    public static String getTypeName(Entity entity) {
        String forReturn = Registry.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:", "");
//        if (entity instanceof PlayerEntity plyr && plyr.thin ((PlayerEntityModelAccessor) plyr).isThinArms()) {
//            forReturn = entityTypeBaseName + "_slim";
//        } else


        if(forReturn.contains(":")){
            forReturn = "modded/"+forReturn.replaceFirst(":","/");
//            String[] split = forReturn.split(":");
//            if(split.length == 2 && !split[0].isBlank() && !split[1].isBlank())
//                forReturn = "modded/"+split[0]+"/"+split[1];
        }


        if (entity instanceof PufferfishEntity puffer) {
            forReturn = "puffer_fish_" + switch (puffer.getPuffState()) {
                case 0 -> "small";
                case 1 -> "medium";
                default -> "big";
            };
        } else if (entity instanceof TropicalFishEntity fish) {
            forReturn =  (fish.getShape() == 0 ? "tropical_fish_a" : "tropical_fish_b");
//        } else if (entity instanceof LlamaEntity llama) {
//            forReturn = llama.isTrader() ? "trader_llama" : "llama";
        } else if (entity instanceof EnderDragonEntity) {
            forReturn = "dragon";
        }


        return forReturn;
    }


    @Nullable
    public static EMFJemData getJemData(String jemFileName,String rawMobName) {

        //try emf folder
        EMFJemData emfJemData = getJemDataWithDirectory("emf/cem/"+jemFileName);
        if (emfJemData != null) return emfJemData;
        emfJemData = getJemDataWithDirectory("emf/cem/"+rawMobName+"/"+jemFileName);
        if (emfJemData != null) return emfJemData;

        //try read optifine jems
        emfJemData = getJemDataWithDirectory("optifine/cem/"+jemFileName);
        if (emfJemData != null) return emfJemData;
        emfJemData = getJemDataWithDirectory("optifine/cem/"+rawMobName+"/"+jemFileName);
        return emfJemData;

    }

    @Nullable
    private static EMFJemData getJemDataWithDirectory(String pathOfJem) {
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        if (EMFManager.getInstance().cache_JemDataByFileName.containsKey(pathOfJem)) {
            return EMFManager.getInstance().cache_JemDataByFileName.get(pathOfJem);
        }
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem));
            if (res.isEmpty()) {
                if (EMFConfig.getConfig().printModelCreationInfoToLog)
                    EMFUtils.EMFModMessage(".jem read failed " + pathOfJem + " does not exist", false);
                return null;
            }
            if (EMFConfig.getConfig().printModelCreationInfoToLog)
                EMFUtils.EMFModMessage(".jem read success " + pathOfJem + " exists", false);
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
            if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFUtils.EMFModMessage(".jem failed to load " + e, false);
        } catch (Exception e) {
            EMFUtils.EMFModMessage(".jem failed to load " + e, false);
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

        if(!"main".equals(layer.getName())){
            mobModelName += "_" + layer.getName();
        }

        //add simple modded check
        if(!"minecraft".equals(layer.getId().getNamespace())){
            mobModelName = "modded/"+layer.getId().getNamespace()+"/"+mobModelName;
        }else {
            //vanilla model
            if (mobModelName.contains("pufferfish"))
                mobModelName = mobModelName.replace("pufferfish", "puffer_fish");


            switch (mobModelName) {
                case "tropical_fish_large" -> mobModelName = "tropical_fish_b";
                case "tropical_fish_small" -> mobModelName = "tropical_fish_a";
                case "tropical_fish_large_pattern" -> mobModelName = "tropical_fish_pattern_b";
                case "tropical_fish_small_pattern" -> mobModelName = "tropical_fish_pattern_a";
//            case "tropical_fish_large" ->{
//                if("pattern".equals(layer.getName())){
//                    mobModelName = "tropical_fish_pattern_b";
//                }else{
//                    mobModelName = "tropical_fish_b";
//                }
//            }
//            case "tropical_fish_small" ->{
//                if("pattern".equals(layer.getName())){
//                    mobModelName = "tropical_fish_pattern_a";
//                }else{
//                    mobModelName = "tropical_fish_a";
//                }
//            }
                case "trader_llama" -> traderLlamaHappened = true;
                case "llama" -> traderLlamaHappened = false;
                case "llama_decor" -> {
//                if("main".equals(layer.getName())){
//                    traderLlamaHappened = false;
//                }else{
                    mobModelName = traderLlamaHappened ? "trader_llama_decor" : "llama_decor";
//                }
                }
                case "ender_dragon" -> mobModelName = "dragon";
                case "dragon_skull" -> mobModelName = "head_dragon";
                case "player_head" -> mobModelName = "head_player";
                case "skeleton_skull" -> mobModelName = "head_skeleton";
                case "wither_skeleton_skull" -> mobModelName = "head_wither_skeleton";
                case "zombie_head" -> mobModelName = "head_zombie";
                case "creeper_head" -> mobModelName = "head_creeper";
                case "piglin_head" -> mobModelName = "head_piglin";
//            case "double_chest_left" -> {
//                mobModelName = "chest_large";
//                isChestLeft = true;
//            }
//            case"double_chest_right" -> {
//                mobModelName = "chest_large";
//                isChestLeft = false;
//            }


                case "creeper_armor" -> mobModelName = "creeper_charge";
                case "sheep_fur" -> mobModelName = "sheep_wool";


                //case "parrot" -> mobModelName = "parrot";//todo check on shoulder parrot models they can technically be different



                default -> {
                    if (cache_AmountOfMobNameAlreadyDone.containsKey(mobModelName)) {
                        int amount = cache_AmountOfMobNameAlreadyDone.getInt(mobModelName);
                        amount++;
                        cache_AmountOfMobNameAlreadyDone.put(mobModelName, amount);
                        //System.out.println("higherCount: "+ mobModelName+amount);
                        String modelVariantAlias = mobModelName + '_' + (amount > 0 && amount < 27 ? String.valueOf((char) (amount + 'a' - 1)) : amount);

                        mobModelName = map_MultiMobVariantMap.getOrDefault(modelVariantAlias, modelVariantAlias);
                    } else {
                        EMFManager.getInstance().cache_AmountOfMobNameAlreadyDone.put(mobModelName, 1);
                    }
                }
            }
        }
        if (printing) System.out.println(" > EMF try to find a model for: " + mobModelName);


        ///jem name is final and correct from here

        if (EMFOptiFinePartNameMappings.getMapOf(mobModelName).isEmpty()) {
            //construct simple map for modded or unknown entities
            EMFOptiFinePartNameMappings.createMapForModdedOrUnknownEntityModel(root,mobModelName);
        }


        if (printing) System.out.println(" >> EMF trying to find: optifine/cem/" + mobModelName + ".jem");
        String jemName = /*"optifine/cem/" +*/ mobModelName + ".jem";//todo mod namespaces
        EMFJemData jemData = getJemData(jemName,mobModelName);
        if (jemData != null) {
//            if (!EMFOptiFinePartNameMappings.getMapOf(mobModelName).isEmpty()) {
                EMFModelPartMutable part = getEMFRootModelFromJem(jemData, root);

                cache_JemNameToCannonModelRoot.put(mobModelName, part);
                part.setPartAsTopLevelRoot();

                cache_JemNameToVanillaModelRoot.put(mobModelName, root);
                if (MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("optifine/cem/" + mobModelName + ".properties")).isPresent())
                    cache_JemNameDoesHaveVariants.put(mobModelName, true);



                return part;
//            } else {
//                //not a cem mob
//                if (printing) System.out.println(" >> no EMF mapping found");//todo modded mob handling
//                EMFUtils.EMFModWarn("EMF Beta does not have the code to read unknown model [" + jemName+ "] yet, soon though." );
//            }
        } else {
            //no mob .jem

            if (printing) System.out.println(" >> EMF mob does not have a .jem file");
        }

        if (printing) System.out.println(" > Vanilla model used for: " + mobModelName);
        return root;
    }

    private EMFModelPartMutable getEMFRootModelFromJem(EMFJemData jemData, ModelPart vanillaRoot) {
        return getEMFRootModelFromJem(jemData, vanillaRoot, 0);
    }

    private EMFModelPartMutable getEMFRootModelFromJem(EMFJemData jemData, ModelPart vanillaRoot, int variantNumber) {
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

                EMFModelPartMutable newPart = new EMFModelPartMutable(partData, variantNumber);
                if (oldPart != null){
                    newPart.applyDefaultModelRotates(oldPart.getDefaultTransform());
                    iterateChildTransformCopy(newPart,oldPart);
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


        EMFModelPartMutable emfRootModelPart = new EMFModelPartMutable( rootChildren, variantNumber, jemData);
        //try
        //todo pretty sure we must match root transforms because of fucking frogs, maybe?
        //emfRootModelPart.pivotY = 24;
        //todo check all were mapped correctly before return
        if (printing) System.out.println(" > EMF model returned");

        //emfRootModelPart.assertChildrenAndCuboids();

        setupAnimationsFromJemToModel(jemData, emfRootModelPart);

        // check for if root is expected below the top level modelpart
        // as in some single part entity models
        if (vanillaRoot.hasChild("root")){
            if(!emfRootModelPart.hasChild("root")) {
                ModelPart subRoot = vanillaRoot.getChild("root");
                if (subRoot.pivotX != 0 ||
                        subRoot.pivotY != 0 ||
                        subRoot.pivotZ != 0 ||
                        subRoot.pitch != 0 ||
                        subRoot.yaw != 0 ||
                        subRoot.roll != 0 ||
                        subRoot.xScale != 0 ||
                        subRoot.yScale != 0 ||
                        subRoot.zScale != 0

                ) {
                    //this covers things like frogs who pivot their root for some reason
                    emfRootModelPart.setTransform(subRoot.getTransform());
                    emfRootModelPart.setDefaultTransform(subRoot.getDefaultTransform());
                }

                emfRootModelPart = new EMFModelPartMutable( Map.of("root", emfRootModelPart), variantNumber, jemData);
            }
        }else if (emfRootModelPart.hasChild("root")){
            //should only be tadpoles
            emfRootModelPart = (EMFModelPartMutable) emfRootModelPart.getChild("root");
        }
        if(EMFConfig.getConfig().attemptToCopyVanillaModelIntoMissingModelPart)
            emfRootModelPart.mergeInVanillaWhereRequired(vanillaRoot);
        return emfRootModelPart;
    }

    private void iterateChildTransformCopy(EMFModelPartMutable newPart, ModelPart oldPart){
        for (String emfChildId:
                newPart.getChildrenEMF().keySet()) {
            if(oldPart.hasChild(emfChildId)){
                EMFModelPartMutable newNewPart = ((EMFModelPartMutable)newPart.getChildrenEMF().get(emfChildId));
                ModelPart oldOldPart =oldPart.getChild(emfChildId);
                newNewPart.applyDefaultModelRotates(oldOldPart.getDefaultTransform());
                iterateChildTransformCopy(newNewPart,oldOldPart);
            }
        }
    }


    private void setupAnimationsFromJemToModel(EMFJemData jemData, EMFModelPartMutable emfRootModelPart) {
        ///////SETUP ANIMATION EXECUTABLES////////////////

        boolean printing =   EMFConfig.getConfig().printModelCreationInfoToLog;

        Object2ObjectOpenHashMap<String, EMFModelPartMutable> allPartByName = new Object2ObjectOpenHashMap<>();
        allPartByName.put("root", emfRootModelPart);
        allPartByName.putAll(emfRootModelPart.getAllChildPartsAsMap());

        Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimations = new Object2ObjectLinkedOpenHashMap<>();

        final EMFAnimationVariableSuppliers variableSuppliers = new EMFAnimationVariableSuppliers();
        if (printing) {
            System.out.println(" > finalAnimationsForModel =");
            jemData.finalAnimationsForModel.forEach((key, expression) -> System.out.println(" >> " + key + " = " + expression));
        }
        jemData.finalAnimationsForModel.forEach((animKey, animationExpression) -> {

            if (EMFConfig.getConfig().printModelCreationInfoToLog)
                EMFUtils.EMFModMessage("parsing animation value: [" + animKey + "]");
            String modelId = animKey.split("\\.")[0];
            String modelVariable = animKey.split("\\.")[1];

            EMFDefaultModelVariable thisVariable = EMFDefaultModelVariable.get(modelVariable);

            EMFModelPartMutable thisPart = allPartByName.get(modelId);
            EMFAnimation thisCalculator;

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
        isAnimationValidationPhase = true;
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
        isAnimationValidationPhase = false;

        EMFAnimationExecutor executor = new EMFAnimationExecutor(variableSuppliers, orderedAnimations);

        cache_EntityNameToAnimationExecutable.put(jemData.mobName, executor);
        ///////////////////////////
    }

    public boolean isAnimationValidationPhase = false;

    public boolean isKnownJemName(String nameOfJem){
        return cache_JemNameToCannonModelRoot.containsKey(nameOfJem);
    }

    public void preRenderEMFActions(String modelName, Entity entity, VertexConsumerProvider provider, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

//        EMFModelPart3 modelRoot = cache_JemNameToCannonModelRoot.get(modelName);
//        if (modelRoot != null && modelRoot.containsInternalTextureOverrides){
//
//        }
        EMFModelPartMutable.currentlyHeldProvider = provider;
        EMFModelPartMutable.currentlyHeldEntity = entity;

        int suffix = cache_UUIDAndTypeToCurrentVariantInt.getInt(new UUIDAndMobTypeKey(entity.getUuid(), entity.getType()));
        if (suffix > 1) modelName = modelName + suffix;
        if (cache_EntityNameToAnimationExecutable.containsKey(modelName)) {
            cache_EntityNameToAnimationExecutable.get(modelName).executeAnimations(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        }
    }

//    public void preRenderEMFActions(Entity entity, VertexConsumerProvider provider, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
//        String mobName = getTypeName(entity);
//        preRenderEMFActions(mobName, entity, provider, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
////        if(mobName.contains("llama") && new Random().nextInt(100)==1)
////            System.out.println("animating: "+mobName);
//    }

    @Nullable
    public Identifier getRootModelTextureOverride(String modelId){
        EMFModelPartMutable modelRoot = cache_JemNameToCannonModelRoot.get(modelId);
        if (modelRoot != null){
            return modelRoot.textureOverride;//null if not valid
        }
        return null;
    }

    public void doVariantCheckFor(String mobName,Entity entity) {
        //String mobName = getTypeName(entity);
        if (cache_JemNameDoesHaveVariants.getBoolean(mobName)
                && cache_UUIDDoUpdating.getBoolean(entity.getUuid())
            && ETFApi.getETFConfigObject().textureUpdateFrequency_V2 != ETFConfig.UpdateFrequency.Never
        ) {
            UUIDAndMobTypeKey key = new UUIDAndMobTypeKey(entity.getUuid(), entity.getType());

            long randomizer = ETFApi.getETFConfigObject().textureUpdateFrequency_V2.getDelay() * 20L;
            if (System.currentTimeMillis() % randomizer == Math.abs(entity.getUuid().hashCode()) % randomizer){
            //if (cache_UUIDAndTypeToLastVariantCheckTime.getLong(key) + 1500 < System.currentTimeMillis()) {

                if (!cache_mobJemNameToPropertyTester.containsKey(mobName)) {
                    Identifier propertyID = new Identifier("optifine/cem/" + mobName + ".properties");
                    if (MinecraftClient.getInstance().getResourceManager().getResource(propertyID).isPresent()) {
                        ETFApi.ETFRandomTexturePropertyInstance emfTester = ETFApi.readRandomPropertiesFileAndReturnTestingObject2(propertyID, "models");
                        cache_mobJemNameToPropertyTester.put(mobName, emfTester);
                    } else {
                        EMFUtils.EMFModWarn("no property" + propertyID);
                        cache_JemNameDoesHaveVariants.put(mobName, false);
                        return;
                    }
                }
                ETFApi.ETFRandomTexturePropertyInstance emfProperty = cache_mobJemNameToPropertyTester.get(mobName);
                if (emfProperty != null) {
                    int suffix = emfProperty.getSuffixForEntity(entity, cache_UUIDDoUpdating.containsKey(entity.getUuid()), cache_UUIDDoUpdating);
                    EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                    if (suffix > 1) { // ignore 0 & 1
                        //System.out.println(" > apply model variant: "+suffix +", to "+mobName);
                        if (!cannonicalRoot.allKnownStateVariants.containsKey(suffix)) {

                            String jemName = /*"optifine/cem/" +*/ mobName + suffix + ".jem";//todo mod namespaces
                            System.out.println(" >> first time load of : " + jemName);
                            EMFJemData jemData = getJemData(jemName,mobName);
                            if (jemData != null) {
                                ModelPart vanillaRoot = cache_JemNameToVanillaModelRoot.get(mobName);
                                if (vanillaRoot != null) {
                                    EMFModelPartMutable variantRoot = getEMFRootModelFromJem(jemData, vanillaRoot, suffix);
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
               // cache_UUIDAndTypeToLastVariantCheckTime.put(key, System.currentTimeMillis());
            }else{
                EMFModelPartMutable cannonicalRoot = cache_JemNameToCannonModelRoot.get(mobName);
                cannonicalRoot.setVariantStateTo(cache_UUIDAndTypeToCurrentVariantInt.getInt(key));
            }
        }
    }



    public void tryRenderVanillaRoot(String modelId,MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay){
        ModelPart vanillaRoot = cache_JemNameToVanillaModelRoot.get(modelId);
        if(vanillaRoot != null) {
            matrixStack.push();
            if (EMFConfig.getConfig().vanillaModelRenderMode == EMFConfig.VanillaModelRenderMode.Positon_offset) {
                matrixStack.translate(1, 0, 0);
            }
            vanillaRoot.render(matrixStack,vertexConsumer,light,overlay,1,0.5f,0.5f,0.5f);
            matrixStack.pop();
        }
    }

    public void tryRenderVanillaRootNormally(String modelId,MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay){
        ModelPart vanillaRoot = cache_JemNameToVanillaModelRoot.get(modelId);
        if(vanillaRoot != null) {
            vanillaRoot.render(matrixStack,vertexConsumer,light,overlay,1,1,1,1);
        }
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

        public void executeAnimations(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
//            if(entity instanceof PiglinEntity){
//                System.out.println("ran animations for: "+entity.getType());
//                System.out.println("animations length =" + orderedAnimations.size());
//                System.out.println("animations =" + orderedAnimations);
//            }

            //constrain head yaw amount
            if(headYaw >= 180 || headYaw <= -180) {
                boolean isNegative = headYaw < 0;
                float newHeadYaw = Math.abs(headYaw) % 360;
                if (newHeadYaw >= 180) {
                    newHeadYaw = 180 - (newHeadYaw - 180);
                    isNegative = !isNegative;
                }
                headYaw = isNegative ? -newHeadYaw : newHeadYaw;
            }

            variableSuppliers.entity = entity;
            variableSuppliers.limbAngle = limbAngle;
            variableSuppliers.limbDistance = limbDistance;
            variableSuppliers.headYaw = headYaw;
            variableSuppliers.headPitch = headPitch;
            //using the minecraft client we get a much smoother and accurate tick delta for animations
            // the downside is a flickering in the pause menu that I have to catch
            variableSuppliers.tickDelta = MinecraftClient.getInstance().isPaused() ? ((MinecraftClientAccessor)MinecraftClient.getInstance()).getPausedTickDelta() : MinecraftClient.getInstance().getTickDelta();

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
