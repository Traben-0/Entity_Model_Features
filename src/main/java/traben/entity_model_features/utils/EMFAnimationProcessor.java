package traben.entity_model_features.utils;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import org.mariuszgromada.math.mxparser.*;
import traben.entity_model_features.Entity_model_featuresClient;
import traben.entity_model_features.models.EMF_CustomModel;
import traben.entity_model_features.models.EMF_CustomModelPart;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EMFAnimationProcessor {

    private static final Object2ReferenceOpenHashMap<String, Properties> ENTITY_TYPE_ANIMATION_PROPERTIES = new Object2ReferenceOpenHashMap<>();





    public static void animateThisModel(HashMap<String, ModelPart> vanillaParts, EMF_CustomModel<LivingEntity> model, LivingEntity entity){
        if(Entity_model_featuresClient.JEMPATH_CustomModel.containsKey(entity.getType().hashCode())) {
            try {
                Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<LivingEntity>> allParts = model.getAllParts();
                UUID id =entity.getUuid();

                    //System.out.println("11");
                    //ENTITY_TYPE_ANIMATION_PROPERTIES.clear();
                    //Object2ObjectOpenHashMap<String, ObjectOpenHashSet<Properties>>  propertiesMap = ENTITY_TYPE_ANIMATION_PROPERTIES;
                    if (!ENTITY_TYPE_ANIMATION_PROPERTIES.containsKey(entity.getType().toString())) {

                        ObjectOpenHashSet<Properties> allProperties = new ObjectOpenHashSet<>() {
                        };
                        for (EMF_CustomModelPart<LivingEntity> part :
                                allParts.values()) {
                            if (part.selfModelData.animations.length != 0) {
                                allProperties.addAll(Arrays.asList(part.selfModelData.animations));
                            }
                        }
                        Properties combinedProperties = new Properties();
                        for (Properties properties :
                                allProperties) {
                            if (!properties.isEmpty()) {
                                combinedProperties.putAll(properties);
                            }
                        }
                        ENTITY_TYPE_ANIMATION_PROPERTIES.put(entity.getType().toString(), combinedProperties.isEmpty() ? null : combinedProperties);
                    }
                    //System.out.println(ENTITY_TYPE_ANIMATION_PROPERTIES.keySet());
                    Properties allProperties = ENTITY_TYPE_ANIMATION_PROPERTIES.get(entity.getType().toString());
                    if (allProperties != null) {
                        if (!allProperties.isEmpty())
                            //animations can be processed
                            if (entity instanceof ZombieEntity)
                                processTheseAnimationProperties(allParts, vanillaParts, allProperties, entity);
                    }


            } catch (Exception e) {
                System.out.println("animate failed " + e);
            }
        }
    }

    private static void processTheseAnimationProperties(Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<LivingEntity>> allParts, HashMap<String,ModelPart> vanillaParts,Properties properties, LivingEntity entity){
        //animations can be processed
        Properties resultsSoFar = new Properties();
        // all part.tx values are not used for anything except animation overrides
        for (Map.Entry<Object, Object> entry:
             properties.entrySet()) {
            //possibly already calculated due to dependency so skip
            if (!resultsSoFar.containsKey(entry.getKey())) {
//                if (entry.getKey().toString().equals("head.rx")
//                        || entry.getKey().toString().equals("head.ry")
//                        || entry.getKey().toString().equals("head.rz")
//                        || entry.getKey().toString().equals("headwear.rx")
//                        || entry.getKey().toString().equals("headwear.ry")
//                        || entry.getKey().toString().equals("headwear.rz")) {
                    String key = entry.getKey().toString();
                    String animationString = entry.getValue().toString(); //properties.getProperty("head.rx");
                    if (animationString != null) {
                        //System.out.println("start animate " + animationString);

                        processSingleAnimationLine(allParts, vanillaParts, entity, animationString, resultsSoFar, key,properties);

                    }
               // }
            }
        }
    }

    private static void processSingleAnimationLine(Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<LivingEntity>> allParts,
                                                   HashMap<String, ModelPart> vanillaParts,
                                                   LivingEntity entity,
                                                   String animationString,
                                                   Properties resultsSoFar,
                                                   String key,
                                                   Properties totalProperties) {
        try {

            animationString = preCalculate(animationString,entity,vanillaParts,resultsSoFar, allParts,key, totalProperties);
            holdEntity = entity;
            vanillaHeadPitch =  vanillaParts.containsKey("head") ? (double) vanillaParts.get("head").pitch : null;
            double result = doMath(animationString);
            //System.out.println(result);
            String[] entryKey = key.split("\\.");
            EMF_CustomModelPart<LivingEntity> part = allParts.get(entryKey[0]); //getPartById(allParts,entryKey[0]);
            //if(part == null) part = getPartVanilla(vanillaParts,"head");

            if(!Double.isNaN(result) && part != null){
                //System.out.println("placing at "+ key);
                resultsSoFar.put(key,String.valueOf(result));


                    switch (entryKey[1]) {
                        case "rx" -> {
                            part.rx = result;
                        }
                        case "ry" -> {
                            part.ry = result;
                        }
                        case "rz" -> {
                            part.rz = result;
                        }

                        case "tx" -> {
                            part.tx = result;
                        }
                        case "ty" -> {
                            part.ty = result;
                        }
                        case "tz" -> {
                            part.tz = result;
                        }

                        case "sx" -> {
                            part.sx = result;
                        }
                        case "sy" -> {
                            part.sy = result;
                        }
                        case "sz" -> {
                            part.sz = result;
                        }

                }
            }

        }catch (Exception e){
            System.out.println("calc failed "+ e);
        }
    }

//        private static EMF_CustomModelPart<LivingEntity> getPartById(ArrayList<EMF_CustomModelPart<LivingEntity>> allParts, String id){
//        for (EMF_CustomModelPart<LivingEntity> part:
//             allParts) {
//            if(id.equals(part.selfModelData.id)){
//                return part;
//            }
//        }
//        return null;
//    }

    private static String preCalculate(String animationString,
                                       LivingEntity entity,
                                       HashMap<String,ModelPart> vanillaParts,
                                       Properties existingResults,
                                       Object2ReferenceOpenHashMap<String,EMF_CustomModelPart<LivingEntity>> allParts,
                                       String key,
                                       Properties totalProperties){
        //replace every boolean literal with a parse-able math string
        //System.out.println(existingResults);

      //  animationString = animationString.replaceAll("torad","rad");

//        if(animationString.contains("is_")) {
//            if(animationString.contains("is_in")) {
//                if (animationString.contains("is_in_hand"))
//                    animationString = animationString.replaceAll("is_in_hand", "1 == " + (false ? "1" : "0"));
//                if (animationString.contains("is_in_item_frame"))
//                    animationString = animationString.replaceAll("is_in_item_frame", "1 == " + (false ? "1" : "0"));
//                if (animationString.contains("is_in_ground"))
//                    animationString = animationString.replaceAll("is_in_ground", "1 == " + (false ? "1" : "0"));
//                if (animationString.contains("is_in_gui"))
//                    animationString = animationString.replaceAll("is_in_gui", "1 == " + (false ? "1" : "0"));
//                if (animationString.contains("is_in_lava"))
//                    animationString = animationString.replaceAll("is_in_lava", "1 == " + (entity.isInLava() ? "1" : "0"));
//                if (animationString.contains("is_in_water"))
//                    animationString = animationString.replaceAll("is_in_water", "1 == " + (entity.isTouchingWater() ? "1" : "0"));
//                if (animationString.contains("is_invisible"))
//                    animationString = animationString.replaceAll("is_invisible", "1 == " + (entity.isInvisible() ? "1" : "0"));
//            }
//            if(animationString.contains("is_child")) animationString = animationString.replaceAll("is_child", "1 == " + (entity.isBaby() ? "1" : "0"));
//            if(animationString.contains("is_riding"))animationString = animationString.replaceAll("is_riding", "1 == " + (entity.hasVehicle() ? "1" : "0"));
//            if(animationString.contains("is_aggressive"))animationString = animationString.replaceAll("is_aggressive", "1 == " + (entity instanceof HostileEntity host && host.getTarget() != null ? "1" : "0"));
//            if(animationString.contains("is_alive"))animationString = animationString.replaceAll("is_alive", "1 == " + (entity.isAlive() ? "1" : "0"));
//            if(animationString.contains("is_burning"))animationString = animationString.replaceAll("is_burning", "1 == " + (entity.isOnFire() ? "1" : "0"));
//            if(animationString.contains("is_glowing"))animationString = animationString.replaceAll("is_glowing", "1 == " + (entity.isGlowing() ? "1" : "0"));
//            if(animationString.contains("is_hurt"))animationString = animationString.replaceAll("is_hurt", "1 == " + (entity.getHealth() < entity.getMaxHealth() ? "1" : "0"));
//            if(animationString.contains("is_on_ground"))animationString = animationString.replaceAll("is_on_ground", "1 == " + (entity.isOnGround() ? "1" : "0"));
//            if(animationString.contains("is_on_head"))animationString = animationString.replaceAll("is_on_head", "1 == " + (false ? "1" : "0"));
//            if(animationString.contains("is_on_shoulder"))animationString = animationString.replaceAll("is_on_shoulder", "1 == " + (false ? "1" : "0"));
//            if(animationString.contains("is_sitting"))animationString = animationString.replaceAll("is_sitting", "1 == " + (entity.getPose() == EntityPose.SITTING ? "1" : "0"));
//            if(animationString.contains("is_sneaking"))animationString = animationString.replaceAll("is_sneaking", "1 == " + (entity.isSneaking() ? "1" : "0"));
//            if(animationString.contains("is_sprinting"))animationString = animationString.replaceAll("is_sprinting", "1 == " + (entity.isSprinting() ? "1" : "0"));
//            if(animationString.contains("is_tamed"))animationString = animationString.replaceAll("is_tamed", "1 == " + (entity instanceof TameableEntity tame && tame.isTamed() ? "1" : "0"));
//            if(animationString.contains("is_wet"))animationString = animationString.replaceAll("is_wet", "1 == " + (entity.isWet() ? "1" : "0"));
//
//        }


//        animationString = animationString.replaceAll("limb_swing",String.valueOf( entity.handSwingTicks));
//        animationString = animationString.replaceAll("limb_speed",String.valueOf( entity.limbDistance));
//        animationString = animationString.replaceAll("age",String.valueOf( entity.age));
//        animationString = animationString.replaceAll("head_pitch",String.valueOf( entity.getPitch())); //(vanillaParts.get("head") == null ? 0 : vanillaParts.get("head").pitch)));
//        animationString = animationString.replaceAll("head_yaw",String.valueOf( (entity.getHeadYaw() - entity.bodyYaw)));
//        animationString = animationString.replaceAll("swing_progress",String.valueOf( entity.handSwingProgress));
//        animationString = animationString.replaceAll("pi",String.valueOf( Math.PI));

        //check if have another animation parameter to be calculated first

        //   e.g. text.rx matches


        Matcher m = PATTERN_FOR_PART.matcher(animationString);
            //System.out.println("matchedf");
            while(m.find()) {
                //check if already cached result
                String otherKey = m.group();
                //System.out.println("otherkey=" + otherKey);
                if(key.equals(otherKey)){//entry[0].equals(key.split("\\.")[0])) {
                    String[] entry = otherKey.split("\\.");
                    ModelPart vanilla = vanillaParts.get(entry[0]);
                    EMF_CustomModelPart<LivingEntity> part = allParts.get(entry[0]);
                    float value = switch (entry[1]){
                        case "rx" -> vanilla.pitch;
                        case "ry" -> vanilla.yaw;
                        case "rz" -> vanilla.roll;
                        case "tx" -> vanilla.pivotX + part.selfModelData.boxes[0].coordinates[0];
                        case "ty" -> vanilla.pivotY + part.selfModelData.boxes[0].coordinates[1];
                        case "tz" -> vanilla.pivotZ + part.selfModelData.boxes[0].coordinates[2];
                        case "sx" -> vanilla.xScale;
                        case "sy" -> vanilla.yScale;
                        case "sz" -> vanilla.zScale;
                        default -> throw new RuntimeException("emf ggq2g3453t");
                    };
                    animationString = animationString.replaceAll(otherKey, String.valueOf(value));
                }else{
                    System.out.println(key +" - "+ otherKey);
                    if (existingResults.containsKey(otherKey)) {
                        animationString = animationString.replaceAll(otherKey, existingResults.getProperty(otherKey));
                    } else {
                        //System.out.println("need to calculate this first = " + otherKey);
                        processSingleAnimationLine(allParts, vanillaParts, entity, totalProperties.getProperty(otherKey), existingResults, otherKey, totalProperties);
                        animationString = animationString.replaceAll(otherKey, existingResults.getProperty(otherKey));

                    }
                    System.out.println(animationString);
                }

            }


        return animationString;
    }

    private static final Pattern PATTERN_FOR_PART = Pattern.compile("[a-zA-Z_-]+\\.[trs][xyz]");

    private static LivingEntity holdEntity = null;
    private static Double vanillaHeadPitch = null;

    private static final Expression calc = new Expression("1+1",
            new Function("torad(x) = x * 0.01745329251"),
            new Function("clamp(x,y,z) = if(x > y, if(x < z, x, z) ,y)"),
            new Constant("limb_swing = 0"){
                @Override
                public double getConstantValue() {
                    return  holdEntity == null ? 0 : holdEntity.handSwingTicks;
                }
            },
            new Constant("limb_speed = 0"){
                @Override
                public double getConstantValue() {
                    return   holdEntity == null ? 0 : holdEntity.limbDistance;
                }
            },
            new Constant("age = 0"){
                @Override
                public double getConstantValue() {
                    return   holdEntity == null ? 0 : holdEntity.age;
                }
            },
            new Constant("head_pitch = 0"){
                @Override
                public double getConstantValue() {
                    return   vanillaHeadPitch == null ? 0 : vanillaHeadPitch;
                }
            },
            new Constant("head_yaw = 0"){
                @Override
                public double getConstantValue() {
                    return   holdEntity == null ? 0 : holdEntity.getHeadYaw()- holdEntity.bodyYaw;
                }
            },
            new Constant("swing_progress = 0") {
                @Override
                public double getConstantValue() {
                    return holdEntity == null ? 0 : holdEntity.handSwingProgress;
                }
            }
            ,
            new Constant("is_child = 0"){
                @Override
                public double getConstantValue() {
                    return   holdEntity == null ? 0 : holdEntity.isBaby() ? 1 : 0;
                }
            },
            new Constant("is_in_water = 0"){
                @Override
                public double getConstantValue() {
                    return   holdEntity == null ? 0 : holdEntity.isTouchingWater() ? 1 : 0;
                }
            },
            new Constant("is_riding = 0"){
                @Override
                public double getConstantValue() {
                    return   holdEntity == null ? 0 : holdEntity.hasVehicle() ? 1 : 0;
                }
            }

//if(animationString.contains("is_child")) animationString = animationString.replaceAll("is_child", "1 == " + (entity.isBaby() ? "1" : "0"));
    );

    private static double doMath(String mathToBeMathed){
        //System.out.println("math="+mathToBeMathed);

        calc.setExpressionString(mathToBeMathed);
        //System.out.println(calc.calculate());
        double dub;
        try {
            //try and get lucky with a quick calculate
            dub = Double.parseDouble(mathToBeMathed);
        }catch(NumberFormatException e){
            //not quick:/
            dub = calc.calculate();
        }

        //System.out.println("result="+dub);

//        if (Double.isNaN(dub)) {
//            //calc.setVerboseMode();
//            mXparser.consolePrintln("syntax = " + calc.checkSyntax());
//            mXparser.consolePrintln(calc.getErrorMessage());
//
//            mXparser.consolePrintln("List of missing user defined functions:");
//            for (String fun : calc.getMissingUserDefinedFunctions())
//                mXparser.consolePrintln("Function '" + fun + "' has to be defined");
//            mXparser.consolePrintln("List of missing user defined arguments:");
//            for (String arg : calc.getMissingUserDefinedArguments())
//                mXparser.consolePrintln("Argument '" + arg + "' has to be defined");
//        }
        return dub;
    }


}
