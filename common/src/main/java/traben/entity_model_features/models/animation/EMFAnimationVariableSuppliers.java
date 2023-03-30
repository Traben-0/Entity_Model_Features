package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class EMFAnimationVariableSuppliers {


    //public boolean riding = false;

    //public boolean child = false;

    //public static long lastFrameTime = System.currentTimeMillis();
    private final Object2LongOpenHashMap<UUID> lastFrameTimeMap = new Object2LongOpenHashMap<>();
    public Entity entity = null;
    public float limbAngle = 0;
    public float limbDistance = 0;
    public float animationProgress = 0;
    public float headYaw = 0;
    public float headPitch = 0;
    public float tickDelta = 0;

    public Entity getEntity() {
        //System.out.println("ran");
        return entity;
    }

    public double getDimension() {
        if (entity == null || entity.getWorld() == null) return 0;
        Identifier id = entity.getWorld().getDimensionKey().getValue();
        if (id.equals(DimensionTypes.THE_NETHER_ID)) return -1;
        if (id.equals(DimensionTypes.THE_END_ID)) return 1;
        return 0;
    }

    public double getPlayerX() {
        return MinecraftClient.getInstance().player == null ? 0 : (double) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevX, MinecraftClient.getInstance().player.getX());
    }

    public double getPlayerY() {
        return MinecraftClient.getInstance().player == null ? 0 : (double) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevY, MinecraftClient.getInstance().player.getY());
    }

    public double getPlayerZ() {
        return MinecraftClient.getInstance().player == null ? 0 : (double) MathHelper.lerp(tickDelta, MinecraftClient.getInstance().player.prevZ, MinecraftClient.getInstance().player.getZ());
    }

    public double getPlayerRX() {
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (double) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevPitch, MinecraftClient.getInstance().player.getPitch()));
    }

    public double getPlayerRY() {
        return (MinecraftClient.getInstance().player == null) ? 0 :
                (double) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, MinecraftClient.getInstance().player.prevYaw, MinecraftClient.getInstance().player.getYaw()));
    }

    public double getEntityX() {
        return getEntity() == null ? 0 : (double) MathHelper.lerp(getTickDelta(), getEntity().prevX, getEntity().getX());
    }

    public double getEntityY() {
        return getEntity() == null ? 0 :
                //(double) getEntity().getY();
                (double) MathHelper.lerp(getTickDelta(), getEntity().prevY, getEntity().getY());
    }

    public double getEntityZ() {
        return getEntity() == null ? 0 : (double) MathHelper.lerp(getTickDelta(), getEntity().prevZ, getEntity().getZ());
    }

    public double getEntityRX() {
        return (getEntity() == null) ? 0 :
                //(double) Math.toRadians(getEntity().getPitch(tickDelta));
                (double) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, getEntity().prevPitch, getEntity().getPitch()));
    }

    public double getEntityRY() {
        return (getEntity() instanceof LivingEntity alive) ?
                (double) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, alive.prevBodyYaw, alive.getBodyYaw())) : 0;
    }

    //long changed to double... should be fine tbh
    public double getTime() {
        if (entity == null || entity.getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            double val = entity.getWorld().getTime();
            return (val >= 24000 ? val % 24000 : val) + tickDelta;
        }
    }

    public double getHealth() {
        return entity instanceof LivingEntity alive ? alive.getHealth() : 1;
    }

    public double getDeathTime() {
        return entity instanceof LivingEntity alive ? alive.deathTime : 0;
    }

    public double getAngerTime() {
        return !(entity instanceof Angerable) ? 0 : ((Angerable) entity).getAngerTime();
    }

    public double getMaxHealth() {
        return entity instanceof LivingEntity alive ? alive.getMaxHealth() : 1;
    }

    public double getId() {
        return entity == null ? 0 : entity.getUuid().hashCode();
    }

    public double getHurtTime() {
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

    public double getClosestCollisionX() {
        if (entity != null && entity.world != null) {
            Iterator<VoxelShape> bob = entity.world.getEntityCollisions(entity, entity.getBoundingBox()).iterator();
            Vec3d entitypos = entity.getPos();
            double closest = Double.MAX_VALUE;
            while (bob.hasNext()) {
                Optional<Vec3d> current = bob.next().getClosestPointTo(entitypos);
                if (current.isPresent()) {
                    double newVec = (double) current.get().x;
                    closest = (double) Math.min(closest, Math.max(entitypos.x, newVec) - Math.min(entitypos.x, newVec));
                }
            }
            if (closest != Double.MAX_VALUE) return closest;
        }
        return 0;
    }

    public double getClosestCollisionY() {
        if (entity != null && entity.world != null) {
            Iterator<VoxelShape> bob = entity.world.getEntityCollisions(entity, entity.getBoundingBox()).iterator();
            Vec3d entitypos = entity.getPos();
            double closest = Double.MAX_VALUE;
            while (bob.hasNext()) {
                Optional<Vec3d> current = bob.next().getClosestPointTo(entitypos);
                if (current.isPresent()) {
                    double newVec = (double) current.get().y;
                    closest = (double) Math.min(closest, Math.max(entitypos.y, newVec) - Math.min(entitypos.y, newVec));
                }
            }
            if (closest != Double.MAX_VALUE) return closest;
        }
        return 0;
    }

    public double getClosestCollisionZ() {
        if (entity != null && entity.world != null) {
            Iterator<VoxelShape> bob = entity.world.getEntityCollisions(entity, entity.getBoundingBox()).iterator();
            Vec3d entitypos = entity.getPos();
            double closest = Double.MAX_VALUE;
            while (bob.hasNext()) {
                Optional<Vec3d> current = bob.next().getClosestPointTo(entitypos);
                if (current.isPresent()) {
                    double newVec = (double) current.get().z;
                    closest = (double) Math.min(closest, Math.max(entitypos.z, newVec) - Math.min(entitypos.z, newVec));
                }
            }
            if (closest != Double.MAX_VALUE) return closest;
        }
        return 0;
    }

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

    public double getSwingProgress() {
        return entity instanceof LivingEntity alive ? alive.getHandSwingProgress((float) tickDelta) : 0;
    }

    public double getAge() {
        //return entity == null ? 0 : entity.age + tickDelta;
        //return animationProgress;
        if (entity == null) {
            return 0 + tickDelta;
        }
        double age = entity.age;
        return (age >= 24000 ? age % 24000 : age) + tickDelta;
    }

    public double getFrameTime() {
        return MinecraftClient.getInstance().getLastFrameDuration() / 10;
    }

    public double getLimbAngle() {
        return limbAngle;
    }

    public double getLimbDistance() {
        return limbDistance;
    }

    public double getHeadYaw() {
        return headYaw;
    }

    public double getHeadPitch() {
        return headPitch;

    }

    public double getTickDelta() {
        return tickDelta;
    }

}
