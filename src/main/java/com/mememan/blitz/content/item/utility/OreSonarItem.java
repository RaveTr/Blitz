package com.mememan.blitz.content.item.utility;

import com.mememan.blitz.core.client.vfx.level.HighlightBlockEffect;
import com.mememan.blitz.core.client.vfx.screen.ScreenShakeEffect;
import com.mememan.blitz.core.config.JsonConfig;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class OreSonarItem extends Item {
    protected static final int DEFAULT_SCAN_COLOR = 0x303030;
    protected static final int DEFAULT_ORE_COLOR = 0xDED5DA;
    protected static final Object2IntOpenHashMap<Supplier<Block>> ORE_COLORS = Util.make(new Object2IntOpenHashMap<>(), oreHighlightMap -> {
        // Normal
        oreHighlightMap.put(() -> Blocks.COAL_ORE, 0x292929);
        oreHighlightMap.put(() -> Blocks.IRON_ORE, 0x808080);
        oreHighlightMap.put(() -> Blocks.COPPER_ORE, 0xB87333);
        oreHighlightMap.put(() -> Blocks.GOLD_ORE, 0xFFD700);
        oreHighlightMap.put(() -> Blocks.DIAMOND_ORE, 0x5EAEFF);
        oreHighlightMap.put(() -> Blocks.EMERALD_ORE, 0x32CD32);
        oreHighlightMap.put(() -> Blocks.LAPIS_ORE, 0x242ABB);
        oreHighlightMap.put(() -> Blocks.REDSTONE_ORE, 0xFF0000);

        // Deepslate (All slightly darker variants of normal)
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_COAL_ORE, 0x000000);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_IRON_ORE, 0x4F4F4F);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_COPPER_ORE, 0x90502F);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_GOLD_ORE, 0xB89400);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_DIAMOND_ORE, 0x3E6798);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_EMERALD_ORE, 0x278C27);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_LAPIS_ORE, 0x191F72);
        oreHighlightMap.put(() -> Blocks.DEEPSLATE_REDSTONE_ORE, 0x8D0000);

        // Nether
        oreHighlightMap.put(() -> Blocks.NETHER_QUARTZ_ORE, 0xEDEDED);
        oreHighlightMap.put(() -> Blocks.NETHER_GOLD_ORE, 0xFFD700);
        oreHighlightMap.put(() -> Blocks.ANCIENT_DEBRIS, 0x280345);

        oreHighlightMap.defaultReturnValue(DEFAULT_ORE_COLOR);
    });
    protected static Object2IntOpenCustomHashMap<Supplier<Block>> ORE_COLORS_BY_BLOCK;
    protected static final LinkedList<SonarScan> ACTIVE_SCANS = new LinkedList<>(); // ONLY ever updated on the client

    public OreSonarItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack heldStack = player.getMainHandItem();

        if (!heldStack.isEmpty() && heldStack.getItem() instanceof OreSonarItem heldOreSonar) {
            if (level.isClientSide()) {
                BlockPos curPos = player.blockPosition();
                ScreenShakeEffect shakeEffect = new ScreenShakeEffect(curPos, 2.5D, 0.01F, 245.5F, 11.2F);

                shakeEffect.enqueue(level);

                scanForOre(level, curPos, player);
            } else player.getCooldowns().addCooldown(heldOreSonar, JsonConfig.ORE_SONAR_COOLDOWN.get().getAsInt());

            return InteractionResultHolder.success(heldStack);
        }

        return InteractionResultHolder.pass(heldStack);
    }

    protected void scanForOre(Level level, BlockPos originPos, Player player) {
        double scanRadius = JsonConfig.ORE_SONAR_SCAN_RADIUS.get().getAsDouble(); // TODO Make these units consistent with each other (seriously)
        int scanLimit = JsonConfig.ORE_SONAR_SCAN_LIMIT.get().getAsInt();

        ACTIVE_SCANS.add(new SonarScan(level, originPos, (int) scanRadius, scanLimit, player));
    }

    public static boolean isOre(BlockState state) {
        return state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.getBlock().getDescriptionId().endsWith("_ore"); // Hacky ahh fix
    }

    public static void tickActiveScans() {
        ACTIVE_SCANS.removeIf(curScan -> {
            curScan.tick();
            return curScan.isComplete();
        });
    }

    protected static Object2IntOpenCustomHashMap<Supplier<Block>> getOreColorsByBlock() { // Used to create a 'lazy' one-time copy with the correct comparison and hashing impls to keep things consistent, cuz suppliers can change but blocks are singletons
        return ORE_COLORS_BY_BLOCK != null
                ? ORE_COLORS_BY_BLOCK
                : Util.make(new Object2IntOpenCustomHashMap<>(ORE_COLORS, new Hash.Strategy<>() {
            @Override
            public int hashCode(Supplier<Block> o) {
                return o == null ? 0 : Objects.hashCode(o.get());
            }

            @Override
            public boolean equals(Supplier<Block> a, Supplier<Block> b) {
                return a != null && b != null && Objects.equals(a.get(), b.get());
            }
        }), oreColors -> oreColors.defaultReturnValue(DEFAULT_ORE_COLOR));
    }

    public static class SonarScan {
        protected final Level curLevel;
        protected final BlockPos originPos;
        protected final double scanRadius;
        protected final int scanLimit;
        protected final Player player;
        protected int tickDelay = 0;
        protected int radiusIdx = 0;
        protected int scanCount = 0;
        protected boolean isComplete = false;

        public SonarScan(Level curLevel, BlockPos originPos, double scanRadius, int scanLimit, Player player) {
            this.curLevel = curLevel;
            this.originPos = originPos;
            this.scanRadius = scanRadius;
            this.scanLimit = scanLimit;
            this.player = player;
        }

        public void tick() {
            if (isComplete) return;

            this.tickDelay++;

            if (tickDelay >= 1) { // TODO Maybe make this configurable? Idk it'd have to be pretty constrained to still look good while being customizable
                this.tickDelay = 0;
                this.radiusIdx++;

                if (radiusIdx < scanRadius && scanCount <= scanLimit) this.scanCount += scanInShellWithGradient(curLevel, originPos, radiusIdx, scanLimit, scanCount, player);
                else this.isComplete = true;
            }
        }

        protected int scanInShellWithGradient(Level level, BlockPos targetPos, int radius, int scanLimit, int scanCount, Player player) {
            int foundOreCount = 0;
            int radiusSquared = radius * radius;
            int innerRadiusSquared = (radius - 1) * (radius - 1);

            List<PosWithAngle> shellPositions = new ObjectArrayList<>();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        int distSquared = x * x + y * y + z * z;

                        if (distSquared <= radiusSquared && distSquared > innerRadiusSquared) {
                            BlockPos curPos = targetPos.offset(x, y, z);
                            double angleFromCenter = Math.atan2(z, x);

                            shellPositions.add(new PosWithAngle(curPos, angleFromCenter));
                        }
                    }
                }
            }

            shellPositions.sort(Comparator.comparingDouble(curPos -> curPos.angle));

            for (PosWithAngle posData : shellPositions) {
                BlockPos checkPos = posData.targetPos();
                BlockState state = level.getBlockState(checkPos);

                if (isOre(state)) {
                    int oreColor = getOreColorsByBlock().getInt((Supplier<Block>) state::getBlock);
                    new HighlightBlockEffect(oreColor, 15.0D, 0.1D, 55.0D).highlightAt(checkPos, level, player);

                    foundOreCount++;

                    if (scanCount + foundOreCount >= scanLimit) return foundOreCount;
                } else if (!state.isAir() && state.isSolid()) {
                    double actualDistance = Math.sqrt(
                            Math.pow(checkPos.getX() - targetPos.getX(), 2) +
                                    Math.pow(checkPos.getY() - targetPos.getY(), 2) +
                                    Math.pow(checkPos.getZ() - targetPos.getZ(), 2)
                    );

                    double shellPosition = (actualDistance - (radius - 1)); // 0.0 to 1.0 within shell
                    double intensity = shellPosition; // Cuz outer edge should be brighter

                    highlightWithGradient(DEFAULT_SCAN_COLOR, intensity, level, checkPos, player);
                }
            }

            return foundOreCount;
        }

        protected void highlightWithGradient(int baseColor, double intensity, Level level, BlockPos targetPos, Player player) {
            /*
             * Create a basic ahh color gradient for the wave:
             * - Leading edge: leading edge color
             * - Trailing edge: base color
             * - Intensity: 0.0 (base color) to 1.0 (leading edge color)
             *
             * (Don't judge the odd choice of colors bruh I am NOT an artist)
             */
            int leadingEdgeColor = 0x00DDDD;

            int r1 = (baseColor >> 16) & 0xFF; // Mix colors based on intensity (derived from baseColor cuz otherwise we'd have to do a LOT more to calculate individual "stronger" wave trails, which is not worth the performance impact)
            int g1 = (baseColor >> 8) & 0xFF;
            int b1 = baseColor & 0xFF;

            int r2 = (leadingEdgeColor >> 16) & 0xFF;
            int g2 = (leadingEdgeColor >> 8) & 0xFF;
            int b2 = leadingEdgeColor & 0xFF;

            int r = (int) (r1 + (r2 - r1) * intensity);
            int g = (int) (g1 + (g2 - g1) * intensity);
            int b = (int) (b1 + (b2 - b1) * intensity);

            int waveColor = (r << 16) | (g << 8) | b; // Pool all extracted color channels back in there

            double duration = 16.0D + (intensity * 8.0D);
            double fadeOut = 8.0D + (intensity * 4.0D);
            double fadeOutRate = 0.8D;

            new HighlightBlockEffect(waveColor, fadeOut, fadeOutRate, 0.22D, duration)
                    .highlightAt(targetPos, level, player);
        }

        public boolean isComplete() {
            return isComplete;
        }
    }

    public record PosWithAngle(BlockPos targetPos, double angle) {

    }
}
