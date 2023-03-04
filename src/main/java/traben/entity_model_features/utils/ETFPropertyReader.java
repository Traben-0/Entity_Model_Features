package traben.entity_model_features.utils;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.EMFData;
import traben.entity_texture_features.ETFApi;

public class ETFPropertyReader {

    @SuppressWarnings("ConstantConditions") // ETFApiVersion is an external constant subject to change
    @Nullable
    public static EMFData.EMFPropertyTester getAllValidPropertyObjects(Identifier propsID){
//        if (ETFApi.ETFApiVersion >= 4) {

            ETFApi.ETFRandomTexturePropertyInstance etfPropertyTester =
                    ETFApi.readRandomPropertiesFileAndReturnTestingObject2(propsID,"models");

            return etfPropertyTester::getSuffixForEntity;
//        }
//        return null;
    }

}
