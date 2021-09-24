package me.aleiv.core.paper.commands;

import org.bukkit.entity.Entity;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import lombok.NonNull;
import me.aleiv.core.paper.Core;

@CommandAlias("global")
@CommandPermission("admin.perm")
public class GlobalCMD extends BaseCommand {

    private @NonNull Core instance;
    Entity current = null;

    public GlobalCMD(Core instance) {
        this.instance = instance;

    }

    
}
