# G2A.com test task

## Informacje ogólne

Zadanie wykonane na stacku:

- Java 11
- Maven
- Cucumber
- Playwright
- JUnit 4
- Docker

Poświęcony czas: około 4 godzin.

## Uruchomienie testów z użyciem Docker

* Zbudowanie obrazu:

```bash
docker build -t g2atests .
```

* Uruchomienie kontenera z testami:

```bash
docker run --rm \
  -v $pwd/dockerreports:/app/target/reports \
  -e GAME_TITLE=Sekiro \
  -e CUCUMBER_FILTER_TAGS=@Web \
  g2atests
```

Gdzie:

- GAME_TITLE - tytuł gry, dla której chcemy uruchomić testy
- CUCUMBER_FILTER_TAGS - tagi Cucumbera, które chcemy uruchomić - w naszym przypadku @Web

NOTE: Po zakończeniu testów, raporty oraz playwright trace'y będą dostępne w katalogu `$pwd/dockerreports`.

## Konfiguracja

Konfiguracja testów znajduje się w pliku `config.properties`.

Dostępne opcje konfiguracyjne:

- `g2a.url` - adres strony G2A.com
- `browser.type` - rodzaj przeglądarki, dostępne opcje: chromium, firefox, webkit
- `browser.headed` - czy przeglądarka ma być uruchomiona w trybie headless (true) czy nie (false)
- `browser.context.default.timeout.ms` - domyślny timeout dla kontekstu przeglądarki w milisekundach
- `browser.slomo.ms` - opóźnienie w milisekundach między kolejnymi akcjami w przeglądarce

## Uwagi

- przydałoby się dodać obsługę na konfig lokalny np. `config.local.properties` nadpisywałby wartości
  z `config.properties`
- zamiast przekazywać tytuł gry z poziomu zmiennych środowiskowych, można by zrobić outline scenario w Cucumberze,
  który by przekazywał tytuł gry jako parametr
- rodzaj przeglądarki również można by przekazywać jako parametr ze zmiennej środowiskowej,
  umożliwiając tym samym uruchomienie testów na różnych przeglądarkach bez konieczności zmian w pliku konfiguracyjnym