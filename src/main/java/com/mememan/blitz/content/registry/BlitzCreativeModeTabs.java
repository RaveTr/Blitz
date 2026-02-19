package com.mememan.blitz.content.registry;

import com.google.common.collect.ImmutableList;
import com.mememan.blitz.Blitz;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.template.property_wrapper.CreativeModeTabPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;
import java.util.stream.Collectors;

@RegistrarEntry
public final class BlitzCreativeModeTabs {
    protected static final ObjectArrayList<Supplier<CreativeModeTab>> CREATIVE_MODE_TABS = new ObjectArrayList<>();

    public static final Supplier<CreativeModeTab> ITEMS = CreativeModeTabPropertyWrapperTemplates.registerAndChain(Blitz.prefix("items"),
                    () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                            .title(Component.translatable("creative_mode_tab.blitz.items"))
                            .icon(() -> BlitzItems.ORE_SONAR.get().getDefaultInstance())
                            .displayItems((param, output) -> output.acceptAll(BlitzItems.ITEMS.stream()
                                    .map(Supplier::get)
                                    .map(Item::getDefaultInstance)
                                    .collect(Collectors.toCollection(ObjectArrayList::new))))
                            .build(), CREATIVE_MODE_TABS)
            .withCustomName("Blitz: Items")
            .buildAndGet();

    public static ImmutableList<Supplier<CreativeModeTab>> getCreativeModeTabs() {
        return ImmutableList.copyOf(CREATIVE_MODE_TABS);
    }
}
