package me.igwb.DeathPain;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MyCommandExecutor implements CommandExecutor {

    Plugin parent;

    public MyCommandExecutor(Plugin parent) {

        this.parent = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {

        if(arg3 == null || arg3.length == 0) {
            sender.sendMessage("No arguments found - nothing to do here.");
            return true;
        }

        switch (arg3[0].toLowerCase()) {
        case "top":
            cmdTop(sender, arg3);
            return true;
        case "info":
            sender.sendMessage("This command was not yet implemented.");
            break;	
        case "list":
            sender.sendMessage("This command was not yet implemented.");
            break;
        case "createfacility":
            cmdCreateFacility(sender, arg3);
            return true;
        default:
            sender.sendMessage("Command not supported!");
            break;
        }		
        return false;
    }

    private void cmdTop(CommandSender sender, String[] args) {

        if(!sender.hasPermission("death.top")) {
            sender.sendMessage("You are not allowed to use /death top!");
            return;
        }

        String ranking[];

        ranking = parent.getSQL().getDeathRanking();

        if(ranking == null) {
            sender.sendMessage("It seems that no one has died yet.");
            parent.getLogger().warning("Could not find any players in the database. @ " + this.getClass().getName());
            return;
        }

        int i, page;
        if(args.length > 1) {
            page = Integer.parseInt(args[1]) - 1;
        } else {
            page = 0;
        }

        if((page + 1) > Math.round((ranking.length / 10) + 0.5)) {
            sender.sendMessage("Page number out of bounds.");
            return;
        }

        sender.sendMessage("Page " + (page + 1) + " of " + Math.round((ranking.length / 10) + 0.5));
        sender.sendMessage("Rank, DeathCount, Players");
        for (i = page * 10; i <= (page * 10) + 10 && i < ranking.length; i++) {
            String element = ranking[i];
            if(element != null) {
                sender.sendMessage("[" + i + "] " + element);
            }
        }
    }

    private void cmdCreateFacility(CommandSender sender, String[]args) {
        
        sender.sendMessage("Place a sign at the start point of your facility.");
        sender.sendMessage("First line: Facility name");
        sender.sendMessage("Second line: Max Severity (1-20)");
        sender.sendMessage("Third line: Min Severity: (0-19");
        sender.sendMessage("Fourth line: Max time a player can remain here (minutes) | 0 to disable");
        sender.sendMessage("Rightclick the sign to complete, rightclick any other block to cancel.");
        
        parent.getFacilityCreator().setActive(true);
        parent.getFacilityCreator().setPlayerActive(sender.getName());
    }
}
