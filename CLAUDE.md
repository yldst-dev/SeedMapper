# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build System

**Prerequisites:**
- Java 23 (required - uses Foreign Function & Memory API from Project Panama)
- LLVM 13.0.0+ with `LLVM_HOME` environment variable set
- GCC compiler for native library compilation

**Common Build Commands:**
```bash
# Full build (includes C library compilation and Java bindings generation)
./gradlew build

# Run tests
./gradlew test

# Run specific test
./gradlew test --tests "dev.xpple.seedmapper.SpiralLoopTest"

# Clean build
./gradlew clean build

# Generate Java bindings only (automated during build)
./gradlew createJavaBindings

# Compile native library manually (platform-specific):
# Windows: gcc -shared -o src/main/resources/cubiomes.dll [C_FILES] -O3
# Linux: gcc -shared -o src/main/resources/libcubiomes.so [C_FILES] -O3 -fPIC
# macOS: gcc -shared -o src/main/resources/libcubiomes.dylib [C_FILES] -O3 -fPIC
```

**Development Workflow:**
1. Compile native cubiomes library for your platform
2. Generate Java bindings using jextract (automated via `createJavaBindings` task)
3. Run standard Gradle build

## Architecture Overview

**Core Technology Stack:**
- **Fabric Mod**: Client-side Minecraft mod framework (1.21.8)
- **Native Integration**: C library (cubiomes) via Foreign Function & Memory API
- **Java Bindings**: Auto-generated using jextract from Project Panama

**Key Architectural Components:**

**Command System (`command/`):**
- `CustomClientCommandSource`: Enhanced command source with additional context
- `arguments/`: Custom argument types for biomes, structures, items, versions
- `commands/`: Core functionality commands (`LocateCommand`, `HighlightCommand`, etc.)

**Native Integration:**
- `com.github.cubiomes.*`: Auto-generated Java bindings for C library functions
- `CreateJavaBindingsTask`: Gradle task that generates bindings using jextract
- Static library loading in `SeedMapper.java` extracts and loads platform-specific native library

**Rendering System (`render/`):**
- `RenderManager`: 3D box rendering with cache-based optimization (5-minute TTL)
- `Line`: Primitive for wireframe rendering
- `NoDepthLayer`: Depth-disabled rendering layer
- Integrates via Mixin hooks into Minecraft's render pipeline

**Mixin Integration (`mixin/`):**
- `LevelRendererMixin`: Hooks into main render pass for custom rendering
- `ClientPacketListenerMixin`: Intercepts network packets
- Runtime bytecode modification for seamless Minecraft integration

**Configuration (`config/`):**
- `Configs`: Static configuration with seed management and resolution order
- `SeedResolutionAdapter`: Custom serialization for seed resolution preferences
- Integrated with BetterConfig for in-game configuration GUI

**Utility Classes (`util/`):**
- `SpiralLoop`/`SpiralSpliterator`: Efficient spiral area search algorithms
- `TwoDTree`: Spatial data structure for coordinate-based searches
- `ChatBuilder`: Minecraft chat component utilities
- `ThreadingHelper`: Async task management for heavy computations

**Critical Build Dependencies:**
- The build requires compiling the C library first, then generating Java bindings
- Platform-specific native libraries must be present in `src/main/resources/`
- The `includes.txt` file defines which C functions/structs to include in Java bindings

**Performance Considerations:**
- Heavy computations (biome/structure searching) run on background threads
- Results are cached in `RenderManager` with automatic expiration
- Native library provides optimized Minecraft world generation algorithms
- Spiral search patterns minimize computational overhead for area searches

**Localization:**
- Language files in `src/main/resources/assets/seedmapper/lang/`
- Supports: English, German, Russian, Hindi, Indonesian, Italian
- Comments in config use translatable components

**Testing:**
- Unit tests focus on utility algorithms (`SpiralLoop`, `SpiralSpliterator`)
- Limited test coverage due to Minecraft/native library dependencies
- Manual testing required for mod functionality within Minecraft