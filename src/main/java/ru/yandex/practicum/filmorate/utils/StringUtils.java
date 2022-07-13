package ru.yandex.practicum.filmorate.utils;

public class StringUtils {
    public static String suffix(String line, char character) {
        int lastIndex = line.lastIndexOf(character) + 1;
        return lastIndex > 0 ? line.substring(lastIndex) : "";
    }
}
