# CxnSkriptAddon

Ein Skript-Addon für Cytooxien API Unterstützung, entwickelt für Skript.

---

## Projektstatus
Einige Syntax Existiert noch nicht 100% der API nutzbar.

---

## Bisherige Syntax

Diese Syntax kannst du im Skript verwenden, um Boost-Counts von Realms und Spielern abzufragen:

- `[the] [realm's] boost count`  
  Gibt als Integer zurück, wie viele Boosts der Realm insgesamt hat.

- `[the] boost count of %player%`  
- `%player%'s boost count`  
- `boost count of %player%`  

  Diese Varianten geben zurück, wie oft ein Spieler den Realm geboostet hat.

- `[the] realm's description`
   Gibt die Beschreibung des Realms als String zurück (Änderbar)

- `[the] realm['s] name`
   Gibt den Namen des Realms als String zurück (Änderbar)

- `[the] cytooxien lang[uage] of player`
- `%player%'s cytooxien lang[uage]`
  Gibt die Cytooxien Sprache eines Spielers zurück.

- `%player%'s [custom] tab[list] prefix / suffix`
- `%player%'s [custom] chat prefix / suffix`
  Gibt den Tablist oder Chat Pre- oder Suffix eines Spielers zurück (Änderbar)
- `[the] [realm's] cpu limit`
- `[the] [realm's] disk megabytes`
- `[the] [realm's] max [custom] group[s] count`
- `[the] [realm's] max loaded world[s] count`
- `[the] [realm's] max player limit`
- `[the] [realm's] max plugin count`
  Expressions um die Realm Limits rauszufinden
- `[the] [realm] (can|can('t| not)) have custom plugins`
- `[the] [realm] (can|can('t| not)) have [a] subdomain`
  Conditions um Realm limits zu checken
---
### Neuer Typ: Groups
Ein Group Element ist eine Realms Gruppe (Ein Rang)
### Group Syntax:
- `[all] [of] [the] realm['s] groups` Alle Gruppen des Realms
- `%player%'s highest group` Höchste Gruppe des Spielers (Nach Priority)
- `[all] [of] %player%'s groups` / `[all] [of] [the] groups of %player%` Alle Gruppen in denen ein Spieler ist
- `%player%'s (cytooxien | primary network) group` / `[the] (cytooxien | primary network) group of %player%` Die Gruppe in der Ein Spieler auf dem Hauptserver ist
- `[the] realm['s] owner group` / `[the] owner group of the realm` die Owner Gruppe des Realms
- `[the] members of %group%` / `%group%'s members` Alle Mitglieder einer Gruppe
- `group named %string%` / `group with name %string%` / `%string%['s] group` Syntax um eine Gruppe anhand ihres Namens zu finden
- `[the] realm['s] default group` / `[the] default group of the realm` Die Default Gruppe eines Realms die jeder Spieler hat.
- `[all] [of] [the] realm['s] booster groups` / `[all] [the] booster groups of the realm` Alle Gruppen die man mit einem Boost auf dem Realm kriegt.
- `[the] realm['s] booster group` / `[the] booster group of the realm` Die Default Booster Gruppe
- `add %player% to %group%` Syntax um einen Spieler zu einer Gruppe zu adden.
- `remove %player% from %group%` Syntax um einen Spieler von einer Gruppe zu entfernen.

