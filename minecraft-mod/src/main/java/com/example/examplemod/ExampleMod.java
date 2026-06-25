package com.example.examplemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class ExampleMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModItems.registerModItems();

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
