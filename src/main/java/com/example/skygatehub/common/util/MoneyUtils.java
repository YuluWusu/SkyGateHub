package com.example.skygatehub.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Tiện ích định dạng và tính toán tiền tệ VNĐ.
 */
public final class MoneyUtils {

    private MoneyUtils() {}

    /**
     * Định dạng số tiền VNĐ: 1,500,000 đ
     */
    public static String formatVnd(BigDecimal amount) {
        if (amount == null) return "0 đ";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(amount) + " đ";
    }

    /**
     * Làm tròn tiền về số nguyên gần nhất theo chuẩn HALF_UP
     */
    public static BigDecimal roundMoney(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;
        return amount.setScale(0, RoundingMode.HALF_UP);
    }
}

