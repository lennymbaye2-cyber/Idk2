package com.example.examplemod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class SmithingRecipeHandler {
    
    public static void processSmithingRecipe(ItemStack base, ItemStack addition) {
        // Check if addition is a boss heart
        if (addition.getItem() == ModItems.BOSS_HEART && !base.isEmpty()) {
            // Check if base is armor from our sets
            if (isArmor(base)) {
                BossDropsAndLifeLeach.addHeart(base);
                addition.decrement(1);
            }
        }
    }
    
    private static boolean isArmor(ItemStack stack) {
        return stack.getItem() == ModItems.REAPER_HELMET ||
               stack.getItem() == ModItems.REAPER_CHESTPLATE ||
               stack.getItem() == ModItems.REAPER_LEGGINGS ||
               stack.getItem() == ModItems.REAPER_BOOTS;
    }
}
