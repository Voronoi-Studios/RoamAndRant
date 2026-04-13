package com.png.RoamAndRant.Core.Actions.Builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.EnumHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.png.RoamAndRant.Core.Actions.ActionPositionCamera;
import com.png.RoamAndRant.Core.Camera.CameraPositioner;

import javax.annotation.Nonnull;

public class BuilderActionPositionCamera extends BuilderActionBase {
    protected final EnumHolder<CameraPositioner.CameraPosition> cameraPosition = new EnumHolder<>();

    @Nonnull
    @Override
    public String getShortDescription() {
        return "Set where the camera should move to";
    }

    @Nonnull
    @Override
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionPositionCamera(this, builderSupport);
    }

    @Nonnull
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public BuilderActionPositionCamera readConfig(@Nonnull JsonElement data) {
        this.getEnum(
                data,
                "CameraPosition",
                this.cameraPosition,
                CameraPositioner.CameraPosition.class,
                CameraPositioner.CameraPosition.RightShoulder,
                BuilderDescriptorState.Stable,
                "Set where the camera will move to",
                null
        );
        //this.requireInstructionType(EnumSet.of(InstructionType.Interaction));
        return this;
    }

    public CameraPositioner.CameraPosition getCameraPosition(@Nonnull BuilderSupport support) {
        return this.cameraPosition.get(support.getExecutionContext());
    }
}
