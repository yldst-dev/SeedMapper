package dev.xpple.seedmapper.gui;

import dev.xpple.seedmapper.util.OreDisplayUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class OreHighlightConfig {
    private static final String CONFIG_FILE_NAME = "ore_highlight_settings.dat";
    private static final String SELECTED_ORES_KEY = "selectedOres";
    private static final String CHUNK_RANGE_KEY = "chunkRange";
    private static final String FILTER_CATEGORY_KEY = "filterCategory";
    private static final String LAST_SEED_KEY = "lastSeed";

    private Set<Integer> selectedOres = new HashSet<>();
    private int chunkRange = 5;
    private OreDisplayUtils.OreCategory filterCategory = null;
    private String lastSeed = "";

    private static OreHighlightConfig instance;

    private OreHighlightConfig() {
        // 기본값으로 일반적인 광물들 선택
        selectedOres.addAll(OreDisplayUtils.getOresByCategory(OreDisplayUtils.OreCategory.COMMON));
        loadFromFile();
    }

    public static OreHighlightConfig getInstance() {
        if (instance == null) {
            instance = new OreHighlightConfig();
        }
        return instance;
    }

    // Getters
    public Set<Integer> getSelectedOres() {
        return new HashSet<>(selectedOres);
    }

    public int getChunkRange() {
        return chunkRange;
    }

    public OreDisplayUtils.OreCategory getFilterCategory() {
        return filterCategory;
    }

    public String getLastSeed() {
        return lastSeed;
    }

    // Setters
    public void setSelectedOres(Set<Integer> selectedOres) {
        this.selectedOres = new HashSet<>(selectedOres);
        saveToFile();
    }

    public void setChunkRange(int chunkRange) {
        this.chunkRange = Math.max(0, Math.min(20, chunkRange)); // 0-20 범위 제한
        saveToFile();
    }

    public void setFilterCategory(OreDisplayUtils.OreCategory filterCategory) {
        this.filterCategory = filterCategory;
        saveToFile();
    }

    public void setLastSeed(String lastSeed) {
        this.lastSeed = lastSeed != null ? lastSeed : "";
        saveToFile();
    }

    // 파일 저장
    private void saveToFile() {
        try {
            File configFile = getConfigFile();
            CompoundTag compound = new CompoundTag();

            // 선택된 광물들 저장
            ListTag oresList = new ListTag();
            for (Integer oreId : selectedOres) {
                oresList.add(IntTag.valueOf(oreId));
            }
            compound.put(SELECTED_ORES_KEY, oresList);

            // 기타 설정 저장
            compound.putInt(CHUNK_RANGE_KEY, chunkRange);
            compound.putString(LAST_SEED_KEY, lastSeed);
            
            if (filterCategory != null) {
                compound.putString(FILTER_CATEGORY_KEY, filterCategory.name());
            }

            // 파일 쓰기
            NbtIo.writeCompressed(compound, configFile.toPath());
            
        } catch (IOException e) {
            System.err.println("Failed to save ore highlight config: " + e.getMessage());
        }
    }

    // 파일 로드
    private void loadFromFile() {
        try {
            File configFile = getConfigFile();
            if (!configFile.exists()) {
                return; // 파일이 없으면 기본값 사용
            }

            CompoundTag compound = NbtIo.readCompressed(configFile.toPath(), NbtAccounter.unlimitedHeap());

            // 선택된 광물들 로드
            if (compound.contains(SELECTED_ORES_KEY)) {
                selectedOres.clear();
                ListTag oresList = compound.getList(SELECTED_ORES_KEY);
                for (int i = 0; i < oresList.size(); i++) {
                    selectedOres.add(oresList.getInt(i));
                }
            }

            // 기타 설정 로드
            if (compound.contains(CHUNK_RANGE_KEY)) {
                chunkRange = Math.max(0, Math.min(20, compound.getInt(CHUNK_RANGE_KEY).orElse(5)));
            }

            if (compound.contains(LAST_SEED_KEY)) {
                lastSeed = compound.getString(LAST_SEED_KEY).orElse("");
            }

            if (compound.contains(FILTER_CATEGORY_KEY)) {
                try {
                    filterCategory = OreDisplayUtils.OreCategory.valueOf(compound.getString(FILTER_CATEGORY_KEY).orElse(""));
                } catch (IllegalArgumentException e) {
                    filterCategory = null; // 잘못된 값이면 null로 설정
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to load ore highlight config: " + e.getMessage());
        }
    }

    // 설정 파일 경로 가져오기
    private File getConfigFile() {
        File gameDir = Minecraft.getInstance().gameDirectory;
        File configDir = new File(gameDir, "config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        File seedMapperDir = new File(configDir, "seedmapper");
        if (!seedMapperDir.exists()) {
            seedMapperDir.mkdirs();
        }
        
        return new File(seedMapperDir, CONFIG_FILE_NAME);
    }

    // 편의 메서드들
    public void addSelectedOre(Integer oreId) {
        selectedOres.add(oreId);
        saveToFile();
    }

    public void removeSelectedOre(Integer oreId) {
        selectedOres.remove(oreId);
        saveToFile();
    }

    public boolean isOreSelected(Integer oreId) {
        return selectedOres.contains(oreId);
    }

    public void selectAllOfCategory(OreDisplayUtils.OreCategory category) {
        selectedOres.addAll(OreDisplayUtils.getOresByCategory(category));
        saveToFile();
    }

    public void clearAllSelected() {
        selectedOres.clear();
        saveToFile();
    }

    public void selectAll() {
        selectedOres.addAll(OreDisplayUtils.getAllOreTypes());
        saveToFile();
    }

    // 설정 초기화
    public void resetToDefaults() {
        selectedOres.clear();
        selectedOres.addAll(OreDisplayUtils.getOresByCategory(OreDisplayUtils.OreCategory.COMMON));
        chunkRange = 5;
        filterCategory = null;
        lastSeed = "";
        saveToFile();
    }
}