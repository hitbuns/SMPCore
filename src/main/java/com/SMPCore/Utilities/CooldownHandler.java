package com.SMPCore.Utilities;

import com.MenuAPI.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CooldownHandler<V> {

    private final V executorUUID;
    private final Map<String,Long> map = new HashMap<>();


    public CooldownHandler(V executorUUID) {
        this.executorUUID = executorUUID;
    }

    public V getExecutorUUID() {
        return executorUUID;
    }

    public void setOnCoolDown(String key) {
        if (key == null) return;

        map.put(key, System.currentTimeMillis());

    }

    public void reduceCoolDown(String executorUUID, TimeUnit timeUnit, long timeSpan) {
        if (executorUUID == null) return;

        long l = Math.max(0,timeSpan);

        if (l > 0)
            map.put(executorUUID,map.getOrDefault(executorUUID,-1L)-
                    (timeUnit != null ? timeUnit.toMillis(l) : l));

    }

    public String cooldownLeftDHMS(String executorUUID,TimeUnit timeUnit,
                                   long delay) {
        return Utils.convertLongToDate(getTimeLeftOnCooldown(executorUUID,
                timeUnit,delay,TimeUnit.MILLISECONDS));
    }

    public long getTimeLeftOnCooldown(String executorUUID,TimeUnit timeUnit,long delay,TimeUnit returningTimeUnitAmount) {
        long diff = timeUnit.toMillis(Math.max(delay,0)) -
                (System.currentTimeMillis()-map.getOrDefault(executorUUID,-1L));
        return returningTimeUnitAmount.convert(Math.max(diff,0),TimeUnit.MILLISECONDS);
    }

    public boolean isOnCoolDown(String executorUUID,TimeUnit timeUnit,long timeSpan) {
        return executorUUID != null && timeUnit != null &&
                System.currentTimeMillis() - map.getOrDefault(executorUUID, -1L)
                        < timeUnit.toMillis(timeSpan);
    }


}
