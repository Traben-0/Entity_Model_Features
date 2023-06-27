package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.UUID;

public class EMFAnimationVariableSuppliers {


    //public boolean riding = false;

    //public boolean child = false;

    //public static long lastFrameTime = System.currentTimeMillis();
   // private final Object2LongOpenHashMap<UUID> lastFrameTimeMap = new Object2LongOpenHashMap<>();
    public Entity entity = null;
    public float limbAngle = 0;
    public float limbDistance = 0;
    public float animationProgress = 0;
    public float headYaw = 0;
    public float headPitch = 0;
    public float tickDelta = 0;

    //TODO
    public float getRuleIndex(){
       return 0;
    }


    public Entity getEntity() {
        //System.out.println("ran");
        return entity;
    }

    public float getDimension() {
        if (entity == null || entity.getWorld() == null) return 0;
        Identifier id = entity.getWorld().getDimensionKey().getValue();
        if (id.equals(DimensionTypes.THE_NETHER_ID)) return -1;
        if (id.equals(DimensionTypes.THE_END_ID)) return 1;
        return 0;
    }

    public float getPlayerX() {
        return MinecraftClient.getInstance().player == null ? 0 : (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevX, MinecraftClient.getInstance().player.getX());
    }

    public float getPlayerY() {
        return MinecraftClient.getInstance().player == null ? 0 : (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevY, MinecraftClient.getInstance().player.getY());
    }

    public float getPlayerZ() {
        return MinecraftClient.getInstance().player == null ? 0 : (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevZ, MinecraftClient.getInstance().player.getZ());
    }

    public float getPlayerRX() {
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevPitch, MinecraftClient.getInstance().player.getPitch()));
    }

    public float getPlayerRY() {
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevYaw, MinecraftClient.getInstance().player.getYaw()));
    }

    public float getEntityX() {
        return getEntity() == null ? 0 : (float) MathHelper.lerp(getTickDelta(), getEntity().prevX, getEntity().getX());
    }

    public float getEntityY() {
        return getEntity() == null ? 0 :
                //(double) getEntity().getY();
                (float) MathHelper.lerp(getTickDelta(), getEntity().prevY, getEntity().getY());
    }

    public float getEntityZ() {
        return getEntity() == null ? 0 : (float) MathHelper.lerp(getTickDelta(), getEntity().prevZ, getEntity().getZ());
    }

    public float getEntityRX() {
        return (getEntity() == null) ? 0 :
                //(double) Math.toRadians(getEntity().getPitch(tickDelta));
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, getEntity().prevPitch, getEntity().getPitch()));
    }

    public float getEntityRY() {
        return (getEntity() instanceof LivingEntity alive) ?
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, alive.prevBodyYaw, alive.getBodyYaw())) : 0;
    }

    //long changed to double... should be fine tbh
    public float getTime() {
        if (entity == null || entity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            long upTimeInTicks =entity.getWorld().getTime(); // (System.currentTimeMillis() - START_TIME)/50;
            return constrainedFloat(upTimeInTicks,720720 ) + tickDelta;
        }
    }
    public float getDayTime() {
        if (entity == null || entity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            return constrainedFloat(entity.getWorld().getTimeOfDay(),24000) + tickDelta;
        }
    }
    public float getDayCount() {
        if (entity == null || entity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            return (float)(entity.getWorld().getTimeOfDay()/24000L) + tickDelta;
        }
    }

    public float getHealth() {
        return entity instanceof LivingEntity alive ? alive.getHealth() : 1;
    }

    public float getDeathTime() {
        return entity instanceof LivingEntity alive ? alive.deathTime : 0;
    }

    public float getAngerTime() {
        if (!(entity instanceof Angerable) ) return 0;

        float currentKnownHighest = knownHighestAngerTimeByUUID.getInt(entity.getUuid());
        int angerTime = ((Angerable) entity).getAngerTime();

        //clear anger info if anger is over
        if(angerTime<=0){
            knownHighestAngerTimeByUUID.put(entity.getUuid(), 0);
            return 0;
        }

        //store this if this is the largest anger time for the entity seen so far
        if(angerTime > currentKnownHighest) {
            knownHighestAngerTimeByUUID.put(entity.getUuid(), angerTime);
        }
        return angerTime - tickDelta;
    }

    private final Object2IntOpenHashMap<UUID> knownHighestAngerTimeByUUID = new Object2IntOpenHashMap<>(){{defaultReturnValue(0);}};

    public float getAngerTimeStart(){
        //this only makes sense if we are calculating it here from the largest known value of anger time
        // i could also reset it when anger time hits 0
        //todo this can't be right and wont work if anger time start is called first
        return !(entity instanceof Angerable) ? 0 : knownHighestAngerTimeByUUID.getInt(entity.getUuid());

    }

    public float getMaxHealth() {
        return entity instanceof LivingEntity alive ? alive.getMaxHealth() : 1;
    }

    public float getId() {
        return entity == null ? 0 : entity.getUuid().hashCode();
    }

    public float getHurtTime() {
        return entity instanceof LivingEntity alive ? (alive.hurtTime > 0 ? alive.hurtTime - tickDelta : 0) : 0;
    }

    public boolean isInWater() {
        return entity != null && entity.isTouchingWater();
    }

    public boolean isBurning() {
        return entity != null && entity.isOnFire();
    }

    public boolean isRiding() {
        //return riding;
        return entity != null && entity.hasVehicle();
    }

    public boolean isChild() {
        //return child;
        return entity instanceof LivingEntity alive && alive.isBaby();
    }

    public boolean isOnGround() {
        return entity != null && entity.isOnGround();
    }

