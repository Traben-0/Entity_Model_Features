package traben.entity_model_features.propeties;

import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class ModelSuffixProperty extends SimpleIntegerArrayProperty {
    protected ModelSuffixProperty(Properties properties, int propertyNum) throws RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "modelSuffix", "model_suffix"));
    }

    public static ModelSuffixProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ModelSuffixProperty(properties, propertyNum);
        } catch (RandomPropertyException var3) {
            return null;
        }
    }

    public @NotNull String[] getPropertyIds() {
        return new String[]{"modelSuffix", "model_suffix"};
    }

    protected int getValueFromEntity(ETFEntity entity) {
        int val = EMFManager.getInstance().lastModelSuffixOfEntity.getInt(entity.etf$getUuid());
        return Math.max(val, 0);
    }
}
