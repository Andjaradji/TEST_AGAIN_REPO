package com.vexanium.vexgift.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vexanium.vexgift.bean.model.Wallet;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse implements Serializable {
    @JsonProperty("wallet")
    private Wallet wallet;
    @JsonProperty("expected_staking_bonus")
    private float expectedStakingBonus;
    @JsonProperty("expected_referral_bonus")
    private float expectedReferralBonus;
    @JsonProperty("next_bonus_payout_amount")
    private float nextBonusPayoutAmount;
    @JsonProperty("time_until_next_bonus")
    private float timeUntilNextBonus;

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public float getExpectedStakingBonus() {
        return expectedStakingBonus;
    }

    public void setExpectedStakingBonus(float expectedStakingBonus) {
        this.expectedStakingBonus = expectedStakingBonus;
    }

    public float getExpectedReferralBonus() {
        return expectedReferralBonus;
    }

    public void setExpectedReferralBonus(float expectedReferralBonus) {
        this.expectedReferralBonus = expectedReferralBonus;
    }

    public float getNextBonusPayoutAmount() {
        return nextBonusPayoutAmount;
    }

    public void setNextBonusPayoutAmount(float nextBonusPayoutAmount) {
        this.nextBonusPayoutAmount = nextBonusPayoutAmount;
    }

    public float getTimeUntilNextBonus() {
        return timeUntilNextBonus;
    }

    public void setTimeUntilNextBonus(float timeUntilNextBonus) {
        this.timeUntilNextBonus = timeUntilNextBonus;
    }
}
