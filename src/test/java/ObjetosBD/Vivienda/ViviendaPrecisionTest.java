package ObjetosBD.Vivienda;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ViviendaPrecisionTest {
    @Test void conservaCentavosDeTodaLaSuperficiePermitida() throws Exception {
        for (String valor : new String[]{"0.01", "131072.01", "262144.01", "9999999999.99"}) {
            var vivienda = new ViviendaBD(1, "Unifamiliar", 0, 1, 0, 1, new BigDecimal(valor));
            var exacto = vivienda.getMtsCuadradosExactos();
            assertEquals(new BigDecimal(valor), exacto);
        }
    }
    @Test void mantieneLaApiFloatCompatible() {
        var vivienda = new ViviendaBD(1, "Unifamiliar", 0, 1, 0, 1, 42.25f);
        assertEquals(42.25f, vivienda.getMts_cuadrados());
        vivienda.setMts_cuadrados(64.75f);
        assertEquals(64.75f, vivienda.getMts_cuadrados());
    }
}
