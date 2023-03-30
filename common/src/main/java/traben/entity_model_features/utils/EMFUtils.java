package traben.entity_model_features.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;


public class EMFUtils {
    public static void EMFModMessage(String message) {
        EMFModMessage(message, false);
    }

    public static void EMFModMessage(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("\u00A76[Entity Model Features]\u00A77: " + message), false);
            } else {
                LogManager.getLogger().info("[Entity Model Features]: " + message);
            }
        } else {
            LogManager.getLogger().info("[Entity Model Features]: " + message);
        }
    }

    public static void EMFModWarn(String message) {
        EMFModWarn(message, false);
    }

    public static void EMFModWarn(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("\u00A76[Entity Model Features]\u00A77: " + message), false);
            } else {
                LogManager.getLogger().warn("[Entity Model Features]: " + message);
            }
        } else {
            LogManager.getLogger().warn("[Entity Model Features]: " + message);
        }
    }

    public static void EMFModError(String message) {
        EMFModError(message, false);
    }

    public static void EMFModError(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("\u00A76[Entity Model Features]\u00A77: " + message), false);
            } else {
                LogManager.getLogger().error("[Entity Model Features]: " + message);
            }
        } else {
            LogManager.getLogger().error("[Entity Model Features]: " + message);
        }
    }


    @Nullable
    public static EMFPartData EMFReadModelPart(String pathOfJpm) {
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        pathOfJpm = "optifine/cem/" + pathOfJpm;
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJpm));
            if (res.isEmpty()) {
                if (EMFConfig.getConfig().printModelCreationInfoToLog)
                    EMFModMessage("jpm failed " + pathOfJpm + " does not exist", false);
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
            if (EMFConfig.getConfig().printModelCreationInfoToLog) EMFModMessage("jpm failed " + e, false);
        }
        return null;
    }


}
