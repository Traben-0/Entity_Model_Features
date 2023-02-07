package traben.entity_model_features.utils;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.utils.ETFTexturePropertiesUtils;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class etfPropertyReader {

    @Nullable
    public static List<EMFPropertyCase> getAllValidPropertyObjects(Identifier propsID, String suffixToTest, String entityTypeName){


        Properties props = ETFUtils2.readAndReturnPropertiesElseNull(propsID);
        if(props == null) return null;

        String modelID = "optifine/cem/" + entityTypeName + ".jem";

        List<ETFTexturePropertiesUtils.ETFTexturePropertyCase> etfs = ETFTexturePropertiesUtils.getAllValidPropertyObjects(props, suffixToTest, new Identifier(modelID));
        List<EMFPropertyCase> emfs = new ArrayList<>();
        for (ETFTexturePropertiesUtils.ETFTexturePropertyCase etfCase:
             etfs) {
            emfs.add(new EMFPropertyCase() {
                @Override
                public boolean testCase(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom) {
                    return etfCase.doesEntityMeetConditionsOfThisCase(entity, isUpdate, UUID_CaseHasUpdateablesCustom);
                }

                @Override
                public int getSuffix(UUID id) {
                    return etfCase.getAnEntityVariantSuffixFromThisCase(id);
                }
            });
        }
        return emfs;
    }

    public abstract static class EMFPropertyCase{

        public abstract boolean testCase(Entity entity, boolean isUpdate, Object2BooleanOpenHashMap<UUID> UUID_CaseHasUpdateablesCustom);

        public abstract int getSuffix(UUID id);
    }

}
