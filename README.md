# IPWA02-Ghost-Net-Fishing
Aufgabenstellung 3: Ghost-Net-Fishing
Webanwendung zum Melden, Anzeigen und Verwalten von Fischernetzen. Das Projekt entstand als Fallstudie 3 im Modul IPWA02-01: Programmierung von industriellen Informationssystemen mit Java EE.

Funktionsumfang:
- Geisternetze mit Koordinaten und geschätzter Größe erfassen
- Meldungen anonym oder mit Kontaktdaten abgeben
- Offene und zur Bergung vorgemerkte Netze anzeigen
- Eine vorhandene oder neue bergende Person einem Netz zuordnen
- Netze als geborgen oder verschollen kennzeichnen
- Pflichtfelder und gültige Wertebereiche für Koordinaten prüfen
- Daten dauerhaft in einer relationalen MySQL-Datenbank speichern
- Statusmodell

Technologie-Stack:
- Programmiersprache: Java 17
- Plattform: Jakarta EE 10
- Benutzeroberfläche: Jakarta Faces, Facelets, PrimeFaces 13
- Komponentenmodell: CDI mit @Named, @ViewScoped, @ApplicationScoped und @Inject
- Persistenz: Jakarta Persistence 3.0, JPA
- Transaktionen: Jakarta Transactions, JTA
- JPA-Provider: Hibernate aus dem Application Server
- Datenbank: MySQL
- Build: Apache Maven, WAR-Paket

PrimeFaces wird mit der Anwendung ausgeliefert. Die Jakarta-EE-API und der JPA-Provider werden zur Laufzeit vom Application Server bereitgestellt.

Die Anwendung ist in drei fachliche Schichten gegliedert:
- bean: Steuerung der JSF-Seiten und Verarbeitung der Benutzereingaben
- dao: Datenbankzugriffe über den EntityManager und transaktionale Schreiboperationen
- entity: JPA-Entitäten, Beziehungen und Statuswerte

Voraussetzungen:
Für den lokalen Betrieb werden benötigt:
- JDK 17
- Apache Maven
- ein Jakarta-EE-10-kompatibler Application Server mit Jakarta Faces, CDI, JPA und JTA, zum Beispiel WildFly Full
- MySQL Server
- MySQL Connector/J

Die folgende Einrichtung verwendet WildFly als Referenz. Bei einem anderen Application Server muss ebenfalls eine JTA-Datasource mit dem JNDI-Namen java:/jdbc/GhostNetDS angelegt werden.

Lokale Einrichtung mit WildFly und MySQL:

1. Repository klonen
git clone https://github.com/simon44LH/IPWA02-Ghost-Net-Fishing.git
cd IPWA02-Ghost-Net-Fishing

2. Datenbank anlegen
Die Datenbank selbst muss vorhanden sein. Tabellen und Beziehungen werden beim Start durch JPA erzeugt beziehungsweise aktualisiert.

CREATE DATABASE ghostnet
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER 'ghostnet'@'localhost' IDENTIFIED BY 'PASSWORT_AENDERN';
GRANT ALL PRIVILEGES ON ghostnet.* TO 'ghostnet'@'localhost';
FLUSH PRIVILEGES;

Wenn MySQL nicht auf demselben Rechner wie WildFly läuft, müssen Host, Benutzerfreigabe und JDBC-URL entsprechend angepasst werden.

3. WildFly starten:
Linux und macOS:
$WILDFLY_HOME/bin/standalone.sh

4. MySQL-JDBC-Treiber bereitstellen:
MySQL Connector/J herunterladen und die JAR-Datei über die WildFly-CLI deployen. WildFly erkennt JDBC-4-kompatible Treiber automatisch.
$WILDFLY_HOME/bin/jboss-cli.sh --connect

Der zweite Befehl zeigt den registrierten driver-name. Dieser Wert wird im nächsten Schritt als <DRIVER_NAME> eingesetzt. Häufig entspricht er dem Dateinamen der JAR-Datei.

5. JTA-Datasource konfigurieren
Den folgenden Befehl in einer Zeile in der WildFly-CLI ausführen. <DRIVER_NAME> und <PASSWORT> müssen ersetzt werden.

data-source add --name=GhostNetDS --jndi-name=java:/jdbc/GhostNetDS --driver-name=<DRIVER_NAME> --connection-url="jdbc:mysql://localhost:3306/ghostnet?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" --user-name=ghostnet --password=<PASSWORT> --enabled=true

