package com.undef.superahorroCalvoAlasino.model

object MockData {
    val compras = listOf(
        Compra(
            id = 1,
            supermercado = "Carrefour",
            fecha = "20/04/2026",
            hora = "10:30",
            total = 5420.50,
            productos = listOf(
                Producto(1, "7790123", "Leche La Serenísima", "1 litro entera", 850.0),
                Producto(2, "7791234", "Pan Lactal Bimbo", "400g", 650.0),
                Producto(3, "7792345", "Manteca La Paulina", "200g", 920.0)
            )
        ),
        Compra(
            id = 2,
            supermercado = "Día",
            fecha = "18/04/2026",
            hora = "17:00",
            total = 3200.00,
            productos = listOf(
                Producto(4, "7793456", "Arroz Gallo", "1kg", 900.0),
                Producto(5, "7794567", "Fideos Matarazzo", "500g", 450.0)
            )
        ),
        Compra(
            id = 3,
            supermercado = "Jumbo",
            fecha = "15/04/2026",
            hora = "11:15",
            total = 8750.75,
            productos = listOf(
                Producto(6, "7795678", "Aceite Natura", "1.5 litros", 1800.0),
                Producto(7, "7796789", "Yogur Danone", "pack x4", 1200.0)
            )
        )
    )

    val usuario = Usuario("Juan García", "juan@gmail.com")

    val supermercados = listOf(
        "Carrefour", "Día", "Jumbo", "Coto", "La Anónima", "Vea", "Disco"
    )
}
