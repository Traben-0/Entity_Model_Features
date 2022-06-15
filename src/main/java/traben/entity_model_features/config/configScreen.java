package traben.entity_model_features.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import traben.entity_model_features.client.EMFUtils;


import java.awt.*;

import static traben.entity_model_features.client.Entity_model_featuresClient.EMFConfigData;


// config translation rework done by @Maximum#8760
public class configScreen {

    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
        // Return the screen here with the one you created from Cloth Config Builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("emf"))
                .setSavingRunnable(this::saveConfig);

        ConfigCategory category = builder.getOrCreateCategory(Text.of(""));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        category.setBackground(new Identifier("textures/block/deepslate_tiles.png"));

        category.addEntry(entryBuilder.startFloatField(Text.of("test rotate divide"), EMFConfigData.testRotationDivider)
                .setDefaultValue(1.0f) // Recommended: Used when user click "Reset"
                .setTooltip(Text.translatable("")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> EMFConfigData.testRotationDivider = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config
        category.addEntry(entryBuilder.startFloatField(Text.of("test rotate multiply"), EMFConfigData.testRotationMultiplier)
                .setDefaultValue(1.0f) // Recommended: Used when user click "Reset"
                .setTooltip(Text.translatable("")) // Optional: Shown when the user hover over this option
                .setSaveConsumer(newValue -> EMFConfigData.testRotationMultiplier = newValue) // Recommended: Called when user save the config
                .build()); // Builds the option entry for cloth config

        //MinecraftClient.getInstance().openScreen(screen);
        return builder.setTransparentBackground(isTransparent).build();
    }

    //this needs to be here due to puzzle mod compatibility, remove this when the full release happens
    public void saveConfig() {
        EMFUtils.EMF_saveConfig();
    }


}


