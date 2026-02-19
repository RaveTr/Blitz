package com.mememan.blitz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class BlitzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        handleClientEvents();
    }

    private static void handleClientEvents() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((phase, listener) -> {
            
            return true;
        });
    }
}
