# 🛒 Self-Service Shopping Cart App

## 🇬🇧 English

### Overview

This is a simple **self-service shopping cart system** developed in **Java** using **JavaFX** and **JDBC** with an **Apache Derby** local server. It is designed for use in supermarkets, retail stores, or companies for digital self-checkout or internal stock management.

---

### Application Structure

The app is divided into two main sections:

#### 1. Home Section

* On the **left side**:

  * A list of products you can click to add to the shopping list.
  * A **search bar** to find products by name or ID.
  * The search bar supports **barcode simulation**: type a product code and press **Enter** to instantly add the product without a quantity prompt.
  * If you add products using the buttons, a **quantity input** will be required.

* On the **right side**:

  * The **shopping list** with all selected products.
  * Displays the **total price**.
  * Allows increasing or decreasing the quantity per product.
  * Option to **delete all products** from the list.

#### 2. Admin Section

* Click the **admin icon** and log in with:

  * Username: `admin`
  * Password: `admin`

* After login:

  * A **logout button** and a **session timer** are shown.
  * A **cogwheel button** appears, unlocking admin features:

    * **Add New Product**
      Requires name, unit price, and image.

    * **Edit Product**
      Enter product name or ID to edit name, price, or image.
      You can view the **full product list**, useful for editing or barcode testing. Use `Ctrl + F` to search the list.

    * **Remove Product**
      Delete products by name or ID.

---

### Database

* `Products` table
* `Users` table (contains only the `admin` user)

---

### How to Start the Application

1. Make sure **Java 14 or higher** is installed
2. Use **Windows OS**
3. Download and extract the project
4. Run the `.bat` file

---

### Developer Info

* **Viewing the Code**
  Open the files in any code editor (VSCode, IntelliJ, etc.)

* **Viewing or Editing the Database**
  Use a Derby client (e.g. SQuirreL SQL) and connect with:

  ```
  URL: jdbc:derby://localhost:1527/Database;create=true;
  User: SHOPPING_CART
  Password: [single space character]
  ```

---

## 🇩🇪 Deutsch

### Übersicht

Dies ist ein einfaches **Self-Service Einkaufswagen-System**, entwickelt in **Java** mit **JavaFX** und **JDBC** über einen lokalen **Apache Derby** Server. Es eignet sich für Supermärkte, Einzelhandel oder Unternehmen zur Selbstbedienung oder internen Lagerverwaltung.

---

### Aufbau der Anwendung

Die App besteht aus zwei Hauptbereichen:

#### 1. Benutzerbereich (Home)

* **Linke Seite**:

  * Liste von Produkten zum Anklicken und Hinzufügen zur Einkaufsliste.
  * **Suchleiste** zum Suchen nach Name oder ID.
  * Unterstützt **Barcodesimulation**: Produktcode eingeben und **Enter** drücken – Produkt wird sofort hinzugefügt.
  * Beim Hinzufügen über Buttons erscheint eine **Mengenabfrage**.

* **Rechte Seite**:

  * Zeigt die **Einkaufsliste** mit allen gewählten Produkten.
  * **Gesamtpreis** wird angezeigt.
  * Mengen lassen sich erhöhen oder verringern.
  * Möglichkeit, **alle Produkte auf einmal zu löschen**.

#### 2. Administratorbereich

* Zugriff über das **Admin-Symbol**, Login mit:

  * Benutzername: `admin`
  * Passwort: `admin`

* Nach dem Login:

  * **Logout-Button** und **Session-Timer**
  * Ein **Zahnrad-Symbol** erscheint, über das folgende Funktionen verfügbar sind:

    * **Produkt hinzufügen**
      Name, Preis pro Einheit und Bild erforderlich.

    * **Produkt bearbeiten**
      Suche über Name oder ID, dann Bearbeitung von Name, Preis oder Bild.
      Die **komplette Produktliste** kann angezeigt werden (nützlich zum Bearbeiten oder Barcodesimulation).
      Suche mit `Strg + F`.

    * **Produkt entfernen**
      Löschung über Name oder ID.

---

### Datenbank

* `Products`-Tabelle
* `Users`-Tabelle (enthält nur den Benutzer `admin`)

---

### Anwendung starten

1. Stelle sicher, dass **Java 14 oder höher** installiert ist
2. Nutze ein **Windows-Betriebssystem**
3. Lade das Projekt herunter und entpacke es
4. Starte die `.bat`-Datei

---

### Entwicklerhinweise

* **Code anzeigen**
  Mit einem beliebigen Editor öffnen (z. B. VSCode, IntelliJ)

* **Datenbank anzeigen oder bearbeiten**
  Verbinde dich mit einem Derby-Client (z. B. SQuirreL SQL) mit folgenden Daten:

  ```
  URL: jdbc:derby://localhost:1527/Database;create=true;
  Benutzer: SHOPPING_CART
  Passwort: [ein Leerzeichen]
  ```
