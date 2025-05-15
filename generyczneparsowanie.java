import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GenericParser {

    /**
     * Wczytuje plik i przetwarza każdą linię na listę elementów (po separatorze)
     */
    public static List<List<String>> parseFile(Path path, String separator) throws IOException {
        return Files.lines(path)
                .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#")) // pomija puste i komentarze
                .map(line -> Arrays.stream(line.split(separator))
                                   .map(String::trim)
                                   .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Filtruje linie na podstawie podanego predykatu (np. wybierz tylko miasta z Polski)
     */
    public static List<List<String>> filterLines(List<List<String>> rows, Predicate<List<String>> predicate) {
        return rows.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Przekształca linie do innego typu (np. mapowanie na własne obiekty)
     */
    public static <T> List<T> mapLines(List<List<String>> rows, Function<List<String>, T> mapper) {
        return rows.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * Wybiera tylko określone kolumny (indeksy)
     */
    public static List<List<String>> selectColumns(List<List<String>> rows, int... indices) {
        return rows.stream()
                .map(row -> Arrays.stream(indices)
                                  .mapToObj(i -> i < row.size() ? row.get(i) : "")
                                  .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}

import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // Wczytaj wszystkie linie jako listy stringów
        List<List<String>> data = GenericParser.parseFile(Paths.get("cities.csv"), ";");

        // Wybierz tylko miasta z Polski
        List<List<String>> polishCities = GenericParser.filterLines(data, row -> row.get(1).equalsIgnoreCase("Poland"));

        // Wybierz tylko nazwę miasta i liczbę mieszkańców (indeksy 0 i 2)
        List<List<String>> nameAndPopulation = GenericParser.selectColumns(polishCities, 0, 2);

        // Wydrukuj
        for (List<String> row : nameAndPopulation) {
            System.out.println(row);
        }

        // Opcjonalnie: mapuj do obiektów (np. City-lite)
        List<CityLite> cities = GenericParser.mapLines(polishCities, row ->
                new CityLite(row.get(0), Integer.parseInt(row.get(2)))
        );

        for (CityLite c : cities) {
            System.out.println(c);
        }
    }

    // pomocnicza klasa
    static class CityLite {
        String name;
        int population;
        CityLite(String name, int population) {
            this.name = name;
            this.population = population;
        }
        public String toString() {
            return name + " (pop: " + population + ")";
        }
    }
}



Warsaw;Poland;1790658;517.24
Krakow;Poland;779115;326.85
Berlin;Germany;3644826;891.68
