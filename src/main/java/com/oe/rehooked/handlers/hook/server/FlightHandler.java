package com.oe.rehooked.handlers.hook.server;

import com.oe.rehooked.data.HookData;
import com.oe.rehooked.handlers.hook.def.IServerPlayerHookHandler;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlightHandler {
    private static final Logger log = LoggerFactory.getLogger(FlightHandler.class);
    private boolean prevMayFly;
    private boolean externalFlight;
    private boolean clientFlightEnabled;
    private boolean serverFlightEnabled;
    
    public FlightHandler() {
        prevMayFly = false;
        externalFlight = false;
        clientFlightEnabled = false;
        serverFlightEnabled = false;
    }
    
    public void afterDeath(ServerPlayer owner) {
        if (serverFlightEnabled && !clientFlightEnabled && !externalFlight) {
            serverFlightEnabled = owner.getAbilities().mayfly = owner.getAbilities().flying = false;
            owner.onUpdateAbilities();
        }
    }
    
    public void updateFlight(ServerPlayer owner, IServerPlayerHookHandler handler) {
        // check if flight was changed externally and we need to restore
        boolean externalFlightChanged = externalFlightChanged(owner);
        // check if flight should be enabled on the server
        boolean shouldEnableServerFlight = shouldEnableServerFlight(owner, handler);
        // check if flight should be enabled on the client
        boolean shouldEnableClientFlight = shouldEnableClientFlight(owner, handler);
        // set flight state correctly on server and client
        setFlightState(owner, shouldEnableServerFlight, shouldEnableClientFlight, externalFlightChanged);
        // last thing in update is to update previous variables for the next iteration
        prevMayFly = owner.getAbilities().mayfly;
    }

    private boolean shouldEnableClientFlight(ServerPlayer owner, IServerPlayerHookHandler handler) {
        return externalFlight || 
                (handler.getHookData().map(HookData::isCreative).orElse(false) && handler.countPulling() >= 2);
    }

    private boolean shouldEnableServerFlight(ServerPlayer owner, IServerPlayerHookHandler handler) {
        return externalFlight || handler.countPulling() > 0;
    }

    private void setFlightState(ServerPlayer owner, boolean shouldEnableServerFlight, boolean shouldEnableClientFlight, boolean externalFlightChanged) {
        // if client flight is in a bad state, update it
        // this needs to happen before the server updates his
        if (clientFlightEnabled != shouldEnableClientFlight || externalFlightChanged) {
            owner.getAbilities().mayfly = owner.getAbilities().flying = shouldEnableClientFlight;
            if (!(externalFlightChanged && externalFlight == shouldEnableClientFlight)) 
                owner.onUpdateAbilities();
            clientFlightEnabled = shouldEnableClientFlight;
        }
        // if the server flight is in a bad state, update it
        if (serverFlightEnabled != shouldEnableServerFlight || externalFlightChanged) {
            owner.getAbilities().mayfly = owner.getAbilities().flying = shouldEnableServerFlight;
            // server doesn't call update because the clause above already handled it
            serverFlightEnabled = shouldEnableServerFlight;
        }
    }

    private boolean externalFlightChanged(ServerPlayer owner) {
        boolean changed = serverFlightEnabled != owner.getAbilities().mayfly && prevMayFly != owner.getAbilities().mayfly;
        if (changed) externalFlight = owner.getAbilities().mayfly;
        return changed;
    }
}
