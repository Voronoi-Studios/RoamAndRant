package com.png.RoamAndRant.Core.UI;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.png.RoamAndRant.Core.Camera.CameraPositioner;
import com.png.RoamAndRant.Core.Dialog.DialogAsset;
import com.png.RoamAndRant.Core.Dialog.DialogButton;
import com.png.RoamAndRant.Core.Dialog.DialogEntry;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNullElse;

public class UIDialogPage extends InteractiveCustomUIPage<UIDialogPage.PageEventData> {
    private static final Logger LOGGER = Logger.getLogger(UIDialogPage.class.getName());

    NPCEntity npcEntity;
    DialogAsset dialogAsset;
    Map<String, DialogEntry> dialogEntryMap;

    List<DialogEntry> dialogList = new LinkedList<>();


    public UIDialogPage(@Nonnull NPCEntity npcEntity, @Nonnull PlayerRef playerRef, String dialogId) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageEventData.CODEC);
        this.npcEntity = npcEntity;
        this.dialogAsset = DialogAsset.getAssetMap().getAsset(dialogId);
        this.dialogEntryMap = dialogAsset != null ? dialogAsset.getDialogEntries() : new HashMap<>();
    }

    @Override
    public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        CameraPositioner.resetCam(playerRef);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/UIDialog/UIDialogPage.ui");
        commandBuilder.set("#DialogTitle.TextSpans", StringToMessage(npcEntity.getRole().getNameTranslationKey()));

        commandBuilder.clear("#DialogList");
        if (dialogEntryMap.isEmpty()) return;
        DialogEntry start = dialogEntryMap.values().stream().filter(DialogEntry::getIsStart).findFirst().orElse(dialogEntryMap.values().stream().toList().getFirst());
        addDialogEntryToList(commandBuilder, eventBuilder, start.getId());
    }

    private void addDialogEntryToList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, String nextId) {
        while (!nextId.isEmpty() && dialogEntryMap.containsKey(nextId)){
            DialogEntry dialogEntry = dialogEntryMap.get(nextId);
            int index = dialogList.size();

            String animationID = dialogEntry.getAnimationId();
            if(animationID != null) npcEntity.playAnimation(npcEntity.getReference(), AnimationSlot.Status, animationID, npcEntity.getReference().getStore());

            Message name = StringToMessage(dialogEntry.getNameOverride());
            if(name.getMessageId() == null) name = StringToMessage(npcEntity.getRole().getNameTranslationKey());

            if(name.getMessageId().toLowerCase().equals("you")) commandBuilder.append("#DialogList", "Pages/UIDialog/Dialogs/DialogPlayer.ui");
            else commandBuilder.append("#DialogList", "Pages/UIDialog/Dialogs/Dialog.ui");

            commandBuilder.set("#DialogList[" + index + "] #Speaker.TextSpans", name);
            commandBuilder.set("#DialogList[" + index + "] #DialogText.TextSpans", StringToMessage(dialogEntry.getDialogText()).param("username", playerRef.getUsername()));

            DialogButton[] dialogButtons = dialogEntry.getDialogButtons();
            if(dialogButtons != null) { //Add dialog reference to this one
                commandBuilder.append("#DialogList[" + index + "] #AnswerContainer", "Pages/UIDialog/Dialogs/DialogAnswer.ui");
                for(int i = 0; i < dialogButtons.length; i++){
                    addDialogButtonToList(commandBuilder, eventBuilder, dialogButtons[i], index, i);
                }
            }

            dialogList.add(dialogEntry);
            nextId = requireNonNullElse(dialogEntry.getNextId(), "");
        }
    }

    private void addDialogButtonToList(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, DialogButton dialogButton, int index, int i) {
        String selector = "#DialogList[" + index + "] #AnswerContainer #AnswerList";
        commandBuilder.append(selector, "Pages/UIDialog/Dialogs/DialogButton.ui");

        commandBuilder.set(selector + "[" + i + "] #DialogButton.TextSpans", StringToMessage(dialogButton.getButtonText()));
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                selector + "[" + i + "] #DialogButton",
                PageEventData.getNew(index, dialogButton)
        );
    }
    private void removeDialogButtons(UICommandBuilder commandBuilder, int index) {
        String selector = "#DialogList[" + index + "] #AnswerContainer";
        commandBuilder.remove(selector);
    }


    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();

        if(!data.runInteractionWithId.isEmpty()) {
            InteractionManager manager = store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
            if (manager == null) {
                LOGGER.warning("bolt: no interaction manager on caster, skipping block interaction");
                return;
            }

            RootInteraction rootInteraction = RootInteraction.getAssetMap().getAsset(data.runInteractionWithId);
            if (rootInteraction == null) { return; }

            InteractionContext ctx = InteractionContext.forInteraction(manager, ref, InteractionType.Use, store);
            ctx.getMetaStore().putMetaObject(Interaction.TARGET_ENTITY, npcEntity.getReference());
            InteractionChain chain = manager.initChain(InteractionType.Use, ctx, rootInteraction, false);
            manager.queueExecuteChain(chain);
        }

        if (!data.nextId.isEmpty() && dialogEntryMap.containsKey(data.nextId)) {
            removeDialogButtons(commandBuilder, data.buttonEntryIndex);
            addDialogEntryToList(commandBuilder, eventBuilder, data.nextId);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        }

        if (data.exit) { this.close(); }
    }



    public static class PageEventData {
        @Nonnull
        public static final String KEY_BUTTON_ENTRY_INDEX = "ButtonEntryIndex";
        @Nonnull
        public static final String KEY_RUN_INTERACTION = "RunInteraction";
        @Nonnull
        public static final String KEY_NEXT_ID = "NextId";
        @Nonnull
        public static final String KEY_EXIT = "Exit";
        @Nonnull
        public static final BuilderCodec<UIDialogPage.PageEventData> CODEC = BuilderCodec.builder(
                        UIDialogPage.PageEventData.class, UIDialogPage.PageEventData::new
                )
                .append(new KeyedCodec<>(PageEventData.KEY_BUTTON_ENTRY_INDEX, Codec.STRING), (pageEventData, s) -> pageEventData.buttonEntryIndex = Integer.parseInt(s), pageEventData -> Integer.toString(pageEventData.buttonEntryIndex))
                .add()
                .append(new KeyedCodec<>(PageEventData.KEY_RUN_INTERACTION, Codec.STRING), (pageEventData, s) -> pageEventData.runInteractionWithId = s, pageEventData -> pageEventData.runInteractionWithId)
                .add()
                .append(new KeyedCodec<>(PageEventData.KEY_NEXT_ID, Codec.STRING), (pageEventData, s) -> pageEventData.nextId = s, pageEventData -> pageEventData.nextId)
                .add()
                .append(new KeyedCodec<>(PageEventData.KEY_EXIT, Codec.STRING), (pageEventData, s) -> pageEventData.exit = Boolean.parseBoolean(s), pageEventData -> Boolean.toString(pageEventData.exit))
                .add()
                .build();
        public int buttonEntryIndex;
        public String runInteractionWithId;
        public String nextId;
        public boolean exit;

        public static EventData getNew(int dialogListIndex, DialogButton dialogButton) {
            return new EventData()
                    .append(PageEventData.KEY_BUTTON_ENTRY_INDEX, Integer.toString(dialogListIndex))
                    .append(PageEventData.KEY_RUN_INTERACTION, requireNonNullElse(dialogButton.getInteractionId(), ""))
                    .append(PageEventData.KEY_NEXT_ID, requireNonNullElse(dialogButton.getNextId(), ""))
                    .append(PageEventData.KEY_EXIT, requireNonNullElse(Boolean.toString(dialogButton.getExit()), "false"));
        }
    }

    public static Message StringToMessage(String str){
        if(str == null) return Message.raw("");
        return Message.translation(str);
    }
}
