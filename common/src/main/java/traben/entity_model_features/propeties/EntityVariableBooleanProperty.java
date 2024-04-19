package traben.entity_model_features.propeties;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.models.animation.math.MathValue;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Map;
import java.util.Properties;

public class EntityVariableBooleanProperty extends RandomProperty {
    private final Map<String, Boolean> VARIABLE_MAP;

    protected EntityVariableBooleanProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
        String keyPrefix = "varb." + propertyNum + ".";
        this.VARIABLE_MAP = new Object2ObjectLinkedOpenHashMap<>();
        properties.forEach((key, value) -> {
            if (key != null && ((String) key).startsWith(keyPrefix)) {
                String instruction = ((String) value).trim();
                String variableKey = "varb." + ((String) key).replaceAll(keyPrefix, "");
                if (!variableKey.isBlank() && !instruction.isBlank()) {
                    boolean matchTrue = instruction.contains("true");
                    this.VARIABLE_MAP.put(variableKey, matchTrue);
                }
            }

        });
        if (this.VARIABLE_MAP.isEmpty()) {
            throw new RandomProperty.RandomPropertyException("Variable booleans failed");
        }
    }

    public static EntityVariableBooleanProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new EntityVariableBooleanProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException var3) {
            return null;
        }
    }

    @Override
    protected boolean testEntityInternal(final ETFEntity etfEntity) {
        if (etfEntity instanceof EMFEntity emfEntity) {
            for (Map.Entry<String, Boolean> stringFunctionEntry : VARIABLE_MAP.entrySet()) {
                boolean value = MathValue.toBoolean(
                        emfEntity.emf$getVariableMap().getFloat(stringFunctionEntry.getKey())
                );
                if (stringFunctionEntry.getValue() != value) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public @NotNull String[] getPropertyIds() {
        return new String[]{"varb"};
    }

    @Override
    protected String getPrintableRuleInfo() {
        return null;
    }
}
