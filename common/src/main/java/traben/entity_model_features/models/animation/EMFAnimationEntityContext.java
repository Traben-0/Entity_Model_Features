package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.gen.Accessor;
import traben.entity_model_features.EMF;
import traben.entity_model_features.mixin.accessor.EntityRenderDispatcherAccessor;
import traben.entity_model_features.mixin.accessor.MinecraftClientAccessor;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.EMFModelPartRoot;
import traben.entity_model_features.utils.*;
import traben.entity_texture_features.ETF;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings({"resource", "SameParameterValue", "unused"})
public class EMFAnimationEntityContext {

    private static final Object2IntOpenHashMap<UUID> knownHighestAngerTimeByUUID = new Object2IntOpenHashMap<>() {{
        defaultReturnValue(0);
    }};
    private static final Object2IntOpenHashMap<UUID> lodEntityTimers = new Object2IntOpenHashMap<>();
    public static boolean setInHand = false;
    public static boolean isFirstPersonHand = false;
    public static boolean setInItemFrame = false;
    public static boolean setIsOnHead = false;
    public static double lastFOV = 70;
    public static boolean is_in_ground_override = false;
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
    private static float headYaw = Float.NaN;
    private static float headPitch = Float.NaN;
    private static float tickDelta = 0;
    private static boolean onShoulder = false;
    private static Function<ResourceLocation, RenderType> layerFactory = null;
    private static Boolean lodFrameSkipping = null;
    private static boolean announceModels = false;

    private EMFAnimationEntityContext() {

    }

    public static boolean isJumping() {
        return emfEntity instanceof LivingEntity alive && alive.jumping;
    }


    public static void setEntityVariable(String variable, float value) {
        //if (variable.equals("var.fly")) System.out.println("setEntityVariable: " + variable + " " + (emfEntity != null ? emfEntity.emf$getVariableMap().getOrDefault(variable, value) : "null"));
        if (emfEntity != null) {
            emfEntity.emf$getVariableMap().put(variable, value);
        }
    }

    public static float getEntityVariable(String variable, float defaultValue) {
        //if (variable.equals("var.fly")) System.out.println("getEntityVariable: " + variable + " " + (emfEntity != null ? emfEntity.emf$getVariableMap().getOrDefault(variable, defaultValue) : "null"));
        if (emfEntity == null) return defaultValue;
        return emfEntity.emf$getVariableMap().getOrDefault(variable, defaultValue);
    }

    public static void setLayerFactory(Function<ResourceLocation, RenderType> layerFactory) {
        EMFAnimationEntityContext.layerFactory = layerFactory;
    }

    private static int distanceOfEntityFrom(BlockPos pos) {
        if (emfEntity == null) return 0;
        var blockPos = emfEntity.etf$getBlockPos();
        float f = (float) (blockPos.getX() - pos.getX());
        float g = (float) (blockPos.getY() - pos.getY());
        float h = (float) (blockPos.getZ() - pos.getZ());
        return (int) Mth.sqrt(f * f + g * g + h * h);
    }

    private static int getLODFactorOfEntity() {
        //if (lodFactor != -1) return lodFactor;

        if (EMF.config().getConfig().animationLODDistance == 0) return 0;

        //no factor when using spyglass or player is null
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.isScoping()) {
            return 0;
        }


        int distance = distanceOfEntityFrom(Minecraft.getInstance().player.blockPosition());
        if (distance < 1) return 0;

        int factor = distance / EMF.config().getConfig().animationLODDistance;
        //reduce factor when using zoom mods or lower fov
        int factorByFOV = (int) (factor * lastFOV / 70);

        int lodFactor;
        //factor in low fps detail retention
        if (EMF.config().getConfig().retainDetailOnLowFps && Minecraft.getInstance().getFps() < 59) { // count often drops to 59 while capped at 60 :/
            float fpsPercentageOf60 = Minecraft.getInstance().getFps() / 60f;
            //reduce factor by the percentage of fps below 60 to recover some level of detail
            lodFactor = (int) (factorByFOV * fpsPercentageOf60);
        } else {
            lodFactor = factorByFOV;
        }

