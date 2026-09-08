package Validation;

import java.math.BigDecimal;

public final class Validaciones {
    private Validaciones() {}

    public static <T> T requerido(T valor, String campo) {
        if (valor == null) throw new IllegalArgumentException("Debe seleccionar " + campo + ".");
        return valor;
    }

    public static String texto(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " es obligatorio.");
        String limpio = valor.strip();
        if (limpio.codePointCount(0, limpio.length()) > max)
            throw new IllegalArgumentException(campo + " admite como máximo " + max + " caracteres.");
        return limpio;
    }

    public static int entero(String valor, String campo, int min, int max) {
        try {
            return rango(Integer.parseInt(valor == null ? "" : valor.strip()), campo, min, max);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un número entero entre " + min + " y " + max + ".");
        }
    }

    public static int rango(int valor, String campo, int min, int max) {
        if (valor < min || valor > max)
            throw new IllegalArgumentException(campo + " debe estar entre " + min + " y " + max + ".");
        return valor;
    }

    public static int id(int valor, String campo) {
        return rango(valor, campo, 1, Integer.MAX_VALUE);
    }

    public static BigDecimal decimalExacto(String valor, String campo) {
        try {
            return superficie(new BigDecimal(valor == null ? "" : valor.strip().replace(',', '.')), campo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " debe ser un decimal positivo con hasta dos decimales (máximo 9999999999.99).");
        }
    }

    public static BigDecimal superficie(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0 || valor.compareTo(new BigDecimal("9999999999.99")) > 0
                || valor.stripTrailingZeros().scale() > 2)
            throw new IllegalArgumentException(campo + " debe ser positivo, tener hasta dos decimales y no exceder 9999999999.99.");
        return valor;
    }

    /** Compatibilidad para consumidores antiguos; nunca redondear una entrada silenciosamente. */
    public static float decimal(String valor, String campo) {
        BigDecimal exacto = decimalExacto(valor, campo);
        float numero = positivo(exacto.floatValue(), campo);
        if (new BigDecimal(Float.toString(numero)).compareTo(exacto) != 0)
            throw new IllegalArgumentException(campo + " requiere precisión decimal; use el valor decimal exacto.");
        return numero;
    }

    public static float positivo(float valor, String campo) {
        if (!Float.isFinite(valor)) throw new IllegalArgumentException(campo + " debe ser un número finito.");
        superficie(new BigDecimal(Float.toString(valor)), campo);
        return valor;
    }
}
