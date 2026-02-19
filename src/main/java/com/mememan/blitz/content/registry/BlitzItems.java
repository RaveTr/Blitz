package com.mememan.blitz.content.registry;

import com.google.common.collect.ImmutableList;
import com.mememan.blitz.Blitz;
import com.mememan.blitz.content.item.utility.OreSonarItem;
import com.mememan.blitz.content.item.utility.RubbleHammerItem;
import com.mememan.blitz.core.template.BlitzRecipeTemplates;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.template.property_wrapper.ItemPropertyWrapperTemplates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

import java.util.function.Supplier;

@RegistrarEntry
public final class BlitzItems {
    protected static final ObjectArrayList<Supplier<Item>> ITEMS = new ObjectArrayList<>();

    public static final Supplier<OreSonarItem> ORE_SONAR = ItemPropertyWrapperTemplates.registerAndChain(Blitz.prefix("ore_sonar"), () -> new OreSonarItem(new Item.Properties().stacksTo(1)), ItemPropertyWrapperTemplates.BASIC_GENERATED, ITEMS)
            .withRecipe(BlitzRecipeTemplates::oreSonarRecipe)
            .buildAndGet();
    public static final Supplier<RubbleHammerItem> RUBBLE_HAMMER = ItemPropertyWrapperTemplates.registerAndChain(Blitz.prefix("rubble_hammer"), () -> new RubbleHammerItem(Tiers.IRON, 3, -2.8F, new Item.Properties().durability(1024)), ItemPropertyWrapperTemplates.BASIC_HANDHELD, ITEMS)
            .withRecipe(BlitzRecipeTemplates::rubbleHammerRecipe)
            .buildAndGet();

    public static ImmutableList<Supplier<Item>> getItems() {
        return ImmutableList.copyOf(ITEMS);
    }
}