        if (EMF.config().getConfig().retainDetailOnLargerMobs && emfEntity instanceof Entity entity) {
            var entitySize = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if (entitySize > 2) {
                lodFactor = (int) (lodFactor / (entitySize / 2));
            }
        }

        return lodFactor;
    }

    public static boolean isLODSkippingThisFrame() {
        if (lodFrameSkipping != null) return lodFrameSkipping;

        //skip for shadow pass
        if(ETF.IRIS_DETECTED
                && IrisShadowPassDetection.getInstance().inShadowPass()
                && EMF.config().getConfig().animationFrameSkipDuringIrisShadowPass) {
            return true;
        }

        if (EMF.config().getConfig().animationLODDistance == 0 || emfEntity == null) return false;

        int lodTimer = lodEntityTimers.getInt(emfEntity.etf$getUuid());
        int lodResult;
        //check lod
        if (lodTimer < 1) {
            lodResult = EMFAnimationEntityContext.getLODFactorOfEntity();
        } else {
            lodResult = lodTimer - 1;
        }
        lodEntityTimers.put(emfEntity.etf$getUuid(), lodResult);
        lodFrameSkipping = lodResult > 0;
        return lodFrameSkipping;
    }

    public static void setCurrentEntityIteration(EMFEntity entityIn) {
        isFirstPersonHand = false;
        EMFManager.getInstance().entityRenderCount++;
        layerFactory = null;

        tickDelta =
        #if MC >= MC_21
                ((MinecraftClientAccessor) Minecraft.getInstance()).getTimer().getGameTimeDeltaPartialTick(true);
        #else
                Minecraft.getInstance().isPaused() ? ((MinecraftClientAccessor) Minecraft.getInstance()).getPausePartialTick() : Minecraft.getInstance().getFrameTime();
        #endif
        shadowSize = Float.NaN;
        shadowOpacity = Float.NaN;
        leashX = 0;
        leashY = 0;
        leashZ = 0;
        shadowX = 0;
        shadowZ = 0;

        newEntity(entityIn);

        if (entityIn != null) {
            //perform variant checking for this entity types models
            //this is the only way to keep it generic and also before the entity is rendered and affect al its models
            Set<Runnable> roots = EMFManager.getInstance().rootPartsPerEntityTypeForVariation.get(entityIn.emf$getTypeString());
            if (roots != null) {
                roots.forEach(Runnable::run);
            }

            //if this entity requires a debug print do it now after models have variated
            if (EMF.config().getConfig().debugOnRightClick
                    && entityIn.etf$getUuid().equals(EMFManager.getInstance().entityForDebugPrint)) {
                announceModels = true;
                EMFManager.getInstance().entityForDebugPrint = null;
            }
        }


        lodFrameSkipping = null;
    }

    public static void anounceModels(EMFEntity assertEntity) {
        String type = assertEntity.emf$getTypeString();
        Set<EMFModelPartRoot> debugRoots = EMFManager.getInstance().rootPartsPerEntityTypeForDebug.get(type);
        EMFUtils.chat("§e-----------EMF Debug Printout-------------§r");
        if (debugRoots == null) {
            EMFUtils.chat(
                    "\n§c§oThe EMF debug printout did not find any custom models registered to the following entity:\n §3§l§u" + type
            );
        } else {
            String message = "\n§2§oThe EMF debug printout found the following custom models for the entity:\n §3§l§u" +
                    type +
                    "§r\n§2§oThis first model is usually the primary model for the entity.";

            EMFUtils.chat(message);

            int count = 1;
            for (EMFModelPartRoot debugRoot :
                    debugRoots) {
                StringBuilder model = new StringBuilder();
                model.append("§eModel #").append(count).append("§r")
                        .append(entryAndValue("name", debugRoot.modelName.getfileName() + ".jem"));
                if (debugRoot.variantDirectoryApplier != null) {
                    model.append(entryAndValue("directory",
                            debugRoot.variantDirectoryApplier
                                    .getThisDirectoryOfFilename(debugRoot.modelName.getNamespace(), debugRoot.modelName.getfileName())));
                }

                if (debugRoot.textureOverride != null) {
                    model.append(entryAndValue("texture_override", debugRoot.textureOverride.toString()));
                }
                if (debugRoot.variantTester != null) {
                    IntSet set = new IntArraySet(debugRoot.allKnownStateVariants.keySet());
                    set.remove(0);
                    model.append(entryAndValue("model_variants", set.toString()))
                            .append(entryAndValue("current_variant", String.valueOf(debugRoot.currentModelVariant)));
                }
                EMFUtils.chat(model + "\n§6 - parts:§r printed in game log only.");

                EMFUtils.log("\n - parts: " + debugRoot.simplePrintChildren(0));

                count++;
            }
        }

        EMFUtils.chat("\n§e----------------------------------------§r");
        if (!EMFManager.getInstance().modelsAnnounced.isEmpty()) {
            String vanillaMessage = "\n§2§oThe EMF debug printout found the following non-custom models for the entity:\n §3§l§u" +
                    type +
                    "§r\n§2§oThis first model is usually the primary model for the entity.";

            EMFUtils.chat(vanillaMessage);
            int count = 1;
            for (OptifineMobNameForFileAndEMFMapId data : EMFManager.getInstance().modelsAnnounced) {
                StringBuilder model = new StringBuilder();
                model.append("\n§Non-Custom Model #").append(count).append("§r")
                        .append(entryAndValue("possible .jem name", data.getDisplayFileName()));

                Map<String, String> map = EMFOptiFinePartNameMappings.getMapOf(data.getMapId(), null);
                if (!map.isEmpty()) {
                    EMFUtils.chat(model + "\n§6 - part names:§r printed in game log only.");
                    StringBuilder parts = new StringBuilder();
                    parts.append("\n - part names: ");
                    map.forEach((k, v) -> parts.append("\n   | - [").append(k).append(']'));

                    EMFUtils.log(parts.toString());
                } else {
                    EMFUtils.chat(model.toString());
                    EMFUtils.log(" - part names: could not be found. use the 'printout unknown models' setting instead.");
                }
            }
            EMFUtils.chat("\n§e----------------------------------------§r");
            EMFManager.getInstance().modelsAnnounced.clear();
        }

        announceModels = false;
    }

    public static boolean doAnnounceModels() {
        return announceModels;
    }

    private static String entryAndValue(String entry, String value) {
        return "\n§6 - " + entry + ":§r " + value;
    }

    public static void setCurrentEntityNoIteration(EMFEntity entityIn) {
        newEntity(entityIn);
    }

    private static void newEntity(EMFEntity entityIn) {
        emfEntity = entityIn;

        limbAngle = Float.NaN;
        limbDistance = Float.NaN;
        headYaw = Float.NaN;
        headPitch = Float.NaN;

        onShoulder = false;
    }

    public static void reset() {
        isFirstPersonHand = false;
        layerFactory = null;
        emfEntity = null;
        limbAngle = Float.NaN;
        limbDistance = Float.NaN;
        headYaw = Float.NaN;
        headPitch = Float.NaN;
        onShoulder = false;
        tickDelta = 0;
        shadowSize = Float.NaN;
        shadowOpacity = Float.NaN;
        leashX = 0;
        leashY = 0;
        leashZ = 0;
        shadowX = 0;
        shadowZ = 0;
        lodFrameSkipping = null;
    }

    public static RenderType getLayerFromRecentFactoryOrETFOverrideOrTranslucent(ResourceLocation identifier) {
        if (layerFactory == null) {
            var layer = ETF.config().getConfig().getRenderLayerOverride();
            if (layer == null) {
                return RenderType.entityTranslucent(identifier);
            } else {
                return switch (layer) {
                    case TRANSLUCENT -> RenderType.entityTranslucent(identifier);
                    case TRANSLUCENT_CULL -> RenderType.entityTranslucentCull(identifier);
                    case END -> RenderType.endGateway();
                    case OUTLINE -> RenderType.outline(identifier);
                };
            }
        }
        return layerFactory.apply(identifier);
    }

    public static float getRuleIndex() {
        if (emfEntity == null) return 0;
        return EMFManager.getInstance().lastModelRuleOfEntity.getInt(emfEntity.etf$getUuid());
    }

    public static EMFEntity getEMFEntity() {
        return emfEntity;
    }

    public static float getDimension() {
        if (emfEntity == null || emfEntity.etf$getWorld() == null) {
            return 0;
        } else {
            var optional = emfEntity.etf$getWorld().dimensionTypeRegistration().unwrapKey();
            if (optional.isEmpty()) return 0;
            ResourceLocation id = optional.get().location();
            if (id.equals(BuiltinDimensionTypes.NETHER_EFFECTS)) {
                return -1;
            } else if (id.equals(BuiltinDimensionTypes.END_EFFECTS)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static float getPlayerX() {
        return Minecraft.getInstance().player == null ? 0 : (float) Mth.lerp(tickDelta, Minecraft.getInstance().player.xo, Minecraft.getInstance().player.getX());
    }

    public static float getPlayerY() {
        return Minecraft.getInstance().player == null ? 0 : (float) Mth.lerp(tickDelta, Minecraft.getInstance().player.yo, Minecraft.getInstance().player.getY());
    }

    public static float getPlayerZ() {
        return Minecraft.getInstance().player == null ? 0 : (float) Mth.lerp(tickDelta, Minecraft.getInstance().player.zo, Minecraft.getInstance().player.getZ());
    }

    public static float getPlayerRX() {
        return (Minecraft.getInstance().player == null) ? 0 :
                (float) Math.toRadians(Mth.rotLerp(tickDelta, Minecraft.getInstance().player.xRotO, Minecraft.getInstance().player.getXRot()));
    }

    public static float getPlayerRY() {
        return (Minecraft.getInstance().player == null) ? 0 :
                (float) Math.toRadians(Mth.rotLerp(tickDelta, Minecraft.getInstance().player.yRotO, Minecraft.getInstance().player.getYRot()));
    }

    public static float getEntityX() {
        return emfEntity == null ? 0 : (float) Mth.lerp(getTickDelta(), emfEntity.emf$prevX(), emfEntity.emf$getX());
    }

    public static float getEntityY() {
        return emfEntity == null ? 0 :
                //(double) entity.getY();
                (float) Mth.lerp(getTickDelta(), emfEntity.emf$prevY(), emfEntity.emf$getY());
    }

    public static float getEntityZ() {
        return emfEntity == null ? 0 : (float) Mth.lerp(getTickDelta(), emfEntity.emf$prevZ(), emfEntity.emf$getZ());
    }

    public static float getEntityRX() {
        return (emfEntity == null) ? 0 :
                //(double) Math.toRadians(entity.getPitch(tickDelta));
                (float) Math.toRadians(Mth.rotLerp(tickDelta, emfEntity.emf$prevPitch(), emfEntity.emf$getPitch()));
    }

    public static float getEntityRY() {
        if (emfEntity == null) return 0;
        return (emfEntity instanceof LivingEntity alive) ?
                (float) Math.toRadians(Mth.rotLerp(tickDelta, alive.yBodyRotO, alive.getVisualRotationYInDegrees())) :
                emfEntity instanceof Entity entity ?
                        (float) Math.toRadians(Mth.rotLerp(tickDelta, entity.yRotO, entity.getYRot()))
                        : 0;
    }

    public static float getTime() {
        if (emfEntity == null || emfEntity.etf$getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            long upTimeInTicks = emfEntity.etf$getWorld().getGameTime(); // (System.currentTimeMillis() - START_TIME)/50;
            return constrainedFloat(upTimeInTicks, 720720) + tickDelta;
        }
    }

    public static float getDayTime() {
        if (emfEntity == null || emfEntity.etf$getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            return constrainedFloat(emfEntity.etf$getWorld().getDayTime(), 24000) + tickDelta;
        }
    }

    public static float getDayCount() {
        if (emfEntity == null || emfEntity.etf$getWorld() == null) {
            return 0 + tickDelta;
        } else {
            //limit value upper limit to preserve floating point precision
            return (float) (emfEntity.etf$getWorld().getDayTime() / 24000L) + tickDelta;
        }
    }


    public static float getHealth() {
        if (emfEntity == null) return 0;
        return emfEntity instanceof LivingEntity alive ? alive.getHealth() : 1;
    }

    public static float getDeathTime() {
        return emfEntity instanceof LivingEntity alive ? alive.deathTime : 0;
    }

    public static float getAngerTime() {
        if (!(emfEntity instanceof NeutralMob)) return 0;

        float currentKnownHighest = knownHighestAngerTimeByUUID.getInt(emfEntity.etf$getUuid());
        int angerTime = ((NeutralMob) emfEntity).getRemainingPersistentAngerTime();

        //clear anger info if anger is over
        if (angerTime <= 0) {
            knownHighestAngerTimeByUUID.put(emfEntity.etf$getUuid(), 0);
            return 0;
        }

        //store this if this is the largest anger time for the entity seen so far
        if (angerTime > currentKnownHighest) {
            knownHighestAngerTimeByUUID.put(emfEntity.etf$getUuid(), angerTime);
        }
        return angerTime - tickDelta;
    }

    public static float getAngerTimeStart() {
        //this only makes sense if we are calculating it here from the largest known value of anger time
        // i could also reset it when anger time hits 0
        return emfEntity instanceof NeutralMob ? knownHighestAngerTimeByUUID.getInt(emfEntity.etf$getUuid()) : 0;

    }

    public static float getMaxHealth() {
        return emfEntity instanceof LivingEntity alive ? alive.getMaxHealth() : 1;
    }

    public static float getId() {
        return emfEntity == null ? 0 : emfEntity.etf$getUuid().hashCode();
    }

    public static float getHurtTime() {
        return emfEntity instanceof LivingEntity alive ? (alive.hurtTime > 0 ? alive.hurtTime - tickDelta : 0) : 0;
    }

    public static float getHeightAboveGround() {
        if (emfEntity == null) return 0;
        float y = getEntityY();
        BlockPos pos = emfEntity.etf$getBlockPos();
        int worldBottom = emfEntity.etf$getWorld().getMinBuildHeight();
        while (!emfEntity.etf$getWorld().getBlockState(pos).getBlock().hasCollision && pos.getY() > worldBottom) {
            pos = pos.below();
        }
        return y - pos.getY();
    }

    public static float getFluidDepthDown() {
        if (emfEntity == null
                || emfEntity.etf$getWorld().getFluidState(emfEntity.etf$getBlockPos()).isEmpty()) return 0;

        BlockPos pos = emfEntity.etf$getBlockPos();
        int worldBottom = emfEntity.etf$getWorld().getMinBuildHeight();
        while (!emfEntity.etf$getWorld().getFluidState(pos).isEmpty() && pos.getY() > worldBottom) {
            pos = pos.below();
        }
        return emfEntity.etf$getBlockPos().getY() - pos.getY();
    }

    public static float getFluidDepthUp() {
        if (emfEntity == null
                || emfEntity.etf$getWorld().getFluidState(emfEntity.etf$getBlockPos()).isEmpty()) return 0;

        BlockPos pos = emfEntity.etf$getBlockPos();
        int worldTop = emfEntity.etf$getWorld().getMaxBuildHeight();
        while (!emfEntity.etf$getWorld().getFluidState(pos).isEmpty() && pos.getY() < worldTop) {
            pos = pos.above();
        }
        return pos.getY() - emfEntity.etf$getBlockPos().getY();
    }

    public static float getFluidDepth() {
        if (emfEntity == null
                || emfEntity.etf$getWorld().getFluidState(emfEntity.etf$getBlockPos()).isEmpty()) return 0;
        return getFluidDepthDown() + getFluidDepthUp() - 1;
    }

    public static boolean isInWater() {
        return emfEntity != null && emfEntity.emf$isTouchingWater();
    }

    public static boolean isBurning() {
        return emfEntity != null && emfEntity.emf$isOnFire();
    }

    public static boolean isRiding() {
        return emfEntity != null && emfEntity.emf$hasVehicle();
    }

    public static boolean isChild() {
        return emfEntity instanceof LivingEntity alive && alive.isBaby();
    }

    public static boolean isOnGround() {
        return emfEntity != null && emfEntity.emf$isOnGround();
    }

    public static boolean isClimbing() {
        return emfEntity instanceof LivingEntity alive && alive.onClimbable();
    }

    public static boolean isAlive() {
        if (emfEntity == null) return false;
        return emfEntity.emf$isAlive();
    }

    public static boolean isUsingHand(boolean right) {
        if (emfEntity == null) return false;
        if (emfEntity instanceof LivingEntity entity) {
            if(!entity.isUsingItem()) return false;

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

    public static boolean isAggressive() {
        return emfEntity instanceof Mob mob && mob.isAggressive();
    }

    public static boolean isGlowing() {
        return emfEntity != null && emfEntity.emf$isGlowing();
    }

    public static boolean isHurt() {
        return emfEntity instanceof LivingEntity alive && alive.hurtTime > 0;
    }

    public static boolean isInHand() {
        return setInHand;
    }

    public static boolean isInItemFrame() {
        return setInItemFrame;
    }

    public static boolean isInGround() {
        return is_in_ground_override || emfEntity instanceof Projectile proj && proj.isInWall();
    }

    public static boolean isInGui() {
        //basing this almost solely on the fact that the inventory screen sets the shadow render flag to false during its render,
        // I assume other gui renderers will follow suit, I'm not sure if other things affect it, it doesn't seem tied to the option setting
        return Minecraft.getInstance().screen != null
                && !((EntityRenderDispatcherAccessor) Minecraft.getInstance().getEntityRenderDispatcher()).isShouldRenderShadow();

    }

    public static boolean isInLava() {
        return emfEntity != null && emfEntity.emf$isInLava();
    }

    public static boolean isInvisible() {
        return emfEntity != null && emfEntity.emf$isInvisible();
    }

    public static boolean isOnHead() {
        return setIsOnHead;
    }

    public static boolean isOnShoulder() {
        return onShoulder;
    }

    public static void setCurrentEntityOnShoulder() {
        onShoulder = true;
    }

    public static boolean isRidden() {
        return emfEntity != null && emfEntity.emf$hasPassengers();
    }

    public static boolean isSitting() {
        if (emfEntity == null) return false;
        return (emfEntity instanceof TamableAnimal tame && tame.isInSittingPose()) ||
                (emfEntity instanceof Fox fox && fox.isSitting()) ||
                (emfEntity instanceof Parrot parrot && parrot.isInSittingPose()) ||
                (emfEntity instanceof Cat cat && cat.isInSittingPose()) ||
                (emfEntity instanceof Wolf wolf && wolf.isInSittingPose()) ||
                (emfEntity instanceof Camel camel && camel.isCamelSitting());
    }


    public static boolean isSneaking() {
        return emfEntity != null && emfEntity.emf$isSneaking();
    }

    public static boolean isSprinting() {
        return emfEntity != null && emfEntity.emf$isSprinting();
    }

    public static boolean isTamed() {
        return emfEntity instanceof TamableAnimal tame && tame.isTame();
    }

    public static boolean isWet() {
        return emfEntity != null && emfEntity.emf$isWet();
    }

    public static float getSwingProgress() {
        return emfEntity instanceof LivingEntity alive ? alive.getAttackAnim(tickDelta) : 0;
    }

    public static float getAge() {
        if (emfEntity == null) {
            return 0 + tickDelta;
        }
        return constrainedFloat(emfEntity.emf$age(), 24000) + tickDelta;
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
    #if MC >= MC_21
        return ((MinecraftClientAccessor)Minecraft.getInstance()).getTimer().getGameTimeDeltaTicks() / 20;
    #else
        return Minecraft.getInstance().getDeltaFrameTime() / 20;
    #endif
    }

    public static float getLimbAngle() {//limb_swing
        if (emfEntity == null) return 0;
        if (Float.isNaN(limbAngle)) {
            doLimbValues();
        }
        return limbAngle;
    }

    public static void setLimbAngle(float limbAngle) {
        EMFAnimationEntityContext.limbAngle = limbAngle;
    }

    public static float getLimbDistance() {//limb_speed
        if (emfEntity == null) return 0;
        if (Float.isNaN(limbDistance)) {
            doLimbValues();
        }
        return limbDistance == Float.MIN_VALUE ? 0 : limbDistance;
    }

    public static void setLimbDistance(float limbDistance) {
        EMFAnimationEntityContext.limbDistance = limbDistance;
    }

    private static void doLimbValues() {
        float o = 0;
        float n = 0;
        if (!emfEntity.emf$hasVehicle() && emfEntity instanceof LivingEntity alive) {
            o = alive.walkAnimation.position(tickDelta);
            n = alive.walkAnimation.speed(tickDelta);
            if (alive.isBaby()) {
                o *= 3.0F;

            }
            if (n > 1.0F) {
                n = 1.0F;
            }
        } else if (emfEntity instanceof Minecart) {
            n = 1;
            o = -(getEntityX() + getEntityZ());
        } else if (emfEntity instanceof Boat boat) {
            n = 1;
            //o = boat.interpolatePaddlePhase(0, tickDelta);//1);
            o = Math.max(boat.getRowingTime(1, tickDelta), boat.getRowingTime(0, tickDelta));
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
        EMFAnimationEntityContext.headYaw = headYaw;
    }

    public static float getHeadPitch() {
        if (emfEntity == null) return 0;
        if (Float.isNaN(headPitch)) {
            doHeadValues();
        }
        return headPitch;
    }

    public static void setHeadPitch(float headPitch) {
        EMFAnimationEntityContext.headPitch = headPitch;
    }

    private static void doHeadValues() {
        if (emfEntity instanceof LivingEntity livingEntity) {
            float h = Mth.rotLerp(tickDelta, livingEntity.yBodyRotO, livingEntity.yBodyRot);
            float j = Mth.rotLerp(tickDelta, livingEntity.yHeadRotO, livingEntity.yHeadRot);
            float k = j - h;
            float l;
            if (livingEntity.isPassenger() && livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
                h = Mth.rotLerp(tickDelta, livingEntity2.yBodyRotO, livingEntity2.yBodyRot);
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

            float m = Mth.lerp(tickDelta, livingEntity.xRotO, livingEntity.getXRot());
            if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
                m *= -1.0F;
                k *= -1.0F;
            }
            headPitch = m;
            //headYaw = k;
            //constrain head yaw amount
            if (k >= 180 || k < -180) {
                headYaw = Mth.wrapDegrees(k);
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
        double lookDir = Math.toRadians(90 - emfEntity.emf$getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3 velocity = emfEntity.emf$getVelocity();

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
        double lookDir = Math.toRadians(90 - emfEntity.emf$getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3 velocity = emfEntity.emf$getVelocity();

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
        EMFAnimationEntityContext.shadowSize = shadowSize;
    }

    public static float getShadowOpacity() {
        return shadowOpacity;
    }

    public static void setShadowOpacity(float shadowOpacity) {
        EMFAnimationEntityContext.shadowOpacity = shadowOpacity;
    }

    public static float getLeashX() {
        return leashX;
    }

    public static void setLeashX(float leashX) {
        EMFAnimationEntityContext.leashX = leashX;
    }

    public static float getLeashY() {
        return leashY;
    }

    public static void setLeashY(float leashY) {
        EMFAnimationEntityContext.leashY = leashY;
    }

    public static float getLeashZ() {
        return leashZ;
    }

    public static void setLeashZ(float leashZ) {
        EMFAnimationEntityContext.leashZ = leashZ;
    }

    public static float getShadowX() {
        return shadowX;
    }

    public static void setShadowX(float shadowX) {
        EMFAnimationEntityContext.shadowX = shadowX;
    }

    public static float getShadowZ() {
        return shadowZ;
    }

    public static void setShadowZ(float shadowZ) {
        EMFAnimationEntityContext.shadowZ = shadowZ;
    }


}
