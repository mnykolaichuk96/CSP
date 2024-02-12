# Automatyzacja systemu powiadomień z wykorzystaniem narzędzi low-code n8n

Rozwiązanie do obsługi powiadomień o zdarzeniach, które wystąpiły w aplikacji backendowej stworzonej w stosie technologicznym Java/Spring.

## Uruchamianie Projektu

### Wymagania

Upewnij się, że masz zainstalowane następujące narzędzia:

- Docker: [Instrukcje instalacji](https://docs.docker.com/get-docker/)
- Docker Compose: [Instrukcje instalacji](https://docs.docker.com/compose/install/)

### Uruchamianie z Docker Compose

Projekt może być łatwo uruchomiony za pomocą Docker Compose. Przejdź do katalogu głównego projektu, gdzie znajduje się plik docker-compose.yml, a następnie wykonaj następujące polecenie:

```bash
docker-compose up
```

## Baza danych (db)
- **Docker image:** mnykolaichuk/praca-dyplomowa-mysql-db:2023
- Aplikacja korzysta z bazy danych MySQL. Możesz uzyskać dostęp do phpMyAdmin pod adresem [http://localhost:8082](http://localhost:8082).

## Aplikacja (iremont)
- **Docker image:** mnykolaichuk/praca-dyplomowa-app:2024-CSP-V5
- Aplikacja dostępna jest pod adresem [http://localhost:8083](http://localhost:8083).

## Narzędzia
- **phpMyAdmin:** Docker image: phpmyadmin/phpmyadmin:latest, dostępny pod adresem [http://localhost:8082](http://localhost:8082)
- **n8n:** Docker image: docker.n8n.io/n8nio/n8n, dostępny pod adresem [http://localhost:5678](http://localhost:5678)

## Narzędzia do monitorowania
- **Conduktor:** Docker image: conduktor/conduktor-platform:1.20.0, dostępny pod adresem [http://localhost:8080](http://localhost:8080)
- **Conduktor Gateway:** Docker image: conduktor/conduktor-gateway:2.3.0, dostępny pod adresem [http://localhost:8888](http://localhost:8888)

## Serwer Redpanda (redpanda-0)
- **Docker image:** docker.redpanda.com/redpandadata/redpanda:v23.1.11
- Kafka, pandaproxy, Schema Registry i wiele innych dostępne są pod adresem [http://localhost:19092](http://localhost:19092).

## Dodatkowe zasoby
- **Sprawozdanie:** Dostępne w folderze `Documents`.
- **Pliki przepływów n8n:** Wyeksportowane do plików JSON i dostępne w folderze `n8n/schemas`.


