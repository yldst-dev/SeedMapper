package dev.xpple.seedmapper.gui;

import com.github.cubiomes.Cubiomes;
import com.github.cubiomes.Generator;
import com.github.cubiomes.OreConfig;
import com.github.cubiomes.Pos3;
import com.github.cubiomes.Pos3List;
import com.github.cubiomes.SurfaceNoise;
import com.mojang.datafixers.util.Pair;
import dev.xpple.seedmapper.config.Configs;
import dev.xpple.seedmapper.render.RenderManager;
import dev.xpple.seedmapper.util.OreDisplayUtils;
import dev.xpple.seedmapper.util.SpiralLoop;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.*;
import java.util.stream.IntStream;

public class OreHighlightTask {
    private OreHighlightTask() {
    }

    /**
     * 선택된 광물들에 대해 하이라이트 계산을 수행합니다.
     * 
     * @param seed 월드 시드
     * @param selectedOres 선택된 광물 ID 집합
     * @param chunkRange 탐색할 청크 반경
     */
    public static void execute(long seed, Set<Integer> selectedOres, int chunkRange) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) {
            return;
        }

        int version = getVersionFromConfigs();
        int dimension = getDimensionFromPlayer();
        
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment generator = Generator.allocate(arena);
            Cubiomes.setupGenerator(generator, version, 0);
            Cubiomes.applySeed(generator, dimension, seed);
            
            MemorySegment surfaceNoise = SurfaceNoise.allocate(arena);
            Cubiomes.initSurfaceNoise(surfaceNoise, dimension, seed);

            ChunkPos center = new ChunkPos(BlockPos.containing(client.player.position()));
            Map<Integer, List<BlockPos>> orePositionsByType = new HashMap<>();

            // 각 청크를 나선형으로 탐색
            SpiralLoop.spiral(center.x, center.z, chunkRange, (chunkX, chunkZ) -> {
                processChunk(arena, generator, surfaceNoise, chunkX, chunkZ, 
                    selectedOres, version, orePositionsByType);
                return false; // 계속 진행
            });

            // UI 스레드에서 렌더링 수행
            client.execute(() -> {
                for (Map.Entry<Integer, List<BlockPos>> entry : orePositionsByType.entrySet()) {
                    Integer oreType = entry.getKey();
                    List<BlockPos> positions = entry.getValue();
                    if (!positions.isEmpty()) {
                        int color = OreDisplayUtils.getColor(oreType);
                        RenderManager.drawBoxes(positions, color);
                    }
                }
            });
        }
    }

    private static void processChunk(Arena arena, MemorySegment generator, MemorySegment surfaceNoise,
                                   int chunkX, int chunkZ, Set<Integer> selectedOres, int version,
                                   Map<Integer, List<BlockPos>> orePositionsByType) {
        
        Minecraft client = Minecraft.getInstance();
        LevelChunk chunk = client.level.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
        boolean doAirCheck = Configs.OreAirCheck && chunk != null;

        // 이 청크에서 생성 가능한 바이옴들 확인
        List<Integer> biomes = getBiomesForChunk(generator, chunkX, chunkZ, version);
        
        // 각 선택된 광물 타입에 대해 처리
        for (Integer oreType : selectedOres) {
            // 이 바이옴에서 해당 광물이 생성 가능한지 확인
            boolean canGenerate = biomes.stream()
                .anyMatch(biome -> Cubiomes.isViableOreBiome(version, oreType, biome) != 0);
                
            if (!canGenerate) {
                continue;
            }

            // 광물 설정 가져오기
            MemorySegment oreConfig = OreConfig.allocate(arena);
            if (Cubiomes.getOreConfig(oreType, version, biomes.get(0), oreConfig) == 0) {
                continue; // 설정 실패시 건너뛰기
            }

            // 광물 위치 생성
            MemorySegment pos3List = Cubiomes.generateOres(arena, generator, surfaceNoise, oreConfig, chunkX, chunkZ);
            List<BlockPos> positions = extractPositions(pos3List, chunk, doAirCheck);
            
            if (!positions.isEmpty()) {
                orePositionsByType.computeIfAbsent(oreType, k -> new ArrayList<>()).addAll(positions);
            }

            // 메모리 정리
            Cubiomes.freePos3List(pos3List);
        }
    }

    private static List<Integer> getBiomesForChunk(MemorySegment generator, int chunkX, int chunkZ, int version) {
        if (version <= Cubiomes.MC_1_17()) {
            return List.of(Cubiomes.getBiomeForOreGen(generator, chunkX, chunkZ, 0));
        } else {
            // 1.18+ 버전에서는 여러 높이의 바이옴 확인
            return IntStream.of(-30, 64, 120)
                .map(y -> Cubiomes.getBiomeForOreGen(generator, chunkX, chunkZ, y))
                .boxed()
                .toList();
        }
    }

    private static List<BlockPos> extractPositions(MemorySegment pos3List, LevelChunk chunk, boolean doAirCheck) {
        List<BlockPos> positions = new ArrayList<>();
        int size = Pos3List.size(pos3List);
        MemorySegment pos3s = Pos3List.pos3s(pos3List);

        for (int i = 0; i < size; i++) {
            MemorySegment pos3 = Pos3.asSlice(pos3s, i);
            BlockPos pos = new BlockPos(Pos3.x(pos3), Pos3.y(pos3), Pos3.z(pos3));
            
            // 공기 블록 체크 (설정이 활성화된 경우)
            if (doAirCheck && chunk != null && chunk.getBlockState(pos).isAir()) {
                continue;
            }
            
            positions.add(pos);
        }

        return positions;
    }

    private static int getVersionFromConfigs() {
        // 현재 마인크래프트 버전에 맞는 cubiomes 버전 반환
        // 기본값으로 최신 버전 사용
        return Cubiomes.MC_NEWEST();
    }

    private static int getDimensionFromPlayer() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return Cubiomes.DIM_OVERWORLD();
        }

        // 차원 확인
        String dimensionName = client.level.dimension().location().toString();
        return switch (dimensionName) {
            case "minecraft:the_nether" -> Cubiomes.DIM_NETHER();
            case "minecraft:the_end" -> Cubiomes.DIM_END();
            default -> Cubiomes.DIM_OVERWORLD();
        };
    }

    /**
     * 단일 광물 타입에 대한 하이라이트 (기존 HighlightCommand와 호환성을 위해)
     */
    public static void executeForSingleOre(long seed, Pair<Integer, Integer> blockPair, int chunkRange) {
        Set<Integer> singleOre = Set.of(blockPair.getFirst());
        execute(seed, singleOre, chunkRange);
    }
}