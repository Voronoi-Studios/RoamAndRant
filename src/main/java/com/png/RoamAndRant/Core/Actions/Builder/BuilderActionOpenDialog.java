package com.png.RoamAndRant.Core.Actions.Builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.png.RoamAndRant.Core.Actions.ActionOpenDialog;
import com.png.RoamAndRant.Core.Actions.DialogExistsValidator;

import java.util.EnumSet;
import javax.annotation.Nonnull;

public class BuilderActionOpenDialog extends BuilderActionBase {
    @Nonnull
    protected final AssetHolder dialogId = new AssetHolder();

    @Nonnull
    @Override
    public String getShortDescription() {
        return "Open the dialog UI for the current player";
    }

    @Nonnull
    @Override
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionOpenDialog(this, builderSupport);
    }

    @Nonnull
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionOpenDialog readConfig(@Nonnull JsonElement data) {
        this.requireAsset(data, "Dialog", this.dialogId, DialogExistsValidator.required(), BuilderDescriptorState.Stable, "The dialog to open", null);
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public String getDialogId(@Nonnull BuilderSupport support) {
        return this.dialogId.get(support.getExecutionContext());
    }
}
