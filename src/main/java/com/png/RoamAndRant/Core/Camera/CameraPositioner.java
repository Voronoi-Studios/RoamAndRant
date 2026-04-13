package com.png.RoamAndRant.Core.Camera;


import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class CameraPositioner {
    public enum CameraPosition implements Supplier<String> {
        RightShoulder("Framing both with cam to the right"),
        LeftShoulder("Framing both with cam to the left"),
        FPSLookAt("Only Look at target");

        public static final CameraPosition[] VALUES = values();
        private final String description;

        private CameraPosition(String description) {
            this.description = description;
        }

        public String get() {
            return this.description;
        }
    }

    public static void lookAtEntity(@NonNull PlayerRef playerRef, @NonNull NPCEntity npcEntity, CameraPosition cameraPosition) {
        TransformComponent npcEntityTransform = npcEntity.getReference().getStore().getComponent(npcEntity.getReference(), TransformComponent.getComponentType()); //store.getComponent(npcEntity.getReference(), TransformComponent.getComponentType());
        Vector3d npcPos = npcEntityTransform.getPosition().clone().add(0, 1.8,0);

        TransformComponent playerTransform = playerRef.getReference().getStore().getComponent(playerRef.getReference(), TransformComponent.getComponentType()); // store.getComponent(playerRef.getReference(), TransformComponent.getComponentType());
        Vector3d playerHeadPos = playerTransform.getPosition().clone().add(0d, 1.8d, 0d);

        ServerCameraSettings settings = createServerCameraSettings();


        Vector3d vectorDir = playerHeadPos.clone().subtract(npcPos.clone());
        Vector3d offsetDir = vectorDir.clone().normalize().rotateY((float)Math.toRadians(90)).scale(2f);
        offsetDir.y = 0;
        Vector3d cameraPos = playerHeadPos.clone();
        Vector3d finalVectorDir = cameraPos.clone().subtract(npcPos.clone());

        switch (cameraPosition){
            case CameraPosition.FPSLookAt:
                settings.isFirstPerson = true;
                break;
            case LeftShoulder:
                cameraPos.add(offsetDir.clone()).add(vectorDir.clone().normalize().scale(-0.5f));
                finalVectorDir = cameraPos.clone().subtract(npcPos.clone());
                finalVectorDir.rotateY((float)Math.toRadians(0));
                break;
            case RightShoulder:
                cameraPos.add(offsetDir.clone()).add(vectorDir.clone().normalize().scale(0.5f));
                finalVectorDir = cameraPos.clone().subtract(npcPos.clone());
                finalVectorDir.rotateY((float)Math.toRadians(0));
                break;
        }
        settings.rotationLerpSpeed = 0.05f;
        settings.positionLerpSpeed = 0.05f;
        settings.rotation = directionToRotation(finalVectorDir.clone().toVector3f());
        settings.position = PositionUtil.toPositionPacket(cameraPos);

        // set players camera to look at them
        playerRef.getPacketHandler().writeNoCache(
                new SetServerCamera(ClientCameraView.Custom, true, settings)
        );
    }

    public static void resetCam(PlayerRef playerRef) {
        TransformComponent playerTransform = playerRef.getComponent(TransformComponent.getComponentType());

        ServerCameraSettings settings = createServerCameraSettings();
        settings.rotationLerpSpeed = 0.1f;
        settings.positionLerpSpeed = 0.1f;
        settings.position = PositionUtil.toPositionPacket(playerTransform.getPosition().clone().add(0,1.8f,0));
        settings.rotation = playerTransform.getSentTransform().lookOrientation;

        playerRef.getPacketHandler().writeNoCache(
                new SetServerCamera(ClientCameraView.Custom, false, settings)
        );

        // delay 0.5f seconds
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                playerRef.getPacketHandler().writeNoCache(
                        new SetServerCamera(ClientCameraView.FirstPerson, false, null)
                );
            }
        }, 500);
    }

    // createServerCameraSettings definition
    private static ServerCameraSettings createServerCameraSettings() {
        ServerCameraSettings settings = new ServerCameraSettings();
        settings.isFirstPerson = false;

        settings.allowPitchControls = false;
        settings.displayCursor = false;
        settings.sendMouseMotion = false;
        settings.lookMultiplier = new Vector2f(0,0);
        settings.positionType = PositionType.Custom;
        settings.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffsetRaycast;

        // Force the camera's rotation to be set by the server.
        settings.applyLookType = ApplyLookType.Rotation;
        // Notify that we provide a custom rotation in "settings.rotation"
        settings.rotationType = RotationType.Custom;
        return settings;
    }

    private static Direction directionToRotation(Vector3f vector3f) {
        // normalize (optional but recommended)
        double length = Math.sqrt(vector3f.x * vector3f.x + vector3f.y * vector3f.y + vector3f.z * vector3f.z);
        if (length == 0) return new Direction(0, 0, 0);

        vector3f.x /= length;
        vector3f.y /= length;
        vector3f.z /= length;

        // yaw (rotation around Y axis)
        float yaw = (float)Math.atan2(vector3f.x, vector3f.z);

        // pitch (rotation around X axis)
        float pitch = (float)Math.asin(-vector3f.y);

        // roll cannot be determined from a direction vector alone
        float roll = 0;

        return new Direction(yaw, pitch, roll);
    }
}
