# About
Dies ist eine extra Software, um den Barcode Scanner verwenden zu können, mit dem die Eintrittskarten der Theaterbesucher gescannt werden können. Das ist auch die einzige Funktion dieses Programmes.
Das Projekt existiert nur, weil man aus dem Webbrowser heraus nicht an angeschlossene Hardware herankommt.

Das Frontend für alle anderen Aufgaben befindet sich unter https://github.com/Schadowpen/kjgBuchung.
Das Backend befindet sich unter https://github.com/Schadowpen/kjgDatabaseAPI.


# IDE
Als Entwicklungsumgebung wird Eclipse empfohlen


# Unterstützte Scanner
Für folgende Scanner ist bekannt, dass sie unterstützt werden. Andere Scanner könnten auch funktionieren, es wurde nicht ausprobiert. Unterstützte Scanner müssen QR-Codes scannen können.

- [NETUM NT-1228BL 2D Barcode Scanner](http://www.netum.cn/Scanner/showproduct.php?id=514)

# Anwendung bauen

## Erstellen einer ausfürbaren .jar
In Eclipse, gehe auf `File -> Export`. In dem sich öffnenden Fenster wähle `Java -> Runnable JAR file` aus und klicke `Next`.
Stelle bei der Konfiguration sicher, dass die richtige main-Methode ausgewählt ist, nämlich `Main - kjgBuchungScanner`. Als Export Destination gebe `kjgBuchungScanner/bin/kjgBuchungScannerExecutable.jar` an. Stelle sicher, dass alle benötigten Bibliotheken mit in die generierte JAR extrahiert werden.

## Erstellen einer .exe mit Launch4j
Öffne Launch4j. In Launch4j, öffne die launch4jConfig.xml. In dieser ist die gesamte Konfiguration enthalten, um eine ausführbare Datei zu erzeugen.

Überprüfe, ob alle Dateipfade korrekt sind, bevor du über das Zahnrad-Symbol die .exe zusammenbauen lässt. Die fertige ausführbare Datei findet sich in /lib.
