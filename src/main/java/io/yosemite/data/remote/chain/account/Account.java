package io.yosemite.data.remote.chain.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.yosemite.Consts;
import io.yosemite.data.types.TypeAsset;
import io.yosemite.util.Utils;

import java.util.List;
import java.util.Optional;

public class Account {

    @Expose
    @SerializedName("account_name")
    private String accountName;

    @Expose
    @SerializedName("head_block_num")
    private Integer headBlockNum;

    @Expose
    @SerializedName("head_block_time")
    private String headBlockTime;

    @Expose
    private boolean privileged;

    @Expose
    @SerializedName("last_code_update")
    private String lastCodeUpdate;

    @Expose
    private String created;

    @Expose
    @SerializedName("core_liquid_balance")
    private Optional<TypeAsset> coreLiquidBalance;

    @Expose
    @SerializedName("ram_quota")
    long ramQuota;

    @Expose
    @SerializedName("net_weight")
    long netWeight;

    @Expose
    @SerializedName("cpu_weight")
    long cpuWeight;

    @Expose
    @SerializedName("net_limit")
    AccountResourceLimit netLimit;

    @Expose
    @SerializedName("cpu_limit")
    AccountResourceLimit cpuLimit;

    @Expose
    @SerializedName("ram_usage")
    long ramUsage;

    @Expose
    private List<Permission> permissions;

    public Account() {
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Integer getHeadBlockNum() {
        return headBlockNum;
    }

    public void setHeadBlockNum(Integer headBlockNum) {
        this.headBlockNum = headBlockNum;
    }

    public String getHeadBlockTime() {
        return headBlockTime;
    }

    public void setHeadBlockTime(String headBlockTime) {
        this.headBlockTime = headBlockTime;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public String getLastCodeUpdate() {
        return lastCodeUpdate;
    }

    public void setLastCodeUpdate(String lastCodeUpdate) {
        this.lastCodeUpdate = lastCodeUpdate;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Optional<TypeAsset> getCoreLiquidBalance() {
        return coreLiquidBalance;
    }

    public void setCoreLiquidBalance(Optional<TypeAsset> coreLiquidBalance) {
        this.coreLiquidBalance = coreLiquidBalance;
    }

    public long getRamQuota() {
        return ramQuota;
    }

    public void setRamQuota(long ramQuota) {
        this.ramQuota = ramQuota;
    }

    public long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(long netWeight) {
        this.netWeight = netWeight;
    }

    public long getCpuWeight() {
        return cpuWeight;
    }

    public void setCpuWeight(long cpuWeight) {
        this.cpuWeight = cpuWeight;
    }

    public AccountResourceLimit getNetLimit() {
        return netLimit;
    }

    public void setNetLimit(AccountResourceLimit netLimit) {
        this.netLimit = netLimit;
    }

    public AccountResourceLimit getCpuLimit() {
        return cpuLimit;
    }

    public void setCpuLimit(AccountResourceLimit cpuLimit) {
        this.cpuLimit = cpuLimit;
    }

    public long getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(long ramUsage) {
        this.ramUsage = ramUsage;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public String toString() {
        return Utils.toJson(this, false);
    }

    /**
     * Get 'active' public key from account permission information.
     * @return public key string
     */
    public String getActivePublicKey() {
        if (permissions == null || permissions.isEmpty()) return null;
        for (Permission permission : permissions) {
            if (Consts.ACTIVE_PERMISSION_NAME.equals(permission.getPermName())) {
                List<KeyWeight> keys = permission.getRequiredAuth().getKeys();
                if (keys.isEmpty()) return null;
                return keys.get(0).getKey();
            }
        }
        return null;
    }
}
