package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.EMFAnimationVariableSuppliers;
import traben.entity_model_features.models.animation.EMFDefaultModelVariable;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class EMFManager {//singleton for data holding and resetting needs


    private Object2ObjectOpenHashMap<String,EMFJemData> cache_JemDataByFileName = new Object2ObjectOpenHashMap<>();
    private Object2IntOpenHashMap<String> cache_AmountOfMobNameAlreadyDone = new Object2IntOpenHashMap<>();
    private Object2ObjectOpenHashMap<String, EMFAnimationExecutor> cache_EntityNameToAnimationExecutable = new Object2ObjectOpenHashMap<>();

    private static Object2ObjectOpenHashMap<String,String> map_MultiMobVariantMap = new Object2ObjectOpenHashMap<>(){{
        put("cat2","cat_collar");
        put("wither_skeleton2","wither_skeleton_inner_armor");
        put("wither_skeleton3","wither_skeleton_outer_armor");
        put("zombie2","zombie_inner_armor");
        put("zombie3","zombie_outer_armor");
        put("skeleton2","skeleton_inner_armor");
        put("skeleton3","skeleton_outer_armor");
        put("zombified_piglin2","zombified_piglin_inner_armor");
        put("zombified_piglin3","zombified_piglin_outer_armor");
        put("piglin2","piglin_inner_armor");
        put("piglin3","piglin_outer_armor");
        put("piglin_brute2","piglin_brute_inner_armor");
        put("piglin_brute3","piglin_brute_outer_armor");
        put("armor_stand2","armor_stand_inner_armor");
        put("armor_stand3","armor_stand_outer_armor");
        put("zombie_villager2","zombie_villager_inner_armor");
        put("zombie_villager3","zombie_villager_outer_armor");
        put("giant2","giant_inner_armor");
        put("giant3","giant_outer_armor");
        put("player2","player_inner_armor");
        put("player3","player_outer_armor");
        put("drowned2","drowned_inner_armor");
        put("drowned3","drowned_outer_armor");
        put("drowned4","drowned_outer");
        put("stray2","stray_inner_armor");
        put("stray3","stray_outer_armor");
        put("stray4","stray_outer");
        put("shulker2","shulker_bullet");
        put("husk2","husk_inner_armor");
        put("husk3","husk_outer_armor");
        put("player_slim2","player_slim_inner_armor");
        put("player_slim3","player_slim_outer_armor");
        put("llama2","llama_decor");
        put("llama3","trader_llama_decor");
        put("creeper2","creeper_charge");
        put("pig2","pig_saddle");
        put("strider2","strider_saddle");
        put("sheep2","sheep_wool");
        put("slime2","slime_outer");

        put("parrot2","parrot_?shoulder2?");//todo
        put("parrot3","parrot_?shoulder3?");//todo

    }};
    public static EMFManager getInstance(){
        if(self == null) self = new EMFManager();
        return self;
    }
    public static void resetInstance(){
        self = new EMFManager();
    }

    private static EMFManager self = null;
    private EMFManager(){

    }







    public static ModelPart injectIntoModelRootGetter(EntityModelLayer layer, ModelPart root) {

//        if (layer == EntityModelLayers.SPIDER ||layer == EntityModelLayers.IRON_GOLEM ||layer == EntityModelLayers.ZOMBIE || layer == EntityModelLayers.COW || layer == EntityModelLayers.SHEEP || layer == EntityModelLayers.VILLAGER) {
//            System.out.println("ran zomb and sheep");
        String mobModelName =layer.getId().getPath();
        if(mobModelName.contains("pufferfish"))
            mobModelName = mobModelName.replace("pufferfish","puffer_fish");

        if(EMFManager.getInstance().cache_AmountOfMobNameAlreadyDone.containsKey(mobModelName)){
            int amount = EMFManager.getInstance().cache_AmountOfMobNameAlreadyDone.getInt(mobModelName);
            amount++;
            EMFManager.getInstance().cache_AmountOfMobNameAlreadyDone.put(mobModelName,amount);
            //System.out.println("higherCount: "+ mobModelName+amount);
            mobModelName = map_MultiMobVariantMap.getOrDefault(mobModelName+amount,mobModelName+amount);
        }else{
            EMFManager.getInstance().cache_AmountOfMobNameAlreadyDone.put(mobModelName,1);
        }

        if(EMFOptiFineMappings2.getMapOf(mobModelName)!= null) {

            String jemName = "optifine/cem/" + mobModelName + ".jem";//todo mod namespaces
            EMFJemData jemData = getJemData(jemName);
            if (jemData != null) {
                Map<String, ModelPart> rootChildren = new HashMap<>();

                for (EMFPartData partData :
                        jemData.models) {
                    if (partData != null && partData.part != null) {
                        ModelPart oldPart = root.hasChild(partData.part) ? root.getChild(partData.part) : null;
                        EMFModelPart3 newPart = new EMFModelPart3(partData);
                        if (oldPart != null) {
                           newPart.applyDefaultModelRotates(oldPart.getDefaultTransform());
                        }

                        System.out.println("part made = " + partData.id + " - " + partData.part);

                        rootChildren.put(partData.part, newPart);

                    } else {
                        //part is not mapped to a vanilla part
                        System.out.println("no part definition");
                    }
                }
                //have iterated over all parts in jem and made them


                EMFModelPart3 emfRootModelPart = new EMFModelPart3(new ArrayList<ModelPart.Cuboid>(), rootChildren);
                //try
                //todo pretty sure we must match root transforms because of fucking frogs, maybe?
                //emfRootModelPart.pivotY = 24;
                //todo check all were mapped correctly before return
                System.out.println("emf returned");

                //emfRootModelPart.assertChildrenAndCuboids();
                ///////SETUP ANIMATION EXECUTABLES////////////////
                Object2ObjectOpenHashMap<String, EMFModelPart3> allPartByName = new Object2ObjectOpenHashMap<>();
                allPartByName.put("root", emfRootModelPart);
                allPartByName.putAll(emfRootModelPart.getAllChildPartsAsMap());

                Object2ObjectLinkedOpenHashMap<String, EMFAnimation> emfAnimations = new Object2ObjectLinkedOpenHashMap<>();

                final EMFAnimationVariableSuppliers variableSuppliers = new EMFAnimationVariableSuppliers();
                System.out.println("finalAnimationsForModel =" + jemData.finalAnimationsForModel);
                jemData.finalAnimationsForModel.forEach((animKey, animationExpression) -> {

                    if (EMFData.getInstance().getConfig().printModelCreationInfoToLog)
                        EMFUtils.EMF_modMessage("parsing animation value: [" + animKey + "]");
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
                System.out.println("> anims: " + emfAnimations);
                emfAnimations.forEach((key, anim) -> {
                    System.out.println(">> anim key: " + key);
                    if (anim != null) {
                        System.out.println(">> anim: " + anim.expressionString);
                        anim.initExpression(emfAnimations, allPartByName);
                        System.out.println(">>> valid: " + anim.isValid());
                        if (anim.isValid()) orderedAnimations.add(anim);
                    }
                });

                EMFAnimationExecutor executor = new EMFAnimationExecutor(variableSuppliers, orderedAnimations);

                EMFManager.getInstance().cache_EntityNameToAnimationExecutable.put(jemData.mobName, executor);
                ///////////////////////////

                // check for if root is expected below the top level modelpart
                // as in some single part entity models
                if(root.hasChild("root") && !emfRootModelPart.hasChild("root")) {
                    ModelPart subRoot = root.getChild("root");
                    if(subRoot.pivotX != 0 ||
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
                    return new EMFModelPart3(new ArrayList<ModelPart.Cuboid>(), Map.of("root", emfRootModelPart));
                }
                return emfRootModelPart;

            } else {
                //not a cem mob
                System.out.println("nocem");
            }
        } else {
            //not a cem mob
            System.out.println("no mapping");//todo modded mob handling
        }
        System.out.println("root returned for: " + mobModelName);
        return root;
    }

    public static class EMFAnimationExecutor {

        private final EMFAnimationVariableSuppliers variableSuppliers;
        private final LinkedList<EMFAnimation> orderedAnimations;
        EMFAnimationExecutor(EMFAnimationVariableSuppliers variableSuppliers, LinkedList<EMFAnimation> orderedAnimations){
            this.variableSuppliers = variableSuppliers;
            this.orderedAnimations = orderedAnimations;
        }
        public void executeAnimations(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch,boolean print){
            if(print){
                System.out.println("ran animations for: "+entity.getType());
                System.out.println("animations length =" + orderedAnimations.size());
                System.out.println("animations =" + orderedAnimations);
            }

            variableSuppliers.entity = entity;
            variableSuppliers.limbAngle = limbAngle;
            variableSuppliers.limbDistance = limbDistance;
            variableSuppliers.headYaw = headYaw;
            variableSuppliers.headPitch = headPitch;
            variableSuppliers.tickDelta = MinecraftClient.getInstance().getTickDelta();

            variableSuppliers.animationProgress = alterAnimationProgress(animationProgress);

            for (EMFAnimation animation:
                 orderedAnimations) {
                animation.calculateAndSet(variableSuppliers.entity);
            }
        }

        private float alterAnimationProgress(float animationProgress){
            if(variableSuppliers.entity == null)
                return animationProgress;
            // if(new Random().nextInt(100)==1 && currentEntity.world != null) System.out.println((System.currentTimeMillis()/50d+tickDelta));
            return variableSuppliers.entity.age + variableSuppliers.tickDelta ;//(System.currentTimeMillis()/50d+ tickDelta);
        }
    }


    public void setAnglesOnParts(String modelName,Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
        //todo modify for multi model names
        //todo modify puffer name with model state


        boolean print =false;//new Random().nextInt(500)==1;
        if (print) System.out.println("mobName = "+modelName);
        if(cache_EntityNameToAnimationExecutable.containsKey(modelName)){
            cache_EntityNameToAnimationExecutable.get(modelName).executeAnimations(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch,print);
        }
    }
    public void setAnglesOnParts(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
        String mobName = getTypeName(entity);
        setAnglesOnParts(mobName,entity,limbAngle,limbDistance,animationProgress,headYaw,headPitch);

    }


    private static String getTypeName(Entity entity){
        String forReturn = Registries.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:","");
//        if (entity instanceof PlayerEntity plyr && plyr.thin ((PlayerEntityModelAccessor) plyr).isThinArms()) {
//            forReturn = entityTypeBaseName + "_slim";
//        } else
        if (entity instanceof PufferfishEntity puffer) {
            forReturn = "puffer_fish_" + switch(puffer.getPuffState()){
                case 0-> "small";
                case 1-> "medium";
                default -> "big";
            };
        } else if (entity instanceof TropicalFishEntity fish) {
            forReturn = forReturn+ (fish.getVariant().getSize() == TropicalFishEntity.Size.LARGE ? "_b" : "_a");
        } else if (entity instanceof LlamaEntity llama) {
            forReturn = llama.isTrader() ? "trader_llama" : "llama";
        }
        return forReturn;
    }




    @Nullable
    public static EMFJemData getJemData(String pathOfJem){
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        if(EMFManager.getInstance().cache_JemDataByFileName.containsKey(pathOfJem)){
            return EMFManager.getInstance().cache_JemDataByFileName.get(pathOfJem);
        }
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem));
            if(res.isEmpty()){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("jem failed "+pathOfJem+" does not exist", false);
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
            EMFManager.getInstance().cache_JemDataByFileName.put(pathOfJem,jem);
            return jem;
            //}
        } catch (Exception e) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMFUtils.EMF_modMessage("jem failed "+e, false);
        }
        return null;
    }

}
