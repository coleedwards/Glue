package me.colejedwards.glue.server;

import lombok.Data;
import me.colejedwards.glue.Glue;
import me.colejedwards.glue.profile.GlueProfile;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;

@Data
public class GlueServer {

    private String name;
    private ServerInfo server;

    public GlueServer(String name) {
        this.name = name;

        this.server = Glue.getInstance().getProxy().getServerInfo(this.name);
    }

    public List<GlueProfile> getOnline() {
        List<GlueProfile> glueProfiles = new ArrayList<>();
        Glue.getInstance().getProfiles().stream().filter(profile -> profile.getName().equalsIgnoreCase(name)).forEach(glueProfiles::add);
        return glueProfiles;
    }
}
