package com.lielamar.auth.bukkit.events;

import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStateChangeEvent extends Event implements Cancellable {

    /**
     * This class was originally written by Connor Linfoot (https://github.com/ConnorLinfoot/MC2FA).
     */

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private boolean cancelled = false;
    private AuthHandler.AuthState oldAuthState, newAuthState;

    @Deprecated
    public PlayerStateChangeEvent(Player player, AuthHandler.AuthState authState) {
        this.player = player;
        this.newAuthState = authState;
    }

    public PlayerStateChangeEvent(Player player, AuthHandler.AuthState oldAuthState, AuthHandler.AuthState newAuthState) {
        this.player = player;
        this.oldAuthState = oldAuthState;
        this.newAuthState = newAuthState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getPlayer() {
        return player;
    }

    public @Nullable AuthHandler.AuthState getOldAuthState() {
        return oldAuthState;
    }

    public void setOldAuthState(@NotNull AuthHandler.AuthState oldAuthState) {
        this.oldAuthState = oldAuthState;
    }

    public @NotNull AuthHandler.AuthState getNewAuthState() {
        return newAuthState;
    }

    public void setNewAuthState(@NotNull AuthHandler.AuthState newAuthState) {
        this.newAuthState = newAuthState;
    }


    @Deprecated
    public @NotNull AuthHandler.AuthState getAuthState() {
        return newAuthState;
    }

    @Deprecated
    public void setAuthState(@NotNull AuthHandler.AuthState authState) {
        this.newAuthState = newAuthState;
    }
}