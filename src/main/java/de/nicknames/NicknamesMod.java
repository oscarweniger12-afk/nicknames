package de.nicknames;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class NicknamesMod implements ClientModInitializer {

    private static final SuggestionProvider<FabricClientCommandSource> PLAYERS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(ctx.getSource().getOnlinePlayerNames(), builder);

    @Override
    public void onInitializeClient() {
        Nicknames.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) ->
                dispatcher.register(ClientCommands.literal("nick")
                        // /nick list
                        .then(ClientCommands.literal("list").executes(ctx -> {
                            if (Nicknames.all().isEmpty()) {
                                ctx.getSource().sendFeedback(Component.literal("Keine Spitznamen gesetzt."));
                            } else {
                                Nicknames.all().forEach((name, nick) ->
                                        ctx.getSource().sendFeedback(Component.literal(name + " -> " + nick.replace('&', '§'))));
                            }
                            return 1;
                        }))
                        // /nick reset <spieler>
                        .then(ClientCommands.literal("reset")
                                .then(ClientCommands.argument("player", StringArgumentType.word())
                                        .suggests(PLAYERS)
                                        .executes(ctx -> {
                                            String player = StringArgumentType.getString(ctx, "player");
                                            boolean had = Nicknames.remove(player);
                                            ctx.getSource().sendFeedback(Component.literal(had
                                                    ? "Spitzname von " + player + " entfernt."
                                                    : player + " hatte keinen Spitznamen."));
                                            return 1;
                                        })))
                        // /nick <spieler> <spitzname>
                        .then(ClientCommands.argument("player", StringArgumentType.word())
                                .suggests(PLAYERS)
                                .then(ClientCommands.argument("nickname", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String player = StringArgumentType.getString(ctx, "player");
                                            String nick = StringArgumentType.getString(ctx, "nickname");
                                            Nicknames.set(player, nick);
                                            ctx.getSource().sendFeedback(Component.literal(
                                                    player + " heißt für dich jetzt: " + nick.replace('&', '§')));
                                            return 1;
                                        })))));
    }
}
