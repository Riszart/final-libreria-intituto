package com.app_frontend.data.local

import com.app_frontend.data.model.Address
import com.app_frontend.data.model.AppNotification
import com.app_frontend.data.model.CartItem
import com.app_frontend.data.model.DeliveryMethod
import com.app_frontend.data.model.NotificationType
import com.app_frontend.data.model.Order
import com.app_frontend.data.model.OrderItem
import com.app_frontend.data.model.OrderStatus
import com.app_frontend.data.model.PaperWeight
import com.app_frontend.data.model.PaymentMethod
import com.app_frontend.data.model.Product
import com.app_frontend.data.model.ProductColor
import com.app_frontend.data.model.SpecItem
import com.app_frontend.data.model.StockStatus
import com.app_frontend.data.model.UserProfile

/**
 * Datos de demostración.
 *
 * Mientras el backend Java (microservicios) no esté desplegado, estos datos
 * alimentan la UI. Cuando la API responda, los repositorios priorizarán la
 * respuesta remota y este archivo dejará de usarse.
 *
 * `imageUrl` es `null` a propósito: la UI muestra una portada con el estilo de
 * la marca. Cuando el backend envíe la URL real de la portada, Coil la cargará.
 */
object MockData {

    // -----------------------------------------------------------------------
    // Usuario
    // -----------------------------------------------------------------------

    val user = UserProfile(
        id = "usr-001",
        fullName = "Sofía Valentina Valdés",
        email = "sofia.valdes@papelytinta.es",
        phone = "+34 612 345 789",
        memberSince = "2021",
        readingLevel = "Lector Voraz",
        levelNumber = 2,
        points = 450,
        address = Address(
            receiver = "Sofía Valentina Valdés",
            street = "Calle de los Libreros 42, 3.ºB",
            city = "Madrid",
            state = "Madrid",
            zip = "28013",
            country = "España",
            phone = "+34 612 345 789"
        ),
        newsletter = true,
        offers = true,
        notificationsEnabled = true
    )

    // -----------------------------------------------------------------------
    // Productos
    // -----------------------------------------------------------------------

    private val notebookColors = listOf(
        ProductColor("verde", "Verde Bosque", "#1F5C4A"),
        ProductColor("navy", "Azul Noche", "#16263F"),
        ProductColor("terracota", "Terracota", "#B4551F")
    )

    private val notebookWeights = listOf(
        PaperWeight("90", "90g (Estándar)"),
        PaperWeight("120", "120g (+ S/ 50.00)", extraPrice = 50.0)
    )

