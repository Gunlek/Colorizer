package com.simpleduino.guild.GuildAPI;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Simple-Duino on 06/07/2016.
 * Copyrights Simple-Duino, all rights reserved
 */

public class InitGuildAPI {

    File cfgFile = new File("plugins/GuildEntityAPI/config.yml");
    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);

    public InitGuildAPI()
    {
        if(!cfgFile.exists())
        {
            cfgFile.getParentFile().mkdirs();
            try {
                cfgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);
            cfg.set("sql.hostname", "localhost");
            cfg.set("sql.database", "guild");
            cfg.set("sql.username", "user");
            cfg.set("sql.password", "password");
            cfg.set("guild.default.motd", "Ceci est le motd par défaut de votre guilde, utilisez /guild motd <new_motd> pour le modifier");
            cfg.set("guild.default.coins", "0");
            cfg.set("guild.default.max-members", "10");
            cfg.set("ranks.0", "Membre");
            cfg.set("ranks.1", "Modérateur");
            cfg.set("ranks.2", "Administrateur");
            cfg.set("ranks.3", "Leader");
            try {
                cfg.save(cfgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
