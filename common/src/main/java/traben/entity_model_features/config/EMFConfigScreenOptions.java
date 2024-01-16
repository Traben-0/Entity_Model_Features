package traben.entity_model_features.config;


import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenOptions extends ETFConfigScreen {


    private final EMFConfigScreenMain emfParent;

    public EMFConfigScreenOptions(EMFConfigScreenMain parent) {
        super(new TranslatableText("entity_model_features.config.options"), parent);
        emfParent = parent;
    }

    @Override
    protected void init() {
        super.init();




        this.addDrawableChild(getETFButton((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20,
                new TranslatableText("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod = true;
                    emfParent.tempConfig.attemptPhysicsModPatch_2 = EMFConfig.PhysicsModCompatChoice.CUSTOM;
                    emfParent.tempConfig.modelUpdateFrequency = ETFConfig.UpdateFrequency.Average;

                    //this.clearAndInit();
                    Objects.requireNonNull(client).setScreen(new EMFConfigScreenOptions(emfParent));
                }
        ));
        this.addDrawableChild(getETFButton((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.BACK,
                (button) -> {
                    //tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.force_models").getString() +
                        ": " + (emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod = !emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod;
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.force_models").getString() +
                            ": " + (emfParent.tempConfig.attemptRevertingEntityModelsAlteredByAnotherMod ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                new TranslatableText("entity_model_features.config.force_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.physics").getString() +
                        ": " + (emfParent.tempConfig.attemptPhysicsModPatch_2.asText()).getString()),
                (button) -> {
                    emfParent.tempConfig.attemptPhysicsModPatch_2 = emfParent.tempConfig.attemptPhysicsModPatch_2.next();
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.physics").getString() +
                            ": " + (emfParent.tempConfig.attemptPhysicsModPatch_2.asText()).getString()));
                },
                new TranslatableText("entity_model_features.config.physics.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(new TranslatableText("entity_model_features.config.update").getString() +
                        ": " + emfParent.tempConfig.modelUpdateFrequency.toString()),
                (button) -> {
                    emfParent.tempConfig.modelUpdateFrequency = emfParent.tempConfig.modelUpdateFrequency.next();
                    button.setMessage(Text.of(new TranslatableText("entity_model_features.config.update").getString() +
                            ": " + emfParent.tempConfig.modelUpdateFrequency.toString()));
                },
                new TranslatableText("entity_model_features.config.update.tooltip")
        ));
    }


}
