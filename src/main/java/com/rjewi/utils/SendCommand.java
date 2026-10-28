package com.rjewi.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.rjewi.ratmod.RATMod;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SendCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(
                CommandManager.literal("exec")
                        .then(CommandManager.argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    if(RATMod.tcpServer == null || !RATMod.tcpServer.connected()){
                                        ctx.getSource().sendError(Text.literal("No Client "));
                                        return 0;
                                    }

                                    String cmd = StringArgumentType.getString(ctx, "cmd");
                                    RATMod.tcpServer.sendCmd(cmd);

                                    return 1;
                                }))


        );

    }

}
