package com.redstoneinfopanels;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.components.Button;

public class RedstoneInfoPanelScreen extends AbstractContainerScreen<RedstoneInfoPanelMenu> {

    private static final int ROW_HEIGHT = 20;
    private static final int VISIBLE_ROWS = 8;

    private Checkbox[] checkboxes = new Checkbox[16];
    private EditBox[] textFields = new EditBox[16];
    private Button[] sizeButtons = new Button[16];
    private Button[] colorButtons = new Button[16];
    private Button[] colorFaceButtons = new Button[16];
    private EditBox titleField;

    private int scrollOffset = 0;

    public RedstoneInfoPanelScreen(
            RedstoneInfoPanelMenu menu,
            Inventory playerInventory,
            Component title) {

        super(menu, playerInventory, title);

        this.imageWidth = 330;
        this.imageHeight = 240;
    }

    @Override
    protected void init() {
        super.init();

        RedstoneInfoPanelBlockEntity panel;

        if (minecraft.level != null) {
            if (minecraft.level.getBlockEntity(menu.getPanelPos())
                    instanceof RedstoneInfoPanelBlockEntity blockEntity) {

                panel = blockEntity;
            } else {
                panel = null;
            }
        } else {
            panel = null;
        }

        titleField = new EditBox(
                this.font,
                leftPos + 120,
                topPos + 2,
                190,
                18,
                Component.literal("Название панели")
        );

        titleField.setMaxLength(90);

        if (panel != null) {
            titleField.setValue(panel.getTitle());
        }

        addRenderableWidget(titleField);


        for (int i = 0; i < 16; i++) {

            final int signal = i;

            checkboxes[i] = new Checkbox(
                    leftPos + 55,
                    0,
                    18,
                    18,
                    Component.literal(""),
                    panel != null && panel.isEntryEnabled(i)
            );

            addRenderableWidget(checkboxes[i]);

            textFields[i] = new EditBox(
                    this.font,
                    leftPos + 157,
                    0,
                    170,
                    18,
                    Component.literal("Текст")
            );
            textFields[i].setMaxLength(90);

            if (panel != null) {
                textFields[i].setValue(panel.getEntryText(i));
            }

            addRenderableWidget(textFields[i]);

            int textSize = 0;

            if (panel != null) {
                textSize = panel.getEntry(i).getTextSize();
            }

            sizeButtons[i] = new Button(
                    leftPos + 75,
                    0,
                    25,
                    18,
                    Component.literal(textSize == 0 ? "О" : "К"),
                    button -> {
                        PanelEntry entry = panel != null
                                ? panel.getEntry(signal)
                                : null;

                        if (entry != null) {

                            int newSize =
                                    entry.getTextSize() == 0 ? 1 : 0;

                            entry.setTextSize(newSize);

                            button.setMessage(
                                    Component.literal(
                                            newSize == 0 ? "О" : "К"
                                    )
                            );
                        }
                    }
            );

            addRenderableWidget(sizeButtons[i]);

            int textColor = 0xFFFFFF;

            if (panel != null) {
                textColor = panel.getEntry(i).getTextColor();
            }

            colorButtons[i] = new Button(
                    leftPos + 102,
                    0,
                    25,
                    18,
                    Component.literal(getColorName(textColor)),
                    button -> {

                        PanelEntry entry = panel != null
                                ? panel.getEntry(signal)
                                : null;

                        if (entry != null) {

                            int newColor = getNextColor(
                                    entry.getTextColor()
                            );

                            entry.setTextColor(newColor);

                            button.setMessage(
                                    Component.literal(
                                            getColorName(newColor)
                                    )
                            );
                        }
                    }
            );

            addRenderableWidget(colorButtons[i]);

            int faceColor = 0x202020;

            if (panel != null) {
                faceColor = panel.getEntry(i).getFaceColor();
            }

            colorFaceButtons[i] = new Button(
                    leftPos + 129,
                    0,
                    25,
                    18,
                    Component.literal(getColorName(faceColor)),
                    button -> {

                        PanelEntry entry = panel != null
                                ? panel.getEntry(signal)
                                : null;

                        if (entry != null) {

                            int newColor = getNextColor(
                                    entry.getFaceColor()
                            );

                            entry.setFaceColor(newColor);

                            button.setMessage(
                                    Component.literal(
                                            getColorName(newColor)
                                    )
                            );
                        }
                    }
            );

            addRenderableWidget(colorFaceButtons[i]);

        }

        Button saveButton = new Button(
                leftPos + 115,
                topPos + 214,
                100,
                20,
                Component.literal("Сохранить"),
                button -> savePanel()
        );

        addRenderableWidget(saveButton);

        updateRowPositions();
    }

