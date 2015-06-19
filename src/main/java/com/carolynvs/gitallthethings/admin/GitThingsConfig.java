package com.carolynvs.gitallthethings.admin;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Preload
@Table("GIT_THINGS_CONFIG")
public interface GitThingsConfig extends Entity
{
    public String getPlanKey();
    public void setPlanKey(String planKey);

    public String getToken();
    public void setToken(String token);
}
