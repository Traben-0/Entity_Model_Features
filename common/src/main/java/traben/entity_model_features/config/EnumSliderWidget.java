package traben.entity_model_features.config;


import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class EnumSliderWidget<T extends Enum<?>> extends SliderWidget {

    private final Consumer<T> valueReceiver;

    private final T[] enumValues;
    private final String title;
    private TriConsumer<MatrixStack,Integer,Integer> tooltipRenderer;

    public EnumSliderWidget(final int x, final int y, final int width, final int height, final Text text, final T defaultValue,
                            Consumer<T> valueReceiver) {
        super(x, y, width, height, text, defaultValue.ordinal() / (double) (defaultValue.getDeclaringClass().getEnumConstants().length - 1));
        this.valueReceiver = Objects.requireNonNull(valueReceiver);
        //noinspection unchecked
        this.enumValues = (T[]) defaultValue.getDeclaringClass().getEnumConstants();
        this.title = text.getString() + ": ";
        updateMessage();
        tooltipRenderer = (a,b,c)->{};
    }

    public EnumSliderWidget(final int x, final int y, final int width, final int height, final Text text, final T defaultValue,
                            Consumer<T> valueReceiver, Text tooltip, Screen parent) {
        this(x, y, width, height, text, defaultValue, valueReceiver);


        boolean tooltipIsEmpty = tooltip.getString().isBlank();
        String[] strings = tooltip.getString().split("\n");
        List<Text> lines = new ArrayList();
        String[] var12 = strings;
        int var13 = strings.length;

        for(int var14 = 0; var14 < var13; ++var14) {
            String str = var12[var14];
            lines.add(Text.of(str.strip()));
        }

        tooltipRenderer = tooltipIsEmpty ?
                (a,b,c)->{} :
                (matrices, mouseX, mouseY) -> parent.renderTooltip(matrices, lines, Optional.empty(), mouseX, mouseY);
    }

    @Override
    public void renderTooltip(final MatrixStack matrices, final int mouseX, final int mouseY) {
        if (isHovered()) {
            tooltipRenderer.accept(matrices, mouseX, mouseY);
        }
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
