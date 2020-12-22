package com.willfp.eco.util.command;

import com.willfp.eco.util.config.Configs;
import com.willfp.eco.util.injection.PluginDependent;
import com.willfp.eco.util.interfaces.Registerable;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends PluginDependent implements CommandExecutor, Registerable {
    private final String name;
    private final String permission;
    private final boolean playersOnly;

    protected AbstractCommand(AbstractEcoPlugin plugin, String name, String permission, boolean playersOnly) {
        super(plugin);
        this.name = name;
        this.permission = permission;
        this.playersOnly = playersOnly;
    }

    public AbstractTabCompleter getTab() {
        return null;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(name)) return false;

        if (playersOnly && !(sender instanceof Player)) {
            sender.sendMessage(Configs.LANG.getMessage("not-player"));
            return true;
        }

        if (!sender.hasPermission(permission) && sender instanceof Player) {
            sender.sendMessage(Configs.LANG.getNoPermission());
            return true;
        }

        onExecute(sender, Arrays.asList(args));

        return true;
    }

    @Override
    public final void register() {
        PluginCommand command = Bukkit.getPluginCommand(name);
        assert command != null;
        command.setExecutor(this);

        AbstractTabCompleter tabCompleter = this.getTab();
        if (tabCompleter != null) {
            command.setTabCompleter(tabCompleter);
        }
    }

    public abstract void onExecute(CommandSender sender, List<String> args);
}
