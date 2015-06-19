package com.carolynvs.gitallthethings.admin;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Preload
@Table("GIT_THINGS_CONFIG")
public interface GitThingsConfig extends Entity
{
    String getPlanKey();
    void setPlanKey(String planKey);

    String getToken();
    void setToken(String token);

    String getSecret();
    void setSecret(String secret);

    String getBotName();
    void setBotName(String botName);
}
