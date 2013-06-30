package me.igwb.DeathPain;


import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        case "listfacilities":
            cmdListFacilities(sender, arg3);
            return true;
        case "deletefacility":
            cmdDeleteFacility(sender, arg3);
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
//      TODO: Add better output formatting
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
        
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command must be excecuted by a player!");
            return;
        }
        
        sender.sendMessage("Place a sign at the start point of your facility.");
        sender.sendMessage("First line: Facility name");
        sender.sendMessage("Second line: Max Severity (1-20)");
        sender.sendMessage("Third line: Min Severity: (0-19");
        sender.sendMessage("Fourth line: Max time a player can remain here (minutes) | 0 to disable");
        sender.sendMessage("Rightclick the sign to complete, rightclick any other block to cancel.");
        
        parent.getFacilityCreator().setActive(true);
        parent.getFacilityCreator().setPlayerActive(sender.getName());
    }

    private void cmdListFacilities(CommandSender sender, String[] args) {
        
        ArrayList<Facility> facilityList = parent.getFacilityManager().getAllFacilities();
        Integer maxStringLength = null;
        
        for (Facility facility : facilityList) {
            if(maxStringLength == null) {
                maxStringLength = facility.getName().length();
            }
            maxStringLength = Math.max(maxStringLength, facility.getName().length());
        }
        
        
        //TODO: Add better formatting
        //TODO: Add multipage support.
        sender.sendMessage("Name | MaxSeverity | MinSeverity");
        
        for (Facility facility : facilityList) {
            sender.sendMessage(facility.getName() + " | " + facility.getMinSeverity().toString() + " | " + facility.getMaxSeverity().toString());
        }
    }
    
    private void cmdDeleteFacility(CommandSender sender, String[] args) {
        
        if(args.length < 2) {
            sender.sendMessage("Which facility should be deleted? No facility name found.");
            sender.sendMessage("Usage: /death deleteFacility [FacilityName]");
        }
        
       if(parent.getFacilityManager().deleteFacility(args[1])) {
           sender.sendMessage("Facility " + args[1] + " deleted successfully!");
       } else {
           sender.sendMessage("Facility " + args[1] + " could not be delted... Does it exist?");
       }
    }
}
