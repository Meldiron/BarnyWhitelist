package sk.meldiron.barlywhitelist.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import sk.meldiron.barlywhitelist.libs.SQL;
import sk.meldiron.barlywhitelist.libs.Utils;

public class OnJoin implements Listener {
    @EventHandler
    public void On(PlayerJoinEvent e) {
        SQL.getInstance().exec("SELECT 1 FROM users WHERE minecraft_uuid = ?", (st) -> {
            st.setString(1, e.getPlayer().getUniqueId().toString().replace("-", ""));
        }, ((con, st, res) -> {
            try {
                if(res.next()) {
                    // is allowed to join
                } else {
                    e.getPlayer().kickPlayer(Utils.msg("kickMsg"));
                }

                SQL.closeAll(con,st,res);
            } catch (Exception exp) {
                exp.printStackTrace();
                SQL.closeAll(con,st,res);
            }
        }));
    }
}
