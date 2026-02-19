package com.mememan.blitz.core.template;

import com.mememan.nexus.property_wrapper.base.generic.DataGenPropertyWrapper;
import com.mememan.nexus.util.PredicateUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Template {@code class} containing helpful recipe shortcut/delegator helper methods, as well as some re-used constants
 * related to recipes in general.
 * <br></br>
 * Conventionally, recipe utility methods generate recipes for provided parent objects, not the other way around.
 */
public final class BlitzRecipeTemplates {

    private BlitzRecipeTemplates() {
        throw new IllegalAccessError("Attempted to construct instance of template class! (BlitzRecipeTemplates)");
    }

    public static <I extends Item> Consumer<Supplier<I>> oreSonarRecipe(Consumer<FinishedRecipe> finishedRecipe) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, parentItemLike)
                    .define('R', Items.REDSTONE)
                    .define('E', Items.ECHO_SHARD)
                    .define('S', Blocks.CALIBRATED_SCULK_SENSOR)
                    .pattern("ERE")
                    .pattern("RSR")
                    .pattern("ERE")
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.REDSTONE), PredicateUtil.has(Items.REDSTONE))
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.ECHO_SHARD), PredicateUtil.has(Items.ECHO_SHARD))
                    .unlockedBy("has_" + BuiltInRegistries.BLOCK.getKey(Blocks.CALIBRATED_SCULK_SENSOR), PredicateUtil.has(Blocks.CALIBRATED_SCULK_SENSOR))
                    .save(finishedRecipe, parentItemLikeId);
        };
    }

    public static <I extends Item> Consumer<Supplier<I>> rubbleHammerRecipe(Consumer<FinishedRecipe> finishedRecipe) {
        return parentItemLikeSup -> {
            I parentItemLike = parentItemLikeSup.get();
            ResourceLocation parentItemLikeId = DataGenPropertyWrapper.RegistryLookupContainer.getObjectRegistryIdOrThrow(parentItemLike);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, parentItemLike)
                    .define('S', Items.STICK)
                    .define('I', Blocks.IRON_BLOCK)
                    .pattern("III")
                    .pattern("ISI")
                    .pattern(" S ")
                    .unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(Items.STICK), PredicateUtil.has(Items.STICK))
                    .unlockedBy("has_" + BuiltInRegistries.BLOCK.getKey(Blocks.IRON_BLOCK), PredicateUtil.has(Blocks.IRON_BLOCK))
                    .save(finishedRecipe, parentItemLikeId);
        };
    }
}
