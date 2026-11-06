package com.redstoneinfopanels;

public class PanelEntry {

    // Включена ли эта строка
    private boolean enabled;

    // Текст сообщения
    private String text;

    // Размер текста:
    // 0 = обычный
    // 1 = крупный
    private int textSize;

    // Цвет текста в формате RGB
    private int textColor;

    // Цвет лицевой области панели в формате RGB
    private int faceColor;


    public PanelEntry() {

        this.enabled = false;
        this.text = "";
        this.textSize = 0;

        // Белый текст
        this.textColor = 0xFFFFFF;

        // Тёмная лицевая область
        this.faceColor = 0x202020;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }


    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    public int getFaceColor() {
        return faceColor;
    }

    public void setFaceColor(int faceColor) {
        this.faceColor = faceColor;
    }
}