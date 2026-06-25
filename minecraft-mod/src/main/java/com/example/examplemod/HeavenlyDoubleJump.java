package com.example.examplemod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HeavenlyDoubleJump {
    private static final String HEAVENLY_JUMP_COUNT_KEY = "HeavenlyJumpsUsed";
    private static final String HEAVENLY_PREV_JUMP_KEY = "HeavenlyPrevJumping";
    private static final String HEAVENLY_WAS_GROUND_KEY = "HeavenlyWasOnGround";
    private static final double EXTRA_JUMP_VELOCITY = 0.45D;

    public static void register() {
        ServerTickEvents.END_PLAYER_TICK.register(player -> {
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return;
            }

            // Only apply on the server and only while wearing heavenly armor pieces.
            int extraJumps = getStackedHeavenlyJumpCount(serverPlayer);
            if (extraJumps <= 0 || serverPlayer.getAbilities().allowFlying || serverPlayer.isFallFlying()) {
                resetHeavenlyJumpState(serverPlayer);
                return;
            }

            NbtCompound persistentData = serverPlayer.getPersistentData();
            boolean isOnGround = serverPlayer.isOnGround();
            boolean previousJumping = persistentData.getBoolean(HEAVENLY_PREV_JUMP_KEY);
            boolean isJumping = serverPlayer.input.jumping;

            if (isOnGround) {
                persistentData.putInt(HEAVENLY_JUMP_COUNT_KEY, 0);
            }

            if (!isOnGround && isJumping && !previousJumping) {
                int jumpsUsed = persistentData.getInt(HEAVENLY_JUMP_COUNT_KEY);
                if (jumpsUsed < extraJumps) {
                    Vec3d velocity = serverPlayer.getVelocity();
                    serverPlayer.setVelocity(velocity.x, EXTRA_JUMP_VELOCITY, velocity.z);
                    persistentData.putInt(HEAVENLY_JUMP_COUNT_KEY, jumpsUsed + 1);
                }
            }

            persistentData.putBoolean(HEAVENLY_PREV_JUMP_KEY, isJumping);
            persistentData.putBoolean(HEAVENLY_WAS_GROUND_KEY, isOnGround);
        });
    }

    private static int getStackedHeavenlyJumpCount(PlayerEntity player) {
        int pieceCount = 0;
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);

        if (helmet.getItem() == ModItems.HEAVENLY_HELMET) pieceCount++;
        if (chest.getItem() == ModItems.HEAVENLY_CHESTPLATE) pieceCount++;
        if (legs.getItem() == ModItems.HEAVENLY_LEGGINGS) pieceCount++;
        if (feet.getItem() == ModItems.HEAVENLY_BOOTS) pieceCount++;

        // Heavenly armor stacks: 2 pieces grant a double jump, 4 pieces grant an extra stacked jump.
        return pieceCount / 2;
    }

    private static void resetHeavenlyJumpState(ServerPlayerEntity player) {
        NbtCompound persistentData = player.getPersistentData();
        persistentData.putInt(HEAVENLY_JUMP_COUNT_KEY, 0);
        persistentData.putBoolean(HEAVENLY_PREV_JUMP_KEY, false);
        persistentData.putBoolean(HEAVENLY_WAS_GROUND_KEY, true);
    }
}
