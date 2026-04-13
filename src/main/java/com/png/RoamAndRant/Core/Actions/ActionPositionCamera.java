package com.png.RoamAndRant.Core.Actions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.png.RoamAndRant.Core.Actions.Builder.BuilderActionPositionCamera;
import com.png.RoamAndRant.Core.Camera.CameraPositioner;

import javax.annotation.Nonnull;

public class ActionPositionCamera extends ActionBase {
    @Nonnull
    protected final CameraPositioner.CameraPosition cameraPosition;

    public ActionPositionCamera(@Nonnull BuilderActionPositionCamera builder, @Nonnull BuilderSupport support) {
        super(builder);
        this.cameraPosition = builder.getCameraPosition(support);
    }

    @Override
    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        return super.canExecute(ref, role, sensorInfo, dt, store) && role.getStateSupport().getInteractionIterationTarget() != null;
    }

    @Override
    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        Ref<EntityStore> playerReference = role.getStateSupport().getInteractionIterationTarget();
        if (playerReference == null) {
            return false;
        } else {
            PlayerRef playerRefComponent = store.getComponent(playerReference, PlayerRef.getComponentType());
            if (playerRefComponent == null) {
                return false;
            } else {
                Player playerComponent = store.getComponent(playerReference, Player.getComponentType());
                if (playerComponent == null) {
                    return false;
                } else {
                    NPCEntity npcComponent = store.getComponent(ref, NPCEntity.getComponentType());
                    assert npcComponent != null;
                    CameraPositioner.lookAtEntity(playerRefComponent, npcComponent, cameraPosition);
                }
            }
        }
        return true;
    }
}
