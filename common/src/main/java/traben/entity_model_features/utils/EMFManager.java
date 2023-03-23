package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
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
    private Object2ObjectOpenHashMap<String, EMFAnimationExecutor> cache_EntityNameToAnimationExecutable = new Object2ObjectOpenHashMap<>();

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
        if(EMFOptiFineMappings2.getMapOf(layer.getId().getPath())!= null) {
            String jemName = "optifine/cem/" + layer.getId().getPath() + ".jem";//todo mod namespaces
            EMFJemData jemData = getJemData(jemName);
            if (jemData != null) {
                Map<String, ModelPart> rootChildren = new HashMap<>();

                for (EMFPartData partData :
                        jemData.models) {
                    if (partData != null && partData.part != null) {
                        ModelPart oldPart = root.hasChild(partData.part) ? root.getChild(partData.part) : null;
                        EMFModelPart3 newPart = new EMFModelPart3(partData);
                        if (oldPart != null) {
                            newPart.applyDefaultModelRotatesToChildren(oldPart.getDefaultTransform());
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

                return emfRootModelPart;

            } else {
                //not a cem mob
                System.out.println("nocem");
            }
        } else {
            //not a cem mob
            System.out.println("no mapping");//todo modded mob handling
        }
        System.out.println("root returned for: " + layer.getId().getPath());
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


    public void setAnglesOnParts(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch){
        //todo modify for multi model names

        String mobName = Registries.ENTITY_TYPE.getId(entity.getType()).toString().replace("minecraft:","");
        boolean print =false;//new Random().nextInt(500)==1;
        if (print) System.out.println("mobName = "+mobName);
        if(cache_EntityNameToAnimationExecutable.containsKey(mobName)){
            cache_EntityNameToAnimationExecutable.get(mobName).executeAnimations(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch,print);
        }
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
