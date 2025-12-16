package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_model_features.EMF;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

//#if MC >= 12111
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import com.mojang.blaze3d.vertex.VertexConsumer;
//$$ import net.minecraft.world.phys.AABB;
//#endif

//#if MC >= 12004
import net.minecraft.network.chat.contents.PlainTextContents;
//#else
//$$ import net.minecraft.network.chat.contents.LiteralContents;
//#endif

public class EMFUtils {

    public static @NotNull ResourceLocation res(String fullPath){
        //#if MC >= 12100
        return ResourceLocation.parse(fullPath);
        //#else
        //$$ return new ResourceLocation(fullPath);
        //#endif
    }

    public static @NotNull ResourceLocation res(String namespace, String path){
        //#if MC >= 12100
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }
    private static final String MOD_ID_SHORT = "EMF";
    private static final Logger LOGGER = LoggerFactory.getLogger("EMF");

    public static EMFModelPartRoot getArrowOrNull(ModelLayerLocation layer) {
        if (EMF.testForForgeLoadingError()) return null;
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        ModelPart part = modelPartData.bake(32, 32);
        //todo default transforms?
//        part.setPivot(0,2.5f,-7);
//        part.setDefaultTransform(part.getTransform());

        ModelPart possiblyEMF = EMFManager.getInstance().injectIntoModelRootGetter(layer, part);
        if (possiblyEMF instanceof EMFModelPartRoot) {
            return (EMFModelPartRoot) possiblyEMF;
        }
        return null;
    }

    // is a copy of the shaperenderer code in 1.21.9
    //#if MC >=12111
    //$$ public static void renderLineBox(PoseStack.Pose pose, VertexConsumer vertexConsumer, AABB aABB, float f, float g, float h, float i) {
    //$$     renderLineBox(pose, vertexConsumer, aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ, f, g, h, i, f, g, h);
    //$$ }
    //$$
    //$$ private static void renderLineBox(PoseStack.Pose pose, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m, float n, float o, float p) {
    //$$     float q = (float)d;
    //$$     float r = (float)e;
    //$$     float s = (float)f;
    //$$     float t = (float)g;
    //$$     float u = (float)h;
    //$$     float v = (float)i;
    //$$     int lineWidth = 2;
    //$$     vertexConsumer.addVertex(pose, q, r, s).setColor(j, o, p, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, r, s).setColor(j, o, p, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, r, s).setColor(n, k, p, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, u, s).setColor(n, k, p, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, r, s).setColor(n, o, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, r, v).setColor(n, o, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, r, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, u, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, u, s).setColor(j, k, l, m).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, u, s).setColor(j, k, l, m).setNormal(pose, -1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, u, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, r, v).setColor(j, k, l, m).setNormal(pose, 0.0F, -1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, r, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, r, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, r, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, r, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, -1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, q, u, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, u, v).setColor(j, k, l, m).setNormal(pose, 1.0F, 0.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, r, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 1.0F, 0.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, u, s).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
    //$$     vertexConsumer.addVertex(pose, t, u, v).setColor(j, k, l, m).setNormal(pose, 0.0F, 0.0F, 1.0F).setLineWidth(lineWidth);
    //$$ }
    //#endif

    public static void overrideMessage(String originalClass, String overriddenClassFromMod, boolean wasReverted) {
        LOGGER.warn("[" + MOD_ID_SHORT + "]: Entity model [" + originalClass + "] has been overridden by [" + overriddenClassFromMod + "] likely from a mod.");
        if (wasReverted)
            LOGGER.warn("[" + MOD_ID_SHORT + "]: Prevent model overrides option is enabled! EMF will attempt to revert the new model [" + overriddenClassFromMod + "] back into the original model [" + originalClass + "]. THIS MAY HAVE UNINTENDED EFFECTS ON THE OTHER MOD, DISABLE THIS EMF SETTING IF IT CAUSES CRASHES!");

    }

    public static void log(String message) {
        log(message, false, false);
    }

    public static void log(String message, boolean inChat) {
        log(message, inChat, false);
    }

    public static void log(String message, boolean inChat, boolean noPrefix) {
        if (inChat) {
            LocalPlayer plyr = Minecraft.getInstance().player;
            if (plyr != null) {
                plyr.displayClientMessage(Component.nullToEmpty((noPrefix ? "" : "§6[" + MOD_ID_SHORT + "]:§r ") + message), false);
            } else {
                LOGGER.info((noPrefix ? "" : "[" + MOD_ID_SHORT + "]: ") + message);
            }
        } else {
            LOGGER.info((noPrefix ? "" : "[" + MOD_ID_SHORT + "]: ") + message);
        }
    }

    public static void chat(String message) {
        LocalPlayer plyr = Minecraft.getInstance().player;
        if (plyr != null) {
            plyr.displayClientMessage(MutableComponent.create(
                    //#if MC >= 12004
                    new PlainTextContents.LiteralContents(message)
                    //#else
                    //$$ new LiteralContents(message)
                    //#endif
            ), false);
        }
    }

    public static void logWarn(String message) {
        logWarn(message, false);
    }


    public static void logWarn(String message, boolean inChat) {
        if (inChat) {
            LocalPlayer plyr = Minecraft.getInstance().player;
            if (plyr != null) {
                plyr.displayClientMessage(Component.nullToEmpty("§6[" + MOD_ID_SHORT + "]§r: " + message), false);
            } else {
                LOGGER.warn("[" + MOD_ID_SHORT + "]: " + message);
            }
        } else {
            LOGGER.warn("[" + MOD_ID_SHORT + "]: " + message);
        }
    }

    public static void logError(String message) {
        logError(message, false);
    }

    public static void logError(String message, boolean inChat) {
        if (inChat) {
            LocalPlayer plyr = Minecraft.getInstance().player;
            if (plyr != null) {
                plyr.displayClientMessage(Component.nullToEmpty("§6[" + MOD_ID_SHORT + "]§r: " + message), false);
            } else {
                LOGGER.error("[" + MOD_ID_SHORT + "]: " + message);
            }
        } else {
            LOGGER.error("[" + MOD_ID_SHORT + "]: " + message);
        }
    }


    @Nullable
    public static EMFPartData readModelPart(ResourceLocation location) {
        boolean print = EMF.config().getConfig().logModelCreationData;
        try {
            Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(location);
            if (res.isEmpty()) {
                if (print) log("jpm failed " + location + " does not exist", false);
                return null;
            }
            Resource jpmResource = res.get();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(jpmResource.open()))) {
                return gson.fromJson(reader, EMFPartData.class);
            }
        } catch (Exception e) {
            log("jpm ["+location.toString()+"] failed " + e, false);
            if (print) e.printStackTrace();
        }
        if (print) log("jpm read returned null for " + location, false);
        return null;
    }


    public static String getIdUnique(Set<String> known, String desired) {
        while (known.contains(desired) || desired.isBlank()) {
            desired += "#";
        }
        return desired;
    }
}
