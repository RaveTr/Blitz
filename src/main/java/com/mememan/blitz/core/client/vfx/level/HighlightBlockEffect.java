package com.mememan.blitz.core.client.vfx.level;

import com.mememan.blitz.core.network.packets.s2c.HighlightBlockPacket;
import com.mememan.nexus.platform.NexusServices;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record HighlightBlockEffect(HighlightBlockData data) {
    private static final Object2ObjectLinkedOpenHashMap<BlockPos, HighlightBlockEffect> BLOCKS = new Object2ObjectLinkedOpenHashMap<>(); // Slightly more overhead than normal open hashmap, but we want to maintain insertion order to avoid sorting later on

    public HighlightBlockEffect(int color, double fadeOut, double fadeOutRate, double initialAlphaMultiplier, double duration) {
        this(new HighlightBlockData(color, fadeOut, fadeOutRate, initialAlphaMultiplier, duration));
    }

    public HighlightBlockEffect(int color, double fadeOut, double fadeOutRate, double duration) {
        this(color, fadeOut, fadeOutRate, 0.7D, duration);
    }

    public void highlightAt(BlockPos targetPos, Level curLevel, @Nullable Player targetPlayer) {
        if (curLevel == null || curLevel.isClientSide) BLOCKS.put(targetPos, this);
        else {
            if (targetPlayer == null) NexusServices.NETWORK_MANAGER.sendToAllClients(new HighlightBlockPacket(targetPos, data.getColor(), data.getFadeOut(), data.getFadeOutRate(), data.getInitialAlphaMultiplier(), data.getDuration()));
            else NexusServices.NETWORK_MANAGER.sendToClient(new HighlightBlockPacket(targetPos, data.getColor(), data.getFadeOut(), data.getFadeOutRate(), data.getInitialAlphaMultiplier(), data.getDuration()), (ServerPlayer) targetPlayer);
        }
    }

    public void highlightAt(BlockPos targetPos, Level curLevel) {
        highlightAt(targetPos, curLevel, null);
    }

    public static Object2ObjectLinkedOpenHashMap<BlockPos, HighlightBlockEffect> getBlockHighlightData() {
        return BLOCKS;
    }

    public static class HighlightBlockData {
        private final int color;
        private final double fadeOut;
        private final double fadeOutRate;
        private final double initialAlphaMultiplier;
        private double duration;

        public HighlightBlockData(int color, double fadeOut, double fadeOutRate, double initialAlphaMultiplier, double duration) {
            this.color = color;
            this.fadeOut = fadeOut;
            this.fadeOutRate = fadeOutRate;
            this.initialAlphaMultiplier = initialAlphaMultiplier;
            this.duration = duration;
        }

        public int getColor() {
            return color;
        }

        public double getFadeOut() {
            return fadeOut;
        }

        public double getFadeOutRate() {
            return fadeOutRate;
        }

        public double getInitialAlphaMultiplier() {
            return initialAlphaMultiplier;
        }

        public double getDuration() {
            return duration;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public double getAlpha() {
            return Math.max(0.0D, Math.min(1.0D, duration / fadeOut));
        }
    }
}