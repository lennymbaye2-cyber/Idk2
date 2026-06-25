package com.example.examplemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModItems.registerModItems();
        BossDropsAndLifeLeach.register();
        
        // Register smithing handler
        ServerTickEvents.END_PLAYER_TICK.register(player -> {
            if (player instanceof ServerPlayerEntity && player.currentScreenHandler instanceof SmithingScreenHandler) {
                SmithingScreenHandler handler = (SmithingScreenHandler) player.currentScreenHandler;
                // Check if result slot has an item (smithing happened)
                if (!handler.getSlot(2).getStack().isEmpty()) {
                    // Process boss heart smithing if base + addition are correct
                    SmithingRecipeHandler.processSmithingRecipe(handler.getSlot(0).getStack(), handler.getSlot(1).getStack());
                }
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("hello")
                .then(CommandManager.argument("name", StringArgumentType.word())
                    .executes(context -> runHello(context.getSource(), StringArgumentType.getString(context, "name")))));
        });
    }

    private int runHello(ServerCommandSource source, String name) {
        source.sendFeedback(new LiteralText("Hello, " + name + "! Welcome to Fabric."), false);
        return 1;
    }
}
