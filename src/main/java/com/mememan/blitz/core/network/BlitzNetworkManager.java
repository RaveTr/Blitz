package com.mememan.blitz.core.network;

import com.mememan.blitz.Blitz;
import com.mememan.blitz.core.network.packets.s2c.ScreenShakePacket;
import com.mememan.nexus.asm.annotations.NetworkRegistrarEntry;
import com.mememan.nexus.network.BasePacket;
import com.mememan.nexus.network.NetworkSide;
import com.mememan.nexus.platform.NexusServices;

@NetworkRegistrarEntry
public final class BlitzNetworkManager {

    public static final BasePacket<ScreenShakePacket> SCREEN_SHAKE = NexusServices.NETWORK_MANAGER.registerPacket(new BasePacket<>(Blitz.prefix("screen_shake"), ScreenShakePacket.class, ScreenShakePacket::encode, ScreenShakePacket::decode, ScreenShakePacket::handle, NetworkSide.S2C));
}
