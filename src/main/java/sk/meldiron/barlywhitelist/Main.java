package sk.meldiron.barlywhitelist;

import fr.minuskube.inv.InventoryManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import sk.meldiron.barlywhitelist.events.OnJoin;
import sk.meldiron.barlywhitelist.events.OnLeave;
import sk.meldiron.barlywhitelist.libs.JSONConfig;
import sk.meldiron.barlywhitelist.libs.MelCommand;
import sk.meldiron.barlywhitelist.libs.SQL;
import sk.meldiron.barlywhitelist.libs.U.UJsonArray;
import sk.meldiron.barlywhitelist.libs.U.UJsonObject;
import sk.meldiron.barlywhitelist.libs.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public final class Main extends JavaPlugin implements Listener {
    private String pluginName = "BarnyWhitelist";
    private Boolean usingSQl = true;

    private ArrayList<Listener> events = new ArrayList<Listener>(){{
        add(new OnJoin());
        add(new OnLeave());
    }};

    private HashMap<String, MelCommand> cmds = new HashMap(){{
        // put("command", new TemplateCmd());
    }};

    private HashMap<String, UJsonObject> configsObjs = new HashMap<String, UJsonObject>(){{
        put("config", new UJsonObject(){{
            put("pw", "SECRET");
            put("mysql", new UJsonObject(){{
               put("host", "localhost");
               put("user", "root");
               put("password", "");
               put("database", "mc");
               put("port", 3306);
               put("enabled", true);
            }});

            put("allowedUuids", new UJsonArray(){{
                add("38d6e62f5f824d98abb88b5ff42a4873");
            }});
       }});

       put("translations", new UJsonObject(){{
           put("kickMsg", "&c&lYou must confirm you are sub! &fhttp://google.sk/IAmSub");
       }});
    }};



    public static Main i;

    public JSONConfig config;
    public JSONConfig lang;

    public InventoryManager invManager;

    @Override
    public void onEnable() {
        i = this;

        loadConfigs();
        registerCommands();
        registerEvents();

        if(usingSQl == true) {
            UJsonObject mysqlConfig = this.config.getData().getObject("mysql");
            if (Utils.toBoolean(mysqlConfig.get("enabled")) == false) {
                SQL.getInstance().connectLite();
            } else {
                SQL.getInstance().connect(
                        mysqlConfig.getString("host"),
                        mysqlConfig.getString("database"),
                        mysqlConfig.getString("user"),
                        mysqlConfig.getString("password"),
                        mysqlConfig.getInteger("port")
                );
            }
        }

        invManager = new InventoryManager(this);
        invManager.init();

        getLogger().info("Plugin " + pluginName + " started");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin " + pluginName + " stopped");
        SQL.getInstance().closeConnections();
    }

    private void registerEvents() {
        for(Listener e : events) {
            getServer().getPluginManager().registerEvents(e, this);
        }
    }

    private void registerCommands() {
        for(String cmd : cmds.keySet()) {
            MelCommand cmdClass = cmds.get(cmd);
            this.getCommand(cmd).setTabCompleter(cmdClass);
            this.getCommand(cmd).setExecutor(cmdClass);
        }
    }

    private void loadConfigs() {
        for(String cfgName : configsObjs.keySet()) {
            JSONConfig cfg = new JSONConfig(cfgName + ".json", this);

            UJsonObject configObj = configsObjs.get(cfgName);

            cfg.addInitFunc(json -> {
                UJsonObject newObj = UJsonObject.deepMerge(json, configObj);
                return newObj;
            });

            cfg.reload();
            cfg.save();

            if(cfgName == "config") {
                config = cfg;
            } else if(cfgName == "translations") {
                lang = cfg;
            }

        }
    }

    public void reloadConfigs() {
        config.reload();
        lang.reload();
    }
}
