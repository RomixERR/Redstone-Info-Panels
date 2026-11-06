package com.redstoneinfopanels;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import java.util.List;

public class RedstoneInfoPanelRenderer
        implements BlockEntityRenderer<RedstoneInfoPanelBlockEntity> {

    public RedstoneInfoPanelRenderer(
            BlockEntityRendererProvider.Context context) {
    }



    @Override
    public void render(
            RedstoneInfoPanelBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay) {

        String text = blockEntity.getTitle() + blockEntity.getText();

        // Поддержка ручного переноса строки:
        // последовательность \n, введённая пользователем,
        // превращается в настоящий перевод строки.
        text = text.replace("\\n", "\n");

        if (text.isEmpty()) {
            return;
        }
        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);

        Direction facing = blockEntity.getBlockState().getValue(
                RedstoneInfoPanelBlock.FACING
        );

        switch (facing) {

            case NORTH -> {
                poseStack.translate(0.0D, 0.0D, +0.370D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
            }

            case SOUTH -> {
                poseStack.translate(0.0D, 0.0D, -0.370D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
            }

            case EAST -> {
                poseStack.translate(-0.370D, 0.0D, 0.0D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(270));
            }

            case WEST -> {
                poseStack.translate(+0.370D, 0.0D, 0.0D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
            }

            case UP -> {
                poseStack.translate(0.0D, -0.370D, 0.0D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
            }

            case DOWN -> {
                poseStack.translate(0.0D, 0.370D, 0.0D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(270));
            }
        }

        // Получаем тот же профиль, который используется для текущего сигнала.
        PanelEntry entry = blockEntity.getEntryForSignal(
                blockEntity.getLastSignal()
        );

        int textColor = 0xFFFFFF;
        int textSize = 0;

        if (entry != null) {
            textColor = entry.getTextColor();
            textSize = entry.getTextSize();
        }

        // Обычный: примерно 15 символов.
        // Крупный: примерно 8 символов.
        int maxWidth = textSize == 1 ? 70 : 95; //максимальная ширина строки для крупного и обычного текста

        int maxLines = textSize == 1 ? 4 : 6;

        List<String> lines = wrapText(
                text,
                font,
                maxWidth,
                maxLines
        );

        float scale = textSize == 1 ? 0.015F : 0.01F;

        poseStack.scale(scale, scale, scale);

        int lineHeight = textSize == 1 ? 12 : 9;

        // Верхний отступ текста относительно верхнего края панели.
        // Значение задаётся в координатах панели, а не в координатах шрифта.
        final float TOP_OFFSET = 0.10F;

        float startY = -(0.5F - TOP_OFFSET) / scale;

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);

            Component component = Component.literal(line);

            float textWidth = font.width(component);

            // Центрируем каждую строку отдельно.
            float x = -textWidth / 2.0F;

            float y = startY + i * lineHeight;

            font.drawInBatch(
                    component,
                    x,
                    y,
                    textColor,
                    true,
                    poseStack.last().pose(),
                    buffer,
                    false,
                    0,
                    packedLight
            );
        }

        poseStack.popPose();
    }


    private List<String> wrapText(
            String text,
            Font font,
            int maxWidth,
            int maxLines) {

        List<String> lines = new java.util.ArrayList<>();

        // Сначала учитываем принудительные переводы строк.
        String[] paragraphs = text.split("\\R", -1);

        for (String paragraph : paragraphs) {

            String[] words = paragraph.trim().split("\\s+");

            // Пустая строка — сохраняем её как явный перенос.
            if (paragraph.trim().isEmpty()) {

                if (lines.size() < maxLines) {
                    lines.add("");
                }

                continue;
            }

            String currentLine = "";

            for (String word : words) {

                String testLine = currentLine.isEmpty()
                        ? word
                        : currentLine + " " + word;

                if (font.width(Component.literal(testLine)) <= maxWidth) {

                    currentLine = testLine;

                } else {

                    if (!currentLine.isEmpty()) {
                        lines.add(currentLine);
                    }

                    currentLine = word;

                    if (lines.size() >= maxLines) {
                        return lines;
                    }
                }
            }

            if (!currentLine.isEmpty()) {
                lines.add(currentLine);
            }

            if (lines.size() >= maxLines) {
                return lines;
            }
        }

        return lines;
    }


}