    private void updateRowPositions() {

        for (int i = 0; i < 16; i++) {

            int row = i - scrollOffset;

            boolean visible = row >= 0 && row < VISIBLE_ROWS;

            checkboxes[i].visible = visible;
            textFields[i].visible = visible;
            sizeButtons[i].visible = visible;
            colorButtons[i].visible = visible;
            colorFaceButtons[i].visible = visible;

            if (visible) {

                int y = topPos + 42 + row * ROW_HEIGHT;

                checkboxes[i].x = leftPos + 55;
                checkboxes[i].y = y;

                sizeButtons[i].x = leftPos + 75;
                sizeButtons[i].y = y;

                colorButtons[i].x = leftPos + 102;
                colorButtons[i].y = y;

                colorFaceButtons[i].x = leftPos + 129;
                colorFaceButtons[i].y = y;

                textFields[i].x = leftPos + 157;
                textFields[i].y = y;
            }
        }
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double delta) {

        if (mouseX >= leftPos
                && mouseX <= leftPos + imageWidth
                && mouseY >= topPos
                && mouseY <= topPos + imageHeight) {

            if (delta < 0) {
                scrollOffset++;
            } else if (delta > 0) {
                scrollOffset--;
            }

            scrollOffset = Math.max(
                    0,
                    Math.min(8, scrollOffset)
            );

            updateRowPositions();

            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void renderBg(
            PoseStack poseStack,
            float partialTick,
            int mouseX,
            int mouseY) {

        // Основной фон
        fill(
                poseStack,
                leftPos,
                topPos,
                leftPos + imageWidth,
                topPos + imageHeight,
                0xFF202020
        );

        // Заголовок таблицы
        fill(
                poseStack,
                leftPos + 8,
                topPos + 24,
                leftPos + imageWidth - 8,
                topPos + 38,
                0xFF303030
        );

        // Область строк
        fill(
                poseStack,
                leftPos + 8,
                topPos + 40,
                leftPos + imageWidth - 8,
                topPos + 204,
                0xFF181818
        );

        // Полоса прокрутки
        fill(
                poseStack,
                leftPos + imageWidth - 15,
                topPos + 42,
                leftPos + imageWidth - 8,
                topPos + 202,
                0xFF303030
        );

        // Ползунок
        int scrollbarY =
                topPos + 42 + (scrollOffset * 160 / 8);

        fill(
                poseStack,
                leftPos + imageWidth - 15,
                scrollbarY,
                leftPos + imageWidth - 8,
                scrollbarY + 20,
                0xFF808080
        );
    }

    @Override
    protected void renderLabels(
            PoseStack poseStack,
            int mouseX,
            int mouseY) {

        drawString(
                poseStack,
                this.font,
                "REDSTONE INFO PANEL",
                8,
                8,
                0xFFFFFF
        );

        drawString(
                poseStack,
                this.font,
                "Сигнал",
                12,
                27,
                0xFF2020
        );

        drawString(
                poseStack,
                this.font,
                "Вкл.",
                55,
                27,
                0xFFFFFF
        );

        drawString(
                poseStack,
                this.font,
                "Разм.",
                79,
                27,
                0xFFFFFF
        );

        drawString(
                poseStack,
                this.font,
                "Цвет.",
                115,
                27,
                0x500050
        );

        drawString(
                poseStack,
                this.font,
                "Текст для сигнала.",
                150,
                27,
                0xFFFFFF
        );

        // Номера сигналов
        for (int i = 0; i < VISIBLE_ROWS; i++) {

            int signal = i + scrollOffset;

            drawString(
                    poseStack,
                    this.font,
                    String.valueOf(signal),
                    25,
                    46 + i * ROW_HEIGHT,
                    0xFFFFFF
            );
        }
    }

    private void savePanel() {
        String title = titleField.getValue();
        String[] texts = new String[16];
        boolean[] enabled = new boolean[16];

        int[] textSizes = new int[16];
        int[] textColors = new int[16];
        int[] faceColors = new int[16];


        RedstoneInfoPanelBlockEntity panel = null;

        if (minecraft.level != null) {

            if (minecraft.level.getBlockEntity(menu.getPanelPos())
                    instanceof RedstoneInfoPanelBlockEntity blockEntity) {

                panel = blockEntity;
            }
        }


        for (int i = 0; i < 16; i++) {

            texts[i] = textFields[i].getValue();
            enabled[i] = checkboxes[i].selected();

            if (panel != null) {

                PanelEntry entry = panel.getEntry(i);

                textSizes[i] = entry.getTextSize();
                textColors[i] = entry.getTextColor();
                faceColors[i] = entry.getFaceColor();
            }
        }


        ModNetwork.CHANNEL.sendToServer(
                new PanelSavePacket(
                        menu.getPanelPos(),
                        title,
                        texts,
                        enabled,
                        textSizes,
                        textColors,
                        faceColors
                )
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (this.getFocused() instanceof EditBox
                && keyCode == this.minecraft.options.keyInventory.getKey().getValue()) {

            return this.getFocused().keyPressed(
                    keyCode,
                    scanCode,
                    modifiers
            );
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int getNextColor(int currentColor) {

        if (currentColor == 0xFFFFFF) {
            return 0x55FF55;
        }

        if (currentColor == 0x55FF55) {
            return 0xFFFF55;
        }

        if (currentColor == 0xFFFF55) {
            return 0xFF5555;
        }

        return 0xFFFFFF;
    }

    private String getColorName(int color) {

        if (color == 0x55FF55) {
            return "З";
        }

        if (color == 0xFFFF55) {
            return "Ж";
        }

        if (color == 0xFF5555) {
            return "К";
        }

        return "Б";
    }
}