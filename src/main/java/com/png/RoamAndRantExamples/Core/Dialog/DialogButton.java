package com.png.RoamAndRantExamples.Core.Dialog;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;

import javax.annotation.Nonnull;

public class DialogButton implements JsonAssetWithMap<String, DefaultAssetMap<String, DialogButton>> {
    @Nonnull
    public static final AssetBuilderCodec<String, DialogButton> CODEC = AssetBuilderCodec.builder(
                    DialogButton.class,
                    DialogButton::new,
                    Codec.STRING,
                    (asset, s) -> asset.id = s,
                    asset -> asset.id,
                    (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData
            )
            .append(new KeyedCodec<>("ButtonText", Codec.STRING, true), (asset, s) -> asset.buttonText = s, asset -> asset.buttonText)
            .add()
            .append(new KeyedCodec<>("Interaction", Codec.STRING, false), (asset, s) -> asset.runInteractionWithId = s, asset -> asset.runInteractionWithId)
            .add()
            .append(new KeyedCodec<>("NextId", Codec.STRING, false), (asset, s) -> asset.nextId = s, asset -> asset.nextId)
            .add()
            .append(new KeyedCodec<>("Exit", Codec.BOOLEAN, false), (asset, s) -> asset.exit = s, asset -> asset.exit)
            .add()
            .build();

    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(DialogButton::getAssetStore));
    private static AssetStore<String, DialogButton, DefaultAssetMap<String, DialogButton>> ASSET_STORE;
    protected AssetExtraInfo.Data extraData;
    protected String id;
    protected String buttonText;
    protected String runInteractionWithId;
    protected String nextId;
    protected boolean exit = false;

    @Nonnull
    public static AssetStore<String, DialogButton, DefaultAssetMap<String, DialogButton>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(DialogButton.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogButton> getAssetMap() {
        return (DefaultAssetMap<String, DialogButton>)getAssetStore().getAssetMap();
    }

    public DialogButton(String id, String buttonText, String interactionId, String nextId, boolean exit) {
        this.id = id;
        this.buttonText = buttonText;
        this.runInteractionWithId = interactionId;
        this.nextId = nextId;
        this.exit = exit;
    }

    protected DialogButton() {
    }

    public String getId() {
        return this.id;
    }

    public String getButtonText() {
        return this.buttonText;
    }

    public String getInteractionId(){ return this.runInteractionWithId; }

    public String getNextId(){
        return this.nextId;
    }

    public boolean getExit() {return this.exit; }

    @Nonnull
    @Override
    public String toString() {
        return "DialogAsset{id='"
                + this.id
                + "', buttonText='"
                + this.buttonText
                + "', nextId="
                + this.nextId
                + "', exit="
                + this.exit
                + "}";
    }
}
