package com.example.examplemod;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item MYTHIC_BLADE = new SwordItem(MythicToolMaterial.INSTANCE, 6, -2.4F,
            new FabricItemSettings().group(ItemGroup.COMBAT).fireproof().rarity(Rarity.RARE));

    public static void registerModItems() {
        Registry.register(Registries.ITEM, new Identifier("examplemod", "mythic_blade"), MYTHIC_BLADE);
    }

    private enum MythicToolMaterial implements ToolMaterial {
        INSTANCE;

        @Override
        public int getDurability() {
            return 2500;
        }

        @Override
        public float getMiningSpeedMultiplier() {
            return 12.0F;
        }

        @Override
        public float getAttackDamage() {
            return 4.0F;
        }

        @Override
        public int getMiningLevel() {
            return 4;
        }

        @Override
        public int getEnchantability() {
            return 22;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.ofItems(Items.NETHERITE_INGOT);
        }
    }
}
