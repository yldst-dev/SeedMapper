package dev.xpple.seedmapper.util;

import com.github.cubiomes.Cubiomes;
import com.google.common.collect.ImmutableMap;
import dev.xpple.seedmapper.feature.OreTypes;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class OreDisplayUtils {
    private OreDisplayUtils() {
    }

    public enum OreCategory {
        COMMON("Common Ores"),
        RARE("Rare Ores"),  
        NETHER("Nether Ores"),
        STONE("Stone Variants"),
        SPECIAL("Special Ores");

        private final String displayName;

        OreCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 광물 ID -> 표시 이름 매핑
    public static final Map<Integer, String> ORE_DISPLAY_NAMES = ImmutableMap.<Integer, String>builder()
        // Common Ores
        .put(Cubiomes.DiamondOre(), "Diamond Ore")
        .put(Cubiomes.IronOre(), "Iron Ore")
        .put(Cubiomes.GoldOre(), "Gold Ore")
        .put(Cubiomes.CoalOre(), "Coal Ore")
        .put(Cubiomes.CopperOre(), "Copper Ore")
        .put(Cubiomes.RedstoneOre(), "Redstone Ore")
        .put(Cubiomes.LapisOre(), "Lapis Ore")
        .put(Cubiomes.EmeraldOre(), "Emerald Ore")
        
        // Size Variants - Diamond
        .put(Cubiomes.LargeDiamondOre(), "Large Diamond Ore")
        .put(Cubiomes.MediumDiamondOre(), "Medium Diamond Ore")
        .put(Cubiomes.BuriedDiamondOre(), "Buried Diamond Ore")
        
        // Size Variants - Iron
        .put(Cubiomes.MiddleIronOre(), "Middle Iron Ore")
        .put(Cubiomes.SmallIronOre(), "Small Iron Ore")
        
        // Size Variants - Copper
        .put(Cubiomes.LargeCopperOre(), "Large Copper Ore")
        
        // Size Variants - Ancient Debris
        .put(Cubiomes.LargeDebrisOre(), "Large Ancient Debris")
        .put(Cubiomes.SmallDebrisOre(), "Small Ancient Debris")
        
        // Upper/Lower Variants
        .put(Cubiomes.UpperIronOre(), "Upper Iron Ore")
        .put(Cubiomes.LowerGoldOre(), "Lower Gold Ore")
        .put(Cubiomes.UpperCoalOre(), "Upper Coal Ore")
        .put(Cubiomes.LowerCoalOre(), "Lower Coal Ore")
        .put(Cubiomes.LowerRedstoneOre(), "Lower Redstone Ore")
        .put(Cubiomes.ExtraGoldOre(), "Extra Gold Ore")
        
        // Stone Variants
        .put(Cubiomes.AndesiteOre(), "Andesite")
        .put(Cubiomes.DioriteOre(), "Diorite")
        .put(Cubiomes.GraniteOre(), "Granite")
        .put(Cubiomes.UpperAndesiteOre(), "Upper Andesite")
        .put(Cubiomes.LowerAndesiteOre(), "Lower Andesite")
        .put(Cubiomes.UpperDioriteOre(), "Upper Diorite")
        .put(Cubiomes.LowerDioriteOre(), "Lower Diorite")
        .put(Cubiomes.UpperGraniteOre(), "Upper Granite")
        .put(Cubiomes.LowerGraniteOre(), "Lower Granite")
        
        // Nether Ores
        .put(Cubiomes.NetherGoldOre(), "Nether Gold Ore")
        .put(Cubiomes.NetherQuartzOre(), "Nether Quartz Ore")
        .put(Cubiomes.DeltasGoldOre(), "Basalt Deltas Gold")
        .put(Cubiomes.DeltasQuartzOre(), "Basalt Deltas Quartz")
        .put(Cubiomes.SoulSandOre(), "Soul Sand")
        .put(Cubiomes.MagmaOre(), "Magma Block")
        .put(Cubiomes.NetherGravelOre(), "Nether Gravel")
        .put(Cubiomes.BlackstoneOre(), "Blackstone")
        
        // Special Ores
        .put(Cubiomes.DeepslateOre(), "Deepslate")
        .put(Cubiomes.TuffOre(), "Tuff")
        .put(Cubiomes.ClayOre(), "Clay")
        .put(Cubiomes.DirtOre(), "Dirt")
        .put(Cubiomes.GravelOre(), "Gravel")
        .put(Cubiomes.BuriedLapisOre(), "Buried Lapis Ore")
        .build();

    // 광물 ID -> 색상 매핑 (MapColor 기반)
    public static final Map<Integer, Integer> ORE_COLORS = ImmutableMap.<Integer, Integer>builder()
        // Common Ores - 기본 색상
        .put(Cubiomes.DiamondOre(), MapColor.DIAMOND.col)
        .put(Cubiomes.IronOre(), MapColor.RAW_IRON.col)
        .put(Cubiomes.GoldOre(), MapColor.GOLD.col)
        .put(Cubiomes.CoalOre(), MapColor.COLOR_BLACK.col)
        .put(Cubiomes.CopperOre(), MapColor.COLOR_ORANGE.col)
        .put(Cubiomes.RedstoneOre(), MapColor.FIRE.col)
        .put(Cubiomes.LapisOre(), MapColor.LAPIS.col)
        .put(Cubiomes.EmeraldOre(), MapColor.EMERALD.col)
        
        // Size Variants - 더 밝은/어두운 색상으로 구분
        .put(Cubiomes.LargeDiamondOre(), MapColor.ICE.col)  // 더 밝은 다이아몬드
        .put(Cubiomes.MediumDiamondOre(), MapColor.DIAMOND.col)
        .put(Cubiomes.BuriedDiamondOre(), MapColor.DEEPSLATE.col)  // 더 어두운 다이아몬드
        
        .put(Cubiomes.MiddleIronOre(), MapColor.RAW_IRON.col)
        .put(Cubiomes.SmallIronOre(), MapColor.COLOR_GRAY.col)
        .put(Cubiomes.UpperIronOre(), MapColor.RAW_IRON.col)
        
        .put(Cubiomes.LargeCopperOre(), MapColor.COLOR_YELLOW.col)
        
        .put(Cubiomes.LargeDebrisOre(), MapColor.TERRACOTTA_BROWN.col)
        .put(Cubiomes.SmallDebrisOre(), MapColor.COLOR_BROWN.col)
        
        // Upper/Lower Variants
        .put(Cubiomes.LowerGoldOre(), MapColor.GOLD.col)
        .put(Cubiomes.UpperCoalOre(), MapColor.COLOR_BLACK.col)
        .put(Cubiomes.LowerCoalOre(), MapColor.COLOR_BLACK.col)
        .put(Cubiomes.LowerRedstoneOre(), MapColor.FIRE.col)
        .put(Cubiomes.ExtraGoldOre(), MapColor.GOLD.col)
        
        // Stone Variants
        .put(Cubiomes.AndesiteOre(), MapColor.STONE.col)
        .put(Cubiomes.DioriteOre(), MapColor.QUARTZ.col)
        .put(Cubiomes.GraniteOre(), MapColor.DIRT.col)
        .put(Cubiomes.UpperAndesiteOre(), MapColor.STONE.col)
        .put(Cubiomes.LowerAndesiteOre(), MapColor.STONE.col)
        .put(Cubiomes.UpperDioriteOre(), MapColor.QUARTZ.col)
        .put(Cubiomes.LowerDioriteOre(), MapColor.QUARTZ.col)
        .put(Cubiomes.UpperGraniteOre(), MapColor.DIRT.col)
        .put(Cubiomes.LowerGraniteOre(), MapColor.DIRT.col)
        
        // Nether Ores
        .put(Cubiomes.NetherGoldOre(), MapColor.GOLD.col)
        .put(Cubiomes.NetherQuartzOre(), MapColor.QUARTZ.col)
        .put(Cubiomes.DeltasGoldOre(), MapColor.GOLD.col)
        .put(Cubiomes.DeltasQuartzOre(), MapColor.QUARTZ.col)
        .put(Cubiomes.SoulSandOre(), MapColor.COLOR_BROWN.col)
        .put(Cubiomes.MagmaOre(), MapColor.NETHER.col)
        .put(Cubiomes.NetherGravelOre(), MapColor.STONE.col)
        .put(Cubiomes.BlackstoneOre(), MapColor.COLOR_BLACK.col)
        
        // Special Ores
        .put(Cubiomes.DeepslateOre(), MapColor.DEEPSLATE.col)
        .put(Cubiomes.TuffOre(), MapColor.COLOR_GRAY.col)
        .put(Cubiomes.ClayOre(), MapColor.CLAY.col)
        .put(Cubiomes.DirtOre(), MapColor.DIRT.col)
        .put(Cubiomes.GravelOre(), MapColor.STONE.col)
        .put(Cubiomes.BuriedLapisOre(), MapColor.LAPIS.col)
        .build();

    // 카테고리별 광물 분류
    public static final Map<OreCategory, Set<Integer>> ORE_CATEGORIES = ImmutableMap.<OreCategory, Set<Integer>>builder()
        .put(OreCategory.COMMON, Set.of(
            Cubiomes.DiamondOre(), Cubiomes.IronOre(), Cubiomes.GoldOre(), 
            Cubiomes.CoalOre(), Cubiomes.CopperOre(), Cubiomes.RedstoneOre(),
            Cubiomes.LapisOre(), Cubiomes.EmeraldOre()
        ))
        .put(OreCategory.RARE, Set.of(
            Cubiomes.LargeDiamondOre(), Cubiomes.MediumDiamondOre(), Cubiomes.BuriedDiamondOre(),
            Cubiomes.MiddleIronOre(), Cubiomes.SmallIronOre(), Cubiomes.UpperIronOre(),
            Cubiomes.LargeCopperOre(), Cubiomes.LargeDebrisOre(), Cubiomes.SmallDebrisOre(),
            Cubiomes.LowerGoldOre(), Cubiomes.UpperCoalOre(), Cubiomes.LowerCoalOre(),
            Cubiomes.LowerRedstoneOre(), Cubiomes.ExtraGoldOre(), Cubiomes.BuriedLapisOre()
        ))
        .put(OreCategory.NETHER, Set.of(
            Cubiomes.NetherGoldOre(), Cubiomes.NetherQuartzOre(), Cubiomes.DeltasGoldOre(),
            Cubiomes.DeltasQuartzOre(), Cubiomes.SoulSandOre(), Cubiomes.MagmaOre(),
            Cubiomes.NetherGravelOre(), Cubiomes.BlackstoneOre()
        ))
        .put(OreCategory.STONE, Set.of(
            Cubiomes.AndesiteOre(), Cubiomes.DioriteOre(), Cubiomes.GraniteOre(),
            Cubiomes.UpperAndesiteOre(), Cubiomes.LowerAndesiteOre(),
            Cubiomes.UpperDioriteOre(), Cubiomes.LowerDioriteOre(),
            Cubiomes.UpperGraniteOre(), Cubiomes.LowerGraniteOre()
        ))
        .put(OreCategory.SPECIAL, Set.of(
            Cubiomes.DeepslateOre(), Cubiomes.TuffOre(), Cubiomes.ClayOre(),
            Cubiomes.DirtOre(), Cubiomes.GravelOre()
        ))
        .build();

    // 모든 카테고리의 광물들을 하나의 리스트로 반환
    public static List<Integer> getAllOreTypes() {
        return OreTypes.ORE_TYPES.stream().sorted().toList();
    }

    // 특정 카테고리의 광물들 반환
    public static Set<Integer> getOresByCategory(OreCategory category) {
        return ORE_CATEGORIES.getOrDefault(category, Set.of());
    }

    // 광물 ID로 표시 이름 가져오기
    public static String getDisplayName(Integer oreId) {
        return ORE_DISPLAY_NAMES.getOrDefault(oreId, "Unknown Ore");
    }

    // 광물 ID로 색상 가져오기
    public static Integer getColor(Integer oreId) {
        return ORE_COLORS.getOrDefault(oreId, MapColor.STONE.col);
    }

    // 광물이 어떤 카테고리에 속하는지 확인
    public static OreCategory getCategoryForOre(Integer oreId) {
        for (Map.Entry<OreCategory, Set<Integer>> entry : ORE_CATEGORIES.entrySet()) {
            if (entry.getValue().contains(oreId)) {
                return entry.getKey();
            }
        }
        return OreCategory.SPECIAL; // 기본값
    }
}