    val products: List<Product> = listOf(
        Product(
            id = "prd-001",
            name = "Cuaderno Artesanal de Tapa Dura - Edición Especial",
            category = "Papelería Fina",
            author = "Taller Papel & Tinta",
            price = 450.00,
            rating = 4.8,
            reviews = 124,
            stock = StockStatus.AVAILABLE,
            badge = "Edición limitada",
            colors = notebookColors,
            paperWeights = notebookWeights,
            description = "Cada cuaderno es encuadernado a mano utilizando técnicas tradicionales de costura copta, permitiendo una apertura total de 180 grados sin comprometer la estructura del lomo. Ideal para caligrafía, bocetos a lápiz o tinta y journaling diario.",
            specs = listOf(
                SpecItem("Materiales", "Lino holandés & Papel reciclado"),
                SpecItem("Dimensiones", "14.8 x 21 cm (A5)"),
                SpecItem("Páginas", "240 páginas numeradas"),
                SpecItem("Origen", "Hecho en Taller Local")
            )
        ),
        Product(
            id = "prd-002",
            name = "Libreta de Tapa Dura",
            category = "Cuadernos",
            author = "Papel & Tinta Studio",
            price = 14.90,
            rating = 4.6,
            reviews = 58,
            stock = StockStatus.AVAILABLE,
            description = "Libreta de tapa dura con papel marfil de 90g. Ideal para uso diario en la oficina o en casa."
        ),
        Product(
            id = "prd-003",
            name = "Pluma Estilográfica Clásica Latón",
            category = "Plumas",
            author = "Papel & Tinta Studio",
            price = 32.00,
            rating = 4.7,
            reviews = 91,
            stock = StockStatus.AVAILABLE,
            description = "Estilográfica de latón con punta fina F y sistema de carga por converter. Un clásico atemporal para escribir con carácter."
        ),
        Product(
            id = "prd-004",
            name = "El Susurro de las Hojas",
            category = "Ficción Histórica",
            author = "Elena M. Alarcón",
            price = 64.90,
            rating = 4.9,
            reviews = 212,
            stock = StockStatus.AVAILABLE,
            tag = "Novedad",
            description = "Una novela envolvente sobre bibliotecas secretas y cartas que cruzan décadas. La novedad editorial del otoño."
        ),
        Product(
            id = "prd-005",
            name = "Set de Pinceles Artesanos",
            category = "Arte",
            author = "Taller Renacir",
            price = 24.00,
            rating = 4.5,
            reviews = 37,
            stock = StockStatus.SOLD_OUT,
            description = "Set de seis pinceles de pelo natural con mangos de madera de haya. Para acuarela y óleo."
        ),
        Product(
            id = "prd-006",
            name = "Marcador Metálico de Bolsillo",
            category = "Oficina",
            author = "Papel & Tinta Studio",
            price = 8.50,
            rating = 4.4,
            reviews = 64,
            stock = StockStatus.AVAILABLE,
            description = "Marcador metálico compacto con tinta indeleble. Caben en cualquier bolsillo de cuaderno."
        ),
        Product(
            id = "prd-007",
            name = "Tinta Caligráfica de Nogal",
            category = "Plumas",
            author = "Tinta & Lacre",
            price = 12.00,
            rating = 4.8,
            reviews = 45,
            stock = StockStatus.LOW,
            stockCount = 4,
            description = "Tinta pigmentada de extracto de nogal en frasco de 30 ml. Tono cálido y estable con el tiempo."
        ),
        Product(
            id = "prd-008",
            name = "Cien Años de Soledad",
            category = "Literatura Clásica",
            author = "Gabriel García Márquez",
            price = 79.90,
            rating = 5.0,
            reviews = 340,
            stock = StockStatus.AVAILABLE,
            description = "Edición anotada con prólogo inédito. La obra cumbre del realismo mágico en una edición de coleccionista."
        ),
        Product(
            id = "prd-009",
            name = "Cuaderno de Tapa Dura Linen",
            category = "Papelería Fina",
            author = "Papel & Tinta Studio",
            price = 45.00,
            rating = 4.7,
            reviews = 88,
            stock = StockStatus.LOW,
            stockCount = 2,
            description = "Tapa de lino natural con acabado mate, costura vista y marcador de cinta integrado."
        ),
        Product(
            id = "prd-010",
            name = "Veinte Poemas de Amor",
            category = "Poesía",
            author = "Pablo Neruda",
            price = 38.50,
            rating = 4.9,
            reviews = 156,
            stock = StockStatus.SOLD_OUT,
            description = "Edición de bolsillo con sobrecubierta estampada en dorado. La colección imprescindible de Neruda."
        ),
        Product(
            id = "prd-011",
            name = "El Arte de Vivir",
            category = "Filosofía",
            author = "Epicteto",
            price = 52.00,
            rating = 4.6,
            reviews = 73,
            stock = StockStatus.AVAILABLE,
            description = "La filosofía estoica traducida y anotada para el lector contemporáneo, con introducción y glosario."
        ),
        Product(
            id = "prd-012",
            name = "Cuaderno Artesanal de Lino",
            category = "Cuadernos",
            author = "Papel & Tinta Studio",
            price = 45.00,
            originalPrice = 45.00,
            rating = 4.8,
            reviews = 119,
            stock = StockStatus.AVAILABLE,
            tag = "Más Vendido",
            description = "El cuaderno más querido de la casa: tapa de lino, papel crema de 100g y encuadernación artesanal."
        ),
        Product(
            id = "prd-013",
            name = "Set Caligrafía 'Renacir'",
            category = "Papelería Fina",
            author = "Taller Renacir",
            price = 119.00,
            originalPrice = 149.00,
            rating = 4.9,
            reviews = 52,
            stock = StockStatus.LOW,
            stockCount = 6,
            tag = "-20% PREVENTA",
            description = "Set completo de caligrafía con plumín, portaplumas de madera, tinta de nogal y manual de práctica."
        ),
        Product(
            id = "prd-014",
            name = "Trilogía Poesía Haiku",
            category = "Poesía",
            author = "Varios autores",
            price = 84.00,
            originalPrice = 99.00,
            rating = 4.7,
            reviews = 41,
            stock = StockStatus.AVAILABLE,
            tag = "-15% EXCLUSIVO",
            description = "Tres volúmenes de haiku japonés traducidos, con ilustraciones a tinta y sobrecubierta texturizada."
        ),
        Product(
            id = "prd-015",
            name = "Lapicero de Latón Recargable",
            category = "Escritura",
            author = "Papel & Tinta Studio",
            price = 68.00,
            rating = 4.6,
            reviews = 77,
            stock = StockStatus.LOW,
            stockCount = 2,
            description = "Lapicero de latón macizo con punta fina y minas recargables. Tinta negra de secado rápido."
        ),
        Product(
            id = "prd-016",
            name = "Set de Washi Tapes Pastel (5 pz)",
            category = "Papelería Fina",
            author = "Papel & Tinta Studio",
            price = 35.00,
            rating = 4.8,
            reviews = 96,
            stock = StockStatus.AVAILABLE,
            description = "Cinco rollos de washi tape en tonos pastel para decorar agendas, regalos y scrapbooking."
        ),
        Product(
            id = "prd-017",
            name = "El Aleph (Edición Coleccionista)",
            category = "Ficción / Literatura",
            author = "Jorge Luis Borges",
            price = 65.00,
            rating = 5.0,
            reviews = 188,
            stock = StockStatus.AVAILABLE,
            description = "Tapa dura con guardas ilustradas y tipografía original de 1949. Edición numerada."
        ),
        Product(
            id = "prd-018",
            name = "Cuaderno de Notas Moleskine Classic",
            category = "Papelería Fina",
            author = "Moleskine",
            price = 89.90,
            rating = 4.8,
            reviews = 264,
            stock = StockStatus.AVAILABLE,
            description = "Rayas, 240 páginas de papel de calidad archivístico, con cierre elástico y funda interior."
        ),
        Product(
            id = "prd-019",
            name = "Marcapáginas de Cuero Artesanal",
            category = "Accesorios",
            author = "Taller Renacir",
            price = 35.00,
            rating = 4.7,
            reviews = 33,
            stock = StockStatus.AVAILABLE,
            description = "Marcapáginas de cuero curtido al vegetal en color ámbar, con relieve del logo de la casa."
        ),
        Product(
            id = "prd-020",
            name = "Set de 6 Marcadores Pastel",
            category = "Oficina",
            author = "Papel & Tinta Studio",
            price = 89.00,
            rating = 4.5,
            reviews = 120,
            stock = StockStatus.AVAILABLE,
            description = "Seis marcadores de doble punta en tonos pastel, con tinta resistente al subrayado."
        )
    )

