package com.png.RoamAndRant.Core.Dialog;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class DialogEntry implements JsonAssetWithMap<String, DefaultAssetMap<String, DialogEntry>> {
    @Nonnull
    public static final AssetBuilderCodec<String, DialogEntry> CODEC = AssetBuilderCodec.builder(
                    DialogEntry.class,
                    DialogEntry::new,
                    Codec.STRING,
                    (asset, s) -> asset.id = s,
                    asset -> asset.id,
                    (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData
            )
            .append(new KeyedCodec<>("Id", Codec.STRING, true),(asset, s) -> asset.id = s, asset -> asset.id)
            .add()
            .append(new KeyedCodec<>("IsStart", Codec.BOOLEAN, false),(asset, s) -> asset.isStart = s, asset -> asset.isStart)
            .add()
            .append(new KeyedCodec<>("NameOverride", Codec.STRING, false), (asset, s) -> asset.nameOverride = s, asset -> asset.nameOverride)
            .add()
            .append(new KeyedCodec<>("DialogText", Codec.STRING, true), (asset, s) -> asset.dialogText = s, asset -> asset.dialogText)
            .add()
            .append(new KeyedCodec<>("Interaction", Codec.STRING, false), (asset, s) -> asset.runInteractionWithId = s, asset -> asset.runInteractionWithId)
            .add()
            .append(new KeyedCodec<>("Animation", Codec.STRING, false), (asset, s) -> asset.runAnimationWithId = s, asset -> asset.runAnimationWithId)
            .add()
            .append(new KeyedCodec<>("NextId", Codec.STRING, false), (asset, s) -> asset.nextId = s, asset -> asset.nextId)
            .add()
            .append(new KeyedCodec<>("DialogButtons", new ArrayCodec<>(DialogButton.CODEC, DialogButton[]::new), false), (asset, s) -> asset.dialogButtons = s, asset -> asset.dialogButtons)
            .add()
            .build();

    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(DialogEntry::getAssetStore));
    private static AssetStore<String, DialogEntry, DefaultAssetMap<String, DialogEntry>> ASSET_STORE;
    protected AssetExtraInfo.Data extraData;
    protected String id;
    protected boolean isStart;
    protected String nameOverride;
    protected String dialogText;
    protected String runInteractionWithId;
    protected String runAnimationWithId;
    protected String nextId;
    protected DialogButton[] dialogButtons;

    @Nonnull
    public static AssetStore<String, DialogEntry, DefaultAssetMap<String, DialogEntry>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(DialogEntry.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogEntry> getAssetMap() {
        return (DefaultAssetMap<String, DialogEntry>)getAssetStore().getAssetMap();
    }

    public DialogEntry(String id, String displayNameKey, String dialogText, String interactionId, String animationId, String nextId, DialogButton[] dialogButtons) {
        this.id = id;
        this.nameOverride = displayNameKey;
        this.dialogText = dialogText;
        this.runInteractionWithId = interactionId;
        this.runAnimationWithId = animationId;
        this.nextId = nextId;
        this.dialogButtons = dialogButtons;
    }

    protected DialogEntry() {
    }

    public String getId() {
        return this.id;
    }

    public boolean getIsStart() {return this.isStart; }

    public String getNameOverride() {
        return this.nameOverride;
    }

    public String getDialogText(){
        return this.dialogText;
    }

    public String getInteractionId(){ return this.runInteractionWithId; }

    public String getAnimationId() {return this.runAnimationWithId; }

    public String getNextId(){ return this.nextId; }

    public DialogButton[] getDialogButtons(){return  this.dialogButtons; }

    @Nonnull
    @Override
    public String toString() {
        return "DialogAsset{id='"
                + this.id
                + "', nameOverride='"
                + this.nameOverride
                + "', dialogText="
                + this.dialogText
                + "', runInteractionWithId="
                + this.runInteractionWithId
                + "', runAnimationWithId="
                + this.runAnimationWithId
                + "', nextId="
                + this.nextId
                + "', dialogButtons="
                + Arrays.toString((Object[])this.dialogButtons)
                + "}";
    }
}
