package com.png.RoamAndRant;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.png.RoamAndRant.Core.Camera.CameraPositioner;
import com.png.RoamAndRant.Core.Dialog.DialogButton;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNullElse;

public class UIStats extends InteractiveCustomUIPage<UIStats.PageEventData> {
    private static final Logger LOGGER = Logger.getLogger(UIStats.class.getName());

    private TestData data;

public record Level(int value, int xp, int xpMax) {
    public float asPercentage(){
        return (float)xp / (float)xpMax;
    }
}

    public record TestData(
            EntityStatMap entityStatMap,
            Level level,
            int unspendPoints,
            int stat1,
            int stat2,
            int stat3
    ) {}

    public UIStats(@Nonnull PlayerRef playerRef, TestData data) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageEventData.CODEC);
        CameraPositioner.selfieCam(playerRef);
        this.data = data;
    }

    @Override
    public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        CameraPositioner.resetCam(playerRef);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/UIDialog/UIStats/UIStatsPage.ui");

        buildStatsBars(commandBuilder);
        buildSkillGraph(commandBuilder);

        commandBuilder.set("#UnspendPoints.Text", Integer.toString(data.unspendPoints));
        commandBuilder.set("#Divinity #ValueText.Text", Integer.toString(data.stat1));
        commandBuilder.set("#Time #ValueText.Text", Integer.toString(data.stat2));
        commandBuilder.set("#Energy #ValueText.Text", Integer.toString(data.stat3));
    }

    private void buildStatsBars(@NonNull UICommandBuilder commandBuilder) {
        commandBuilder.set("#Level #Title.Text", "Level: " + (int)data.level.value);
        commandBuilder.set("#Level #ValueText.Text", Integer.toString((int)data.level.xp()));
        commandBuilder.set("#Level #MaxText.Text", Integer.toString((int)data.level.xpMax()));
        commandBuilder.set("#Level #ProgressBarDefault.Value", (double)data.level.asPercentage());

        EntityStatValue health = data.entityStatMap.get(DefaultEntityStatTypes.getHealth());
        EntityStatValue stamina = data.entityStatMap.get(DefaultEntityStatTypes.getStamina());
        EntityStatValue mana = data.entityStatMap.get(DefaultEntityStatTypes.getMana());

        commandBuilder.set("#HP #ValueText.Text", Integer.toString((int)health.get()));
        commandBuilder.set("#HP #MaxText.Text", Integer.toString((int)health.getMax()));
        commandBuilder.set("#HP #ProgressBarRed.Value", (double)health.asPercentage());

        commandBuilder.set("#Stamina #ValueText.Text", Integer.toString((int)stamina.get()));
        commandBuilder.set("#Stamina #MaxText.Text", Integer.toString((int)stamina.getMax()));
        commandBuilder.set("#Stamina #ProgressBarYellow.Value", (double)stamina.asPercentage());

        commandBuilder.set("#Mana #ValueText.Text", Integer.toString((int)mana.get()));
        commandBuilder.set("#Mana #MaxText.Text", Integer.toString((int)mana.getMax()));
        commandBuilder.set("#Mana #ProgressBarBlue.Value", (double)mana.asPercentage());
    }

    private void buildSkillGraph(@NonNull UICommandBuilder commandBuilder) {
        int toPixel = 10; // max 10 values and full distance is 100

        Vector2d center = new Vector2d(173 / 2,150 / 3 * 2);

        double angle = Math.toRadians(30);

        Vector2d topPoint = center.clone().subtract(new Vector2d(0, data.stat1).scale(toPixel));
        Vector2d leftPoint = center.clone().add(new Vector2d(-Math.cos(angle), Math.sin(angle)).scale(data.stat2).scale(toPixel));
        Vector2d rightPoint = center.clone().add(new Vector2d(Math.cos(angle), Math.sin(angle)).scale(data.stat3).scale(toPixel));
        Vector2d bottomPoint = new Vector2d(0, Math.max(leftPoint.y, rightPoint.y));
        boolean right = rightPoint.y > leftPoint.y;

        //Points
        Anchor anchorTop = new Anchor();
        anchorTop.setLeft(Value.of((int)topPoint.x - 7));
        anchorTop.setTop(Value.of((int)topPoint.y - 6));
        anchorTop.setWidth(Value.of(15));
        anchorTop.setHeight(Value.of(15));
        commandBuilder.setObject("#TopPoint.Anchor", anchorTop);

        Anchor anchorLeft = new Anchor();
        anchorLeft.setLeft(Value.of((int)leftPoint.x - 7));
        anchorLeft.setTop(Value.of((int)leftPoint.y - 6));
        anchorLeft.setWidth(Value.of(15));
        anchorLeft.setHeight(Value.of(15));
        commandBuilder.setObject("#LeftPoint.Anchor", anchorLeft);

        Anchor anchorRight = new Anchor();
        anchorRight.setLeft(Value.of((int)rightPoint.x - 7));
        anchorRight.setTop(Value.of((int)rightPoint.y - 6));
        anchorRight.setWidth(Value.of(15));
        anchorRight.setHeight(Value.of(15));
        commandBuilder.setObject("#RightPoint.Anchor", anchorRight);

        //InnerSquare
        Anchor anchorInnerSquare = new Anchor();
        anchorInnerSquare.setLeft(Value.of((int)leftPoint.x));
        anchorInnerSquare.setTop(Value.of((int)topPoint.y));
        anchorInnerSquare.setWidth(Value.of((int)rightPoint.x - (int)leftPoint.x));
        anchorInnerSquare.setHeight(Value.of((int)bottomPoint.y - (int)topPoint.y));
        commandBuilder.setObject("#InnerSquare.Anchor", anchorInnerSquare);

        //Triangles
        Anchor anchorTopLeft = new Anchor();
        anchorTopLeft.setLeft(Value.of(0));
        anchorTopLeft.setTop(Value.of(0));
        anchorTopLeft.setWidth(Value.of((int)center.x - (int)leftPoint.x));
        anchorTopLeft.setHeight(Value.of((int)leftPoint.y - (int)topPoint.y));
        commandBuilder.setObject("#TopLeft.Anchor", anchorTopLeft);

        Anchor anchorTopRight = new Anchor();
        anchorTopRight.setRight(Value.of(0));
        anchorTopRight.setTop(Value.of(0));
        anchorTopRight.setWidth(Value.of((int)rightPoint.x - (int)center.x));
        anchorTopRight.setHeight(Value.of((int)rightPoint.y - (int)topPoint.y));
        commandBuilder.setObject("#TopRight.Anchor", anchorTopRight);

        Anchor anchorBottomLeft = new Anchor();
        anchorBottomLeft.setBottom(Value.of(0));
        anchorBottomLeft.setHeight(Value.of((int)bottomPoint.y - (int)leftPoint.y));
        commandBuilder.setObject("#BottomLeft.Anchor", anchorBottomLeft);
        commandBuilder.set("#BottomLeft.Visible", right);

        Anchor anchorBottomRight = new Anchor();
        anchorBottomRight.setBottom(Value.of(0));
        anchorBottomRight.setHeight(Value.of((int)bottomPoint.y - (int)rightPoint.y));
        commandBuilder.setObject("#BottomRight.Anchor", anchorBottomRight);
        commandBuilder.set("#BottomRight.Visible", !right);
    }


    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();

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
        public static final BuilderCodec<UIStats.PageEventData> CODEC = BuilderCodec.builder(
                        UIStats.PageEventData.class, UIStats.PageEventData::new
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