    fun product(id: String): Product? = products.firstOrNull { it.id == id }

    // -----------------------------------------------------------------------
    // Carrito
    // -----------------------------------------------------------------------

    val cart: List<CartItem> = listOf(
        CartItem(product = products.first { it.id == "prd-012" }.copy(name = "Cuaderno de Tapa Dura Vintage", price = 45.00), quantity = 1),
        CartItem(product = products.first { it.id == "prd-015" }, quantity = 3),
        CartItem(product = products.first { it.id == "prd-008" }.copy(name = "Cien Años de Soledad (Edición Aniversario)", price = 59.00), quantity = 1)
    )

    /** Artículos del mockup "Confirmación de Pedido". */
    val checkoutItems: List<OrderItem> = listOf(
        OrderItem(products.first { it.id == "prd-012" }.copy(name = "Cuaderno Artesanal de Tapa Dura"), quantity = 1, unitPrice = 85.00),
        OrderItem(products.first { it.id == "prd-003" }, quantity = 1, unitPrice = 120.00),
        OrderItem(products.first { it.id == "prd-016" }, quantity = 1, unitPrice = 35.00)
    )

    val checkoutSubtotal: Double = checkoutItems.sumOf { it.lineTotal }

    // -----------------------------------------------------------------------
    // Pedidos
    // -----------------------------------------------------------------------

