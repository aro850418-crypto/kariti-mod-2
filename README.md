# Kariti Mod — Minecraft Forge 1.20.1

Мод добавляет сущность **Карити** — союзника, который автоматически появляется рядом с игроком при входе в мир и охотится на сущности с "verity" в названии типа.

## Установка для разработки

### Требования
- Java 17 (JDK)
- IntelliJ IDEA или Eclipse

### Шаги

1. Скачай [Forge MDK 1.20.1-47.2.0](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
2. Распакуй MDK и скопируй файлы из этого проекта поверх
3. Открой папку в терминале и запусти:
   ```
   ./gradlew genEclipseRuns   # для Eclipse
   ./gradlew genIntellijRuns  # для IntelliJ IDEA
   ```
4. Запусти игру: `./gradlew runClient`

### Сборка .jar файла
```
./gradlew build
```
Файл появится в `build/libs/kariti-mod-1.0.0.jar` — его кидают в папку `mods/`.

## Что делает мод

- При входе игрока в мир рядом с ним спавнится **Карити** (базовая модель Iron Golem)
- Карити отправляет оранжевое сообщение в чат: *"Не верь Верити — он опасен!"*
- Карити **не атакует игрока**
- Карити атакует любую сущность, у которой `"verity"` есть в ID типа
- Карити **не деспавнится** (persistenceRequired = true)

## Структура проекта

```
src/main/java/com/kariti/mod/
  KaritiMod.java       — главный класс мода + сущность + события

src/main/resources/
  META-INF/mods.toml   — метаданные мода
  pack.mcmeta
  assets/kariti_mod/
    lang/ru_ru.json    — русский перевод
    lang/en_us.json    — английский перевод
```
