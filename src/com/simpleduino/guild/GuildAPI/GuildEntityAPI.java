package com.simpleduino.guild.GuildAPI;

/**
 * Created by Simple-Duino on 02/07/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class GuildEntityAPI {

    private String guildName;
    private GuildSQL guildSQL = new GuildSQL();

    public GuildEntityAPI(String gName)
    {
        this.guildName = gName;
    }

    public String getTag()
    {
        return guildSQL.getTag(this.guildName);
    }

    public String getMotd()
    {
        return guildSQL.getMotd(this.guildName);
    }

    public int getCoins()
    {
        return Integer.parseInt(guildSQL.getGuildCoins(this.guildName));
    }

    public int getCoinsPerDay()
    {
        return Integer.parseInt(guildSQL.getGuildCoinsPerDay(this.guildName));
    }

    public int getDailyCoins()
    {
        return guildSQL.getDailyCoins(this.guildName);
    }

    public int getMaxMembers()
    {
        return Integer.parseInt(guildSQL.getGuildMaxMembers(this.guildName));
    }

    public boolean hasTag()
    {
        return guildSQL.getHasTag(this.guildName);
    }

    public void setTag(String tag)
    {
        guildSQL.setTag(tag, this.guildName);
    }

    public void setMotd(String motd)
    {
        guildSQL.setMotd(motd, this.guildName);
    }

    public void setCoins(int coins)
    {
        guildSQL.setGuildCoins(this.guildName, coins);
    }

    public void setCoinsPerDay(int coins)
    {
        guildSQL.setGuildCoinsPerDay(this.guildName, coins);
    }

    public void setDailyCoins(int coins)
    {
        guildSQL.setDailyCoins(this.guildName, coins);
    }

    public void setMaxMembers(int amount)
    {
        guildSQL.setGuildMaxMembers(this.guildName, amount);
    }

    public void setHasTag(boolean value)
    {
        guildSQL.setHasTag(value, this.guildName);
    }

    public void closeConnection()
    {
        this.guildSQL.closeConnection();
    }

    public void setGuild(String gName)
    {
        this.guildName = gName;
    }
}
