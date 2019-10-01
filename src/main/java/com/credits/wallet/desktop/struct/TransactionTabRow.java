package com.credits.wallet.desktop.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionTabRow {
    private String id;
    private String time;
    private String sender;
    private String receiver;
    private String amount;
    private String type;
}
