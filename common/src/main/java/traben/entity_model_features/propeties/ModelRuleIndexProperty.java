package traben.entity_model_features.propeties;

import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.utils.EMFManager;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Properties;

public class ModelRuleIndexProperty extends SimpleIntegerArrayProperty {
    protected ModelRuleIndexProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        super(getGenericIntegerSplitWithRanges(properties, propertyNum, "modelRule", "model_rule"));
    }

    public static ModelRuleIndexProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new ModelRuleIndexProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException var3) {
            return null;
        }
    }

    public @NotNull String[] getPropertyIds() {
        return new String[]{"modelRule", "model_rule"};
    }

    protected int getValueFromEntity(ETFEntity entity) {
        return EMFManager.getInstance().lastModelRuleOfEntity.getInt(entity.etf$getUuid());
    }
}
