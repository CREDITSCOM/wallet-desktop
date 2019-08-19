package com.credits.wallet.desktop.struct;

import com.credits.general.thrift.generated.Variant;

import java.io.Serializable;
import java.util.List;

public class UnapprovedTransactionData implements Serializable {

    private String id;
    private String source;
    private String target;
    private String amount ;
    private String currency;
    private String scMethod;
    private List<Variant> scParams;

    public UnapprovedTransactionData(String id, String source, String target, String amount, String currency, String scMethod, List<Variant> scParams) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.currency = currency;
        this.scMethod = scMethod;
        this.scParams = scParams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getScMethod() {
        return scMethod;
    }

    public void setScMethod(String scMethod) {
        this.scMethod = scMethod;
    }

    public List<Variant> getScParams() {
        return scParams;
    }

    public void setScParams(List<Variant> scParams) {
        this.scParams = scParams;
    }
}
