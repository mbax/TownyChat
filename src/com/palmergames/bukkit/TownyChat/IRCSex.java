package com.palmergames.bukkit.TownyChat;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.ensifera.animosity.craftirc.CraftIRC;
import com.ensifera.animosity.craftirc.EndPoint;
import com.ensifera.animosity.craftirc.RelayedMessage;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;

public class IRCSex {

    private static CraftIRC irc;
    private static Chat chat;
    private static Server server;

    public static void message(String message, String channel) {
        if(IRCSex.irc==null){
            return;
        }
        final EndPoint townyPoint = IRCSex.getTownyPoint(channel);
        final EndPoint ircPoint = IRCSex.getIRCPoint(channel);
        final RelayedMessage msg = IRCSex.irc.newMsg(townyPoint, ircPoint, "chat");
        msg.setField("message", message);
        msg.post();
    }
    
    public static void neverStop() {
        IRCSex.chat = null;
        IRCSex.irc = null;
        IRCSex.server = null;
    }
    
    public static void playerJoin(Player player){
        if(IRCSex.irc==null){
            return;
        }
        Town town = null;
        Nation nation = null;
        try {
            final Resident resident = TownyUniverse.getDataSource().getResident(player.getName());
            town = resident.getTown();
            nation = resident.getTown().getNation();
        } catch (final NotRegisteredException e1) {
        }
        if(town!=null){
            IRCSex.poke(town.getName());
        }
        if(nation!=null){
            IRCSex.poke("nation_"+nation.getName());
        }
    }

    public static void startMeUp(Chat chat, CraftIRC irc) {
        IRCSex.chat = chat;
        IRCSex.irc = irc;
        IRCSex.server = chat.getServer();
    }
    
    private static EndPoint getIRCPoint(String channel) {
        final String tag = "irc_" + channel;
        EndPoint point = IRCSex.irc.getEndPoint(tag);
        if (point == null) {
            IRCSex.irc.getBot(0).addChannel(channel);
            point = IRCSex.irc.getEndPoint(tag);
        }
        return point;
    }

    private static EndPoint getTownyPoint(String name) {
        final String tag = "towny_" + name;
        EndPoint point = IRCSex.irc.getEndPoint(tag);
        if (point == null) {
            boolean town = true;
            if (name.startsWith("nation_")) {
                town = false;
            }
            ResidentList target;
            try {
                if (town) {
                    IRCSex.chat.getTowny().getTownyUniverse();
                    target = TownyUniverse.getDataSource().getTown(name);
                } else {
                    target = TownyUniverse.getDataSource().getNation(name.substring(7));
                }
            } catch (final NotRegisteredException e) {
                //How the hell did this happen
                return IRCSex.irc.getEndPoint("moosevalley");
            }
            point = new ResidentListPoint(target, IRCSex.server);
            IRCSex.irc.registerEndPoint(tag, point);
        }
        return point;
    }

    private static void poke(String name){
        IRCSex.getTownyPoint(name);
        IRCSex.getIRCPoint(name);
    }

}
