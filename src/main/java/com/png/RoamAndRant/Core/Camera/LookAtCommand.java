package com.png.RoamAndRant.Core.Camera;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.commands.NPCMultiSelectCommandBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.jspecify.annotations.NonNull;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class LookAtCommand extends NPCMultiSelectCommandBase {

    public LookAtCommand() {
        super("lookAt", "Looks at a position");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }


    @Override
    protected void execute(@NonNull CommandContext ctx, @NonNull NPCEntity npcEntity, @NonNull World world, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref) {
        PlayerRef playerRef = store.getComponent(ctx.senderAsPlayerRef(), PlayerRef.getComponentType());
        CameraPositioner.lookAtEntity(playerRef, npcEntity, CameraPositioner.CameraPosition.LeftShoulder);
    }
}