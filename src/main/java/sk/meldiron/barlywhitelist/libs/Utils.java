package sk.meldiron.barlywhitelist.libs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sk.meldiron.barlywhitelist.Main;
import sk.meldiron.barlywhitelist.libs.U.UItemStack;
import sk.meldiron.barlywhitelist.libs.U.UJsonArray;
import sk.meldiron.barlywhitelist.libs.U.UJsonObject;
import sk.meldiron.barlywhitelist.libs.U.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class Utils {
    public interface PlaceholderFunction {
        String callback(String msg);
    }

    public static String toCommand(String cmd) {
        if(cmd.startsWith("/")) {
            return cmd.substring(1);
        } else {
            return cmd;
        }
    }

    public static UJsonObject toJsonObject(Object obj) {
        if(obj == null) {
            return null;
        }

        if(obj instanceof  UJsonObject) {
            return (UJsonObject) obj;
        }

        if(obj instanceof JSONObject) {
            return new UJsonObject((JSONObject) obj);
        }

        return new UJsonObject();
    }

    public static UJsonArray toJsonArray(Object obj) {
        try {
            if(obj == null) {
                return null;
            }

            if(obj instanceof JSONArray) {
                return new UJsonArray((JSONArray) obj);
            }

            return (UJsonArray) obj;
        } catch (Exception exp) {
            return new UJsonArray();
        }
    }

    public static Double toDouble(Object obj) {
        if(obj == null) {
            return 0.D;
        }

        return Double.parseDouble(obj.toString());
    }

    public static Float toFloat(Object obj) {
        if(obj == null) {
            return 0.0F;
        }

        return Float.parseFloat(obj.toString());
    }

    public static boolean toBoolean(Object obj) {
        if(obj == null) {
            return false;
        }

        return Boolean.parseBoolean(obj.toString());
    }

    public static Integer toInteger(Object obj) {
        if(obj == null) {
            return 0;
        }

        return Integer.parseInt(obj.toString());
    }

    public static String toString(Object obj) {
        if(obj == null) {
            return "";
        }

        return obj.toString();
    }

    public static Location toLocation(UJsonObject obj) {
        double locX = Utils.toDouble(obj.get("x"));
        double locY = Utils.toDouble(obj.get("y"));
        double locZ = Utils.toDouble(obj.get("z"));
        float locYaw = Utils.toFloat(obj.get("yaw"));
        float locPitch = Utils.toFloat(obj.get("pitch"));
        String locWorld = Utils.toString(obj.get("world"));

        return new Location(Bukkit.getWorld(locWorld), locX, locY, locZ, locYaw, locPitch);
    }

    public static Location toBlockLocation(UJsonObject obj) {
        Integer locX = Utils.toInteger(obj.get("x"));
        Integer locY = Utils.toInteger(obj.get("y"));
        Integer locZ = Utils.toInteger(obj.get("z"));
        String locWorld = Utils.toString(obj.get("world"));

        return new Location(Bukkit.getWorld(locWorld), locX, locY, locZ);
    }

    public static ItemStack toItemStack(UJsonObject obj) {
        return toItemStack(obj, (msg) -> { return msg; });
    }

    public static ItemStack toItemStack(UJsonObject obj, PlaceholderFunction func) {
        List<String> loreArr = new ArrayList<>();

        for(Object lineObj : Utils.toJsonArray(obj.get("lore"))) {
            String line = Utils.toString(lineObj);

            loreArr.add(func.callback(line));
        }

        ItemStack item = UItemStack.createItem(
                UMaterial.valueOf(Utils.toString(obj.get("material"))),
                Utils.toString(obj.get("title")),
                loreArr
        );

        return item;
    }

    public static String removeLast(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static String color(String msg) {
        return msg.replace("&", "§");
    }

    public static String msg(boolean withPrefix, String path) {
        if(withPrefix == true) {
            return Utils.color(Main.i.lang.getData().getString("prefix") + Main.i.lang.getData().getString(path));
        } else {
            return Utils.color(Main.i.lang.getData().getString(path));
        }
    }


    public static String msg(String path) {
        return Utils.color(Main.i.lang.getData().getString(path));
    }

}
