package sk.meldiron.barlywhitelist.libs.U;

import org.json.simple.JSONArray;
import sk.meldiron.barlywhitelist.libs.Utils;

import java.util.ArrayList;


public class UJsonArray extends JSONArray {

    public UJsonArray() {

    }

    public UJsonArray(JSONArray obj) {
        for(Object objP: obj) {
            add(objP);
        }
    }

    public ArrayList<String> getStrings() {
        ArrayList<String> arr = new ArrayList<>();

        for(Object obj : this) {
            arr.add(Utils.color(Utils.toString(obj)));
        }

        return arr;
    }

    public ArrayList<Boolean> getBooleans() {
        ArrayList<Boolean> arr = new ArrayList<>();

        for(Object obj : this) {
            arr.add(Utils.toBoolean(obj));
        }

        return arr;
    }

    public ArrayList<Double> getDoubles() {
        ArrayList<Double> arr = new ArrayList<>();

        for(Object obj : this) {
            arr.add(Utils.toDouble(obj));
        }

        return arr;
    }

    public ArrayList<Integer> getIntegers() {
        ArrayList<Integer> arr = new ArrayList<>();

        for(Object obj : this) {
            arr.add(Utils.toInteger(obj));
        }

        return arr;
    }

    public ArrayList<UJsonObject> getObjects() {
        ArrayList<UJsonObject> arr = new ArrayList<>();

        for(Object obj : this) {
            arr.add(Utils.toJsonObject(obj));
        }

        return arr;
    }

    public ArrayList<UJsonArray> getArrays() {
        ArrayList<UJsonArray> arr = new ArrayList<>();

        for(Object obj : this) {
            arr.add(Utils.toJsonArray(obj));
        }

        return arr;
    }
}
