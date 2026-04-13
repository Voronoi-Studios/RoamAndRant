package com.png.RoamAndRantExamples.Core.Actions;

import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import com.png.RoamAndRantExamples.Core.Dialog.DialogAsset;

import java.util.EnumSet;
import javax.annotation.Nonnull;

public class DialogExistsValidator extends AssetValidator {
    @Nonnull
    private static final DialogExistsValidator DEFAULT_INSTANCE = new DialogExistsValidator();

    private DialogExistsValidator() {
    }

    private DialogExistsValidator(EnumSet<AssetValidator.Config> config) {
        super(config);
    }

    @Nonnull
    @Override
    public String getDomain() {
        return "Dialog";
    }

    @Override
    public boolean test(String marker) {
        return DialogAsset.getAssetMap().getAsset(marker) != null;
    }

    @Nonnull
    @Override
    public String errorMessage(String marker, String attributeName) {
        return "The dialog asset with the name \"" + marker + "\" does not exist for attribute \"" + attributeName + "\"";
    }

    @Nonnull
    @Override
    public String getAssetName() {
        return DialogAsset.class.getSimpleName();
    }

    public static DialogExistsValidator required() {
        return DEFAULT_INSTANCE;
    }

    @Nonnull
    public static DialogExistsValidator withConfig(EnumSet<AssetValidator.Config> config) {
        return new DialogExistsValidator(config);
    }
}
