package traben.entity_model_features.models.animation.math;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.mixin.mixins.accessor.MinecraftClientAccessor;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.animation.state.EMFState;
import traben.entity_model_features.utils.EMFEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EMFMath {

    //region value wrapping
    public static final int WRAP_CONST = 27720;

    @SuppressWarnings("SameParameterValue")
    private static float constrainedFloat(float value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    @SuppressWarnings("unused")
    private static float constrainedFloat(float value) {
        return constrainedFloat(value, WRAP_CONST);
    }

    private static float constrainedFloat(long value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    @SuppressWarnings("unused")
    private static float constrainedFloat(long value) {
        return constrainedFloat(value, WRAP_CONST);
    }

    @SuppressWarnings("SameParameterValue")
    private static float constrainedFloat(int value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    @SuppressWarnings("unused")
    private static float constrainedFloat(int value) {
        return constrainedFloat(value, WRAP_CONST);
    }
    //endregion

    private static final Map<UUID, Integer> knownHighestAngerTimeByUUID = new HashMap<>() {
        @Override
        public Integer get(Object key) {
            return super.getOrDefault(key, 0);
        }
    };

    public static void setEntityVariable(String variable, float value) {
        var state = emfState();
        if (state != null) {
            state.variableMap().put(variable, value);
        }
    }

    public static float getEntityVariable(String variable, float defaultValue) {
        var state = emfState();
        if (state == null) return defaultValue;
        return state.variableMap().getOrDefault(variable, defaultValue);
    }




    //region player position and rotation
    public static float getPlayerX() {
        return Minecraft.getInstance().player == null ? 0 : (float) Mth.lerp(getTickDelta(), Minecraft.getInstance().player.xo, Minecraft.getInstance().player.getX());
    }

    public static float getPlayerY() {
        return Minecraft.getInstance().player == null ? 0 : (float) Mth.lerp(getTickDelta(), Minecraft.getInstance().player.yo, Minecraft.getInstance().player.getY());
    }

    public static float getPlayerZ() {
        return Minecraft.getInstance().player == null ? 0 : (float) Mth.lerp(getTickDelta(), Minecraft.getInstance().player.zo, Minecraft.getInstance().player.getZ());
    }

    public static float getPlayerRX() {
        return (Minecraft.getInstance().player == null) ? 0 :
                (float) Math.toRadians(Mth.rotLerp(getTickDelta(), Minecraft.getInstance().player.xRotO, Minecraft.getInstance().player.getXRot()));
    }

    public static float getPlayerRY() {
        return (Minecraft.getInstance().player == null) ? 0 :
                (float) Math.toRadians(Mth.rotLerp(getTickDelta(), Minecraft.getInstance().player.yRotO, Minecraft.getInstance().player.getYRot()));
    }
    //endregion

    //region entity position and rotation
    public static float getEntityX() {
        var state = emfState();
        return state == null ? 0 : (float) Mth.lerp(getTickDelta(), state.prevX(), state.x());
    }

    public static float getEntityY() {
        var state = emfState();
        return state == null ? 0 :
                //(double) entity.getY();
                (float) Mth.lerp(getTickDelta(), state.prevY(), state.y());
    }

    public static float getEntityZ() {
        var state = emfState();
        return state == null ? 0 : (float) Mth.lerp(getTickDelta(), state.prevZ(), state.z());
    }

    public static float getEntityRX() {
        var state = emfState();
        return (state == null) ? 0 :
                //(double) Math.toRadians(entity.getPitch(tickDelta));
                (float) Math.toRadians(Mth.rotLerp(getTickDelta(), state.prevPitch(), state.pitch()));
    }

    public static float getEntityRY() {
        var state = emfState();
        if (state == null) return 0;
        var emfEntity = state.entity();
        // yBodyRotO/yBodyRot oscillate between ticks while inventory is open, skip the lerp in gui
        if (isInGui()) {
            return (emfEntity instanceof LivingEntity alive) ?
                    (float) Math.toRadians(alive.yBodyRot) :
                    emfEntity instanceof Entity entity ?
                            (float) Math.toRadians(entity.getYRot())
                            : 0;
        }
        return (emfEntity instanceof LivingEntity alive) ?
                (float) Math.toRadians(Mth.rotLerp(getTickDelta(), alive.yBodyRotO, alive.yBodyRot)) :
                emfEntity instanceof Entity entity ?
                        (float) Math.toRadians(Mth.rotLerp(getTickDelta(), entity.yRotO, entity.yRot))
                        : 0;
    }
    //endregion

    //region move methods
    public static float getMoveForward() {
        var state = emfState();
        if (state == null || isInGui()) return 0;
        double lookDir = Math.toRadians(90 - state.yaw());
        //float speed = entity.horizontalSpeed;
        Vec3 velocity = state.emfVelocity();

        //consider 2d plane of movement with x y
        double x = velocity.x;
        double y = velocity.z;

        // compute the new x and y components after rotation
        double newX = (x * Math.cos(lookDir)) - (y * Math.sin(lookDir));
        //double newY = (x * Math.sin(lookDir)) + (y * Math.cos(lookDir));


        return processMove(newX, x, y);
    }

    public static float getMoveStrafe() {
        var state = emfState();
        if (state == null || isInGui()) return 0;
        double lookDir = Math.toRadians(90 - state.yaw());
        //float speed = entity.horizontalSpeed;
        Vec3 velocity = state.emfVelocity();

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
    //endregion

    //region head methods
    public static float getHeadYaw() {
        var state = emfState();
        if (state == null) return 0;
        if (Float.isNaN(state.headYaw())) {
            if (isInGui()) return 0;
            doHeadValues();
        }
        return state.headYaw();
    }

    public static void setHeadYaw(float headYaw) {
        var state = emfState();
        if (state != null) {
            state.setHeadYaw(headYaw);
        }
    }

    public static float getHeadPitch() {
        var state = emfState();
        if (state == null) return 0;
        if (Float.isNaN(state.headPitch())) {
            if (isInGui()) return 0;
            doHeadValues();
        }
        return state.headPitch();
    }

    public static void setHeadPitch(float headPitch) {
        var state = emfState();
        if (state != null) {
            state.setHeadPitch(headPitch);
        }
    }

    private static void doHeadValues() {
        var state = emfState();
        if (state instanceof LivingEntity livingEntity) {
            float h = Mth.rotLerp(getTickDelta(), livingEntity.yBodyRotO, livingEntity.yBodyRot);
            float j = Mth.rotLerp(getTickDelta(), livingEntity.yHeadRotO, livingEntity.yHeadRot);
            float k = j - h;
            float l;
            if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
                h = Mth.rotLerp(getTickDelta(), livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
                k = j - h;
                l = Mth.wrapDegrees(k);
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

            float m = Mth.lerp(getTickDelta(), livingEntity.xRotO, livingEntity.getXRot());
            if (
                //#if MC >= 12109
                // duplicate check of now non-static method
                    livingEntity.getCustomName() != null && LivingEntityRenderer.isUpsideDownName(livingEntity.getCustomName().getString() )
                //#else
                //$$ LivingEntityRenderer.isEntityUpsideDown(livingEntity)
                //#endif
            ) {
                m *= -1.0F;
                k *= -1.0F;
            }
            state.setHeadPitch(m);
            //constrain head yaw amount
            if (k >= 180 || k < -180) {
                state.setHeadYaw(Mth.wrapDegrees(k));
            } else {
                state.setHeadYaw(k);
            }
        } else {
            if (state != null) {
                state.setHeadPitch(0);
                state.setHeadYaw(0);
            }
        }
    }
    //endregion

    //region limb methods
    public static float getLimbAngle() {//limb_swing
        var state = emfState();
        if (state == null) return 0;
        if (Float.isNaN(state.limbAngle())) {
            doLimbValues(state);
        }
        return state.limbAngle();
    }

    public static void setLimbAngle(float limbAngle) {
        var state = emfState();
        if (state != null) {
            state.setLimbAngle(limbAngle);
        }
    }

    public static float getLimbDistance() {//limb_speed
        var state = emfState();
        if (state == null) return 0;
        if (Float.isNaN(state.limbDistance())) {
            doLimbValues(state);
        }
        return state.limbDistance() == Float.MIN_VALUE ? 0 : state.limbDistance();
    }

    public static void setLimbDistance(float limbDistance) {
        var state = emfState();
        if (state != null) {
            state.setLimbDistance(limbDistance);
        }
    }

    private static void doLimbValues(@NotNull EMFEntityRenderState state) {
        float o = 0;
        float n = 0;
        var entity = state.emfEntity();
        if (!state.hasVehicle() && entity instanceof LivingEntity alive) {
            o = alive.walkAnimation.position(getTickDelta());
            n = alive.walkAnimation.speed(getTickDelta());
            if (alive.isBaby()) {
                o *= 3.0F;

            }
            if (n > 1.0F) {
                n = 1.0F;
            }
        } else if (entity instanceof AbstractMinecart) {
            n = 1;
            o = -(getEntityX() + getEntityZ());
        } else if (entity instanceof
                //#if MC >= 12102
                net.minecraft.world.entity.vehicle.AbstractBoat
                //#else
                //$$ net.minecraft.world.entity.vehicle.Boat
                //#endif
                        boat) {
            n = 1;
            //o = boat.interpolatePaddlePhase(0, tickDelta);//1);
            o = Math.max(boat.getRowingTime(1, getTickDelta()), boat.getRowingTime(0, getTickDelta()));
        }
        state.setLimbDistance(n);
        state.setLimbAngle(o);
    }
    //endregion

    //region is_ methods
    public static boolean isJumping() {
        return emfEntity() instanceof LivingEntity alive && alive.jumping;
    }

    public static boolean isInWater() {
        var state = emfState();
        return state != null && state.isTouchingWater();
    }

    public static boolean isBurning() {
        var state = emfState();
        return state != null && state.isOnFire();
    }

    public static boolean isRiding() {
        var state = emfState();
        return state != null && state.hasVehicle();
    }

    public static boolean isChild() {
        return emfEntity() instanceof LivingEntity alive && alive.isBaby();
    }

    public static boolean isOnGround() {
        var state = emfState();
        return state != null && state.isOnGround();
    }

    public static boolean isClimbing() {
        return emfEntity() instanceof LivingEntity alive && alive.onClimbable();
    }

    public static boolean isAlive() {
        var state = emfState();
        return state != null && state.isAlive();
    }

    public static boolean isUsingItem() {
        var state = emfState();
        if (state == null) return false;
        if (state.emfEntity() instanceof LivingEntity entity) {
            return entity.isUsingItem();
        }
        return false;
    }

    public static boolean isSwingingArm(boolean right) {
        var state = emfState();
        if (state == null) return false;
        if (getSwingProgress() == 0 && !isUsingItem()) return false;
        if (state.emfEntity() instanceof LivingEntity entity) {
            boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
            boolean usingMainHand = entity.getUsedItemHand() == InteractionHand.MAIN_HAND;
            if (right){
                return isRightHanded == usingMainHand;
            } else {
                return isRightHanded != usingMainHand;
            }

        }
        return false;
    }

    public static boolean isHoldingItem(boolean right) {
        var state = emfState();
        if (state == null) return false;
        if (state.emfEntity() instanceof LivingEntity entity) {
            boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
            InteractionHand arm;
            if (right){
                arm = isRightHanded ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            } else {
                arm = isRightHanded ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            }
            return !entity.getItemInHand(arm).isEmpty();
        }
        return false;
    }

    @SuppressWarnings("IfCanBeSwitch")//only in java 21
    public static boolean isAggressive() {
        var state = emfState();
        if (state == null) return false;
        var emfEntity = state.emfEntity();

        if (emfEntity instanceof final EnderMan enderman) {
            return enderman.isCreepy();
        }
        if (emfEntity instanceof final Blaze blaze) {
            return blaze.isOnFire();
        }
        if (emfEntity instanceof final Guardian guardian) {
            return guardian.getActiveAttackTarget() != null;
        }
        if (emfEntity instanceof final Vindicator vindicator) {
            return vindicator.isAggressive();
        }
        if (emfEntity instanceof final SpellcasterIllager caster) {
            return caster.isCastingSpell();
        }
        if (emfEntity instanceof final Vex vex) {
            return vex.isCharging();
        }

        // these can fallback just incase the specific method doesn't sync for clients for modded mobs
        if (emfEntity instanceof final NeutralMob angry && angry.isAngry()) {
            return true;
        }
        if (emfEntity instanceof Targeting targets && targets.getTarget() != null) {
            return true;
        }
        return emfEntity instanceof Mob mob && mob.isAggressive();
    }

    public static boolean isGlowing() {
        var state = emfState();
        return state != null && state.isGlowing();
    }

    public static boolean isHurt() {
        var emfEntity = emfEntity();
        return emfEntity instanceof LivingEntity alive && alive.hurtTime > 0;
    }


    public static boolean isInGround() {
        return EMFState.isInGroundOverride || emfEntity() instanceof Projectile proj && proj.isInWall();
    }


    public static boolean isClientHovered() {
        var state = emfState();
        if (state == null) return false;
        var mc = Minecraft.getInstance();

        //block entity looked at
        if (state.isBlockEntity()){
            var player = Minecraft.getInstance().player;
            if(player != null
                    && state.distanceTo(player) <=
                    //#if MC >= 12006
                    player.blockInteractionRange()
                            //#else
                            //$$ (player.isCreative() ? 5F : 4.5F)
                            //#endif
                            + 1) {
                Entity entity = mc.getCameraEntity();
                if (entity != null) {
                    var block = entity.pick(20.0, 0.0F, false);
                    if (block.getType() == HitResult.Type.BLOCK) {
                        return ((BlockHitResult) block).getBlockPos().equals(state.blockPos());
                    }
                }
            }
            return false;
        }

        //entity looked at
        return mc.crosshairPickEntity != null && mc.crosshairPickEntity.equals(state.emfEntity());
    }

    public static boolean isInLava() {
        var state = emfState();
        return state != null && state.isInLava();
    }

    public static boolean isInvisible() {
        var state = emfState();
        return state != null && state.isInvisible();
    }


    public static boolean isOnShoulder() {
        var state = emfState();
        return state != null && state.onShoulder();
    }
    public static boolean isRidden() {
        var state = emfState();
        return state != null && state.hasPassengers();
    }

    public static boolean isSitting() {
        var state = emfState();
        if (state == null) return false;
        var entity = state.emfEntity();
        return (entity instanceof TamableAnimal tame && tame.isInSittingPose()) ||
                (entity instanceof Fox fox && fox.isSitting()) ||
                (entity instanceof Parrot parrot && parrot.isInSittingPose()) ||
                (entity instanceof Cat cat && cat.isInSittingPose()) ||
                (entity instanceof Wolf wolf && wolf.isInSittingPose()) ||
                (entity instanceof Camel camel && camel.isCamelSitting());
    }


    public static boolean isSneaking() {
        var state = emfState();
        return state != null && state.isSneaking();
    }

    public static boolean isSprinting() {
        var state = emfState();
        return state != null && state.isSprinting();
    }

    public static boolean isTamed() {
        return emfEntity() instanceof TamableAnimal tame && tame.isTame();
    }

    public static boolean isWet() {
        var state = emfState();
        return state != null && state.isWet();
    }
    //endregion

    //region get_ methods
    public static float getRuleIndex() {
        var state = emfState();
        if (state == null) return 0;
        return EMFManager.getInstance().lastModelRuleOfEntity.get(state.uuid());
    }

    public static float getDimension() {
        var state = emfState();
        if (state == null || state.world() == null) {
            return 0;
        } else {
            var optional = state.world().dimensionTypeRegistration().unwrapKey();
            if (optional.isEmpty()) return 0;
            ResourceLocation id = optional.get().location();
            //#if MC>= 12111
            //$$ if (id.equals(BuiltinDimensionTypes.NETHER.identifier())) {
            //$$     return -1;
            //$$ } else if (id.equals(BuiltinDimensionTypes.END.identifier())) {
            //#else
            if (id.equals(BuiltinDimensionTypes.NETHER_EFFECTS)) {
                return -1;
            } else if (id.equals(BuiltinDimensionTypes.END_EFFECTS)) {
                //#endif
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static float getTickDelta() {
        return
                //#if MC >= 1.21
                ((MinecraftClientAccessor) Minecraft.getInstance())
                        //#if MC >= 1.21.2
                        .getDeltaTracker()
                        //#else
                        //$$ .getTimer()
                        //#endif
                        .getGameTimeDeltaPartialTick(true);
                //#else
                //$$ Minecraft.getInstance().isPaused() ? ((MinecraftClientAccessor) Minecraft.getInstance()).getPausePartialTick() : Minecraft.getInstance().getFrameTime();
                //#endif
    }

    public static float getTime(EMFEntityRenderState state) {
        if (state == null || state.world() == null) {
            return 0 + getTickDelta();
        } else {
            //limit value upper limit to preserve floating point precision
            long upTimeInTicks = state.world().getGameTime();
            return constrainedFloat(upTimeInTicks, WRAP_CONST) + getTickDelta();
        }
    }

    public static float getDayTime() {
        var state = emfState();
        if (state == null || state.world() == null) {
            return 0 + getTickDelta();
        } else {
            //limit value upper limit to preserve floating point precision
            return constrainedFloat(state.world()
                            //#if MC >= 26.1
                            //$$ .getOverworldClockTime()
                            //#else
                            .getDayTime()
                            //#endif
                    , 24000) + getTickDelta();
        }
    }

    public static float getDayCount() {
        var state = emfState();
        if (state == null || state.world() == null) {
            return 0;
        } else {
            //limit value upper limit to preserve floating point precision
            return (float) (state.world()
                    //#if MC >= 26.1
                    //$$ .getOverworldClockTime()
                    //#else
                    .getDayTime()
                    //#endif
                    / 24000);
        }
    }


    public static float getHealth() {
        var state = emfState();
        if (state == null) return 0;
        return state.emfEntity() instanceof LivingEntity alive ? alive.getHealth() : 1;
    }

    public static float getDeathTime() {
        return emfEntity() instanceof LivingEntity alive ? (alive.deathTime > 0 ? alive.deathTime + getTickDelta() : 0) : 0;
    }

    public static float getAngerTime() {
        var state = emfState();
        if (state == null || !(state.emfEntity() instanceof NeutralMob)) return 0;

        float currentKnownHighest = knownHighestAngerTimeByUUID.getOrDefault(state.uuid(), 0);
        //#if MC >= 12111
        //$$ int angerTime = (int) (((NeutralMob) state.emfEntity()).getPersistentAngerEndTime() - state.world().getGameTime());
        //#else
        int angerTime = ((NeutralMob) state.emfEntity()).getRemainingPersistentAngerTime();
        //#endif

        //clear anger info if anger is over
        if (angerTime <= 0) {
            knownHighestAngerTimeByUUID.put(state.uuid(), 0);
            return 0;
        }

        //store this if this is the largest anger time for the entity seen so far
        if (angerTime > currentKnownHighest) {
            knownHighestAngerTimeByUUID.put(state.uuid(), angerTime);
        }
        return angerTime - getTickDelta();
    }

    public static float getAngerTimeStart() {
        //this only makes sense if we are calculating it here from the largest known value of anger time
        // i could also reset it when anger time hits 0
        var state = emfState();
        return state != null && state.emfEntity() instanceof NeutralMob ? knownHighestAngerTimeByUUID.getOrDefault(state.uuid(), 0) : 0;

    }

    public static float getMaxHealth() {
        return emfEntity() instanceof LivingEntity alive ? alive.getMaxHealth() : 1;
    }

    public static float getId() {
        var state = emfState();
        return state == null || isOnShoulder()
                ? 0
                : Math.abs(state.optifineId()) % WRAP_CONST;
    }

    public static float getHurtTime() {
        return emfEntity() instanceof LivingEntity alive ? (alive.hurtTime > 0 ? alive.hurtTime - getTickDelta() : 0) : 0;
    }

    public static float getHeightAboveGround() {
        var state = emfState();
        if (state == null || !(state.emfEntity() instanceof Entity)) return 0;
        float y = getEntityY();
        BlockPos.MutableBlockPos pos = state.blockPos().mutable();

        int worldBottom =
                //#if MC >=12102
                state.world().getMinY();
                //#else
                //$$ state.world().getMinBuildHeight();
                //#endif

        if (state.isBlockEntity()) // Don't include self
            pos.move(Direction.DOWN);

        //loop down until we hit a block that can be stood on
        while (!state.world().getBlockState(pos)
                .entityCanStandOn(state.world(),pos, (Entity) state.emfEntity())
                && pos.getY() > worldBottom) {
            pos.move(Direction.DOWN);
        }
        return y - pos.getY();
    }

    public static float getFluidDepthDown() {
        var state = emfState();
        if (state == null
                || state.world().getFluidState(state.blockPos()).isEmpty()) return 0;

        BlockPos pos = state.blockPos();
        int worldBottom =
                //#if MC >=12102
                state.world().getMinY();
                //#else
                //$$ state.world().getMinBuildHeight();
                //#endif
        while (!state.world().getFluidState(pos).isEmpty() && pos.getY() > worldBottom) {
            pos = pos.below();
        }
        return state.blockPos().getY() - pos.getY();
    }


    public static float getFluidDepthUp() {
        var state = emfState();
        if (state == null
                || state.world().getFluidState(state.blockPos()).isEmpty()) return 0;

        BlockPos pos = state.blockPos();
        int worldTop =
                //#if MC >=12102
                state.world().getMaxY();
                //#else
                //$$ state.world().getMaxBuildHeight();
                //#endif
        while (!state.world().getFluidState(pos).isEmpty() && pos.getY() < worldTop) {
            pos = pos.above();
        }
        return pos.getY() - state.blockPos().getY();
    }

    public static float getFluidDepth() {
        var state = emfState();
        if (state == null
                || state.world().getFluidState(state.blockPos()).isEmpty()) return 0;
        return getFluidDepthDown() + getFluidDepthUp() - 1;
    }

    public static float getSwingProgress() {
        if (isInGui()) return 0;
        return emfEntity() instanceof LivingEntity alive ? alive.getAttackAnim(getTickDelta()) : 0;
    }


    public static float getAge() {
        var state = emfState();
        if (state == null) {
            return 0 + getTickDelta();
        }else {
            return constrainedFloat(state.age(), WRAP_CONST) + getTickDelta();
        }
    }

    public static float getFrameTime() {
        if (Minecraft.getInstance().isPaused()) return 0;
        //#if MC > 12002
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.tickRateManager().isFrozen()) return 0;
        //#endif

        //#if MC >= 12100
        return ((MinecraftClientAccessor)Minecraft.getInstance())
                //#if MC >= 12102
                .getDeltaTracker()
                //#else
                //$$ .getTimer()
                //#endif
                .getGameTimeDeltaTicks() / 20;
        //#else
        //$$ return Minecraft.getInstance().getDeltaFrameTime() / 20;
        //#endif
    }
    //endregion




    //region state passthrough
    public static boolean isInGui() {
        return EMFState.isInGui;
    }
    public static boolean isInHand() {
        return EMFState.isInHand;
    }
    public static boolean isInItemFrame() {
        return EMFState.isInItemFrame;
    }
    public static boolean isOnHead() {
        return EMFState.isOnHead;
    }

    private static @Nullable EMFEntityRenderState emfState() { return EMFState.state(); }
    private static @Nullable EMFEntity emfEntity() {
        var state = emfState();
        return state == null ? null : state.emfEntity();
    }
    //endregion
}
