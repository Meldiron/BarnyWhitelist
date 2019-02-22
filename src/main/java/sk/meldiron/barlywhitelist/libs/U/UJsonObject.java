package sk.meldiron.barlywhitelist.libs.U;

import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import sk.meldiron.barlywhitelist.libs.Utils;


public class UJsonObject extends JSONObject {

    public UJsonObject() {

    }

    public UJsonObject(JSONObject obj) {
        super(obj);
    }

    public static UJsonObject deepMerge(UJsonObject target, UJsonObject source) {
        UJsonObject newObj = new UJsonObject();

        source.forEach((key, value) -> {
            if(target.get(key) == null) {
                newObj.put(key, value);
            } else {
                if(value instanceof UJsonObject) {
                    UJsonObject deepObj = (UJsonObject) value;
                    UJsonObject newTarget = target.getObject(key.toString());

                    newObj.put(key, deepMerge(newTarget, deepObj));
                } else {
                    newObj.put(key, target.get(key));
                }
            }
        });

        return newObj;
    }

    public void set(String key, Object value) {
        if(getRawData(key).equalsIgnoreCase("")) {
            put(key, value);
        } else {
            replace(key, value);
        }
    }

    public String getRawData(String key) {
        if(key.contains(".")) {
            UJsonObject obj = null;

            int i = 0;

            String[] keys = key.split("\\.");
            for(String part : keys) {
                if(i + 1 >= keys.length) {
                    return Utils.toString(obj.get(part));
                } else {
                    if(obj == null) {
                        obj = Utils.toJsonObject(this.get(part));
                    } else {
                        obj = Utils.toJsonObject(obj.get(part));
                    }
                }

                i++;
            }
            return "";
        } else {
            return Utils.toString(this.get(key));
        }
    }

    public String getString(String key) {
        try {
            return ChatColor.translateAlternateColorCodes('&', getRawData(key));
        } catch (Exception exp) {
            return null;
        }
    }

    public Boolean getBoolean(String key) {
        try {
            return Boolean.valueOf(getRawData(key));
        } catch(Exception exp) {
            return null;
        }
    }

    public Double getDouble(String key) {
        try {
            return Double.parseDouble(getRawData(key));
        } catch (Exception ex) {
            return null;
        }
    }

    public Integer getInteger(String key) {
        try {
            return Integer.parseInt(getRawData(key));
        } catch (Exception ex) {
            return null;
        }
    }


    public UJsonObject getObject(String key) {
        try {
            if(key.contains(".")) {
                UJsonObject obj = null;

                int i = 0;

                String[] keys = key.split("\\.");
                for(String part : keys) {
                    if(i + 1 >= keys.length) {
                        return Utils.toJsonObject(obj.get(part));
                    } else {
                        if(obj == null) {
                            obj = Utils.toJsonObject(this.get(part));
                        } else {
                            obj = Utils.toJsonObject(obj.get(part));
                        }
                    }

                    i++;
                }
                return null;
            } else {
                return Utils.toJsonObject(this.get(key));
            }
        } catch (Exception exp) {
            return null;
        }
    }

    public UJsonArray getArray(String key) {
        try {
            if (key.contains(".")) {
                UJsonObject obj = null;

                int i = 0;

                String[] keys = key.split("\\.");
                for (String part : keys) {
                    if (i + 1 >= keys.length) {
                        return Utils.toJsonArray(obj.get(part));
                    } else {
                        if (obj == null) {
                            obj = Utils.toJsonObject(this.get(part));
                        } else {
                            obj = Utils.toJsonObject(obj.get(part));
                        }
                    }

                    i++;
                }
                return null;
            } else {
                return Utils.toJsonArray(this.get(key));
            }
        } catch (Exception exp) {
            return null;
        }
    }
}
