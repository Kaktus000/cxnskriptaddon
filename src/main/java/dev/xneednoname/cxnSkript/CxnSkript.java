package dev.xneednoname.cxnSkript;


import java.io.IOException;

import de.cytooxien.realms.api.RealmInformationProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

public class CxnSkript extends JavaPlugin {

    CxnSkript instance;
    SkriptAddon addon;

    public void onEnable() {
        instance = this;
        addon = Skript.registerAddon(this);

        try {
            // Load ALL classes (including expressions) in one call
            addon.loadClasses("dev.xneednoname.cxnSkript", "elements");
            getLogger().info("CxnSkript loaded successfully!");
        } catch (IOException e) {
            getLogger().severe("Failed to load CxnSkript: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Optional: Check if RealmInformationProvider exists
        if(Bukkit.getServicesManager().load(RealmInformationProvider.class) == null) {
            getLogger().warning("RealmInformationProvider not found - boost counts will be unavailable");
        }
    }

    public CxnSkript getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
}