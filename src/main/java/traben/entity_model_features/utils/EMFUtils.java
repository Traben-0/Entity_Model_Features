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
import traben.entity_model_features.EMFData;
import traben.entity_model_features.models.jem_objects.EMFJemData;
import traben.entity_model_features.models.jem_objects.EMFPartData;

import java.io.*;
import java.util.Optional;
import java.util.Properties;


public class EMFUtils {
    public static void EMF_modMessage(String message) {
        EMF_modMessage(message,false);
    }
    public static void EMF_modMessage(String message, boolean inChat) {
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
    public static void EMF_modWarn(String message) {
        EMF_modMessage(message,false);
    }
    public static void EMF_modWarn(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("\u00A76[Entity Model Features]\u00A77: " + message), false);
            } else {
                LogManager.getLogger().info("[Entity Model Features]: " + message);
            }
        } else {
            LogManager.getLogger().warn("[Entity Model Features]: " + message);
        }
    }

    static boolean EMF_isExistingFileAndSameResourcepackAs(Identifier id,Identifier vanillaIdToMatch){
        if(EMF_isExistingFile(id)){
            try {
                Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).get();
                Resource resource2 = MinecraftClient.getInstance().getResourceManager().getResource(vanillaIdToMatch).get();
                if (resource.getResourcePackName().equals(resource2.getResourcePackName())){
                    return true;
                }
            }catch (Exception e){
                //
            }
        }
        return false;
    }

    static boolean EMF_isExistingFile(Identifier id) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).get();
            try {
                //NativeImage.read(resource.getInputStream());
                resource.getInputStream().close();;

                return true;
            } catch (IOException e) {

                return false;
            }
        } catch (Exception f) {
            return false;
        }
    }

    //default boolean checkPathExist(String path) {
    //    return isExistingFile(new Identifier(path));
    //}

    static Properties EMF_readProperties(String path) {
        return EMF_readProperties(path,null);
    }

    static Properties EMF_readProperties(String path, String pathOfTextureToUseForResourcepackCheck) {
        Properties props = new Properties();
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path)).get();
            //skip if needs to be same resourcepack
            if (pathOfTextureToUseForResourcepackCheck != null){
                Resource resourceOriginal = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfTextureToUseForResourcepackCheck)).get();
                if (!resource.getResourcePackName().equals(resourceOriginal.getResourcePackName())){
                    //System.out.println("not same pack "+path+" // "+pathOfTextureToUseForResourcepackCheck);
                    return null;
                }
            }
            try {
                InputStream in = resource.getInputStream();
                props.load(in);
                in.close();
            } catch (Exception e) {

                return null;
            }
        } catch (Exception e) {
            return null;
        }
        // Example return
        // {skins.4=3, skins.5=1-3, skins.2=2, skins.3=3, weights.5=1 1 , biomes.2=desert, health.3=1-50%, names.4=iregex:mob name.*}
        return props;
    }

    @Nullable
    public static EMFJemData EMF_readJemData(String pathOfJem){
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem));
            if(res.isEmpty()){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMF_modMessage("jem failed "+pathOfJem+" does not exist", false);
                return null;
            }
            Resource jemResource = res.get();
            //File jemFile = new File(pathOfJem);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println("jem exists "+ jemFile.exists());
            //if (jemFile.exists()) {
                //FileReader fileReader = new FileReader(jemFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(jemResource.getInputStream()));

                EMFJemData jem = gson.fromJson(reader, EMFJemData.class);
                reader.close();
                jem.prepare();
                return jem;
            //}
        } catch (Exception e) {
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMF_modMessage("jem failed "+e, false);
        }
        return null;
    }
    @Nullable
    public static EMFPartData EMF_readModelPart(String pathOfJpm){
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        pathOfJpm = "optifine/cem/"+ pathOfJpm;
        try {
            Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJpm));
            if(res.isEmpty()){
                if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMF_modMessage("jpm failed "+pathOfJpm+" does not exist", false);
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
            if(EMFData.getInstance().getConfig().printModelCreationInfoToLog) EMF_modMessage("jpm failed "+e, false);
        }
        return null;
    }


}
