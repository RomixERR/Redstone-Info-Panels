package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class PanelSavePacket {

    private final BlockPos panelPos;
    private final String title;
    private final String[] texts;
    private final boolean[] enabled;

    private final int[] textSizes;
    private final int[] textColors;
    private final int[] faceColors;


    public PanelSavePacket(
            BlockPos panelPos,
            String title,
            String[] texts,
            boolean[] enabled,
            int[] textSizes,
            int[] textColors,
            int[] faceColors) {

        this.panelPos = panelPos;
        this.title = title;

        this.texts = texts;
        this.enabled = enabled;

        this.textSizes = textSizes;
        this.textColors = textColors;
        this.faceColors = faceColors;
    }


    // Клиент → сервер
    public PanelSavePacket(FriendlyByteBuf buffer) {

        this.panelPos = buffer.readBlockPos();
        this.title = buffer.readUtf(1024);
        this.texts = new String[16];
        this.enabled = new boolean[16];

        this.textSizes = new int[16];
        this.textColors = new int[16];
        this.faceColors = new int[16];

        for (int i = 0; i < 16; i++) {

            texts[i] = buffer.readUtf(1024);
            enabled[i] = buffer.readBoolean();

            textSizes[i] = buffer.readInt();
            textColors[i] = buffer.readInt();
            faceColors[i] = buffer.readInt();
        }
    }


    // Кодирование пакета
    public void encode(FriendlyByteBuf buffer) {

        buffer.writeBlockPos(panelPos);
        buffer.writeUtf(title, 1024);
        for (int i = 0; i < 16; i++) {

            buffer.writeUtf(texts[i], 1024);
            buffer.writeBoolean(enabled[i]);

            buffer.writeInt(textSizes[i]);
            buffer.writeInt(textColors[i]);
            buffer.writeInt(faceColors[i]);
        }
    }


    public BlockPos getPanelPos() {
        return panelPos;
    }


    public String[] getTexts() {
        return texts;
    }


    public boolean[] getEnabled() {
        return enabled;
    }


    public int[] getTextSizes() {
        return textSizes;
    }


    public int[] getTextColors() {
        return textColors;
    }


    public int[] getFaceColors() {
        return faceColors;
    }

    public String getTitle() {
        return title;
    }
}