Danach die Verbindung testen:
/subsystem=datasources/data-source=GhostNetDS:test-connection-in-pool

Bei erfolgreicher Konfiguration liefert WildFly success zurück. Die Optionen useSSL=false und allowPublicKeyRetrieval=true sind nur für eine lokale Entwicklungsumgebung gedacht.

6. Anwendung bauen:
mvn clean package

Maven erzeugt die Datei:
target/ghostnet.war

7. Anwendung deployen:
In der verbundenen WildFly-CLI:
deploy /ABSOLUTER/PFAD/ZUM/PROJEKT/target/ghostnet.war --force

Alternativ kann ghostnet.war in das Verzeichnis $WILDFLY_HOME/standalone/deployments/ kopiert werden.

Die Anwendung ist anschließend erreichbar unter:
http://localhost:8080/ghostnet/

Bedienung:
Geisternetz melden:
Unter Geisternetz melden werden Breitengrad, Längengrad und optional die geschätzte Größe erfasst. Der Breitengrad muss zwischen -90 und 90, der Längengrad zwischen -180 und 180 liegen.
Bei einer anonymen Meldung werden keine Personendaten gespeichert. Wird die anonyme Meldung deaktiviert, sind Name und Telefonnummer Pflichtfelder.

Offene Netze anzeigen:
Unter Offene Netze erscheinen alle Netze mit dem Status GEMELDET oder BERGUNG_BEVORSTEHEND.

Netze verwalten:
Unter Netzübersicht werden alle gespeicherten Netze angezeigt. Dort kann:
- eine vorhandene bergende Person ausgewählt werden,
- eine neue bergende Person angelegt werden,
- eine Bergung angekündigt werden,
- ein Netz als geborgen markiert werden,
- ein Netz als verschollen markiert werden.

Datenmodell:
Die abstrakte Entität Person verwendet JPA-Single-Table-Vererbung. MeldendePerson und BergendePerson werden gemeinsam in der Tabelle person gespeichert und über die Spalte personentyp unterschieden.

Ein Geisternetz kann jeweils mit einer meldenden und einer bergenden Person verbunden sein. Beide Beziehungen sind als @ManyToOne modelliert. Die bergende Person besitzt zusätzlich die inverse @OneToMany-Beziehung zu ihren zugewiesenen Netzen.

Konfiguration:
Die zentrale Persistenzkonfiguration befindet sich in src/main/resources/META-INF/persistence.xml.

- Persistence-Unit: ghostnetPU
- Transaktionstyp: JTA
- Datasource: java:/jdbc/GhostNetDS
- Hibernate-Dialekt: org.hibernate.dialect.MySQLDialect
- Schema-Aktion: update

Die Zugangsdaten zur Datenbank gehören nicht in das Repository. Sie werden ausschließlich in der Datasource des Application Servers hinterlegt.

Manuelle Funktionsprüfung:
Nach dem Deployment können die wichtigsten Abläufe in dieser Reihenfolge geprüft werden:

Ein Netz anonym melden.
Ein weiteres Netz mit Name und Telefonnummer melden.
Beide Einträge unter Offene Netze kontrollieren.
In der Netzübersicht eine bergende Person anlegen und einem Netz zuordnen.
Prüfen, ob der Status auf Bergung bevorstehend wechselt.
Das Netz als geborgen markieren.
Prüfen, ob es nicht mehr unter Offene Netze erscheint.
Eine Verschollen-Meldung ohne Person versuchen und die fachliche Validierung kontrollieren.

Hinweise zum Betriebsmodus:
Die aktuelle Konfiguration ist für Entwicklung und Demonstration ausgelegt:

Jakarta Faces läuft im Development-Modus.
Hibernate gibt SQL-Anweisungen im Server-Log aus.
Das Datenbankschema wird mit update automatisch angepasst.
Es gibt derzeit keine Anmeldung und keine rollenbasierte Zugriffskontrolle.
Automatisierte Tests sind im Projekt noch nicht enthalten.

Für einen produktiven Betrieb sollten der Faces-Modus auf Production gestellt, SQL-Logging deaktiviert, versionierte Datenbankmigrationen eingesetzt und Zugangsschutz ergänzt werden.
