# EasySnup (Fabric 1.21.4)

Klientowy mod: **Kostium Snupa** (wzorowany na kostiumie mima z EasyKostium) + pomniejszenie jak lizak z EasyEventówka.
Wszystko dzieje się tylko u Ciebie (nic nie jest wysyłane do serwera).

Dwie wersje budowane z jednego kodu:

| Plik | Działanie |
|---|---|
| `easysnup-1.0.0-swap.jar` | Menu **B** zamienia trzymany przedmiot w kostium (jak EasyKostium) |
| `easysnup-1.0.0-give.jar` | Menu **B** ma przycisk **GIVE** - daje kostium, który istnieje tylko u Ciebie |

Instaluj **tylko jedną** z nich naraz (obie mają to samo ID moda).

## Jak zbudować na GitHubie
1. Wrzuć cały folder do repozytorium (`git init`, `git add .`, `git commit`, `git push`).
2. Zakładka **Actions** -> workflow **Build** -> po zakończeniu pobierz artefakty `easysnup-swap` i `easysnup-give`.

## Funkcje
- `B` - menu: przebranie/GIVE, data ważności, opcja **Na zawsze** (wtedy data nie pokazuje się na kostiumie).
- `/szafka`, `/szafa` - lokalna szafka: armor stand **Kostiumy** -> Kostium Snupa (z bonusami i datą lub bez), strzałka **Powrót**.
- Prawy klik kostiumem zakłada go; zakładasz = przedmiot znika. Zdjęcie: kliknij kostium w `/szafka`.
- Wygląd: głowa Snupa (ze skina `snupxb.png`), skórzany pancerz farbowany na błękitno.
- +2 serca pełne (wizualnie, doliczone do paska serc).
- 3x Shift = pomniejszenie do 0,65 na 10 s, potem odnawianie 120 s (boss bary błękitny / czerwony przy odnawianiu).
