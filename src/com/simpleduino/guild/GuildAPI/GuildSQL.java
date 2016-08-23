package com.simpleduino.guild.GuildAPI;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Simple-Duino on 28/06/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class GuildSQL {

    private File cfgFile = new File("plugins/Guild/config.yml");
    private YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);
    private Connection con;

    public GuildSQL()
    {
        String hostname = this.cfg.get("sql.hostname").toString();
        String database = this.cfg.get("sql.database").toString();
        String username = this.cfg.get("sql.username").toString();
        String password = this.cfg.get("sql.password").toString();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.con = DriverManager.getConnection("jdbc:mysql://"+hostname+":3306/"+database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!this.isInit())
        {
            this.initDb();
        }
    }

    private void initDb()
    {
        try {
            Statement statement = this.con.createStatement();
            statement.execute("CREATE TABLE `"+cfg.get("sql.database").toString()+"`.`guild_members` ( `id` INT NOT NULL AUTO_INCREMENT , `member` VARCHAR(255) NOT NULL , `uuid` VARCHAR(255) NOT NULL , `guild_name` VARCHAR(255) NOT NULL , `guild_rank` VARCHAR(255) NOT NULL , `guild_notif` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB");
            statement.execute("CREATE TABLE `"+cfg.get("sql.database").toString()+"`.`guild_list` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `tag` VARCHAR(255) NOT NULL , `motd` TEXT NOT NULL , `coins` VARCHAR(255) NOT NULL , `max_members` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isInit()
    {
        try {
            Statement statement = this.con.createStatement();
            int table = 0;
            statement.execute("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE `TABLE_NAME` = \"guild_members\"");
            ResultSet result = statement.getResultSet();
            if(result.next())
            {
                table++;
            }
            result.close();
            statement.execute("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE `TABLE_NAME` = \"guild_list\"");
            result = statement.getResultSet();
            if(result.next())
            {
                table++;
            }
            if(table >= 2)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createGuild(String name, Player p)
    {
        try {
            Statement statement = this.con.createStatement();
            String defaultMotd = cfg.get("guild.default.motd").toString();
            String defaultCoins = cfg.get("guild.default.coins").toString();
            String defaultMaxMembers = cfg.get("guild.default.max-members").toString();
            String request = "SELECT * FROM guild_list WHERE name=\""+name+"\"";
            ResultSet result = statement.executeQuery(request);
            if(!result.next()) {
                request = "INSERT INTO guild_list(name, hasTag, tag, motd, coins_per_day, daily_coins, coins, max_members) VALUES(\"" + name + "\", \"0\", \"" + name + "\", \"" + defaultMotd + "\", \"" + defaultCoins + "\", \"0\", \"0\", \""+defaultMaxMembers+"\")";
                statement.execute(request);
                request = "SELECT * FROM guild_members WHERE uuid=\"" + p.getUniqueId().toString() + "\"";
                ResultSet result2 = statement.executeQuery(request);
                if (result2.next()) {
                    request = "UPDATE guild_members SET guild_name=\"" + name + "\", guild_rank=\"3\", guild_notif=\"1\" WHERE uuid=\"" + p.getUniqueId().toString() + "\"";
                    statement.execute(request);
                } else {
                    request = "INSERT INTO guild_members(member, uuid, guild_name, guild_rank, guild_notif) VALUES(\"" + p.getName() + "\", \"" + p.getUniqueId().toString() + "\", \"" + name + "\", \"3\", \"1\")";
                    statement.execute(request);
                }

                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getGuildMembers(String name)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE guild_name=\""+name+"\"";
            ResultSet result = statement.executeQuery(request);
            ArrayList<String> members = new ArrayList<>();
            while(result.next())
            {
                String member = result.getString("member");
                members.add(member);
            }
            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasGuild(Player p)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            String guildName = null;
            if(result.next())
            {
                guildName = result.getString("guild_name");
            }
            if(guildName != null && !guildName.equalsIgnoreCase(""))
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasGuild(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+uuid.toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            String guildName = null;
            if(result.next())
            {
                guildName = result.getString("guild_name");
            }
            if(guildName != null && !guildName.equalsIgnoreCase(""))
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasGuild(String name)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE member=\""+name+"\"";
            ResultSet result = statement.executeQuery(request);
            String guildName = null;
            if(result.next())
            {
                guildName = result.getString("guild_name");
            }
            if(guildName != null && !guildName.equalsIgnoreCase(""))
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getGuild(Player p)
    {
        String guildName = null;
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                guildName = result.getString("guild_name");
            }
            return guildName;
        } catch (SQLException e) {
            e.printStackTrace();
            return guildName;
        }
    }

    public String getGuild(UUID uuid)
    {
        String guildName = null;
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+uuid.toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                guildName = result.getString("guild_name");
            }
            return guildName;
        } catch (SQLException e) {
            e.printStackTrace();
            return guildName;
        }
    }

    public String getGuild(String name)
    {
        String guildName = null;
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE member=\""+name+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                guildName = result.getString("guild_name");
            }
            return guildName;
        } catch (SQLException e) {
            e.printStackTrace();
            return guildName;
        }
    }

    public int getRank(Player p)
    {
        int rank = -1;
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                rank = Integer.parseInt(result.getString("guild_rank"));
            }
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return rank;
        }
    }

    public int getRank(UUID uuid)
    {
        int rank = -1;
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+uuid.toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                rank = Integer.parseInt(result.getString("guild_rank"));
            }
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return rank;
        }
    }

    public int getRank(String name) {
        int rank = -1;
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE member=\"" + name + "\"";
            ResultSet result = statement.executeQuery(request);
            if (result.next()) {
                rank = Integer.parseInt(result.getString("guild_rank"));
            }
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
            return rank;
        }
    }

    public void setRank(Player p, int rank)
    {
        try {
            Statement statement = this.con.createStatement();
            statement.execute("UPDATE guild_members SET guild_rank=\""+Integer.toString(rank)+"\" WHERE uuid=\""+p.getUniqueId().toString()+"\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRank(UUID uuid, int rank)
    {
        try {
            Statement statement = this.con.createStatement();
            statement.execute("UPDATE guild_members SET guild_rank=\""+Integer.toString(rank)+"\" WHERE uuid=\""+uuid.toString()+"\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRank(String name, int rank)
    {
        try {
            Statement statement = this.con.createStatement();
            statement.execute("UPDATE guild_members SET guild_rank=\""+Integer.toString(rank)+"\" WHERE member=\""+name+"\"");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean joinGuild(Player p, String name)
    {
        String request = "SELECT * FROM guild_members WHERE uuid=\""+p.getUniqueId().toString()+"\"";
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                request = "UPDATE guild_members SET guild_name=\""+name+"\", guild_rank=\"0\", guild_notif=\"1\" WHERE uuid=\""+p.getUniqueId().toString()+"\"";
                statement.execute(request);
                return true;
            }
            else
            {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerMember(Player p)
    {
        String request = "INSERT INTO guild_members(member, uuid, guild_name, guild_rank, guild_notif) VALUES(\""+p.getName()+"\", \""+p.getUniqueId().toString()+"\", \"\", \"\", \"\")";
        try {
            Statement statement = this.con.createStatement();
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void leaveGuild(Player p)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_members SET guild_name=\"\", guild_rank=\"\" WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getGuildCoins(String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM guild_list WHERE name=\""+guildName+"\"");
            if(result.next())
            {
                return result.getString("coins");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setGuildCoins(String guildName, int amount)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_list SET coins=\""+Integer.toString(amount)+"\" WHERE name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getGuildCoinsPerDay(String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM guild_list WHERE name=\""+guildName+"\"");
            if(result.next())
            {
                return result.getString("coins_per_day");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setGuildCoinsPerDay(String guildName, int amount)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_list SET coins_per_day=\""+Integer.toString(amount)+"\" WHERE name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getGuildMaxMembers(String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM guild_list WHERE name=\""+guildName+"\"");
            if(result.next())
            {
                return result.getString("max_members");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setGuildMaxMembers(String guildName, int maxMembers)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_list SET max_members=\""+Integer.toString(maxMembers)+"\" WHERE name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disbandGuild(String name)
    {
        try
        {
            Statement statement = this.con.createStatement();
            String request = "DELETE FROM guild_list WHERE name=\""+name+"\"";
            statement.execute(request);
            for(String pName : this.getGuildMembers(name))
            {
                request = "UPDATE guild_members SET guild_name=\"\", guild_rank=\"\", guild_notif=\"\" WHERE guild_name=\""+name+"\"";
                statement.execute(request);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setNotif(Player p, boolean value)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "";
            if(value)
                request = "UPDATE guild_members SET guild_notif=\"1\" WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            else
                request = "UPDATE guild_members SET guild_notif=\"0\" WHERE uuid=\""+p.getUniqueId().toString()+"\"";

            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNotif(UUID u, boolean value)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "";
            if(value)
                request = "UPDATE guild_members SET guild_notif=\"1\" WHERE uuid=\""+u.toString()+"\"";
            else
                request = "UPDATE guild_members SET guild_notif=\"0\" WHERE uuid=\""+u.toString()+"\"";

            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNotif(String name, boolean value) {
        try {
            Statement statement = this.con.createStatement();
            String request = "";
            if (value)
                request = "UPDATE guild_members SET guild_notif=\"1\" WHERE member=\"" + name + "\"";
            else
                request = "UPDATE guild_members SET guild_notif=\"0\" WHERE member=\"" + name + "\"";

            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getNotif(Player p)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                int notif = Integer.parseInt(result.getString("guild_notif"));
                if(notif==1)
                    return true;
                else
                    return false;
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getNotif(UUID uuid)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+uuid.toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                int notif = Integer.parseInt(result.getString("guild_notif"));
                if(notif==1)
                    return true;
                else
                    return false;
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getNotif(String name)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE member=\""+name+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                int notif = Integer.parseInt(result.getString("guild_notif"));
                if(notif==1)
                    return true;
                else
                    return false;
            }
            else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void kickMember(Player p, String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_members SET guild_name=\"\", guild_rank=\"\", guild_notif=\"\" WHERE uuid=\""+p.getUniqueId().toString()+"\" AND guild_name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void kickMember(UUID uuid, String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_members SET guild_name=\"\", guild_rank=\"\", guild_notif=\"\" WHERE uuid=\""+uuid+"\" AND guild_name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void kickMember(String name, String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_members SET guild_name=\"\", guild_rank=\"\", guild_notif=\"\" WHERE member=\""+name+"\" AND guild_name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMotd(String motd, String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_list SET motd=\""+motd+"\" WHERE name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTag(String tag, String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_list SET tag=\""+tag+"\" WHERE name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getMotd(String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_list WHERE name=\""+guildName+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return result.getString("motd");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTag(String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_list WHERE name=\""+guildName+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return result.getString("tag");
            }
            else
            {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setHasTag(boolean value, String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "";
            if(value)
                request = "UPDATE guild_list SET hasTag=\"1\" WHERE name=\""+guildName+"\"";
            else
                request = "UPDATE guild_list SET hasTag=\"0\" WHERE name=\""+guildName+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getHasTag(String guildName)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_list WHERE name=\""+guildName+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                if(result.getString("hasTag").equalsIgnoreCase("1"))
                    return true;
                else
                    return false;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRegistered(Player p)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_members WHERE uuid=\""+p.getUniqueId().toString()+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getDailyCoins(String name)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_list WHERE name=\""+name+"\"";
            ResultSet result = statement.executeQuery(request);
            if(result.next())
            {
                return Integer.parseInt(result.getString("daily_coins"));
            }
            else
            {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setDailyCoins(String name, int amount)
    {
        try {
            Statement statement = this.con.createStatement();
            String request = "UPDATE guild_list SET daily_coins=\""+Integer.toString(amount)+"\" WHERE name=\""+name+"\"";
            statement.execute(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getGuilds()
    {
        ArrayList<String> guildList = new ArrayList<>();
        try {
            Statement statement = this.con.createStatement();
            String request = "SELECT * FROM guild_list";
            ResultSet result = statement.executeQuery(request);
            while(result.next())
            {
                Bukkit.broadcastMessage("ok");
                guildList.add(result.getString("name"));
            }

            return guildList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection()
    {
        try {
            this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
