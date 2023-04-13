# Praktikumsbericht
## Kunden
| Name | E-Mailadresse |
| ------ | ------ |
| Joshua Wiedenkopf | j.wiedekopf@uni-luebeck.de |
| Jan Matyssek | jan.matyssek@rwth-aachen.de |

## Mentor
| Name | E-Mailadresse |
| ------ | ------ |
| Juljan Bouchagiar | juljan.bouchagiar@isp.uni-luebeck.de |

## Teilnehmer
Tabellarische Auflistung aller Gruppenmitglieder inklusive des angestrebten Moduls

| Name | Modul |
| ------ | ------ |
| Felix Winkler | Erweitertes Bachelorprojekt |
| Florian Vierkant | Erweitertes Bachelorprojekt |
| Marie Biethahn | Erweitertes Bachelorprojekt |
| Max Henning Junghans | Projektpraktikum SSE |
| Sebastian Guhl | Erweitertes Bachelorprojekt |
| Steffen Marbach | Erweitertes Bachelorprojekt |

## Beschreibung
Hier soll das Projekt kurz in einem Fließtext beschreiben werden.
Maximal 100 Wörter.

Dieses Projekt befasst sich mit der Entwicklung eines Ortungssimulators als App.
Genau bedeutet dies, dass die App in der Lage sein soll, Vibrationen und Töne abzugeben, mit denen die Bergung von Personen geübt werden kann.
Diese Geräusche sollen eingerichtet und randomisiert werden können, um die Realität möglichst gut wiederzugeben.
Die App soll mindestens eine Stunde lang laufen, ohne, dass mit dem Handy interagiert wird.


## Projektplan
Ausgehend vom Lasten- bzw. Pflichtenheft soll eine zeitliche Einteilung der Arbeitspakete über die Projektlaufzeit erfolgen. Diese soll in der Gantt Chart Vorlage aus Moodle erfolgen. Dies kann zum Ende des Projektes im Git-Lab mit Hochgeladen, sowie hier als Screenshot eingefügt werden.


## Vorgehen
Im Nachgang an das Projekt haben sich aus unserem Vorgehen zwei größere, übergreifende Arbeitsphasen herauskristallisiert.
In diesen Arbeitsphasen sind wir unterschiedlich vorgegangen.
Über das komplette Projekt hinweg haben wir uns wöchentlich donnerstags abends zu einer teaminternen Onlinebesprechung getroffen.
Das Protokoll der Arbeitswochen orientiert sich dementsprechend an diesem Rhythmus.
Als Plattform haben wir hierfür einen Discord-Server genutzt.
Rückblickend hat sich Discord für uns als geeignete Plattform und sehr hilfreich erwiesen.
Auch außerhalb des wöchentlichen Meetings haben wir uns per Chat ausgetauscht.
Wenn mehrere Personen zusammen an einem Issue gearbeitet haben, so geschah dies via Discord über Sprachkanäle.
Mit den Kunden haben wir uns zu größeren Meilensteinen getroffen.
Hier haben wir den Fortschritt besprochen und umfangreiches Feedback eingeholt.
Außerdem waren die Kunden auch auf dem Discord-Server vertreten, sodass wir uns auch außerhalb von Meetings mit ihnen austauschen konnten.
Diese Chance hätten wir rückblickend noch häufiger nutzen sollen.
Ebenso haben wir uns alle zwei Wochen mit unserem Betreuer getroffen.

Die erste Phase beinhaltet die Projektplanung, inkl. der Vorarbeit für die Entwicklung und dem Entwurf der Architektur.
Die zweite Phase beinhaltet die tatsächliche Implementierung des Systems.
In der ersten Phase sind wir keinem bestimmten System gefolgt.
Dies lag auch daran, dass wir keine alle im Team keine Ahnung von Androidentwicklung hatten.
Zunächst mussten wir uns auf ein Framework einigen, dann musste sich jeder auch in dieses einarbeiten.
Wir haben zwar wie gefordert Lastenheft, Pflichtenheft und Gantt-Chart erstellt.
Dies geschah aber nur aus den Anforderungen heraus und spiegelte sich nicht in unserer Arbeitsweise wider.
In unseren Meetings haben wir gesammelt, was zu tun ist und diese Aufgaben dann erledigt.
Dabei haben wir recht schnell Verantwortlichkeiten geklärt, mit dem Ziel, dass keine Aspekte liegen bleiben.
Dies hat auch zu Problemen geführt.
Nicht jeder hatte immer einen Überblick über das, was die Anderen aktuell tun.
Dadurch ist „Silowissen“ entstanden.

