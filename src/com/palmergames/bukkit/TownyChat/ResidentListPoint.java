package com.palmergames.bukkit.TownyChat;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.ensifera.animosity.craftirc.RelayedMessage;
import com.ensifera.animosity.craftirc.SecuredEndPoint;
import com.palmergames.bukkit.towny.object.ResidentList;

public class ResidentListPoint implements SecuredEndPoint {

    private final ResidentList residentList;
    private final Server server;

    public ResidentListPoint(ResidentList residentList, Server server) {
        this.residentList = residentList;
        this.server = server;
    }

    @Override
    public Type getType() {
        return Type.MINECRAFT;
    }

    @Override
    public void messageIn(RelayedMessage msg) {
        final String message = msg.getMessage(this);
        for (final Player p : this.server.getOnlinePlayers()) {
            if (residentList.hasResident(p.getName())) {
                p.sendMessage(message);
            }
        }
    }

    @Override
    public boolean userMessageIn(String username, RelayedMessage msg) {
        final Player p = this.server.getPlayer(username);
        if ((p == null) || !residentList.hasResident(p.getName())) {
            return false;
        }
        p.sendMessage(msg.getMessage(this));
        return true;
    }

    @Override
    public boolean adminMessageIn(RelayedMessage msg) {
        final String message = msg.getMessage(this);
        boolean success = false;
        for (final Player p : this.server.getOnlinePlayers()) {
            if (p.isOp() && residentList.hasResident(p.getName())) {
                p.sendMessage(message);
                success = true;
            }
        }
        return success;
    }

    @Override
    public List<String> listUsers() {
        final List<String> users = new LinkedList<String>();
        for (final Player player : this.server.getOnlinePlayers()) {
            if (residentList.hasResident(player.getName())) {
                users.add(player.getName());
            }
        }
        return users;
    }

    @Override
    public List<String> listDisplayUsers() {
        final LinkedList<String> users = new LinkedList<String>();
        for (final Player p : this.server.getOnlinePlayers()) {
            if (residentList.hasResident(p.getName())) {
                users.add(p.getDisplayName());
            }
        }
        Collections.sort(users);
        return users;
    }

    @Override
    public Security getSecurity() {
        return Security.UNSECURED;
    }
}
