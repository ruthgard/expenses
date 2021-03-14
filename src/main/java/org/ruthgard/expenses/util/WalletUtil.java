package org.ruthgard.expenses.util;

import org.ruthgard.expenses.model.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletUtil {

    private List<Wallet> currentWallets;

    public WalletUtil(List<Wallet> currentWallets) {
        if (currentWallets == null)
            currentWallets = new ArrayList<>();
        this.currentWallets = currentWallets;
    }

    public boolean getEnabled(Long position) {
        for (Wallet currentWallet : currentWallets) {
            if (currentWallet.getId() == position)
                return true;
        }
        return false;
    }

}