In der zweiten Phase der Entwicklung sind wir strukturierter vorgegangen.
Unser Vorgehen war agil orientiert.
Hierbei haben wir die Tools von Gitlab genutzt.
Aus den Anforderungen haben wir in unseren wöchentlichen Meetings Issues erstellt.
Diese haben wir um weitere Issues ergänzt, wenn uns Dinge aufgefallen sind, wie Bugs etc.
Auch Aspekte der Dokumentation und des Projektmanagements wurden als Issues abgebildet.
Größere Issues, an denen alle gearbeitet haben, haben wir in kleinere Tasks unterteilt.
Die Issues waren auf dem Issue Board in Listen strukturiert.
Wir haben die Listen genutzt Open, Doing, Feedback und Closed genutzt.

![Zustandsdiagramm unserer Issue-Listen](Zustandsdiagramm-Issue-Listen.svg)

Die Liste Open war unser Backlog.
Meistens haben wir uns die Woche über selbst die Issues zugewiesen, an denen wir diese Woche arbeiten wollten.
Dies hatte den Vorteil, dass jeder entsprechend seiner zeitlichen Verfügbarkeit und Präferenzen entscheiden konnte, was er erledigen kann.
Wir haben mittels Tags wie „Critical“ unverbindliche Prioritäten kommuniziert. 
Vor Deadlines sind wir dazu übergegangen, die wichtigsten Issues im wöchentlichen Meeting direkt zuzuweisen.
Damit haben wir sichergestellt, dass jedes wichtige Issue erledigt wird, da die Verantwortung geklärt war.
Hat sich jemand eines Issues angenommen, so wurde dieses auf Doing verschoben.
Für größere Issues und Breaking Changes haben wir Branches und Merge Request eingesetzt.
Bei kleineren Issues haben wir auf dies meist verzichtet.
Dies haben wir nicht rigoros durchgesetzt, weshalb es in der Entwicklung zwischendurch zu überflüssigen Merge-Konflikten gekommen ist.
Auch haben sich dabei manchmal nicht funktionierende Commits in den Master-Branch eingeschlichen, was in der Entwicklung zusätzlich störend war.
Hat jemand ein Issue umgesetzt, so wurde dieses auf Feedback verschoben.
Mindestens eine weitere Person sollte kontrollieren, ob das Issue korrekt umgesetzt wurde und ob sich keine Bugs eingeschlichen haben.
Auch hier waren wir nicht rigoros genug in der Durchsetzung.
Vor allem bei kleineren Issues wurde dies manchmal übergangen oder nur oberflächlich erledigt.
Dies hat dazu geführt, dass wir später mehr Bugs zu fixen hatten.
Auch haben wir beim Feedback nicht kontrolliert, ob das Feature ausreichend kommentiert war.
Dies hat am Ende vom Projekt für zusätzlichen Aufwand gesorgt, um diese Dokumentationslücken zu schließen.
Gleiches gilt für Unittests.
Diese haben wir erst am Ende des Projekts eingeführt, wodurch ein Mehraufwand entstanden ist.
Wurde das Issue nun als erledigt akzeptiert, so wurde es auf Done verschoben.
Wenn nicht, kam es zurück zu Doing.


## Arbeitsbericht
Hier soll in kurzen Absätzen beschreiben werden, was alles pro Woche für das Projekt gemacht wurde.
Als Hilfestellung können folgende Fragen dienen:
- Was wurde gemacht?
- Wie wurde dabei vorgegangen?
- Gab es Erkenntnisse?
- Gab es Fehlschläge?

Die Beschreibung erfolgt gemeinsam in der Gruppe und soll Einblicke in die Fortschritte des Projektes geben.
Als mögliche Unterteilung habe ich die einzelnen Kalenderwochen vorgeschlagen, dies kann aber beliebig abgeändert werden.

### Projektwoche 1 (21.10 - 27.10)
Wir haben uns als Team kennengelernt und erste Termine geplant.

### Projektwoche 2 (28.10 – 03.11)
Wir haben uns das erste Mal mit unserem Betreuer getroffen und Fragen geklärt.

### Projektwoche 3 (04.11 – 10.11)
Wir haben uns mit den Kunden getroffen und die Anforderungen an das Projekt geklärt.

### Projektwoche 4 (11.11 – 17.11)
Wir haben angefangen, Lastenheft und Pflichtenheft zu erstellen.
Außerdem gab es ein Mentorentreffen, bei dem wir Feedback zu der Version bekommen haben.

