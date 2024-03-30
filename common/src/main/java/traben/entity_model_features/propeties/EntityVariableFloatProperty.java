package traben.entity_model_features.propeties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.FloatRangeFromStringArrayProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class EntityVariableFloatProperty extends RandomProperty {

    private final List<FloatRangeFromStringArrayProperty> VARIABLES;

    protected EntityVariableFloatProperty(Properties properties, int propertyNum) throws RandomProperty.RandomPropertyException {
//        super(readPropertiesOrThrow(properties, propertyNum, "var"));
        String keyPrefix = "var." + propertyNum + ".";
        this.VARIABLES = new ArrayList<>();
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            String key = objectObjectEntry.getKey().toString();
            Object value = objectObjectEntry.getValue();
            if (key != null && key.startsWith(keyPrefix)) {
                String instruction = ((String) value).trim();
                String variableKey = "var." + key.replaceAll(keyPrefix, "");
                if (!variableKey.isBlank() && !instruction.isBlank()) {
                    var tester = new InnerTester(instruction, (emfEntity) -> emfEntity.emf$getVariableMap().getFloat(variableKey), variableKey);
                    this.VARIABLES.add(tester);
                }
            }
        }
        if (this.VARIABLES.isEmpty()) {
            throw new RandomProperty.RandomPropertyException("Variable float failed");
        }
    }

    public static EntityVariableFloatProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new EntityVariableFloatProperty(properties, propertyNum);
        } catch (RandomProperty.RandomPropertyException var3) {
            return null;
        }
    }


    @Override
    protected boolean testEntityInternal(final ETFEntity etfEntity) {
        for (FloatRangeFromStringArrayProperty variable : VARIABLES) {
            if (!variable.testEntityInternal(etfEntity)) {
                return false;
            }
        }
        return true;
    }

    public @NotNull String[] getPropertyIds() {
        return new String[]{"var"};
    }

    @Override
    protected String getPrintableRuleInfo() {
        return null;
    }

    private static class InnerTester extends FloatRangeFromStringArrayProperty {

        private final Function<EMFEntity, Float> getter;
        private final String id;

        protected InnerTester(final String string, Function<EMFEntity, Float> getter, String id) throws RandomPropertyException {
            super(string);
            this.getter = getter;
            this.id = id;
        }

        @Override
        protected @Nullable Float getRangeValueFromEntity(final ETFEntity etfEntity) {
            if (etfEntity instanceof EMFEntity emfEntity)
                return getter.apply(emfEntity);
            return null;
        }

        @Override
        public @NotNull String[] getPropertyIds() {
            return new String[]{id};
        }
    }
}
