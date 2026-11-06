package com.redstoneinfopanels;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class RedstoneInfoPanelBlockEntity extends BlockEntity {

    // Координаты подключённого источника Redstone.
    // null = источник ещё не подключён.
    private BlockPos sourcePos = null;
    private String title = "RIP\\n";
    private int currentSignal = 0;

    // 16 строк для сигналов 0–15.
    private final PanelEntry[] entries = new PanelEntry[16];

    // Служебный счётчик тиков.
    // Его НЕ сохраняем в NBT.
    private int tickCounter = 0;
    private int lastSignal = -1;

    // Текущий отображаемый текст.
    private String text = "Не активна";

    public RedstoneInfoPanelBlockEntity(BlockPos pos, BlockState state) {
        super(
                RedstoneInfoPanels.REDSTONE_INFO_PANEL_BLOCK_ENTITY.get(),
                pos,
                state
        );

        // Начальные значения.
        for (int i = 0; i < 16; i++) {
            entries[i] = new PanelEntry();
        }

        entries[0].setText("ВЫКЛЮЧЕНО");
        entries[0].setEnabled(true);
        entries[1].setText("ВКЛЮЧЕНО");
        entries[1].setEnabled(true);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setChanged();
    }

    public String getText() {
        return text;
    }
    public int getLastSignal() {
        return lastSignal;
    }
    public void refreshSignal() {
        lastSignal = -1;
    }
    public PanelEntry getEntry(int signal) {
        return entries[signal];
    }
    public void setText(String text) {
        this.text = text;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }

    public BlockPos getSourcePos() {
        return sourcePos;
    }

    public void setSourcePos(BlockPos sourcePos) {
        this.sourcePos = sourcePos;
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(
                    worldPosition,
                    getBlockState(),
                    getBlockState(),
                    3
            );
        }
    }


    public String getEntryText(int signal) {
        return entries[signal].getText();
    }

    public void setEntryText(int signal, String text) {
        entries[signal].setText(text);
        setChanged();
    }


    public boolean isEntryEnabled(int signal) {
        return entries[signal].isEnabled();
    }

    public void setEntryEnabled(int signal, boolean enabled) {
        entries[signal].setEnabled(enabled);
        setChanged();
    }

    /**
     * Возвращает entry, соответствующий сигналу Redstone.
     *
     * Если строка для текущего сигнала отключена,
     * ищем ближайшую предыдущую активную строку.
     */
    public PanelEntry getEntryForSignal(int signal) {

        for (int i = signal; i >= 0; i--) {

            if (entries[i].isEnabled()) {
                return entries[i];
            }
        }

        return null;
    }

    /**
     * Вызывается примерно 20 раз в секунду.
     * Пока оставляем тикер как основу для будущего
     * опроса Redstone.
     */
    public static void tick(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            BlockEntity blockEntity) {

        if (level.isClientSide) {
            return;
        }

        if (!(blockEntity instanceof RedstoneInfoPanelBlockEntity panel)) {
            return;
        }

        panel.tickCounter++;

        // Проверяем Redstone раз в 5 тиков (4 раза в секунду)
        if (panel.tickCounter < 5) {
            return;
        }

        panel.tickCounter = 0;

        // Нет привязанного источника
        if (panel.sourcePos == null) {
            return;
        }

        // Если источник не загружен, пока ничего не делаем
        if (!level.isLoaded(panel.sourcePos)) {
            return;
        }

        int signal = 0;

        // Проверяем все 6 сторон источника
        for (net.minecraft.core.Direction direction :
                net.minecraft.core.Direction.values()) {

            signal = Math.max(
                    signal,
                    level.getSignal(panel.sourcePos, direction)
            );
        }

        if (signal != panel.lastSignal) {

            panel.lastSignal = signal;

            // Получаем активный профиль.
            // Здесь же определяется fallback на предыдущую
            // включённую строку.
            PanelEntry entry = panel.getEntryForSignal(signal);

            int faceColor = 0;

            if (entry != null) {

                panel.setText(entry.getText());

                // Переводим RGB FaceColor в индекс BlockState:
                // 0 = белый
                // 1 = зелёный
                // 2 = жёлтый
                // 3 = красный

                if (entry.getFaceColor() == 0x55FF55) {
                    faceColor = 1;
                } else if (entry.getFaceColor() == 0xFFFF55) {
                    faceColor = 2;
                } else if (entry.getFaceColor() == 0xFF5555) {
                    faceColor = 3;
                }

            } else {

                panel.setText("");
            }

            // Меняем цвет всей модели только если он действительно изменился.
            if (state.getValue(RedstoneInfoPanelBlock.FACE_COLOR) != faceColor) {

                level.setBlock(
                        pos,
                        state.setValue(
                                RedstoneInfoPanelBlock.FACE_COLOR,
                                faceColor
                        ),
                        3
                );
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);
        tag.putString("Title", title);
        tag.putString("Text", text);
        tag.putInt("LastSignal", lastSignal);
        // Позиция источника Redstone
        if (sourcePos != null) {

            CompoundTag sourceTag = new CompoundTag();

            sourceTag.putInt("X", sourcePos.getX());
            sourceTag.putInt("Y", sourcePos.getY());
            sourceTag.putInt("Z", sourcePos.getZ());

            tag.put("SourcePos", sourceTag);
        }

        // Все 16 профилей Redstone
        ListTag entriesTag = new ListTag();

        for (int i = 0; i < 16; i++) {

            PanelEntry entry = entries[i];

            CompoundTag entryTag = new CompoundTag();

            entryTag.putInt("Signal", i);
            entryTag.putBoolean("Enabled", entry.isEnabled());
            entryTag.putString("Text", entry.getText());
            entryTag.putInt("TextSize", entry.getTextSize());
            entryTag.putInt("TextColor", entry.getTextColor());
            entryTag.putInt("FaceColor", entry.getFaceColor());

            entriesTag.add(entryTag);
        }

        tag.put("Entries", entriesTag);
    }

    @Override
    public void load(CompoundTag tag) {

        super.load(tag);
        title = tag.getString("Title");
        text = tag.getString("Text");
        lastSignal = tag.getInt("LastSignal");
        // Создаём 16 профилей
        for (int i = 0; i < 16; i++) {
            entries[i] = new PanelEntry();
        }

        // Источник Redstone
        if (tag.contains("SourcePos")) {

            CompoundTag sourceTag = tag.getCompound("SourcePos");

            sourcePos = new BlockPos(
                    sourceTag.getInt("X"),
                    sourceTag.getInt("Y"),
                    sourceTag.getInt("Z")
            );

        } else {
            sourcePos = null;
        }

        // Профили Redstone
        if (tag.contains("Entries")) {

            ListTag entriesTag = tag.getList(
                    "Entries",
                    Tag.TAG_COMPOUND
            );

            for (int i = 0; i < entriesTag.size(); i++) {

                CompoundTag entryTag = entriesTag.getCompound(i);

                int signal = entryTag.getInt("Signal");

                if (signal < 0 || signal >= 16) {
                    continue;
                }

                PanelEntry entry = entries[signal];

                entry.setEnabled(
                        entryTag.getBoolean("Enabled")
                );

                entry.setText(
                        entryTag.getString("Text")
                );

                entry.setTextSize(
                        entryTag.getInt("TextSize")
                );

                entry.setTextColor(
                        entryTag.getInt("TextColor")
                );

                entry.setFaceColor(
                        entryTag.getInt("FaceColor")
                );
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}