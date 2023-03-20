package traben.entity_model_features;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class EMFVersionDifferenceManager {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isThisModLoaded(String modId) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static boolean isETFValidAPI(){
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
    @ExpectPlatform
    public static EMFData.EMFPropertyTester getAllValidPropertyObjects(Identifier propsID){
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }


}
