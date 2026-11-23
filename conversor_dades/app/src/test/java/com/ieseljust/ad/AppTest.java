package com.ieseljust.ad;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void elLlançadorPotMostrarElCataleg() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }

    @Test
    void unNomDesconegutNoProvocaUnaExcepcio() {
        assertDoesNotThrow(() -> App.main(new String[]{"no-existeix"}));
    }
}
