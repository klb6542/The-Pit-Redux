package me.keegan.commands;

import me.keegan.pitredux.ThePitRedux;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class announce implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) { return false; }

        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(args)
                .forEach(string -> stringBuilder.append(string + " "));

        ThePitRedux.getPlugin().getServer().broadcastMessage(stringBuilder.toString());
        return true;
    }
}
