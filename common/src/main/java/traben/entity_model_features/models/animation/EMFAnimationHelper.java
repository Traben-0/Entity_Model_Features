package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionTypes;
import traben.entity_model_features.mixin.accessor.EntityRenderDispatcherAccessor;
import traben.entity_model_features.mixin.accessor.MinecraftClientAccessor;
import traben.entity_model_features.utils.EMFBlockEntityWrapper;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.EMFEntityWrapper;
import traben.entity_model_features.utils.EMFManager;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class EMFAnimationHelper {


    //public static long lastFrameTime = System.currentTimeMillis();
    // private final Object2LongOpenHashMap<UUID> lastFrameTimeMap = new Object2LongOpenHashMap<>();

    private static final Object2IntOpenHashMap<UUID> knownHighestAngerTimeByUUID = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(0);
    }};
    private static EMFEntity emfEntity = null;
    private static float shadowSize = Float.NaN;
    private static float shadowOpacity = Float.NaN;
    private static float leashX = Float.NaN;
    private static float leashY = Float.NaN;
    private static float leashZ = Float.NaN;
    private static float shadowX = Float.NaN;
    private static float shadowZ = Float.NaN;
    private static float limbAngle = Float.NaN;
    private static float limbDistance = Float.NaN;
    //private static  float animationProgress = Float.NaN;
    private static float headYaw = Float.NaN;
    private static float headPitch = Float.NaN;
    private static float tickDelta = 0;
    private static int ruleIndex = 0;
    private static float dimension = Float.NaN;
    private static boolean onShoulder = false;

    private EMFAnimationHelper() {

    }

    public static void setCurrentEntity(Entity entityIn) {
        resetForNewEntity();
        EMFManager.getInstance().entityRenderCount++;
        emfEntity = entityIn == null ? null : new EMFEntityWrapper(entityIn);
    }

    public static void setCurrentBlockEntity(BlockEntity entityIn) {
        resetForNewEntity();
        EMFManager.getInstance().entityRenderCount++;
        emfEntity = entityIn == null ? null : new EMFBlockEntityWrapper(entityIn);
    }



    public static void resetForNewEntity() {//todo extend for possible performance benefits
        dimension = Float.NaN;
        limbAngle = Float.NaN;
        limbDistance = Float.NaN;
        headYaw = Float.NaN;
        headPitch = Float.NaN;
        tickDelta = MinecraftClient.getInstance().isPaused() ? ((MinecraftClientAccessor) MinecraftClient.getInstance()).getPausedTickDelta() : MinecraftClient.getInstance().getTickDelta();
        ruleIndex = 0;

        shadowSize = Float.NaN;
        shadowOpacity = Float.NaN;
        leashX = 0;
        leashY = 0;
        leashZ = 0;
        shadowX = 0;
        shadowZ = 0;

        onShoulder = false;
    }

    public static float getRuleIndex() {
        return ruleIndex;
    }

    public static void setRuleIndex(int newIndex) {
        ruleIndex = newIndex;
    }

    public static EMFEntity getEMFEntity() {
        return emfEntity;
    }

    public static float getDimension() {
        if (Float.isNaN(dimension)) {
            if (emfEntity == null || emfEntity.getWorld() == null) {
                dimension = 0;
            } else {
                Identifier id = emfEntity.getWorld().getDimensionKey().getValue();
                if (id.equals(DimensionTypes.THE_NETHER_ID)) {
                    dimension = -1;
                } else if (id.equals(DimensionTypes.THE_END_ID)) {
                    dimension = 1;
                } else {
                    dimension = 0;
                }
            }
        }
        return dimension;
    }

    public static float getPlayerX() {
        return MinecraftClient.getInstance().player == null ? 0 : (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevX, MinecraftClient.getInstance().player.getX());
    }

    public static float getPlayerY() {
        return MinecraftClient.getInstance().player == null ? 0 : (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevY, MinecraftClient.getInstance().player.getY());
    }

    public static float getPlayerZ() {
        return MinecraftClient.getInstance().player == null ? 0 : (float) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevZ, MinecraftClient.getInstance().player.getZ());
    }

    public static float getPlayerRX() {
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevPitch, MinecraftClient.getInstance().player.getPitch()));
    }

    public static float getPlayerRY() {
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevYaw, MinecraftClient.getInstance().player.getYaw()));
    }

    public static float getEntityX() {
        return emfEntity == null ? 0 : (float) MathHelper.lerp(getTickDelta(), emfEntity.prevX(), emfEntity.getX());
    }

    public static float getEntityY() {
        return emfEntity == null ? 0 :
                //(double) entity.getY();
                (float) MathHelper.lerp(getTickDelta(), emfEntity.prevY(), emfEntity.getY());
    }

    public static float getEntityZ() {
        return emfEntity == null ? 0 : (float) MathHelper.lerp(getTickDelta(), emfEntity.prevZ(), emfEntity.getZ());
    }

    public static float getEntityRX() {
        return (emfEntity == null) ? 0 :
                //(double) Math.toRadians(entity.getPitch(tickDelta));
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, emfEntity.prevPitch(), emfEntity.getPitch()));
    }

    public static float getEntityRY() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        return (alive != null) ?
                (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, alive.prevBodyYaw, alive.getBodyYaw())) :
                emfEntity.entity() != null ?
                        (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, emfEntity.entity().prevYaw, emfEntity.entity().getYaw()))
                        : 0;
    }

    //long changed to double... should be fine tbh
    public static float getTime() {
        if (emfEntity == null || emfEntity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            long upTimeInTicks = emfEntity.getWorld().getTime(); // (System.currentTimeMillis() - START_TIME)/50;
            return constrainedFloat(upTimeInTicks, 720720) + tickDelta;
        }
    }

    public static float getDayTime() {
        if (emfEntity == null || emfEntity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            return constrainedFloat(emfEntity.getWorld().getTimeOfDay(), 24000) + tickDelta;
        }
    }

    public static float getDayCount() {
        if (emfEntity == null || emfEntity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            return (float) (emfEntity.getWorld().getTimeOfDay() / 24000L) + tickDelta;
        }
    }

    public static float getHealth() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null ? alive.getHealth() : 1;
    }

    public static float getDeathTime() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null ? alive.deathTime : 0;
    }

    public static float getAngerTime() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        if (!(alive instanceof Angerable)) return 0;

        float currentKnownHighest = knownHighestAngerTimeByUUID.getInt(emfEntity.getUuid());
        int angerTime = ((Angerable) alive).getAngerTime();

        //clear anger info if anger is over
        if (angerTime <= 0) {
            knownHighestAngerTimeByUUID.put(emfEntity.getUuid(), 0);
            return 0;
        }

        //store this if this is the largest anger time for the entity seen so far
        if (angerTime > currentKnownHighest) {
            knownHighestAngerTimeByUUID.put(emfEntity.getUuid(), angerTime);
        }
        return angerTime - tickDelta;
    }

    public static float getAngerTimeStart() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        //this only makes sense if we are calculating it here from the largest known value of anger time
        // i could also reset it when anger time hits 0
        //todo this can't be right and wont work if anger time start is called first
        return !(alive instanceof Angerable) ? 0 : knownHighestAngerTimeByUUID.getInt(alive.getUuid());

    }

    public static float getMaxHealth() {
        if (emfEntity == null) return 1;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null ? alive.getMaxHealth() : 1;
    }

    public static float getId() {
        return emfEntity == null ? 0 : emfEntity.getUuid().hashCode();
    }

    public static float getHurtTime() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null ? (alive.hurtTime > 0 ? alive.hurtTime - tickDelta : 0) : 0;
    }

    public static boolean isInWater() {
        return emfEntity != null && emfEntity.isTouchingWater();
    }

    public static boolean isBurning() {
        return emfEntity != null && emfEntity.isOnFire();
    }

    public static boolean isRiding() {
        //return riding;
        return emfEntity != null && emfEntity.hasVehicle();
    }

    public static boolean isChild() {
        if (emfEntity == null) return false;
        LivingEntity alive = emfEntity.getLiving();
        //return child;
        return alive != null && alive.isBaby();
    }

    public static boolean isOnGround() {
        return emfEntity != null && emfEntity.isOnGround();
    }

    public static boolean isClimbing() {
        if (emfEntity == null) return false;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null && alive.isClimbing();
    }

    public static boolean isAlive() {
        if (emfEntity == null) return false;
        return emfEntity.isAlive();
    }

    public static boolean isAggressive() {
        if (emfEntity == null) return false;
        Entity entity = emfEntity.entity();
        return entity instanceof MobEntity && ((MobEntity) entity).isAttacking();
    }

    public static boolean isGlowing() {
        return emfEntity != null && emfEntity.isGlowing();
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

    public static boolean isHurt() {
        if (emfEntity == null) return false;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null && alive.hurtTime > 0;
    }

    public static boolean isInHand() {
        return false;//todo
    }

    public static boolean isInItemFrame() {
        return false;//todo
    }

    public static boolean isInGround() {
        if (emfEntity == null) return false;
        Entity entity = emfEntity.entity();
        return entity instanceof ProjectileEntity proj && proj.isInsideWall();
    }

    public static boolean isInGui() {
        //basing this almost solely on the fact that the inventory screen sets the shadow render flag to false during its render,
        // I assume other gui renderers will follow suit, I'm not sure if other things affect it, it doesn't seem tied to the option setting
        return MinecraftClient.getInstance().currentScreen != null
                && !((EntityRenderDispatcherAccessor) MinecraftClient.getInstance().getEntityRenderDispatcher()).isRenderShadows();

    }

    public static boolean isInLava() {
        return emfEntity != null && emfEntity.isInLava();
    }

    public static boolean isInvisible() {
        return emfEntity != null && emfEntity.isInvisible();
    }

    public static boolean isOnHead() {
        return false;//todo
    }

    public static boolean isOnShoulder() {
        return onShoulder;
    }

    public static void setCurrentEntityOnShoulder() {
        onShoulder = true;
    }

    public static boolean isRidden() {
        return emfEntity != null && emfEntity.hasPassengers();
    }

    public static boolean isSitting() {
        if (emfEntity == null) return false;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null && (
                (alive instanceof TameableEntity tame && tame.isInSittingPose()) ||
                        (alive instanceof FoxEntity fox && fox.isSitting()) ||
                        (alive instanceof ParrotEntity parrot && parrot.isInSittingPose()) ||
                        (alive instanceof CatEntity cat && cat.isInSittingPose()) ||
                        (alive instanceof WolfEntity wolf && wolf.isInSittingPose())

        );
    }

    public static boolean isSneaking() {
        return emfEntity != null && emfEntity.isSneaking();
    }

    public static boolean isSprinting() {
        return emfEntity != null && emfEntity.isSprinting();
    }

    public static boolean isTamed() {
        if (emfEntity == null) return false;
        LivingEntity alive = emfEntity.getLiving();
        return alive instanceof TameableEntity tame && tame.isTamed();
    }

    public static boolean isWet() {
        return emfEntity != null && emfEntity.isWet();
    }

    public static float getSwingProgress() {
        if (emfEntity == null) return 0;
        LivingEntity alive = emfEntity.getLiving();
        return alive != null ? alive.getHandSwingProgress(tickDelta) : 0;
    }

    public static float getAge() {
        if (emfEntity == null) {
            return 0 + tickDelta;
        }
        return constrainedFloat(emfEntity.age(), 24000) + tickDelta;
    }

    private static float constrainedFloat(float value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    private static float constrainedFloat(float value) {
        return constrainedFloat(value, 24000);
    }

    private static float constrainedFloat(long value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    private static float constrainedFloat(long value) {
        return constrainedFloat(value, 24000);
    }

    private static float constrainedFloat(int value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    private static float constrainedFloat(int value) {
        return constrainedFloat(value, 24000);
    }

    public static float getFrameTime() {

        return MinecraftClient.getInstance().getLastFrameDuration() / 20;
    }

    public static float getLimbAngle() {//limb_swing
        if (emfEntity == null) return 0;
        if (Float.isNaN(limbAngle)) {
            doLimbValues();
        }
        return limbAngle;
    }

    public static void setLimbAngle(float limbAngle) {
        EMFAnimationHelper.limbAngle = limbAngle;
    }

    public static float getLimbDistance() {//limb_speed
        if (emfEntity == null) return 0;
        if (Float.isNaN(limbDistance)) {
            doLimbValues();
        }
        return limbDistance == Float.MIN_VALUE ? 0 : limbDistance;
    }

    public static void setLimbDistance(float limbDistance) {
        EMFAnimationHelper.limbDistance = limbDistance;
    }

    private static void doLimbValues() {
        float o = 0;
        float n = 0;
        LivingEntity alive = emfEntity.getLiving();
        if (!emfEntity.hasVehicle() && alive != null) {
            o = alive.limbAnimator.getPos(tickDelta);
            n = alive.limbAnimator.getSpeed(tickDelta);
            if (alive.isBaby()) {
                o *= 3.0F;

            }
            if (n > 1.0F) {
                n = 1.0F;
            }
        } else if (emfEntity.entity() instanceof MinecartEntity) {
            n = 1;
            o = -(getEntityX() + getEntityZ());
        } else if (emfEntity.entity() instanceof BoatEntity boat) {
            n = 1;
            //o = boat.interpolatePaddlePhase(0, tickDelta);//1);
            o = Math.max(boat.interpolatePaddlePhase(1, tickDelta), boat.interpolatePaddlePhase(0, tickDelta));
        }
        limbDistance = n;
        limbAngle = o;
    }

    public static float getHeadYaw() {
        if (emfEntity == null) return 0;
        if (Float.isNaN(headYaw)) {
            doHeadValues();
        }
        return headYaw;
    }

    public static void setHeadYaw(float headYaw) {
        EMFAnimationHelper.headYaw = headYaw;
    }

    public static float getHeadPitch() {
        if (emfEntity == null) return 0;
        if (Float.isNaN(headPitch)) {
            doHeadValues();
        }
        return headPitch;
    }

    public static void setHeadPitch(float headPitch) {
        EMFAnimationHelper.headPitch = headPitch;
    }

    private static void doHeadValues() {
        LivingEntity livingEntity = emfEntity.getLiving();
        if (livingEntity != null) {
            float h = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
            float j = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevHeadYaw, livingEntity.headYaw);
            float k = j - h;
            float l;
            if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
                h = MathHelper.lerpAngleDegrees(tickDelta, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
                k = j - h;
                l = MathHelper.wrapDegrees(k);
                if (l < -85.0F) {
                    l = -85.0F;
                }

                if (l >= 85.0F) {
                    l = 85.0F;
                }

                h = j - l;
                if (l * l > 2500.0F) {
                    h += l * 0.2F;
                }

                k = j - h;
            }

            float m = MathHelper.lerp(tickDelta, livingEntity.prevPitch, livingEntity.getPitch());
            if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
                m *= -1.0F;
                k *= -1.0F;
            }
            headPitch = m;
            //headYaw = k;
            //constrain head yaw amount
            if (k > 180 || k < -180) {
                float normalizedAngle = k % 360;
                if (normalizedAngle > 180) {
                    normalizedAngle -= 360;
                } else if (normalizedAngle < -180) {
                    normalizedAngle += 360;
                }
                headYaw = normalizedAngle;
            } else {
                headYaw = k;
            }
        } else {
            headPitch = 0;
            headYaw = 0;
        }
    }

    public static float getTickDelta() {
        return tickDelta;
    }

    public static float getMoveForward() {
        if (emfEntity == null) return 0;
        double lookDir = Math.toRadians(90 - emfEntity.getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3d velocity = emfEntity.getVelocity();

        //consider 2d plane of movement with x y
        double x = velocity.x;
        double y = velocity.z;

        // compute the new x and y components after rotation
        double newX = (x * Math.cos(lookDir)) - (y * Math.sin(lookDir));
        //double newY = (x * Math.sin(lookDir)) + (y * Math.cos(lookDir));


        return processMove(newX, x, y);
    }

    public static float getMoveStrafe() {
        if (emfEntity == null) return 0;
        double lookDir = Math.toRadians(90 - emfEntity.getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3d velocity = emfEntity.getVelocity();

        //consider 2d plane of movement with x y
        double x = velocity.x;
        double y = velocity.z;

        // compute the new x and y components after rotation
        //double newX = (x * Math.cos(lookDir)) - (y * Math.sin(lookDir));
        double newY = (x * Math.sin(lookDir)) + (y * Math.cos(lookDir));
        return processMove(newY, x, y);
    }

    private static float processMove(double value, double x, double y) {

        double totalMovementVector = Math.sqrt(x * x + y * y);

        if (totalMovementVector == 0) return 0;

        //return percentage that is forward/strafe
        return (float) -(value / totalMovementVector);

    }

    public static float getShadowSize() {
        return shadowSize;
    }

    public static void setShadowSize(float shadowSize) {
        EMFAnimationHelper.shadowSize = shadowSize;
    }

    public static float getShadowOpacity() {
        return shadowOpacity;
    }

    public static void setShadowOpacity(float shadowOpacity) {
        EMFAnimationHelper.shadowOpacity = shadowOpacity;
    }

    public static float getLeashX() {
        return leashX;
    }

    public static void setLeashX(float leashX) {
        EMFAnimationHelper.leashX = leashX;
    }

    public static float getLeashY() {
        return leashY;
    }

    public static void setLeashY(float leashY) {
        EMFAnimationHelper.leashY = leashY;
    }

    public static float getLeashZ() {
        return leashZ;
    }

    public static void setLeashZ(float leashZ) {
        EMFAnimationHelper.leashZ = leashZ;
    }

    public static float getShadowX() {
        return shadowX;
    }

    public static void setShadowX(float shadowX) {
        EMFAnimationHelper.shadowX = shadowX;
    }

    public static float getShadowZ() {
        return shadowZ;
    }

    public static void setShadowZ(float shadowZ) {
        EMFAnimationHelper.shadowZ = shadowZ;
    }
}
