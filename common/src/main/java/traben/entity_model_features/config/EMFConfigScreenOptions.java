package traben.entity_model_features.config;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import traben.entity_texture_features.config.screens.ETFConfigScreen;

import java.util.Objects;

public class EMFConfigScreenOptions extends ETFConfigScreen {



    public EMFConfigScreenOptions(EMFConfigScreenMain parent) {
        super(Text.translatable("entity_model_features.options"),parent);
        emfParent = parent;
    }

    private final EMFConfigScreenMain emfParent;



    @Override
    protected void init() {
        super.init();




        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("dataPack.validation.reset"),
                (button) -> {
                    emfParent.tempConfig.attemptToCopyVanillaModelIntoMissingModelPart = false;
                    emfParent.tempConfig.tryForceEmfModels = true;
                    emfParent.tempConfig.attemptPhysicsModPatch_1 = false;
                    this.clearAndInit();
                    //Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.4), (int) (this.height * 0.9), (int) (this.width * 0.22), 20).build());
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.BACK,
                (button) -> {
                    //tempConfig = null;
                    Objects.requireNonNull(client).setScreen(parent);
                }).dimensions((int) (this.width * 0.1), (int) (this.height * 0.9), (int) (this.width * 0.2), 20).build());



        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.2), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.substitute_vanilla").getString() +
                        ": " + (emfParent.tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.attemptToCopyVanillaModelIntoMissingModelPart = !emfParent.tempConfig.attemptToCopyVanillaModelIntoMissingModelPart;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.substitute_vanilla").getString() +
                            ": " + (emfParent.tempConfig.attemptToCopyVanillaModelIntoMissingModelPart ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.substitute_vanilla.tooltip")
        ));


        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.3), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.force_models").getString() +
                        ": " + (emfParent.tempConfig.tryForceEmfModels ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.tryForceEmfModels = !emfParent.tempConfig.tryForceEmfModels;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.force_models").getString() +
                            ": " + (emfParent.tempConfig.tryForceEmfModels ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.force_models.tooltip")
        ));

        this.addDrawableChild(getETFButton((int) (this.width * 0.2), (int) (this.height * 0.4), (int) (this.width * 0.6), 20,
                Text.of(Text.translatable("entity_model_features.config.physics").getString() +
                        ": " + (emfParent.tempConfig.attemptPhysicsModPatch_1 ? ScreenTexts.ON : ScreenTexts.OFF).getString()),
                (button) -> {
                    emfParent.tempConfig.attemptPhysicsModPatch_1 = !emfParent.tempConfig.attemptPhysicsModPatch_1;
                    button.setMessage(Text.of(Text.translatable("entity_model_features.config.physics").getString() +
                            ": " + (emfParent.tempConfig.attemptPhysicsModPatch_1 ? ScreenTexts.ON : ScreenTexts.OFF).getString()));
                },
                Text.translatable("entity_model_features.config.physics.tooltip")
        ));
    }


}
