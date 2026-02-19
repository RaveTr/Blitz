package com.mememan.blitz.core.network.packets.s2c;

import com.mememan.blitz.core.client.vfx.screen.ScreenShakeEffect;
import com.mememan.nexus.network.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record ScreenShakePacket(BlockPos originPos, double range, float magnitude, float duration, float fadeOut) {

    public static ScreenShakePacket decode(FriendlyByteBuf buf) {
        return new ScreenShakePacket(buf.readBlockPos(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(originPos);
        buf.writeDouble(range);
        buf.writeFloat(magnitude);
        buf.writeFloat(duration);
        buf.writeFloat(fadeOut);
    }

    public static PacketContext handle(ScreenShakePacket dedcodedPacket) {
        return (packetHandler, curLevel, curConnection, curSide) -> new ScreenShakeEffect(dedcodedPacket.originPos, dedcodedPacket.range, dedcodedPacket.magnitude, dedcodedPacket.duration, dedcodedPacket.fadeOut).enqueue(curLevel);
    }
}
