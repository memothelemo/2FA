package com.lielamar.auth.bukkit.commands.subcommands;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.AuthHandler;
import com.lielamar.auth.shared.handlers.MessageHandler;
import com.lielamar.auth.shared.utils.Constants;
import com.lielamar.lielsutils.bukkit.commands.StandaloneCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ReloadCommand extends StandaloneCommand {

    private final TwoFactorAuthentication plugin;

    public ReloadCommand(TwoFactorAuthentication plugin) {
        super(Constants.reloadCommand.getA(), Constants.reloadCommand.getB());

        this.plugin = plugin;
    }

    @Override
    public boolean runCommand(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        Map<UUID, AuthHandler.AuthState> states = new HashMap<>();
        AuthHandler.AuthState state;

        for(Player pl : Bukkit.getOnlinePlayers()) {
            state = this.plugin.getAuthHandler().getAuthState(pl.getUniqueId());
            if(state != null) states.put(pl.getUniqueId(), state);
        }

        this.plugin.reloadConfig();
        this.plugin.getMessageHandler().reload();
        this.plugin.setupAuth();

        for(UUID uuid : states.keySet())
            this.plugin.getAuthHandler().changeState(uuid, states.get(uuid));

        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.RELOADED_CONFIG);
        return false;
    }

    @Override
    public List<String> tabOptions(@NotNull CommandSender commandSender, @NotNull String[] strings) {
        return new ArrayList<>();
    }

    @Override
    public void noPermissionEvent(@NotNull CommandSender commandSender) {
        this.plugin.getMessageHandler().sendMessage(commandSender, MessageHandler.TwoFAMessages.NO_PERMISSIONS);
    }

    @Override
    public @NotNull String getDescription() {
        return ChatColor.translateAlternateColorCodes('&', MessageHandler.TwoFAMessages.DESCRIPTION_OF_RELOAD_COMMAND.getMessage());
    }

    public String[] getAliases() {
        return new String[] { "rl" };
    }
}