package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Wallet;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse implements Serializable{
    @JsonProperty("wallet")
    private Wallet wallet;
    @JsonProperty("expected_staking_bonus")
    private long expectedStakingBonus;
    @JsonProperty("expected_referral_bonus")
    private long expectedReferralBonus;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public long getExpectedStakingBonus() {
        return expectedStakingBonus;
    }

    public void setExpectedStakingBonus(long expectedStakingBonus) {
        this.expectedStakingBonus = expectedStakingBonus;
    }

    public long getExpectedReferralBonus() {
        return expectedReferralBonus;
    }

    public void setExpectedReferralBonus(long expectedReferralBonus) {
        this.expectedReferralBonus = expectedReferralBonus;
    }
}