    val orders: List<Order> = listOf(
        Order(
            id = "#PT-84920",
            date = "18 Oct 2023",
            status = OrderStatus.IN_TRANSIT,
            items = listOf(
                OrderItem(products.first { it.id == "prd-017" }, quantity = 1, unitPrice = 65.00),
                OrderItem(products.first { it.id == "prd-018" }, quantity = 1, unitPrice = 89.90),
                OrderItem(products.first { it.id == "prd-019" }, quantity = 2, unitPrice = 35.00)
            ),
            subtotal = 189.90,
            tax = 34.18,
            shipping = 15.00,
            shippingLabel = "Envío Express",
            shippingAddress = Address(
                receiver = "María Fernanda Cáceres",
                street = "Av. Larco 456, Dpto. 402",
                city = "Miraflores",
                state = "Lima",
                country = "Perú"
            ),
            paymentMethod = PaymentMethod.CARD,
            paymentLastDigits = "4892",
            paymentBrand = "Visa",
            deliveryEstimate = "Mañana, 2:00 PM - 5:00 PM",
            pointsEarned = 140
        ),
        Order(
            id = "#PT-83110",
            date = "02 Oct 2023",
            status = OrderStatus.DELIVERED,
            items = listOf(
                OrderItem(products.first { it.id == "prd-020" }, quantity = 1, unitPrice = 89.00)
            ),
            subtotal = 89.00,
            tax = 16.02,
            shipping = 0.0,
            shippingLabel = "Envío estándar a domicilio",
            shippingAddress = MockData.user.address.copy(receiver = user.fullName),
            paymentMethod = PaymentMethod.CARD,
            paymentLastDigits = "4892",
            paymentBrand = "Visa",
            deliveryEstimate = "Entregado el 05 Oct 2023",
            pointsEarned = 105
        ),
        Order(
            id = "#PT-81005",
            date = "15 Sep 2023",
            status = OrderStatus.DELIVERED,
            items = listOf(
                OrderItem(products.first { it.id == "prd-011" }, quantity = 1, unitPrice = 65.00)
            ),
            subtotal = 65.00,
            tax = 11.70,
            shipping = 0.0,
            shippingLabel = "Recolección en tienda",
            shippingMethod = DeliveryMethod.STORE_PICKUP,
            shippingAddress = user.address,
            paymentMethod = PaymentMethod.QR,
            paymentLastDigits = "0000",
            paymentBrand = "Yape",
            deliveryEstimate = "Entregado el 18 Sep 2023",
            pointsEarned = 78
        )
    )

    fun order(id: String): Order? = orders.firstOrNull { it.id == id || it.shortId == id.removePrefix("#") }

    /** Resumen del mockup "Carrito de Compras". */
    val cartSummarySubtotal = 240.00
    val cartSummaryTax = 43.20
    val cartSummaryShipping = 0.0

    /** Resumen del mockup "Método de Pago y Entrega". */
    val checkoutSummarySubtotal = 271.20
    val checkoutShipping = 12.00

    // -----------------------------------------------------------------------
    // Notificaciones
    // -----------------------------------------------------------------------

    val notifications: List<AppNotification> = listOf(
        AppNotification(
            id = "ntf-001",
            type = NotificationType.ORDER,
            title = "Pedido en camino",
            message = "Tu pedido #PT-84920 está en ruta con delivery express hacia Miraflores.",
            timeLabel = "Hace 2 h",
            isRead = false,
            group = "HOY",
            highlight = "Repartidor asignado: Marco V.",
            actionLabel = "Rastrear"
        ),
        AppNotification(
            id = "ntf-002",
            type = NotificationType.REWARD,
            title = "¡Sumaste 150 Puntos Tinta!",
            message = "Tu compra reciente te ha acercado a tu próximo libro de regalo en la sección de favoritos.",
            timeLabel = "Hace 4 h",
            isRead = false,
            group = "HOY",
            progressCurrent = 450,
            progressTotal = 600
        ),
        AppNotification(
            id = "ntf-003",
            type = NotificationType.ORDER,
            title = "Pedido confirmado",
            message = "El pedido #PT-84920 fue recibido con éxito y está siendo cuidadosamente empaquetado en nuestra sede central.",
            timeLabel = "Ayer, 18:30",
            isRead = true,
            group = "AYER"
        ),
        AppNotification(
            id = "ntf-004",
            type = NotificationType.PREORDER,
            title = "Preventa exclusiva de autores",
            message = "Ya puedes reservar la nueva edición conmemorativa anotada de Cien Años de Soledad con un 20% de descuento de lanzamiento.",
            timeLabel = "Ayer, 11:15",
            isRead = true,
            group = "AYER",
            highlight = "Edición Anotada Gabriel García"
        ),
        AppNotification(
            id = "ntf-005",
            type = NotificationType.SYSTEM,
            title = "Cupones de temporada activos",
            message = "Tienes 2 cupones disponibles: 15% en editoriales independientes y 20% en papelería.",
            timeLabel = "2 días",
            isRead = true,
            group = "AYER"
        )
    )

    // -----------------------------------------------------------------------
    // Catálogo / filtros
    // -----------------------------------------------------------------------

    val categories = listOf("Todos", "Libros", "Cuadernos", "Plumas", "Papelería", "Oficina")

    /** Rincones literarios del home. */
    val readingCorners = listOf("Ficción", "Papelería Fina", "Poesía")
}
