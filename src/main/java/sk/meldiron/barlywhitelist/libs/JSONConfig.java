package sk.meldiron.barlywhitelist.libs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import org.json.simple.JSONObject;
import sk.meldiron.barlywhitelist.Main;
import org.bukkit.ChatColor;
import org.json.simple.parser.JSONParser;
import sk.meldiron.barlywhitelist.libs.U.UJsonArray;
import sk.meldiron.barlywhitelist.libs.U.UJsonObject;


public class JSONConfig {

    private File file;
    public UJsonObject json;
    private JSONParser parser = new JSONParser();

    private List<InitFunction> initFuncs;

    public interface InitFunction {
        UJsonObject callback(UJsonObject config);
    }

    public JSONConfig(String fileName, Main main) {


        this.initFuncs = new ArrayList<>();

        this.file = new File(main.getDataFolder(), fileName);
    }

    public void addInitFunc(InitFunction func) {
        initFuncs.add(func);
    }

    public void reload() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                PrintWriter pw = new PrintWriter(file, "UTF-8");
                pw.print("{");
                pw.print("}");
                pw.flush();
                pw.close();
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                try (InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {
                    json = new UJsonObject((JSONObject) parser.parse(isr));

                    for (InitFunction initFunc : initFuncs) {
                        json = initFunc.callback(json);

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean save() {
        try {
            Gson g = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(json.toJSONString());
            String prettyJsonString = g.toJson(je);

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(prettyJsonString);
                fw.flush();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public UJsonObject getData() {
        return json;
    }
}
