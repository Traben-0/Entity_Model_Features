package traben.entity_model_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import traben.entity_model_features.models.jemJsonObjects.EMF_JemData;

import java.io.*;
import java.util.Properties;

import static traben.entity_model_features.client.Entity_model_featuresClient.EMFConfigData;

public class EMFUtils {

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

    public static void EMF_saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(EMFConfigData));
            fileWriter.close();
        } catch (IOException e) {
            EMF_modMessage("Config could not be saved", false);
        }
    }

    public static EMF_JemData EMF_readJemData(String pathOfJem){
        //File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        try {
            Resource jemResource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(pathOfJem)).get();
            //File jemFile = new File(pathOfJem);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println("jem exists "+ jemFile.exists());
            //if (jemFile.exists()) {
                //FileReader fileReader = new FileReader(jemFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(jemResource.getInputStream()));

                EMF_JemData jem = gson.fromJson(reader, EMF_JemData.class);
                reader.close();
                jem.prepare();
                return jem;
            //}
        } catch (IOException e) {
            EMF_modMessage("jem failed "+e, false);
        }
        return null;
    }


}
