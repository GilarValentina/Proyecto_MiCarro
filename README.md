# 🚗 MiCarro — Sistema Móvil para el Control de Mantenimiento Vehicular

![Android](https://img.shields.io/badge/Android-Native-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2F%20Clean-blue?style=for-the-badge)
![Offline](https://img.shields.io/badge/Mode-100%25%20Offline%20First-green?style=for-the-badge)

**MiCarro** es una aplicación nativa para Android diseñada para ayudar a propietarios particulares a llevar un registro organizado, confiable y centralizado del mantenimiento preventivo y correctivo de sus vehículos, kilometraje, repuestos, costos y documentos legales.

---

## 📌 Problema que Resuelve

Muchos propietarios de vehículos guardan facturas impresas, notas dispersas o dependen únicamente de su memoria para recordar cambios de aceite, revisión de frenos o renovación de documentos como el SOAT y la revisión técnico-mecánica. **MiCarro** centraliza toda esta información y genera alertas oportunas por fecha y kilometraje reportado.

---

## ✨ Características Principales (MVP)

* 🚘 **Gestión de Vehículos:** Registro de automóviles, camionetas y motocicletas con datos básicos (placa, marca, línea, modelo, año, odómetro actual) y soporte para vehículo principal.
* 📈 **Control de Kilometraje:** Historial de lecturas con validaciones automáticas ante discrepancias o lecturas menores a la anterior.
* 📋 **Plan de Mantenimiento:** Creación y programación de actividades preventivas por fecha, kilometraje o ambos criterios, con categorías personalizables (aceite, frenos, llantas, batería, etc.).
* 🛠️ **Registro de Mantenimientos & Repuestos:** Control detallado de servicios realizados, costos de mano de obra, repuestos instalados con proveedor y garantía.
* 🔔 **Alertas Locales & Documentos:** Recordatorios offline vía `WorkManager` para próximos mantenimientos y vencimientos de SOAT o revisión técnico-mecánica.
* 📊 **Historial y Resumen de Gastos:** Filtros avanzados por fecha, categoría y taller, junto con resúmenes gráficos de gastos.
* 📂 **Exportación y Respaldo:** Capacidad de exportar historiales y realizar copias de seguridad locales.

---

## 🛠️ Stack Tecnológico & Arquitectura

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) / Material Design 3
* **Persistencia de Datos:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite)
* **Segundo Plano / Alertas:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
* **Arquitectura:** **MVVM (Model-View-ViewModel)** orientado a Clean Architecture
  $$\text{UI (Compose)} \longrightarrow \text{ViewModel} \longrightarrow \text{Repository} \longrightarrow \text{Room DAO}$$
* **Navegación:** Jetpack Navigation

---

## 👥 Equipo de Desarrollo

* **Gilar** — Módulo de Vehículos, Lecturas de Kilometraje, Historial Cronológico y Resumen de Gastos.
* **Tomas** — Módulo de Plan de Mantenimiento, Registro de Servicios, Repuestos y Alertas/WorkManager.

---

## 🚀 Requisitos de Instalación y Ejecución

### Prerrequisitos
* **Android Studio:** Ladybug (2024.2.1) o superior.
* **JDK:** Java 17 o superior.
* **Dispositivo / Emulador:** Android 8.0 (API level 26) o superior.

### Pasos para Ejecutar
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/GilarValentina/Proyecto_MiCarro.git
   cd Proyecto_MiCarro
   ```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar el proyecto con Gradle (**Sync Project with Gradle Files**).
4. Seleccionar un emulador o dispositivo físico con depuración USB activada.
5. Ejecutar la aplicación (`Shift + F10` o clic en **Run 'app'**).

---

## 🔒 Privacidad & Modo Offline (RNF-04 / RNF-08)

* **100% Offline First:** La aplicación funciona completamente sin necesidad de conexión a Internet. Todos los datos, historiales y alertas permanecen en el almacenamiento local del dispositivo del usuario.
* **Privacidad:** No se recopilan ni transmiten datos sensibles como placas, números VIN o información personal a servidores externos.

---

## 📝 Licencia & Curso

Proyecto Integrador desarrollado para el **Curso de Aplicaciones Móviles**.
