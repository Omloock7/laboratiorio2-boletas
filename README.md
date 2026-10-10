# Sistema de boletería — Laboratorio 2, Unidad 4

Aplicación Java de consola para consultar eventos, ver disponibilidad, vender asientos y generar tickets PDF con QR y reportes de ingresos. No utiliza base de datos: los datos se leen y guardan en archivos.

## Requisitos
- JDK 17 o superior
- Maven 3.8 o superior
- Conexión a internet la primera vez para descargar dependencias Maven

## Ejecución
Desde la carpeta raíz del proyecto:

```bash
mvn clean test
mvn exec:java
```

El menú permite listar eventos, consultar disponibilidad, realizar compras y crear el reporte PDF. La compra queda en `data/compras.json`, la auditoría en `logs/auditoria.txt`, el ticket en `tickets/` y la gráfica en `reportes/ventas_por_evento.pdf`.

## Estructura
- `src/main/java/com/boleteria/Main.java`: menú de consola.
- `model/`: modelos `Asiento`, `Evento`, `Compra`, `ReporteEvento`.
- `service/`: carga y persistencia, lógica de boletería, PDF/QR y reportes.
- `exception/`: excepciones personalizadas de datos y reglas de negocio.
- `src/test/`: pruebas unitarias JUnit 5.
- `data/`: archivos CSV de eventos/asientos y JSON de compras.
- `logs/`: auditoría TXT.
- `tickets/`, `reportes/`: archivos generados.

## Librerías
- Gson: serialización/deserialización JSON.
- Apache PDFBox: generación de tickets PDF.
- ZXing: creación de códigos QR.
- XChart: gráfica de barras PDF.
- JUnit 5: pruebas unitarias.

## Reglas importantes
- Un asiento se identifica junto con su evento; no se permite vender dos veces el mismo código para el mismo evento.
- El servicio corresponde al 10 % del subtotal.
- Las compras se validan antes de persistirse.
- Si se desea iniciar una demostración desde cero, respalde primero `data/compras.json` y luego restaure su contenido a `[]`.

