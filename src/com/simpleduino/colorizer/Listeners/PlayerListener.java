package com.simpleduino.colorizer.Listeners;

import com.simpleduino.colorizer.ColorizerPlugin;
import com.simpleduino.guild.GuildAPI.GuildEntityAPI;
import com.simpleduino.guild.GuildAPI.GuildMemberAPI;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Simple-Duino on 16/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class PlayerListener implements Listener {

    File cfgFile = new File("plugins/Colorizer/config.yml");
    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        String groupPrefix = "";
        try
        {
            groupPrefix = ColorizerPlugin.chat.getGroupPrefix(p.getWorld(), ColorizerPlugin.permission.getPrimaryGroup(p));
        }
        catch(Exception e1)
        {

        }
        String prefixColor = groupPrefix;
        if(!prefixColor.equals(""))
        {
            prefixColor = prefixColor.replace("&", "§");
            String fullDisplayName = prefixColor+p.getName();
            if(Bukkit.getPluginManager().isPluginEnabled("GuildEntityAPI"));
            {
                GuildMemberAPI guildMemberAPI = new GuildMemberAPI(p);
                GuildEntityAPI guildEntityAPI = new GuildEntityAPI(guildMemberAPI.getGuild());
                if(guildEntityAPI.hasTag())
                    fullDisplayName+=" "+ ChatColor.DARK_GRAY+"["+ guildEntityAPI.getTag()+"]";
            }
            p.setCustomName(fullDisplayName);
            p.setCustomNameVisible(true);
            p.setDisplayName(fullDisplayName);
            p.setPlayerListName(fullDisplayName);
        }

        CraftPlayer cPlayer = (CraftPlayer)e.getPlayer();
        String headerStr = cfg.get("tab.header").toString().replace("&", "§");
        String footerStr = cfg.get("tab.footer").toString().replace("&", "§");
        IChatBaseComponent tabHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+headerStr+"\"}");
        IChatBaseComponent tabFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+footerStr+"\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field header = packet.getClass().getDeclaredField("a");
            header.setAccessible(true);
            header.set(packet, tabHeader);
            header.setAccessible(!header.isAccessible());
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        try {
            Field footer = packet.getClass().getDeclaredField("b");
            footer.setAccessible(true);
            footer.set(packet, tabFooter);
            footer.setAccessible(!footer.isAccessible());
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }

        cPlayer.getHandle().playerConnection.sendPacket(packet);
    }
}
