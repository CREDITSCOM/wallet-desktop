package com.credits.wallet.desktop.pojo;


import lombok.Value;

@Value
public class CompiledSmartContract {
    private final String address;
    private final String sourcecode;
    private final Class<?> contractClass;
}
