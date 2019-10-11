package com.credits.wallet.desktop.struct;

import javafx.scene.control.ToggleButton;
import lombok.Value;

@Value
public class SmartContractTabRow {
    private final String contractAddress;
    private final ToggleButton button;
}
