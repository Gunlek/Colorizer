package com.simpleduino.guild.GuildAPI;

import org.bukkit.entity.Player;

/**
 * Created by Simple-Duino on 02/07/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class GuildMemberAPI {

    private Player player;
    private GuildSQL guildSQL = new GuildSQL();

    public GuildMemberAPI(Player p)
    {
        this.player = p;
    }

    public int getRank()
    {
        return guildSQL.getRank(this.player);
    }

    public boolean hasGuild()
    {
        return guildSQL.hasGuild(this.player);
    }

    public String getGuild()
    {
        return guildSQL.getGuild(this.player);
    }

    public boolean hasNotifications()
    {
        return guildSQL.getNotif(this.player);
    }

    public void closeConnection()
    {
        guildSQL.closeConnection();
    }

    public void setPlayer(Player p)
    {
        this.player = p;
    }
}
