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
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, DialogAsset>> {
    @Nonnull
    public static final AssetBuilderCodec<String, DialogAsset> CODEC = AssetBuilderCodec.builder(
                    DialogAsset.class,
                    DialogAsset::new,
                    Codec.STRING,
                    (asset, s) -> asset.id = s,
                    asset -> asset.id,
                    (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData
            )
            .append(new KeyedCodec<>("DialogEntries", new ArrayCodec<>(DialogEntry.CODEC, DialogEntry[]::new)), (asset, s) -> asset.dialogEntries = s, asset -> asset.dialogEntries)
            .add()
            .build();

    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(DialogAsset::getAssetStore));
    private static AssetStore<String, DialogAsset, DefaultAssetMap<String, DialogAsset>> ASSET_STORE;
    protected AssetExtraInfo.Data extraData;
    protected String id;
    protected DialogEntry[] dialogEntries;

    @Nonnull
    public static AssetStore<String, DialogAsset, DefaultAssetMap<String, DialogAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(DialogAsset.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, DialogAsset> getAssetMap() {
        return (DefaultAssetMap<String, DialogAsset>)getAssetStore().getAssetMap();
    }

    public DialogAsset(String id, DialogEntry[] dialogEntries) {
        this.id = id;
        this.dialogEntries = dialogEntries;
    }

    protected DialogAsset() {
    }

    public String getId() {
        return this.id;
    }

    public Map<String, DialogEntry> getDialogEntries() {
        return Arrays.stream(this.dialogEntries)
                .filter(entry -> entry.id != null)
                .collect(Collectors.toMap(entry -> entry.id, entry -> entry));
    }

    @Nonnull
    @Override
    public String toString() {
        return "DialogAsset{id='"
                + this.id
                + ", dialogEntries="
                + Arrays.toString((Object[])this.dialogEntries);
    }
}
