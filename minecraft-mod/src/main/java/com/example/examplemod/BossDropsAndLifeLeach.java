package com.example.examplemod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.entity.ServerLivingEntityEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.wither.WitherEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;

public class BossDropsAndLifeLeach {
    private static final String BOSS_HEARTS_NBT_KEY = "boss_hearts";
    private static final int MAX_HEARTS = 5;
    
    public static void register() {
        // Boss death handler - drop boss heart
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity.getWorld().isClient) return;
            
            if ((entity instanceof WitherEntity || entity instanceof EnderDragonEntity || entity instanceof WardenEntity)
                    && damageSource.getAttacker() instanceof ServerPlayerEntity) {
                ItemStack heart = new ItemStack(ModItems.BOSS_HEART);
                entity.dropStack(heart);
            }
        });
        
        // Life leach on tick - check armor NBT tags for Reaper set only
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isDead() || player.getHealth() <= 0) continue;
                
                if (hasFullReaperSet(player)) {
                    int totalHearts = getTotalHearts(player);
                    if (totalHearts > 0) {
                        applyLifeLeach(player, Math.min(totalHearts, MAX_HEARTS));
                    }
                }
            }
        });
    }
    
    private static boolean hasFullMythicSet(PlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET);
        
        return helmet.getItem() == ModItems.MYTHIC_HELMET && chest.getItem() == ModItems.MYTHIC_CHESTPLATE
                && legs.getItem() == ModItems.MYTHIC_LEGGINGS && feet.getItem() == ModItems.MYTHIC_BOOTS;
    }
    
    private static boolean hasFullReaperSet(PlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET);
        
        return helmet.getItem() == ModItems.REAPER_HELMET && chest.getItem() == ModItems.REAPER_CHESTPLATE
                && legs.getItem() == ModItems.REAPER_LEGGINGS && feet.getItem() == ModItems.REAPER_BOOTS;
    }
    
    private static int getTotalHearts(PlayerEntity player) {
        int total = 0;
        ItemStack helmet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET);
        
        total += getHeartCount(helmet);
        total += getHeartCount(chest);
        total += getHeartCount(legs);
        total += getHeartCount(feet);
        return total;
    }
    
    public static int getHeartCount(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        NbtCompound tag = stack.getOrCreateNbt();
        return tag.getInt(BOSS_HEARTS_NBT_KEY);
    }
    
    public static void addHeart(ItemStack stack) {
        NbtCompound tag = stack.getOrCreateNbt();
        int current = tag.getInt(BOSS_HEARTS_NBT_KEY);
        if (current < MAX_HEARTS) {
            tag.putInt(BOSS_HEARTS_NBT_KEY, current + 1);
            updateItemName(stack, current + 1);
        }
    }
    
    private static void updateItemName(ItemStack stack, int heartCount) {
        if (heartCount > 0) {
            String baseName = stack.getItem().getName(stack).getString();
            if (!baseName.contains("(")) {
                stack.setCustomName(net.minecraft.text.Text.literal(baseName + " (" + heartCount + " hearts)"));
            }
        }
    }
    
    private static void applyLifeLeach(ServerPlayerEntity player, int activeHearts) {
        // Life leach triggers every 20 ticks (1 second)
        if (player.age % 20 == 0) {
            if (player.getHealth() < player.getMaxHealth()) {
                float heal = 0.5f * activeHearts;
                player.heal(heal);
                player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.3f, 1.0f);
            }
        }

    }
}
