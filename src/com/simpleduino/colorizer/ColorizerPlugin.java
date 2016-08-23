package com.simpleduino.colorizer;

import com.simpleduino.colorizer.Listeners.PlayerListener;
import com.simpleduino.guild.GuildAPI.InitGuildAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Simple-Duino on 16/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class ColorizerPlugin extends JavaPlugin{

    File cfgFile = new File("plugins/Colorizer/config.yml");
    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);

    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    public void onEnable()
    {
        this.setupPermissions();
        this.setupChat();
        this.setupEconomy();

        if(!cfgFile.exists())
        {
            cfgFile.getParentFile().mkdirs();
            try {
                cfgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            cfg.set("tab.header", "Default header");
            cfg.set("tab.footer", "Default footer");

            try {
                cfg.save(cfgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new InitGuildAPI();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public void onDisable()
    {

    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

}
