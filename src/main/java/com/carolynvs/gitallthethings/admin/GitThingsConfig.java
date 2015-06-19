package com.carolynvs.gitallthethings.admin;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface GitThingsConfig extends Entity
{
    public String getToken();
    public void setToken(String abc);
}
