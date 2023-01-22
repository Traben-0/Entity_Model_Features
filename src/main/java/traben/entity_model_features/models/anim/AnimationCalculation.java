package traben.entity_model_features.models.anim;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionTypes;
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.EMF_EntityModel;
import traben.entity_model_features.models.EMF_ModelPart;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class AnimationCalculation {




     public Entity getEntity() {
        return entity;
    }

     public float getDimension() {
        if(entity == null || entity.getWorld() == null) return 0;
        Identifier id = entity.getWorld().getDimensionKey().getValue();
        if(id.equals(DimensionTypes.THE_NETHER_ID))return -1;
        if(id.equals(DimensionTypes.THE_END_ID))return 1;
        return 0;
    }

    public float getPlayerX(){
        return MinecraftClient.getInstance().player == null ? 0: MinecraftClient.getInstance().player.getBlockX();
    }
    public float getPlayerY(){
        return MinecraftClient.getInstance().player == null ? 0: MinecraftClient.getInstance().player.getBlockY();
    }
    public float getPlayerZ(){
        return MinecraftClient.getInstance().player == null ? 0: MinecraftClient.getInstance().player.getBlockZ();
    }
    public float getPlayerRX(){
        return (MinecraftClient.getInstance().player == null) ? 0 :
         MinecraftClient.getInstance().player.getPitch(tickDelta);
    }
    public float getPlayerRY(){
        return (MinecraftClient.getInstance().player == null) ? 0 :
                MinecraftClient.getInstance().player.getYaw(tickDelta);
    }
    public float getEntityX(){
        return getEntity() == null ? 0: getEntity().getBlockX();
    }
    public float getEntityY(){
        return getEntity() == null ? 0: getEntity().getBlockY();
    }
    public float getEntityZ(){
        return getEntity() == null ? 0: getEntity().getBlockZ();
    }
    public float getEntityRX(){
        return (getEntity() == null) ? 0 :
                getEntity().getPitch(tickDelta);
    }
    public float getEntityRY(){
        return (getEntity() == null) ? 0 :
                getEntity().getYaw(tickDelta);
    }

    //long changed to float... should be fine tbh
    public float getTime() {
        return entity == null || entity.getWorld() == null ? 0 : entity.getWorld().getTime() + tickDelta;
    }

    public float getHealth() {
        return entity == null ? 0 : entity.getHealth();
    }
    public float getDeathTime() {
        return entity == null ? 0 : entity.deathTime;
    }
    public float getAngerTime() {
        return entity == null || !(entity instanceof Angerable) ? 0 : ((Angerable)entity).getAngerTime();
    }
    public float getMaxHealth() {
        return entity == null ? 0 : entity.getMaxHealth();
    }
    public float getId() {
        return entity == null ? 0 : entity.getUuid().hashCode();
    }


    public float getHurtTime() {
        return entity == null ? 0 : entity.hurtTime;
    }
    public boolean isInWater() {
        return entity != null && entity.isTouchingWater();
    }
    public boolean isBurning() {
        return entity != null && entity.isOnFire();
    }
    public boolean isRiding() {
        return entity != null && entity.hasVehicle();
    }

    public boolean isChild() {
        return entity != null && entity.isBaby();
    }
    public boolean isOnGround() {
        return entity != null && entity.isOnGround();
    }
    public boolean isAlive() {
        return entity != null && entity.isAlive();
    }
    public boolean isAggressive() {return entity != null && entity.getAttacking() != null;}
    public boolean isGlowing() {
        return entity != null && entity.isGlowing();
    }
    public boolean isHurt() {return entity != null && entity.hurtTime > 0;}
    public boolean isInHand() {return false;}
    public boolean isInItemFrame() {
        return false;
    }
    public boolean isInGround() {
        return false;
    }
    public boolean isInGui() {
        return false;
    }
    public boolean isInLava() {
        return entity != null && entity.isInLava();
    }
    public boolean isInvisible() {
        return entity != null && entity.isInvisible();
    }
    public boolean isOnHead() {
        return false;
    }
    public boolean isOnShoulder() {
        return false;
    }
    public boolean isRidden() {return entity != null && entity.hasPassengers();}
    public boolean isSitting() {
        return entity != null &&
                (
                 (entity instanceof ParrotEntity parrot && parrot.isSitting()) ||
                 (entity instanceof CatEntity cat && cat.isSitting()) ||
                 (entity instanceof WolfEntity wolf && wolf.isSitting())
                );
    }
    public boolean isSneaking() {
        return entity != null && entity.isSneaking();
    }
    public boolean isSprinting() {
        return entity != null && entity.isSprinting();
    }
    public boolean isTamed() {return entity != null && entity instanceof TameableEntity tame && tame.isTamed();}
    public boolean isWet() {
        return entity != null && entity.isWet();
    }
    public float getSwingProgress() {
        return  entity == null ? 0 : entity.getHandSwingProgress(tickDelta);
    }

    public float getAge() {
         return entity == null ? 0 : entity.age + tickDelta;
        //return animationProgress;
    }

    public float getLimbAngle() {
        return limbAngle;
    }

    public float getLimbDistance() {
        return limbDistance;
    }

    public float getAnimationProgress() {
        return animationProgress;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public float getHeadPitch() {
        return headPitch;

    }

    public float getTickDelta() {
        return tickDelta;
    }

    LivingEntity entity;
    float limbAngle=0;
    float limbDistance=0;
    float animationProgress=0;
    float headYaw=0;
    float headPitch=0;
    float tickDelta=0;

     public EMF_ModelPart modelPart = null;
      ModelPart vanillaModelPart = null;

     public final EMF_EntityModel<?> parentModel;
     final AnimVar varToChange;
     public final String animKey;

     final ObjectOpenHashSet<String> animKeysThatAreNeeded = new ObjectOpenHashSet<>();

     final float defaultValue;

    //private final String expressionString;

    AnimationCalculation(EMF_EntityModel<?> parent, ModelPart part, AnimVar varToChange, String animKey) {
        prevInterp.defaultReturnValue(EMFData.getInstance().getConfig().minimunAnimationCalculationRate);
        //expressionString = ;
        this.animKey = animKey;
        this.parentModel = parent;
        this.varToChange = varToChange;
        if(part instanceof EMF_ModelPart emf)
            this.modelPart = emf;
        else
            this.vanillaModelPart = part;

        if(varToChange != null) {
            defaultValue = varToChange.getDefaultFromModel(part);
            if(this.modelPart != null)
                varToChange.setValueAsAnimated(this.modelPart);
        } else {
            defaultValue = 0;
        }
        prevResults.defaultReturnValue(defaultValue);
        prevPrevResults.defaultReturnValue(defaultValue);
        //calculator = new Expression(initialExpression);

    }

    public boolean verboseMode = false;
    abstract void setVerbose(boolean val);

    //optimize so we dont calculate multiple times
    public double getResultOnly(LivingEntity entity0, float limbAngle0, float limbDistance0, float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        if(vanillaModelPart != null){
            return varToChange.getFromVanillaModel(vanillaModelPart);
        }
        if(entity0 == null) {
            System.out.println("entity was null for getResultOnly");
            return 0;
        }
        UUID id = entity0.getUuid();

        //if we haven't already calculated a result this frame get another

        double interpolationLength = prevInterp.getDouble(id);

        if (animationProgress0 >= prevResultsTick.getFloat(id) +interpolationLength){

            //vary interpolation length by distance from client
            if (getEntity() != null && MinecraftClient.getInstance().player != null) {
                double val =((entity0.distanceTo(MinecraftClient.getInstance().player ) - EMFData.getInstance().getConfig().animationRateMinimumDistanceDropOff)
                        / EMFData.getInstance().getConfig().animationRateDistanceDropOffRate );// LOWER == lower quality
                //if((new Random()).nextInt(400) == 1)
                   // System.out.println("val="+val);
                //distances seem wrong check them
                prevInterp.put(id, EMFData.getInstance().getConfig().minimunAnimationCalculationRate + (val> 0 ? val  : 0 ));
            } else {
                prevInterp.put(id, EMFData.getInstance().getConfig().minimunAnimationCalculationRate);
            }

            entity = entity0;
            limbAngle = limbAngle0;
            limbDistance = limbDistance0;
            animationProgress = animationProgress0;
            headYaw = headYaw0;
            headPitch = headPitch0;
            tickDelta = tickDelta0;
            prevResultsTick.put(id ,getAge());

            double result = calculatorRun();
            double oldResult = prevResults.getDouble(id);
            prevPrevResults.put(id,oldResult);
            prevResults.put(id,result);
            return oldResult;

        }else if(animationProgress0 < prevResultsTick.getFloat(id) -100-interpolationLength){
            //this is required as animation progress resets with the entity entering render distance
            //todo possibly use world time ticks instead ??
            prevResultsTick.put(id,-100);
        }else if(prevPrevResults.containsKey(id)){
            float delta = (float) ((animationProgress0 - prevResultsTick.getFloat(id) ) / interpolationLength);
            return MathHelper.lerp(delta,prevPrevResults.getDouble(id), prevResults.getDouble(id));
        }
        return prevResults.getDouble(id);
    }

    abstract double calculatorRun();

    //public double getLastResult() {
    //    return getEntity() != null ? 0 : prevResults.getDouble(getEntity().getUuid());
   // }
    public float getLastResultTick() {
        return getEntity() != null ? 0 : prevResultsTick.getFloat(getEntity().getUuid());
    }
      Object2DoubleOpenHashMap<UUID> prevInterp = new Object2DoubleOpenHashMap<>();
     Object2DoubleOpenHashMap<UUID> prevPrevResults = new Object2DoubleOpenHashMap<>();
     public Object2DoubleOpenHashMap<UUID> prevResults = new Object2DoubleOpenHashMap<>();
      Object2FloatOpenHashMap<UUID> prevResultsTick = new Object2FloatOpenHashMap<>();
    //private double lastResult = 0;
   // private float lastResultTick = -1000;

    public void calculateAndSet(LivingEntity entity0, float limbAngle0, float limbDistance0, float animationProgress0, float headYaw0, float headPitch0, float tickDelta0){
        double result = getResultOnly( entity0,  limbAngle0,  limbDistance0,  animationProgress0,  headYaw0,  headPitch0,  tickDelta0);

        if(Double.isNaN(result)){
            //System.out.println(isRidden()+", "+isChild());
            //if(rand.nextInt(100) == 1)System.out.println("result was NaN from: "+animKey);
            if(varToChange != null)
                varToChange.set(modelPart, -999D);
            //isValid();
        }else if(modelPart == null){

           // if(rand.nextInt(100) == 1)System.out.println("model null "+animKey);
          //  if(vanillaModelPart != null) {
               // varToChange.setValueInVanillaModel(vanillaModelPart,result);
           // }
        }else {
           // if(rand.nextInt(100) == 1)System.out.println("worked?"+animKey);
            varToChange.set(modelPart, result);
        }

    }
    private static Random rand = new Random();

    public abstract boolean isValid();




    static final Pattern PATTERN_FOR_PART_VAR = Pattern.compile("[a-zA-Z0-9_]+\\.[trs][xyz]");
    static final Pattern PATTERN_FOR_STANDALONE_VAR = Pattern.compile("var\\.\\w+");
    static final Pattern PATTERN_FOR_STANDALONE_VAR_B = Pattern.compile("varb\\.\\w+");




    public enum AnimVar {
        tx,ty,tz,
        rx,ry,rz,
        sx,sy,sz,
        visible,
        visible_boxes,
        CUSTOM();


        public void set(EMF_ModelPart part, Double value) {
            if (value == null){
                System.out.println("this model couldn't be set as the calculation returned null: "+part.selfModelData.id+"."+this);
                return;
            }
            switch (this){
                case tx -> {
                    //part.tx = value;
                    part.setAnimPivotX(value.floatValue());
                }
                case ty -> {
                    //part.ty = value;
                    part.setAnimPivotY( value.floatValue());
                }
                case tz -> {
                    //part.tz = value;
                    part.setAnimPivotZ( value.floatValue());
                }
                case rx -> {
                    //part.rx = value;
                    //part.pitch = value.floatValue();
                    part.setAnimPitch(value.floatValue());
                }
                case ry -> {
                    //part.ry = value;
                    //part.yaw = value.floatValue();
                    part.setAnimYaw(value.floatValue());
                }
                case rz -> {
                    //part.rz = value;
                   // part.roll = value.floatValue();
                    part.setAnimRoll(value.floatValue());
                }
                case sx -> {
                    //part.sx = value;
                    part.xScale = value.floatValue();
                }
                case sy -> {
                    //part.sy = value;
                    part.yScale = value.floatValue();
                }
                case sz -> {
                    //part.sz = value;
                    part.zScale = value.floatValue();
                }
                case CUSTOM -> {
                    //todo pain.jpeg
                }
            }
        }
        public void setValueAsAnimated(EMF_ModelPart part) {
            if (part == null){
                System.out.println("this model couldn't be anim set as the method sent null part to "+this);
                return;
            }
            switch (this){
                case tx -> {
                    part.doesAnimtx = true;
                }
                case ty -> {
                    part.doesAnimty = true;
                }
                case tz -> {
                    part.doesAnimtz = true;
                }
                case rx -> {
                    part.doesAnimrx = true;
                }
                case ry -> {
                    part.doesAnimry = true;
                }
                case rz -> {
                    part.doesAnimrz = true;
                }
                case sx -> {
                    part.doesAnimsx = true;
                }
                case sy -> {
                    part.doesAnimsy = true;
                }
                case sz -> {
                    part.doesAnimsz = true;
                }
                default-> {
                    //hmmm
                }
            }
        }
        public float getFromEMFModel(EMF_ModelPart modelPart) {
            return getFromEMFModel(modelPart,false);
        }
        public float getFromEMFModel(EMF_ModelPart modelPart, boolean isSibling) {
            if(modelPart == null){
                System.out.println("EMF model part was null cannot get its value");
                return 0;
            }
            switch (this){
                case tx -> {
                    //return modelPart.tx.floatValue();
                    //sibling check is required to remove parent offsets if they are from the same parent
                    //todo this might actually be required on every single get call to a parent num == 1 part, i have only seen the issue on parts matching parents, check this
                    return isSibling ? modelPart.getAnimPivotXSibling() : modelPart.getAnimPivotX();
                }
                case ty -> {
                    //return modelPart.ty.floatValue();
                    return isSibling ? modelPart.getAnimPivotYSibling() : modelPart.getAnimPivotY();
                }
                case tz -> {
                    //return modelPart.tz.floatValue();
                    return isSibling ? modelPart.getAnimPivotZSibling() : modelPart.getAnimPivotZ();
                }
                case rx -> {
                    //return modelPart.rx.floatValue();
                    return modelPart.pitch;
                }
                case ry -> {
                    //return modelPart.ry.floatValue();
                    return modelPart.yaw;
                }
                case rz -> {
                    //return modelPart.rz.floatValue();
                    return modelPart.roll;
                }
                case sx -> {
                    //return modelPart.sx.floatValue();
                    return modelPart.xScale;
                }
                case sy -> {
                    //return modelPart.sy.floatValue();
                    return modelPart.yScale;
                }
                case sz -> {
                    //return modelPart.sz.floatValue();
                    return modelPart.zScale;
                }
                default -> {
                    System.out.println("model variable was defaulted cannot get its value");
                    return 0;
                }
            }
        }
        public float getDefaultFromModel(ModelPart modelPart){
            if(modelPart == null){
                System.out.println("model part was null cannot get its default value");
                return 0;
            }
            ModelTransform transform = modelPart.getDefaultTransform();
            switch (this){
                case tx -> {
                    return transform.pivotX;
                }
                case ty -> {
                    return transform.pivotY;
                }
                case tz -> {
                    return transform.pivotZ;
                }
                case rx -> {
                    return transform.pitch;
                }
                case ry -> {
                    return transform.yaw;
                }
                case rz -> {
                    return transform.roll;
                }
                case sx, sz, sy -> {
                    if(modelPart instanceof EMF_ModelPart emf)
                        return emf.selfModelData.scale;
                    else
                        return 1;
                }
                default -> {
                    System.out.println("model variable was defaulted cannot get its default value");
                    return 0;
                }
            }
        }

        public float getFromVanillaModel(ModelPart modelPart) {
            if(modelPart == null){
                System.out.println("model part was null cannot get its value");
                return 0;
            }
            switch (this){
                case tx -> {
                    return modelPart.pivotX;
                }
                case ty -> {
                    return modelPart.pivotY;
                }
                case tz -> {
                    return modelPart.pivotZ;
                }
                case rx -> {
                    return modelPart.pitch;
                }
                case ry -> {
                    return modelPart.yaw;
                }
                case rz -> {
                    return modelPart.roll;
                }
                case sx -> {
                    return modelPart.xScale;
                }
                case sy -> {
                    return modelPart.yScale;
                }
                case sz -> {
                    return modelPart.zScale;
                }
                default -> {
                    System.out.println("model variable was defaulted cannot get its value");
                    return 0;
                }
            }
        }
//        public void setValueInVanillaModel(ModelPart modelPart, Double value) {
//            if(modelPart == null){
//                System.out.println("model part was null cannot set its value");
//                return;
//            }
//            switch (this){
//                case tx -> {
//                     modelPart.pivotX = value.floatValue();
//                }
//                case ty -> {
//                     modelPart.pivotY = value.floatValue();
//                }
//                case tz -> {
//                     modelPart.pivotZ = value.floatValue();
//                }
//                case rx -> {
//                     modelPart.pitch = value.floatValue();
//                }
//                case ry -> {
//                     modelPart.yaw = value.floatValue();
//                }
//                case rz -> {
//                     modelPart.roll = value.floatValue();
//                }
//                case sx -> {
//                     modelPart.xScale = value.floatValue();
//                }
//                case sy -> {
//                     modelPart.yScale = value.floatValue();
//                }
//                case sz -> {
//                     modelPart.zScale = value.floatValue();
//                }
//                default -> {
//                    System.out.println("model variable was defaulted cannot set its value");
//                }
//            }
//        }
    }
}
