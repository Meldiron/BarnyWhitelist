package sk.meldiron.barlywhitelist.commands;

import sk.meldiron.barlywhitelist.libs.MelCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TemplateCmd implements MelCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
//        return new ArrayList<String>(){{
//            add("");
//        }};

        return null;
    }
}
