package traben.entity_model_features.config;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Consumer;

public class EnumSliderWidget<T extends Enum<?>> extends SliderWidget {

    private final Consumer<T> valueReceiver;

    private final T[] enumValues;
    private final String title;

    public EnumSliderWidget(final int x, final int y, final int width, final int height, final Text text, final T defaultValue,
                            Consumer<T> valueReceiver) {
        super(x, y, width, height, text, defaultValue.ordinal() / (double) (defaultValue.getDeclaringClass().getEnumConstants().length - 1));
        this.valueReceiver = Objects.requireNonNull(valueReceiver);
        //noinspection unchecked
        this.enumValues = (T[]) defaultValue.getDeclaringClass().getEnumConstants();
        this.title = text.getString() + ": ";
        updateMessage();
    }

    public EnumSliderWidget(final int x, final int y, final int width, final int height, final Text text, final T defaultValue,
                            Consumer<T> valueReceiver, Text tooltip) {
        this(x, y, width, height, text, defaultValue, valueReceiver);
        setTooltip(Tooltip.of(tooltip));
    }

    @Override
    protected void updateMessage() {
        setMessage(Text.of(title + enumValues[getIndex()].toString()));
    }

    private int getIndex() {
        return (int) Math.round(value * (enumValues.length - 1));
    }

    @Override
    protected void applyValue() {
        //take value and ensure its value between 0.0 and 1.0 is snapped to a ratio of the enum length
        int index = getIndex();
        value = index / (double) (enumValues.length - 1);
        valueReceiver.accept(enumValues[index]);
    }
}
