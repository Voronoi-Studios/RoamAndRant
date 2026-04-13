package com.png.RoamAndRant;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.png.RoamAndRant.Core.Actions.Builder.BuilderActionPositionCamera;
import com.png.RoamAndRant.Core.Camera.LookAtCommand;
import com.png.RoamAndRant.Core.Camera.ResetViewCommand;
import com.png.RoamAndRant.Core.Dialog.DialogAsset;
import com.png.RoamAndRant.Core.Actions.Builder.BuilderActionOpenDialog;

public class RARPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public RARPlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new LookAtCommand());
        this.getCommandRegistry().registerCommand(new ResetViewCommand());

        AssetRegistry.register(HytaleAssetStore.builder(DialogAsset.class, new DefaultAssetMap<String, DialogAsset>())
                .setPath("Dialogs")
                .setCodec(DialogAsset.CODEC)
                .setKeyFunction(DialogAsset::getId)
                .loadsAfter(Item.class)
                .build()
        );

        NPCPlugin.get().registerCoreComponentType("OpenDialog", BuilderActionOpenDialog::new);
        NPCPlugin.get().registerCoreComponentType("PositionCamera", BuilderActionPositionCamera::new);
    }
}