//    public float getClosestCollisionX() {
//        if (entity != null && entity.world != null) {
//            Iterator<VoxelShape> bob = entity.world.getEntityCollisions(entity, entity.getBoundingBox()).iterator();
//            Vec3d entitypos = entity.getPos();
//            double closest = Double.MAX_VALUE;
//            while (bob.hasNext()) {
//                Optional<Vec3d> current = bob.next().getClosestPointTo(entitypos);
//                if (current.isPresent()) {
//                    double newVec = (double) current.get().x;
//                    closest = (double) Math.min(closest, Math.max(entitypos.x, newVec) - Math.min(entitypos.x, newVec));
//                }
//            }
//            if (closest != Double.MAX_VALUE) return closest;
//        }
//        return 0;
//    }
//
//    public float getClosestCollisionY() {
//        if (entity != null && entity.world != null) {
//            Iterator<VoxelShape> bob = entity.world.getEntityCollisions(entity, entity.getBoundingBox()).iterator();
//            Vec3d entitypos = entity.getPos();
//            double closest = Double.MAX_VALUE;
//            while (bob.hasNext()) {
//                Optional<Vec3d> current = bob.next().getClosestPointTo(entitypos);
//                if (current.isPresent()) {
//                    double newVec = (double) current.get().y;
//                    closest = (double) Math.min(closest, Math.max(entitypos.y, newVec) - Math.min(entitypos.y, newVec));
//                }
//            }
//            if (closest != Double.MAX_VALUE) return closest;
//        }
//        return 0;
//    }
//
//    public float getClosestCollisionZ() {
//        if (entity != null && entity.world != null) {
//            Iterator<VoxelShape> bob = entity.world.getEntityCollisions(entity, entity.getBoundingBox()).iterator();
//            Vec3d entitypos = entity.getPos();
//            double closest = Double.MAX_VALUE;
//            while (bob.hasNext()) {
//                Optional<Vec3d> current = bob.next().getClosestPointTo(entitypos);
//                if (current.isPresent()) {
//                    double newVec = (double) current.get().z;
//                    closest = (double) Math.min(closest, Math.max(entitypos.z, newVec) - Math.min(entitypos.z, newVec));
//                }
//            }
//            if (closest != Double.MAX_VALUE) return closest;
//        }
//        return 0;
//    }

    public boolean isClimbing() {

        return entity instanceof LivingEntity alive && alive.isClimbing();
    }

    public boolean isAlive() {
        return entity != null && entity.isAlive();
    }

    public boolean isAggressive() {
        return entity instanceof MobEntity && ((MobEntity) entity).isAttacking();
    }

    public boolean isGlowing() {
        return entity != null && entity.isGlowing();
    }

    public boolean isHurt() {
        return entity instanceof LivingEntity alive && alive.hurtTime > 0;
    }

    public boolean isInHand() {
        return false;
    }

    public boolean isInItemFrame() {
        return false;
    }

    public boolean isInGround() {
        return entity instanceof ArrowEntity arrow && arrow.isOnGround();
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

    public boolean isRidden() {
        return entity != null && entity.hasPassengers();
    }

    public boolean isSitting() {
        //if(new Random().nextInt(100) == 1) System.out.println("check was "+check);
        return entity != null && (
                (entity instanceof TameableEntity tame && tame.isInSittingPose()) ||
                        (entity instanceof FoxEntity fox && fox.isSitting()) ||
                        (entity instanceof ParrotEntity parrot && parrot.isInSittingPose()) ||
                        (entity instanceof CatEntity cat && cat.isInSittingPose()) ||
                        (entity instanceof WolfEntity wolf && wolf.isInSittingPose())

        );
    }

    public boolean isSneaking() {
        return entity != null && entity.isSneaking();
    }

    public boolean isSprinting() {
        return entity != null && entity.isSprinting();
    }

    public boolean isTamed() {
        return entity instanceof TameableEntity tame && tame.isTamed();
    }

    public boolean isWet() {
        return entity != null && entity.isWet();
    }

    public float getSwingProgress() {
        return entity instanceof LivingEntity alive ? alive.getHandSwingProgress((float) tickDelta) : 0;
    }

    public float getAge() {
        //return entity == null ? 0 : entity.age + tickDelta;
        //return animationProgress;
        if (entity == null) {
            return 0 + tickDelta;
        }
        return constrainedFloat(entity.age,24000) + tickDelta;
    }

    private float constrainedFloat(float value, int constraint ){
        return (value >= constraint ? value % constraint : value);
    }
    private float constrainedFloat(float value){
        return constrainedFloat(value,24000);
    }

    private float constrainedFloat(long value, int constraint){
        return (value >= constraint ? value % constraint : value);
    }
    private float constrainedFloat(long value){
        return constrainedFloat(value,24000);
    }
    private float constrainedFloat(int value, int constraint ){
        return (value >= constraint ? value % constraint : value);
    }

    private float constrainedFloat(int value){
        return constrainedFloat(value,24000);
    }

    public float getFrameTime() {

        //float lastFrameDurationInMiliSecondsDividedBy20 = MinecraftClient.getInstance().getLastFrameDuration();
        //float lastFrameDurationInSeconds = lastFrameDurationInMiliSecondsDividedBy20 / 50;
        return MinecraftClient.getInstance().getLastFrameDuration() / 20;
    }

    public float getLimbAngle() {
        return limbAngle;
    }

    public float getLimbDistance() {
        return limbDistance == Float.MIN_VALUE? 0 : limbDistance;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public float getHeadPitch() { return headPitch; }

    public float getTickDelta() {
        return tickDelta;
    }

    public float getMoveForward(){
        if(entity == null) return 0;
        double lookDir = Math.toRadians(90-entity.getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3d velocity = entity.getVelocity();

        //consider 2d plane of movement with x y
        double x = velocity.x;
        double y = velocity.z;

        // compute the new x and y components after rotation
        double newX = (x * Math.cos(lookDir)) - (y * Math.sin(lookDir));
        //double newY = (x * Math.sin(lookDir)) + (y * Math.cos(lookDir));


        return processMove(newX,x,y);
    }

    public float getMoveStrafe(){
        if(entity == null) return 0;
        double lookDir = Math.toRadians(90-entity.getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3d velocity = entity.getVelocity();

        //consider 2d plane of movement with x y
        double x = velocity.x;
        double y = velocity.z;

        // compute the new x and y components after rotation
        //double newX = (x * Math.cos(lookDir)) - (y * Math.sin(lookDir));
        double newY = (x * Math.sin(lookDir)) + (y * Math.cos(lookDir));
        return processMove( newY,x,y);
    }

    private float processMove(double value, double x, double y){

        double totalMovementVector = Math.sqrt(x*x + y*y);

        if(totalMovementVector == 0) return 0;

        //return percentage that is forward/strafe
        return (float) -(value / totalMovementVector);

    }

}
