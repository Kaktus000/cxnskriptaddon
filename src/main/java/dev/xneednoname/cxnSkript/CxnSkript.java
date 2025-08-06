package dev.xneednoname.cxnSkript;

import java.io.IOException;
import de.cytooxien.realms.api.RealmInformationProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

public class CxnSkript extends JavaPlugin implements Listener {

    private CxnSkript instance;
    private SkriptAddon addon;
    private String currentVersion;
    private String latestVersion;

    @Override
    public void onEnable() {
        instance = this;
        addon = Skript.registerAddon(this);
        currentVersion = getPluginMeta().getVersion();
        Bukkit.getPluginManager().registerEvents(this, this);
        checkForUpdates();

        try {
            addon.loadClasses("dev.xneednoname.cxnSkript", "elements");
            getLogger().info("CxnSkript loaded successfully!");
        } catch (IOException e) {
            getLogger().severe("Failed to load CxnSkript: " + e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (Bukkit.getServicesManager().load(RealmInformationProvider.class) == null) {
            getLogger().warning("RealmInformationProvider not found - boost counts will be unavailable");
        }
    }

    private void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                URL url = new URL("https://raw.githubusercontent.com/Kaktus000/cxnskriptaddon/d2e5606a08b9cbd3e885126e335aad5fe57ae26d/src/main/resources/plugin.yml");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("version:")) {
                        latestVersion = line.split(":")[1].trim();
                        break;
                    }
                }
                reader.close();
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CxnSkript] Update-Check fehlgeschlagen: " + e.getMessage());
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() && latestVersion != null && !currentVersion.equals(latestVersion)) {
            player.sendMessage(ChatColor.YELLOW + "[CxnSkript] Eine neue Version (" + latestVersion + ") ist verfügbar!");
            player.sendMessage(ChatColor.GOLD + "Download: https://github.com/Kaktus000/cxnskriptaddon/releases");
        }
        else if (player.getName() == "XNeedNoName") {
            player.sendMessage("[CxnSkript] Dieser Realm Nutzt CxnSkript");
            }
    }

    public CxnSkript getInstance() {
        return instance;
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
}