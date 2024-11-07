package Model;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeRates {
    private int id;
    private transient int baseCurrencyId;
    private transient int targetCurrencyId;
    private BigDecimal rate;
    private Currencies baseCurrency;
    private Currencies targetCurrency;

    public ExchangeRates(int id, int baseCurrencyId, int targetCurrencyId, BigDecimal rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
        this.rate = this.rate.setScale(6, RoundingMode.HALF_UP);
    }
    public ExchangeRates(int id, Currencies baseCurrency, Currencies targetCurrency, BigDecimal rate){
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.rate = rate.setScale(6, RoundingMode.HALF_UP);
    }
    public ExchangeRates(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal bigDecimal) {
        this.rate = rate;
    }

    public Currencies getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currencies baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currencies getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currencies targetCurrency) {
        this.targetCurrency = targetCurrency;
    }
}
