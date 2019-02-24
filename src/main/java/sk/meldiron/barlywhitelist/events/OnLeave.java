package sk.meldiron.barlywhitelist.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sk.meldiron.barlywhitelist.Main;
import sk.meldiron.barlywhitelist.libs.SQL;
import sk.meldiron.barlywhitelist.libs.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OnLeave implements Listener {
    @EventHandler
    public void On(PlayerQuitEvent e) {
        final String trimmedUuid = e.getPlayer().getUniqueId().toString().replace("-", "");

        Bukkit.getScheduler().runTaskAsynchronously(Main.i, () -> {
            try {
                URL url = new URL("https://barny.herokuapp.com/check/" + trimmedUuid + "?pw=" + Main.i.config.getData().getString("pw"));
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(15000);
                con.setReadTimeout(15000);
                con.setInstanceFollowRedirects(false);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));


                in.close();
                con.disconnect();
            } catch(Exception exp) {
                System.out.println("Error while trying to update sub-status");
                exp.printStackTrace();
            }
        });
    }
}