### Projektwoche 5 (18.11 – 24.11)
Wir haben angefangen, Arbeitspakete zu definieren und einen Proof of Concept zu entwickeln.

### Projektwoche 6 (25.11 – 01.12)
Wir haben angefangen, den UI-Prototypen zu entwickeln.
Außerdem sind das Lastenheft und das Pflichtenheft fertig.
Der Proof of Concept schreitet gut voran.

### Projektwoche 7 (02.12 – 08.12)
Wir haben angefangen die Zwischenpräsentation vorzubereiten.
Hierfür haben wir ein Folienset erstellt.
Außerdem haben wir angefangen eine Architektur zu entwickeln, die auf MVVM basiert.

### Projektwoche 8 (09.12 – 15.12)
Wir haben die Folien verbessert.
Außerdem haben wir angefangen, den UI-Prototypen zu entwickeln.

### Projektwoche 9 (16.12 – 22.12)
Wir haben bei Prof. Leucker die Zwischenpräsentation für das Projekt abgelegt.
Außerdem haben wir uns mit den Kunden getroffen um den UI-Prototypen zu besprechen.

### Projektwoche 10 (23.12 – 29.12)
Wir haben den UI-Prototypen mit den Wünschen der Kunden finalisiert und besprochen.
Außerdem haben wir mit der Implementierung der App begonnen.

### Projektwoche 11 (30.12 – 05.01)
Wir haben das Grundgerüst für die App fertiggestellt, sodass wir nun anfangen können, parallel an dieser zu arbeiten.

### Projektwoche 12 (06.01 – 12.01)
Wir haben angefangen, die ersten Views zu erstellen und die Vibrationen zu verbesern.

### Projektwoche 13 (12.01 – 19.01)
Wir haben angefangen, den Sound zu implementieren und Pattern für die Vibrationen einzubauen.

### Projektwoche 14 (20.01 – 26.01)
Wir haben den Import von Dateien zum Teil implementiert.
Außerdem haben wir angefangen, die Timeline zu entwickeln.
Und wir haben angefangen, den Import und Export von Konfigurationen zu implementieren.

### Projektwoche 15 (27.01 – 02.02)
Wir haben weitere Screens hinzugefügt: Den Homescreen, Infoscreen und die Seite zur Auswahl der Sounds.
Außerdem haben wir beim Import und Export von Konfigurationen einen großen Schritt nach vorne gemacht.

### Projektwoche 16 (03.02 – 09.02)
Wir haben das Interface der App etwas überarbeitet und kleinere Probleme gefixt.

### Projektwoche 17 (10.02 – 16.02)
Wir haben diese Woche keine Fortschritte verzeichnet, da wir alle in die Klausuren eingebunden sind.
Die Abgabe des Projekts wurde verschoben.

### Projektwoche 18 (17.02 – 23.02)
Wir haben diese Woche vor allem Bugfixes und UI-Probleme bearbeitet.

### Projektwoche 19 (24.02 – 02.03)
Wir haben diese Woche nur Kleinigkeiten erledigt.

### Projektwoche 20 (03.03 – 09.03)
Wir haben diese Woche die Soundauswahlseite und die Timeline überarbeitet und generelles Codecleanup betrieben.

### Projektwoche 21 (10.03 – 16.03)
Wir haben die Möglichkeit hinzugefügt, aus der App heraus eine Soundaufnahme zu starten.
Außerdem ist die Timeline jetzt zufällig abspielbar.
Zudem hat die App nun Standardsounds.

### Projektwoche 22 (17.03 – 23.03)
Wir haben uns mit den Kunden getroffen und Feedback zur aktuellen Version der App geholt.
Außerdem wurde ein Darkmode hinzugefügt.

### Projektwoche 23 (24.03 – 30.03)
Wir haben diese Woche angefangen, das Feedback der Kunden umzusetzen.
Außerdem haben wir angefangen, Integrationstests einzuführen.

### Projektwoche 25 (31.03 – 06.04)
Wir haben diese Woche weiter an den Tests gearbeitet.
Außerdem haben wir generelles Bugfixing betrieben und kleinere Issues abgearbeitet.

### Projektwoche 26 (07.04 – 13.04)
Wir haben das Projekt in einen auslieferbaren Stand gebracht und die Endpräsentation mit den Kunden abgeliefert.

### Projektwoche 26 (14.04 – 20.04)


## Abgabetermin Dokumentation
Abgabetermin ist zur Abschlusspräsentation bei Herr Prof. M. Leucker 17. April 2023.

Der Praktikumsbericht soll **maximal** 5 Seiten (oder 8250 Wörter) umfassen.

