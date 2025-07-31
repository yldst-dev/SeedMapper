package dev.xpple.seedmapper.gui;

import dev.xpple.seedmapper.config.Configs;
import dev.xpple.seedmapper.util.OreDisplayUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OreHighlightScreen extends Screen {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 500;
    private static final int CHECKBOX_HEIGHT = 20;
    private static final int ORES_PER_ROW = 2;

    private EditBox seedInput;
    private Button currentSeedButton;
    private OreListWidget oreListWidget;
    private Button selectAllButton;
    private Button clearAllButton;
    private Button calculateButton;
    private Button clearRenderButton;
    private Component statusText;
    
    private final Set<Integer> selectedOres = new HashSet<>();
    private OreDisplayUtils.OreCategory currentFilter = null;
    private CompletableFuture<Void> currentTask;
    private int chunkRange = 5;

    public OreHighlightScreen() {
        super(Component.translatable("gui.seedmapper.ore_highlight.title"));
        this.statusText = Component.translatable("gui.seedmapper.ore_highlight.status.ready");
        
        // 이전 설정 로드
        loadSettings();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = (this.height - WINDOW_HEIGHT) / 2 + 30;

        // 시드 입력 필드
        this.seedInput = new EditBox(this.font, centerX - 120, startY, 180, 20, Component.translatable("gui.seedmapper.ore_highlight.seed"));
        this.seedInput.setValue(getCurrentSeed());
        this.addRenderableWidget(this.seedInput);

        // 현재 시드 버튼
        this.currentSeedButton = Button.builder(Component.translatable("gui.seedmapper.ore_highlight.current_seed"), 
            button -> this.seedInput.setValue(getCurrentSeed()))
            .bounds(centerX + 70, startY, 60, 20)
            .build();
        this.addRenderableWidget(this.currentSeedButton);

        // 카테고리 필터 버튼들
        int filterY = startY + 30;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.seedmapper.ore_highlight.filter.all"), 
            button -> setFilter(null))
            .bounds(centerX - 160, filterY, 50, 20)
            .build());
        
        this.addRenderableWidget(Button.builder(Component.translatable("gui.seedmapper.ore_highlight.filter.common"), 
            button -> setFilter(OreDisplayUtils.OreCategory.COMMON))
            .bounds(centerX - 100, filterY, 60, 20)
            .build());
            
        this.addRenderableWidget(Button.builder(Component.translatable("gui.seedmapper.ore_highlight.filter.rare"), 
            button -> setFilter(OreDisplayUtils.OreCategory.RARE))
            .bounds(centerX - 30, filterY, 50, 20)
            .build());
            
        this.addRenderableWidget(Button.builder(Component.translatable("gui.seedmapper.ore_highlight.filter.nether"), 
            button -> setFilter(OreDisplayUtils.OreCategory.NETHER))
            .bounds(centerX + 30, filterY, 60, 20)
            .build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.seedmapper.ore_highlight.filter.special"), 
            button -> setFilter(OreDisplayUtils.OreCategory.SPECIAL))
            .bounds(centerX + 100, filterY, 60, 20)
            .build());

        // 광물 선택 리스트
        int listY = filterY + 30;
        this.oreListWidget = new OreListWidget(this.minecraft, WINDOW_WIDTH - 40, 200, listY, CHECKBOX_HEIGHT + 2);
        this.oreListWidget.setX(centerX - (WINDOW_WIDTH - 40) / 2);
        this.addWidget(this.oreListWidget);
        updateOreList();

        // 선택 버튼들
        int buttonY = listY + 210;
        this.selectAllButton = Button.builder(Component.translatable("gui.seedmapper.ore_highlight.select_all"),
            button -> selectAllVisible())
            .bounds(centerX - 100, buttonY, 60, 20)
            .build();
        this.addRenderableWidget(this.selectAllButton);

        this.clearAllButton = Button.builder(Component.translatable("gui.seedmapper.ore_highlight.clear_all"),
            button -> clearAllSelected())
            .bounds(centerX - 30, buttonY, 60, 20)
            .build();
        this.addRenderableWidget(this.clearAllButton);

        // 범위 슬라이더 (간단 구현)
        int rangeY = buttonY + 30;
        // TODO: 실제 슬라이더 구현 (지금은 버튼으로 대체)
        this.addRenderableWidget(Button.builder(Component.literal("Range: " + chunkRange + " chunks"),
            button -> {
                chunkRange = (chunkRange + 1) % 21; // 0~20 순환
                button.setMessage(Component.literal("Range: " + chunkRange + " chunks"));
            })
            .bounds(centerX - 80, rangeY, 160, 20)
            .build());

        // 실행 버튼들
        int actionY = rangeY + 40;
        this.calculateButton = Button.builder(Component.translatable("gui.seedmapper.ore_highlight.calculate"),
            button -> startCalculation())
            .bounds(centerX - 100, actionY, 90, 20)
            .build();
        this.addRenderableWidget(this.calculateButton);

        this.clearRenderButton = Button.builder(Component.translatable("gui.seedmapper.ore_highlight.clear"),
            button -> clearRender())
            .bounds(centerX, actionY, 60, 20)
            .build();
        this.addRenderableWidget(this.clearRenderButton);

        // 닫기 버튼
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"),
            button -> this.onClose())
            .bounds(centerX + 70, actionY, 50, 20)
            .build());
    }

    private String getCurrentSeed() {
        if (Configs.Seed != null) {
            return String.valueOf(Configs.Seed);
        }
        return "";
    }

    private void setFilter(OreDisplayUtils.OreCategory filter) {
        this.currentFilter = filter;
        updateOreList();
    }

    private void updateOreList() {
        this.oreListWidget.updateEntries();
    }

    private void selectAllVisible() {
        List<Integer> visibleOres = getFilteredOres();
        selectedOres.addAll(visibleOres);
        updateOreList();
    }

    private void clearAllSelected() {
        selectedOres.clear();
        updateOreList();
    }

    private List<Integer> getFilteredOres() {
        List<Integer> allOres = OreDisplayUtils.getAllOreTypes();
        if (currentFilter == null) {
            return allOres;
        }
        
        Set<Integer> categoryOres = OreDisplayUtils.getOresByCategory(currentFilter);
        return allOres.stream()
            .filter(categoryOres::contains)
            .toList();
    }

    private void startCalculation() {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }

        String seedText = seedInput.getValue().trim();
        if (seedText.isEmpty()) {
            statusText = Component.translatable("gui.seedmapper.ore_highlight.error.no_seed");
            return;
        }

        if (selectedOres.isEmpty()) {
            statusText = Component.translatable("gui.seedmapper.ore_highlight.error.no_ores");
            return;
        }

        long seed;
        try {
            seed = Long.parseLong(seedText);
        } catch (NumberFormatException e) {
            statusText = Component.translatable("gui.seedmapper.ore_highlight.error.invalid_seed");
            return;
        }

        statusText = Component.translatable("gui.seedmapper.ore_highlight.status.calculating");
        calculateButton.active = false;

        // 비동기 계산 시작
        currentTask = CompletableFuture.runAsync(() -> {
            try {
                OreHighlightTask.execute(seed, selectedOres, chunkRange);
                statusText = Component.translatable("gui.seedmapper.ore_highlight.status.completed", selectedOres.size());
            } catch (Exception e) {
                statusText = Component.translatable("gui.seedmapper.ore_highlight.error.calculation_failed");
                e.printStackTrace();
            } finally {
                // UI 스레드에서 버튼 활성화
                Minecraft.getInstance().execute(() -> {
                    calculateButton.active = true;
                });
            }
        });
    }

    private void clearRender() {
        // RenderManager를 통해 모든 렌더링 지우기
        dev.xpple.seedmapper.render.RenderManager.clear();
        statusText = Component.translatable("gui.seedmapper.ore_highlight.status.cleared");
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 제목 렌더링
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, (this.height - WINDOW_HEIGHT) / 2 + 10, 0xFFFFFF);
        
        // 상태 텍스트 렌더링
        int statusY = (this.height + WINDOW_HEIGHT) / 2 - 30;
        guiGraphics.drawCenteredString(this.font, statusText, this.width / 2, statusY, 0xFFFFFF);
        
        // 선택된 광물 수 표시
        Component selectedCount = Component.translatable("gui.seedmapper.ore_highlight.selected_count", selectedOres.size());
        guiGraphics.drawCenteredString(this.font, selectedCount, this.width / 2, statusY + 15, 0xAAAAAA);
    }

    @Override
    public void onClose() {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }
        saveSettings();
        super.onClose();
    }

    private void loadSettings() {
        OreHighlightConfig config = OreHighlightConfig.getInstance();
        selectedOres.addAll(config.getSelectedOres());
        chunkRange = config.getChunkRange();
        currentFilter = config.getFilterCategory();
        
        // 시드 필드가 비어있으면 마지막 시드 사용
        if (seedInput != null && seedInput.getValue().isEmpty()) {
            String lastSeed = config.getLastSeed();
            if (!lastSeed.isEmpty()) {
                seedInput.setValue(lastSeed);
            }
        }
    }

    private void saveSettings() {
        OreHighlightConfig config = OreHighlightConfig.getInstance();
        config.setSelectedOres(selectedOres);
        config.setChunkRange(chunkRange);
        config.setFilterCategory(currentFilter);
        
        if (seedInput != null) {
            config.setLastSeed(seedInput.getValue());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // 광물 선택 리스트 위젯
    private class OreListWidget extends AbstractSelectionList<OreListWidget.OreEntry> {
        public OreListWidget(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);
        }

        public void updateEntries() {
            this.clearEntries();
            List<Integer> filteredOres = getFilteredOres();
            
            for (int i = 0; i < filteredOres.size(); i += ORES_PER_ROW) {
                List<Integer> rowOres = new ArrayList<>();
                for (int j = 0; j < ORES_PER_ROW && (i + j) < filteredOres.size(); j++) {
                    rowOres.add(filteredOres.get(i + j));
                }
                this.addEntry(new OreEntry(rowOres));
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return this.x0 + this.width - 6;
        }

        @Override
        public int getRowWidth() {
            return this.width - 20;
        }

        private class OreEntry extends Entry<OreEntry> {
            private final List<OreCheckbox> checkboxes;

            public OreEntry(List<Integer> ores) {
                this.checkboxes = new ArrayList<>();
                for (int i = 0; i < ores.size(); i++) {
                    Integer oreId = ores.get(i);
                    String displayName = OreDisplayUtils.getDisplayName(oreId);
                    OreCheckbox checkbox = new OreCheckbox(0, 0, 180, 20, 
                        Component.literal(displayName), selectedOres.contains(oreId), oreId);
                    this.checkboxes.add(checkbox);
                }
            }

            @Override
            public void render(net.minecraft.client.gui.GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                for (int i = 0; i < checkboxes.size(); i++) {
                    OreCheckbox checkbox = checkboxes.get(i);
                    int checkboxX = x + (i * (entryWidth / ORES_PER_ROW));
                    checkbox.setX(checkboxX);
                    checkbox.setY(y);
                    checkbox.render(guiGraphics, mouseX, mouseY, tickDelta);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                for (OreCheckbox checkbox : checkboxes) {
                    if (checkbox.mouseClicked(mouseX, mouseY, button)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public List<? extends net.minecraft.client.gui.narration.NarratableEntry> narratables() {
                return checkboxes;
            }
        }
    }

    // 커스텀 체크박스 (광물 ID 포함)
    private class OreCheckbox extends Checkbox {
        private final Integer oreId;

        public OreCheckbox(int x, int y, int width, int height, Component message, boolean selected, Integer oreId) {
            super(x, y, width, height, message, selected);
            this.oreId = oreId;
        }

        @Override
        public void onPress() {
            super.onPress();
            if (this.selected()) {
                selectedOres.add(oreId);
            } else {
                selectedOres.remove(oreId);
            }
        }
    }
}