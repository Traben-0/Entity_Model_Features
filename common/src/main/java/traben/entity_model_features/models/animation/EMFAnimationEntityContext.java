package traben.entity_model_features.models.animation;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
#if MC > MC_21
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.vehicle.AbstractBoat;
#endif
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
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.mixin.accessor.EntityRenderDispatcherAccessor;
import traben.entity_model_features.mixin.accessor.MinecraftClientAccessor;
import traben.entity_model_features.mod_compat.IrisShadowPassDetection;
import traben.entity_model_features.models.EMFModelMappings;
import traben.entity_model_features.models.EMFModel_ID;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.*;
import traben.entity_texture_features.ETF;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings({ "SameParameterValue", "unused"})
public final class EMFAnimationEntityContext {

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
    public static ObjectSet<UUID> entitiesToForceVanillaModel = new ObjectOpenHashSet<>();
    private static @Nullable EMFEntity IEMFEntity = null;
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
    private static boolean onShoulder = false;
    private static Function<ResourceLocation, RenderType> layerFactory = null;
    private static Boolean lodFrameSkipping = null;
    private static boolean announceModels = false;
    private static float frameCounter = 0;

    public static Object2ObjectOpenHashMap<UUID, ModelPart[]> entitiesPausedParts = new Object2ObjectOpenHashMap<>();
    public static ObjectSet<UUID> entitiesPaused = new ObjectOpenHashSet<>();

    public static boolean isEntityAnimPaused(){
        if (IEMFEntity == null) return false;
        return entitiesPaused.contains(IEMFEntity.etf$getUuid());
    }

    public static @Nullable ModelPart[] getEntityPartsAnimPaused(){
        if (IEMFEntity == null) return null;
        var parts = entitiesPausedParts.get(IEMFEntity.etf$getUuid());
        return parts == null || parts.length == 0 ? null : parts;
    }

    private EMFAnimationEntityContext() {

    }

    public static void incFrameCount(){
        //not 100% certain if the shadow pass passes through this method, I highly doubt it but just in case
        if(IrisShadowPassDetection.getInstance().inShadowPass()) return;

        float inc = frameCounter + 1;
        //reset counter after exceeding floating point precision cutoff
        frameCounter = inc > 27719 ? 0 : inc;
    }

    public static float getFrameCounter(){
        return frameCounter;
    }

    public static boolean isJumping() {
        return IEMFEntity instanceof LivingEntity alive && alive.jumping;
    }


    public static void setEntityVariable(String variable, float value) {
        //if (variable.equals("var.fly")) System.out.println("setEntityVariable: " + variable + " " + (emfEntity != null ? emfEntity.emf$getVariableMap().getOrDefault(variable, value) : "null"));
        if (IEMFEntity != null) {
            IEMFEntity.emf$getVariableMap().put(variable, value);
        }
    }

    public static float getEntityVariable(String variable, float defaultValue) {
        //if (variable.equals("var.fly")) System.out.println("getEntityVariable: " + variable + " " + (emfEntity != null ? emfEntity.emf$getVariableMap().getOrDefault(variable, defaultValue) : "null"));
        if (IEMFEntity == null) return defaultValue;
        return IEMFEntity.emf$getVariableMap().getOrDefault(variable, defaultValue);
    }

    public static void setLayerFactory(Function<ResourceLocation, RenderType> layerFactory) {
        EMFAnimationEntityContext.layerFactory = layerFactory;
    }

    private static int distanceOfEntityFrom(BlockPos pos) {
        if (IEMFEntity == null) return 0;
        var blockPos = IEMFEntity.etf$getBlockPos();
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

        if (EMF.config().getConfig().retainDetailOnLargerMobs && IEMFEntity instanceof Entity entity) {
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
                && EMF.config().getConfig().animationFrameSkipDuringIrisShadowPass
                //not client player in first person
                && !(EMFAnimationEntityContext.getEMFEntity() instanceof Player player
                    && player.isLocalPlayer()
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson())) {
            return true;
        }

        if (EMF.config().getConfig().animationLODDistance == 0 || IEMFEntity == null) return false;

        int lodTimer = lodEntityTimers.getInt(IEMFEntity.etf$getUuid());
        int lodResult;
        //check lod
        if (lodTimer < 1) {
            lodResult = EMFAnimationEntityContext.getLODFactorOfEntity();
        } else {
            lodResult = lodTimer - 1;
        }
        lodEntityTimers.put(IEMFEntity.etf$getUuid(), lodResult);
        //intellij requires it for certain versions :/
        //noinspection RedundantCast
        lodFrameSkipping = (Boolean) (lodResult > 0);
        return lodFrameSkipping;
    }


