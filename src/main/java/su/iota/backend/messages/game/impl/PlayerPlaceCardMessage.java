package su.iota.backend.messages.game.impl;

import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;
import su.iota.backend.messages.game.AbstractPlayerActionMessage;
import su.iota.backend.models.game.Coordinate;

import java.util.UUID;

public class PlayerPlaceCardMessage extends AbstractPlayerActionMessage {

    @Expose
    private Coordinate coordinate;

    @Expose
    private UUID uuid;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @NotNull
    @Override
    public String toString() {
        return "PlayerPlaceCardMessage{" +
                "coordinate=" + coordinate +
                ", uuid=" + uuid +
                '}';
    }

    public static class ResultMessage extends AbstractPlayerActionMessage.AbstractResultMessage {

        public ResultMessage(boolean ok) {
            this.ok = ok;
            if (ok) {
                broadcast = true;
            }
        }

    }

}
