package me.colejedwards.glue.profile;

import lombok.Data;
import me.colejedwards.glue.Glue;

import java.util.UUID;

@Data
public class GlueProfile {

    public GlueProfile() {}

    private UUID uuid;

    private String server;

    private String proxy;

    private String username;

    public GlueProfile(UUID uuid, String server, String proxy) {
        this.uuid = uuid;
        this.server = server;
        this.proxy = proxy;

        this.username = Glue.getInstance().getProxy().getPlayer(this.uuid).getName();
    }

    public static GlueProfile getGlueProfileFromUUID(UUID uuid) {
        return Glue.getInstance().getProfiles().stream().filter(profile -> profile.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