    #if MC > MC_21
    private static EntityRenderState entityRenderState = null;

    public static EntityRenderState getEntityRenderState() {
        return entityRenderState;
    }
    #endif

    public static void setCurrentEntityIteration(EMFEntity entityIn #if MC > MC_21 , EntityRenderState state #endif) {

        #if MC > MC_21
        entityRenderState = state;



        #endif

        EMFAttachments.closeBoth();
        isFirstPersonHand = false;
        EMFManager.getInstance().entityRenderCount++;
        layerFactory = null;


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
            Set<EMFModelPartRoot> roots = EMFManager.getInstance().rootPartsPerEntityTypeForVariation.get(entityIn.emf$getTypeString());
            if (roots != null) {
                if (isEntityForcedToVanillaModel()) {
                    roots.forEach((root) -> root.setVariantStateTo(0));
                } else {
                    roots.forEach(EMFModelPartRoot::doVariantCheck);
                }

                if(entityIn instanceof Player player && EMF.config().getConfig().resetPlayerModelEachRender){
                    roots.forEach(EMFModelPartRoot::resetVanillaPartsToDefaults);
                }
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
                if (debugRoot.directoryContext != null) {
                    model.append(entryAndValue("directory",
                            debugRoot.directoryContext
                                    .getRelativeDirectoryLocationNoValidation(debugRoot.modelName.getfileName())));
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
            for (EMFModel_ID data : EMFManager.getInstance().modelsAnnounced) {
                var second = data.getNextFallbackModel();
                StringBuilder model = new StringBuilder();
                model.append("\n§Non-Custom Model #").append(count).append("§r")
                        .append(entryAndValue("possible .jem name", data.getDisplayFileName()));
                if(second != null){
                    model.append(entryAndValue("possible secondary .jem name", second.getDisplayFileName()));
                }
                Map<String, String> map = EMFModelMappings.getMapOf(data, null);
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

//todo check    public static void setCurrentEntityNoIteration(EMFEntity entityIn) {
//        newEntity(entityIn);
//    }

    private static void newEntity(EMFEntity entityIn) {
        IEMFEntity = entityIn;

        #if MC > MC_21
        if (entityRenderState instanceof LivingEntityRenderState livingEntityRenderState) {
            limbAngle = livingEntityRenderState.walkAnimationPos;
            limbDistance = livingEntityRenderState.walkAnimationSpeed;
            headYaw = livingEntityRenderState.yRot;
            if (headYaw >= 180 || headYaw < -180) {
                headYaw = (Mth.wrapDegrees(headYaw));
            }
            headPitch = livingEntityRenderState.xRot;
        }else{//block entity
            limbAngle = Float.NaN;
            limbDistance = Float.NaN;
            headYaw = Float.NaN;
            headPitch = Float.NaN;
        }
        #else
        limbAngle = Float.NaN;
        limbDistance = Float.NaN;
        headYaw = Float.NaN;
        headPitch = Float.NaN;
        #endif

//        age = Float.NaN;

        onShoulder = false;
    }

//    public static void setAge(final float age) {
//        EMFAnimationEntityContext.age = age;
//    }

    public static void globalReset(){
        reset();
        frameCounter = 0;
    }

    public static void reset() {
        isFirstPersonHand = false;
        layerFactory = null;
        IEMFEntity = null;
        limbAngle = Float.NaN;
        limbDistance = Float.NaN;
        headYaw = Float.NaN;
        headPitch = Float.NaN;
//        age = Float.NaN;
        onShoulder = false;
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
                    case TRANSLUCENT_CULL ->
                            #if MC > MC_21
                            RenderType.entityTranslucent(identifier);
                            #else
                            RenderType.entityTranslucentCull(identifier);
                            #endif
                    case END -> RenderType.endGateway();
                    case OUTLINE -> RenderType.outline(identifier);
                };
            }
        }
        return layerFactory.apply(identifier);
    }

    public static float getRuleIndex() {
        if (IEMFEntity == null) return 0;
        return EMFManager.getInstance().lastModelRuleOfEntity.getInt(IEMFEntity.etf$getUuid());
    }

    public static boolean isEntityForcedToVanillaModel(){
        if (IEMFEntity == null) return false;
        if (entitiesToForceVanillaModel.contains(IEMFEntity.etf$getUuid())) return true;
        return EMF.config().getConfig().onlyClientPlayerModel
                && EMFAnimationEntityContext.getEMFEntity() instanceof Player player && !player.isLocalPlayer();
    }

    public static @Nullable EMFEntity getEMFEntity() {
        return IEMFEntity;
    }

    public static float getDimension() {
        if (IEMFEntity == null || IEMFEntity.etf$getWorld() == null) {
            return 0;
        } else {
            var optional = IEMFEntity.etf$getWorld().dimensionTypeRegistration().unwrapKey();
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

    public static float getEntityX() {
        return IEMFEntity == null ? 0 : (float) Mth.lerp(getTickDelta(), IEMFEntity.emf$prevX(), IEMFEntity.emf$getX());
    }

    public static float getEntityY() {
        return IEMFEntity == null ? 0 :
                //(double) entity.getY();
                (float) Mth.lerp(getTickDelta(), IEMFEntity.emf$prevY(), IEMFEntity.emf$getY());
    }

    public static float getEntityZ() {
        return IEMFEntity == null ? 0 : (float) Mth.lerp(getTickDelta(), IEMFEntity.emf$prevZ(), IEMFEntity.emf$getZ());
    }

    public static float getEntityRX() {
        return (IEMFEntity == null) ? 0 :
                //(double) Math.toRadians(entity.getPitch(tickDelta));
                (float) Math.toRadians(Mth.rotLerp(getTickDelta(), IEMFEntity.emf$prevPitch(), IEMFEntity.emf$getPitch()));
    }

    public static float getEntityRY() {
        if (IEMFEntity == null) return 0;
        return (IEMFEntity instanceof LivingEntity alive) ?
                (float) Math.toRadians(Mth.rotLerp(getTickDelta(), alive.yBodyRotO, alive.yBodyRot)) :
                IEMFEntity instanceof Entity entity ?
                        (float) Math.toRadians(Mth.rotLerp(getTickDelta(), entity.yRotO, entity.yRot))
                        : 0;
    }

    public static float getTime() {
        if (IEMFEntity == null || IEMFEntity.etf$getWorld() == null) {
            return 0 + getTickDelta();
        } else {
            //limit value upper limit to preserve floating point precision
            long upTimeInTicks = IEMFEntity.etf$getWorld().getGameTime(); // (System.currentTimeMillis() - START_TIME)/50;
            return constrainedFloat(upTimeInTicks, 27720) + getTickDelta();
        }
    }

    public static float getDayTime() {
        if (IEMFEntity == null || IEMFEntity.etf$getWorld() == null) {
            return 0 + getTickDelta();
        } else {
            //limit value upper limit to preserve floating point precision
            return constrainedFloat(IEMFEntity.etf$getWorld().getDayTime(), 31415) + getTickDelta();
        }
    }

    public static float getDayCount() {
        if (IEMFEntity == null || IEMFEntity.etf$getWorld() == null) {
            return 0 + getTickDelta();
        } else {
            //limit value upper limit to preserve floating point precision
            return (float) (IEMFEntity.etf$getWorld().getDayTime() / 27720L);
        }
    }


    public static float getHealth() {
        if (IEMFEntity == null) return 0;
        return IEMFEntity instanceof LivingEntity alive ? alive.getHealth() : 1;
    }

    public static float getDeathTime() {
        return IEMFEntity instanceof LivingEntity alive ? (alive.deathTime > 0 ? alive.deathTime + getTickDelta() : 0) : 0;
    }

    public static float getAngerTime() {
        if (!(IEMFEntity instanceof NeutralMob)) return 0;

        float currentKnownHighest = knownHighestAngerTimeByUUID.getInt(IEMFEntity.etf$getUuid());
        int angerTime = ((NeutralMob) IEMFEntity).getRemainingPersistentAngerTime();

        //clear anger info if anger is over
        if (angerTime <= 0) {
            knownHighestAngerTimeByUUID.put(IEMFEntity.etf$getUuid(), 0);
            return 0;
        }

        //store this if this is the largest anger time for the entity seen so far
        if (angerTime > currentKnownHighest) {
            knownHighestAngerTimeByUUID.put(IEMFEntity.etf$getUuid(), angerTime);
        }
        return angerTime - getTickDelta();
    }

    public static float getAngerTimeStart() {
        //this only makes sense if we are calculating it here from the largest known value of anger time
        // i could also reset it when anger time hits 0
        return IEMFEntity instanceof NeutralMob ? knownHighestAngerTimeByUUID.getInt(IEMFEntity.etf$getUuid()) : 0;

    }

    public static float getMaxHealth() {
        return IEMFEntity instanceof LivingEntity alive ? alive.getMaxHealth() : 1;
    }

    public static float getId() {
        return IEMFEntity == null ? 0 : Math.abs(IEMFEntity.etf$getOptifineId()) % 27720 ;
    }

    public static float getHurtTime() {
        return IEMFEntity instanceof LivingEntity alive ? (alive.hurtTime > 0 ? alive.hurtTime - getTickDelta() : 0) : 0;
    }

    public static float getHeightAboveGround() {
        if (!(IEMFEntity instanceof Entity)) return 0;
        float y = getEntityY();
        BlockPos pos = IEMFEntity.etf$getBlockPos();
        int worldBottom =
                #if MC > MC_21
                IEMFEntity.etf$getWorld().getMinY();
                #else
                IEMFEntity.etf$getWorld().getMinBuildHeight();
                #endif

        //loop down until we hit a block that can be stood on
        while (!IEMFEntity.etf$getWorld().getBlockState(pos)
                .entityCanStandOn(IEMFEntity.etf$getWorld(),pos, (Entity) IEMFEntity)
                && pos.getY() > worldBottom) {
            pos = pos.below();
        }
        return y - pos.getY();
    }

    public static float getFluidDepthDown() {
        if (IEMFEntity == null
                || IEMFEntity.etf$getWorld().getFluidState(IEMFEntity.etf$getBlockPos()).isEmpty()) return 0;

        BlockPos pos = IEMFEntity.etf$getBlockPos();
        int worldBottom =
                #if MC > MC_21
                IEMFEntity.etf$getWorld().getMinY();
                #else
                IEMFEntity.etf$getWorld().getMinBuildHeight();
                #endif
        while (!IEMFEntity.etf$getWorld().getFluidState(pos).isEmpty() && pos.getY() > worldBottom) {
            pos = pos.below();
        }
        return IEMFEntity.etf$getBlockPos().getY() - pos.getY();
    }


    public static float getFluidDepthUp() {
        if (IEMFEntity == null
                || IEMFEntity.etf$getWorld().getFluidState(IEMFEntity.etf$getBlockPos()).isEmpty()) return 0;

        BlockPos pos = IEMFEntity.etf$getBlockPos();
        int worldTop =
                #if MC > MC_21
                IEMFEntity.etf$getWorld().getMaxY();
                #else
                IEMFEntity.etf$getWorld().getMaxBuildHeight();
                #endif
        while (!IEMFEntity.etf$getWorld().getFluidState(pos).isEmpty() && pos.getY() < worldTop) {
            pos = pos.above();
        }
        return pos.getY() - IEMFEntity.etf$getBlockPos().getY();
    }

    public static float getFluidDepth() {
        if (IEMFEntity == null
                || IEMFEntity.etf$getWorld().getFluidState(IEMFEntity.etf$getBlockPos()).isEmpty()) return 0;
        return getFluidDepthDown() + getFluidDepthUp() - 1;
    }

    public static boolean isInWater() {
        return IEMFEntity != null && IEMFEntity.emf$isTouchingWater();
    }

    public static boolean isBurning() {
        return IEMFEntity != null && IEMFEntity.emf$isOnFire();
    }

    public static boolean isRiding() {
        return IEMFEntity != null && IEMFEntity.emf$hasVehicle();
    }

    public static boolean isChild() {
        return IEMFEntity instanceof LivingEntity alive && alive.isBaby();
    }

    public static boolean isOnGround() {
        return IEMFEntity != null && IEMFEntity.emf$isOnGround();
    }

    public static boolean isClimbing() {
        return IEMFEntity instanceof LivingEntity alive && alive.onClimbable();
    }

    public static boolean isAlive() {
        if (IEMFEntity == null) return false;
        return IEMFEntity.emf$isAlive();
    }

    public static boolean isUsingItem() {
        if (IEMFEntity == null) return false;
        if (IEMFEntity instanceof LivingEntity entity) {
            return entity.isUsingItem();
        }
        return false;
    }

    public static boolean isSwingingArm(boolean right) {
        if (IEMFEntity == null) return false;
        if (getSwingProgress() == 0 && !isUsingItem()) return false;
        if (IEMFEntity instanceof LivingEntity entity) {
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
        if (IEMFEntity == null) return false;
        if (IEMFEntity instanceof LivingEntity entity) {
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
        if (IEMFEntity == null) return false;

        if (IEMFEntity instanceof final EnderMan enderman) {
            return enderman.isCreepy();
        }
        if (IEMFEntity instanceof final Blaze blaze) {
            return blaze.isOnFire();
        }
        if (IEMFEntity instanceof final Guardian guardian) {
            return guardian.getActiveAttackTarget() != null;
        }
        if (IEMFEntity instanceof final Vindicator vindicator) {
            return vindicator.isAggressive();
        }
        if (IEMFEntity instanceof final SpellcasterIllager caster) {
            return caster.isCastingSpell();
        }
        if (IEMFEntity instanceof final Vex vex) {
            return vex.isCharging();
        }

        //these ones can fallback just incase the specific method doesn't sync for clients for modded mobs
        if (IEMFEntity instanceof final NeutralMob angry && angry.isAngry()) {
            return true;
        }
        if (IEMFEntity instanceof Targeting targets && targets.getTarget() != null) {
            return true;
        }
        return IEMFEntity instanceof Mob mob && mob.isAggressive();
    }

    public static boolean isGlowing() {
        return IEMFEntity != null && IEMFEntity.emf$isGlowing();
    }

    public static boolean isHurt() {
        return IEMFEntity instanceof LivingEntity alive && alive.hurtTime > 0;
    }

    public static boolean isInHand() {
        return setInHand;
    }

    public static boolean isInItemFrame() {
        return setInItemFrame;
    }

    public static boolean isInGround() {
        return is_in_ground_override || IEMFEntity instanceof Projectile proj && proj.isInWall();
    }

    public static boolean isInGui() {
        //basing this almost solely on the fact that the inventory screen sets the shadow render flag to false during its render,
        // I assume other gui renderers will follow suit, I'm not sure if other things affect it, it doesn't seem tied to the option setting
        return Minecraft.getInstance().screen != null
                && !((EntityRenderDispatcherAccessor) Minecraft.getInstance().getEntityRenderDispatcher()).isShouldRenderShadow();

    }

    public static boolean isClientHovered() {
        if (IEMFEntity == null) return false;
        var mc = Minecraft.getInstance();

        //block entity looked at
        if (IEMFEntity.etf$isBlockEntity()){
            var player = Minecraft.getInstance().player;
            if(player != null
                    && IEMFEntity.etf$distanceTo(player) <= #if MC > MC_20_4 player.blockInteractionRange() #else (player.isCreative() ? 5F : 4.5F) #endif + 1) {
                Entity entity = mc.getCameraEntity();
                if (entity != null) {
                    var block = entity.pick(20.0, 0.0F, false);
                    if (block.getType() == HitResult.Type.BLOCK) {
                        return ((BlockHitResult) block).getBlockPos().equals(IEMFEntity.etf$getBlockPos());
                    }
                }
            }
            return false;
        }

        //entity looked at
        return mc.crosshairPickEntity != null && mc.crosshairPickEntity.equals(IEMFEntity);
    }

    public static boolean isInLava() {
        return IEMFEntity != null && IEMFEntity.emf$isInLava();
    }

    public static boolean isInvisible() {
        return IEMFEntity != null && IEMFEntity.emf$isInvisible();
    }

    public static boolean isOnHead() {
        return setIsOnHead;
    }

    public static boolean isOnShoulder() {
        return onShoulder;
    }

    public static void setCurrentEntityOnShoulder(boolean onShoulder) {
        EMFAnimationEntityContext.onShoulder = onShoulder;
    }

    public static boolean isRidden() {
        return IEMFEntity != null && IEMFEntity.emf$hasPassengers();
    }

    public static boolean isSitting() {
        if (IEMFEntity == null) return false;
        return (IEMFEntity instanceof TamableAnimal tame && tame.isInSittingPose()) ||
                (IEMFEntity instanceof Fox fox && fox.isSitting()) ||
                (IEMFEntity instanceof Parrot parrot && parrot.isInSittingPose()) ||
                (IEMFEntity instanceof Cat cat && cat.isInSittingPose()) ||
                (IEMFEntity instanceof Wolf wolf && wolf.isInSittingPose()) ||
                (IEMFEntity instanceof Camel camel && camel.isCamelSitting());
    }


    public static boolean isSneaking() {
        return IEMFEntity != null && IEMFEntity.emf$isSneaking();
    }

    public static boolean isSprinting() {
        return IEMFEntity != null && IEMFEntity.emf$isSprinting();
    }

    public static boolean isTamed() {
        return IEMFEntity instanceof TamableAnimal tame && tame.isTame();
    }

    public static boolean isWet() {
        return IEMFEntity != null && IEMFEntity.emf$isWet();
    }

    public static float getSwingProgress() {
        return IEMFEntity instanceof LivingEntity alive ? alive.getAttackAnim(getTickDelta()) : 0;
    }

//    private static float age = Float.NaN;

    public static float getAge() {
//        if (!Float.isNaN(age)) return age;

        if (IEMFEntity == null) {
            return 0 + getTickDelta();
        }else {
            return constrainedFloat(IEMFEntity.emf$age(), 27720) + getTickDelta();
        }
//        return age;
    }

    private static float constrainedFloat(float value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    private static float constrainedFloat(float value) {
        return constrainedFloat(value, 27720);
    }

    private static float constrainedFloat(long value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    private static float constrainedFloat(long value) {
        return constrainedFloat(value, 27720);
    }

    private static float constrainedFloat(int value, int constraint) {
        return (value >= constraint ? value % constraint : value);
    }

    private static float constrainedFloat(int value) {
        return constrainedFloat(value, 27720);
    }

    public static float getFrameTime() {
        if (Minecraft.getInstance().isPaused()) return 0;
        #if MC > MC_20_2
        if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.tickRateManager().isFrozen()) return 0;
        #endif

        #if MC >= MC_21
        return ((MinecraftClientAccessor)Minecraft.getInstance())
                #if MC > MC_21 .getDeltaTracker() #else .getTimer() #endif
                .getGameTimeDeltaTicks() / 20;
        #else
        return Minecraft.getInstance().getDeltaFrameTime() / 20;
        #endif
    }

    public static float getLimbAngle() {//limb_swing
        if (IEMFEntity == null) return 0;
        if (Float.isNaN(limbAngle)) {
            doLimbValues();
        }
        return limbAngle;
    }

    public static void setLimbAngle(float limbAngle) {
        EMFAnimationEntityContext.limbAngle = limbAngle;
    }

    public static float getLimbDistance() {//limb_speed
        if (IEMFEntity == null) return 0;
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
        assert IEMFEntity != null;
        if (!IEMFEntity.emf$hasVehicle() && IEMFEntity instanceof LivingEntity alive) {
            o = alive.walkAnimation.position(getTickDelta());
            n = alive.walkAnimation.speed(getTickDelta());
            if (alive.isBaby()) {
                o *= 3.0F;

            }
            if (n > 1.0F) {
                n = 1.0F;
            }
        } else if (IEMFEntity instanceof AbstractMinecart) {
            n = 1;
            o = -(getEntityX() + getEntityZ());
        } else if (IEMFEntity instanceof #if MC > MC_21 AbstractBoat #else Boat #endif  boat) {
            n = 1;
            //o = boat.interpolatePaddlePhase(0, tickDelta);//1);
            o = Math.max(boat.getRowingTime(1, getTickDelta()), boat.getRowingTime(0, getTickDelta()));
        }
        limbDistance = n;
        limbAngle = o;
    }

    public static float getHeadYaw() {
        if (IEMFEntity == null) return 0;
        if (Float.isNaN(headYaw)) {
            doHeadValues();
        }
        return headYaw;
    }

    public static void setHeadYaw(float headYaw) {
        EMFAnimationEntityContext.headYaw = headYaw;
    }

    public static float getHeadPitch() {
        if (IEMFEntity == null) return 0;
        if (Float.isNaN(headPitch)) {
            doHeadValues();
        }
        return headPitch;
    }

    public static void setHeadPitch(float headPitch) {
        EMFAnimationEntityContext.headPitch = headPitch;
    }

    private static void doHeadValues() {
        if (IEMFEntity instanceof LivingEntity livingEntity) {
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
        return   #if MC >= MC_21
        ((MinecraftClientAccessor) Minecraft.getInstance())
                #if MC > MC_21 .getDeltaTracker() #else .getTimer() #endif
                .getGameTimeDeltaPartialTick(true);
        #else
                Minecraft.getInstance().isPaused() ? ((MinecraftClientAccessor) Minecraft.getInstance()).getPausePartialTick() : Minecraft.getInstance().getFrameTime();
        #endif
    }


    public static float getMoveForward() {
        if (IEMFEntity == null) return 0;
        double lookDir = Math.toRadians(90 - IEMFEntity.emf$getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3 velocity = IEMFEntity.emf$getVelocity();

        //consider 2d plane of movement with x y
        double x = velocity.x;
        double y = velocity.z;

        // compute the new x and y components after rotation
        double newX = (x * Math.cos(lookDir)) - (y * Math.sin(lookDir));
        //double newY = (x * Math.sin(lookDir)) + (y * Math.cos(lookDir));


        return processMove(newX, x, y);
    }

    public static float getMoveStrafe() {
        if (IEMFEntity == null) return 0;
        double lookDir = Math.toRadians(90 - IEMFEntity.emf$getYaw());
        //float speed = entity.horizontalSpeed;
        Vec3 velocity = IEMFEntity.emf$getVelocity();

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

    public static IterationContext getIterationContext() {
        return new IterationContext(
                EMFManager.getInstance().entityRenderCount,
                IEMFEntity,
                layerFactory,
                lodFrameSkipping,
                shadowSize,
                shadowOpacity,
                leashX,
                leashY,
                leashZ,
                shadowX,
                shadowZ
                #if MC > MC_21
                ,entityRenderState
                #endif
        );
    }

    public static void setIterationContext(IterationContext context) {
        EMFManager.getInstance().entityRenderCount = context.entityRenderCount;
        IEMFEntity = context.IEMFEntity;
        layerFactory = context.layerFactory;
        lodFrameSkipping = context.lodFrameSkipping;
        shadowSize = context.shadowSize;
        shadowOpacity = context.shadowOpacity;
        leashX = context.leashX;
        leashY = context.leashY;
        leashZ = context.leashZ;
        shadowX = context.shadowX;
        shadowZ = context.shadowZ;
        #if MC > MC_21
        entityRenderState = context.state;
        #endif
    }
    #if MC > MC_21
        public record IterationContext(
            long entityRenderCount,
            @Nullable EMFEntity IEMFEntity,
            Function<ResourceLocation, RenderType> layerFactory,
            Boolean lodFrameSkipping,
            float shadowSize,
            float shadowOpacity,
            float leashX,
            float leashY,
            float leashZ,
            float shadowX,
            float shadowZ,
            EntityRenderState state
        ){}
        #else
    public record IterationContext(
            long entityRenderCount,
            @Nullable EMFEntity IEMFEntity,
            Function<ResourceLocation, RenderType> layerFactory,
            Boolean lodFrameSkipping,
            float shadowSize,
            float shadowOpacity,
            float leashX,
            float leashY,
            float leashZ,
            float shadowX,
            float shadowZ
    ){}
    #endif
}
