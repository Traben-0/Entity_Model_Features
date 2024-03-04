package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


public class EMFUtils {

    private static final String MOD_ID_SHORT = "EMF";

    private static final Logger LOGGER = LoggerFactory.getLogger("EMF");


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
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of((noPrefix ? "" : "§6[" + MOD_ID_SHORT + "]:§r ") + message), false);
            } else {
                LOGGER.info((noPrefix ? "" : "[" + MOD_ID_SHORT + "]: ") + message);
            }
        } else {
            LOGGER.info((noPrefix ? "" : "[" + MOD_ID_SHORT + "]: ") + message);
        }
    }

    public static void chat(String message) {
        ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
        if (plyr != null) {
            plyr.sendMessage(MutableText.of(new LiteralTextContent(message)), false);
        }
    }

    public static void logWarn(String message) {
        logWarn(message, false);
    }


    public static void logWarn(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("§6[" + MOD_ID_SHORT + "]§r: " + message), false);
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
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("§6[" + MOD_ID_SHORT + "]§r: " + message), false);
            } else {
                LOGGER.error("[" + MOD_ID_SHORT + "]: " + message);
            }
        } else {
            LOGGER.error("[" + MOD_ID_SHORT + "]: " + message);
        }
    }


    @Nullable
    public static EMFPartData readModelPart(String pathOfJpm, String filePath) {
        //String folderOfModel = new File(mobModelIDInfo.getfileName()).getParent();
        //assume
        pathOfJpm = Objects.requireNonNullElse(filePath, "optifine/cem/") + pathOfJpm;
        if (!pathOfJpm.endsWith(".jpm")) {
            pathOfJpm = pathOfJpm + ".jpm";
        }
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJpm));
            if (res.isEmpty()) {
                if (EMFConfig.getConfig().logModelCreationData)
                    log("jpm failed " + pathOfJpm + " does not exist", false);
                return null;
            }
            Resource jpmResource = res.get();
            //File jemFile = new File(pathOfJem);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println("jem exists "+ jemFile.exists());
            //if (jemFile.exists()) {
            //FileReader fileReader = new FileReader(jemFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(jpmResource.getInputStream()));

            EMFPartData jpm = gson.fromJson(reader, EMFPartData.class);
            reader.close();
            //jpm.prepare();
            return jpm;
            //}
        } catch (Exception e) {
            if (EMFConfig.getConfig().logModelCreationData) log("jpm failed " + e, false);
        }
        return null;
    }


    public static String getIdUnique(Set<String> known, String desired) {
        //if (desired.isBlank()) desired = "EMF_#";
        while (known.contains(desired) || desired.isBlank()) {
            desired = desired + "#";
        }
        return desired;
    }
}
