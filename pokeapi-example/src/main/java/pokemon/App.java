package pokemon;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class App {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Digite o nome do Pokémon: ");
        String nomePokemon = input.nextLine().toLowerCase();

        try {
            // =============================
            // 1️⃣ Requisição principal (pokemon)
            // =============================
            JsonObject pokemon = getJsonFromUrl("https://pokeapi.co/api/v2/pokemon/" + nomePokemon);

            if (pokemon == null) {
                System.out.println("Pokémon não encontrado!");
                return;
            }

            // Informações básicas
            String nome = pokemon.get("name").getAsString();
            int altura = pokemon.get("height").getAsInt();
            int peso = pokemon.get("weight").getAsInt();

            System.out.println("\n===== INFORMAÇÕES DO POKÉMON =====");
            System.out.println("Nome: " + capitalize(nome));
            System.out.println("Altura: " + altura);
            System.out.println("Peso: " + peso);

            // =============================
            // 2️⃣ Tipos
            // =============================
            List<String> tipos = new ArrayList<>();
            System.out.println("\nTipos:");
            JsonArray types = pokemon.getAsJsonArray("types");
            for (JsonElement t : types) {
                String tipo = t.getAsJsonObject().getAsJsonObject("type").get("name").getAsString();
                tipos.add(tipo);
                System.out.println("- " + capitalize(tipo));
            }

            // =============================
            // 3️⃣ Habilidades
            // =============================
            System.out.println("\nHabilidades:");
            JsonArray habilidades = pokemon.getAsJsonArray("abilities");
            for (JsonElement h : habilidades) {
                String habilidade = h.getAsJsonObject().getAsJsonObject("ability").get("name").getAsString();
                System.out.println("- " + capitalize(habilidade));
            }

            // =============================
            // 4️⃣ Status Base
            // =============================
            System.out.println("\nStatus Base:");
            JsonArray stats = pokemon.getAsJsonArray("stats");
            for (JsonElement s : stats) {
                JsonObject stat = s.getAsJsonObject();
                String nomeStat = stat.getAsJsonObject("stat").get("name").getAsString();
                int valor = stat.get("base_stat").getAsInt();
                System.out.println(capitalize(nomeStat) + ": " + valor);
            }

            // =============================
            // 5️⃣ Sprite (imagem)
            // =============================
            String sprite = pokemon.getAsJsonObject("sprites").get("front_default").getAsString();
            System.out.println("\nSprite (imagem): " + sprite);

            // =============================
            // 6️⃣ Informações de species
            // =============================
            String speciesUrl = pokemon.getAsJsonObject("species").get("url").getAsString();
            JsonObject species = getJsonFromUrl(speciesUrl);

            if (species != null) {
                // Descrição (flavor text)
                JsonArray flavorTextEntries = species.getAsJsonArray("flavor_text_entries");
                String descricao = "";
                for (JsonElement entry : flavorTextEntries) {
                    JsonObject obj = entry.getAsJsonObject();
                    if (obj.getAsJsonObject("language").get("name").getAsString().equals("en")) {
                        descricao = obj.get("flavor_text").getAsString().replace("\n", " ").replace("\f", " ");
                        break;
                    }
                }
                System.out.println("\nDescrição: " + descricao);

                // Habitat
                if (species.has("habitat") && !species.get("habitat").isJsonNull()) {
                    String habitat = species.getAsJsonObject("habitat").get("name").getAsString();
                    System.out.println("Habitat: " + capitalize(habitat));
                }

                // Cadeia evolutiva
                if (species.has("evolution_chain") && !species.get("evolution_chain").isJsonNull()) {
                    String evolutionUrl = species.getAsJsonObject("evolution_chain").get("url").getAsString();
                    JsonObject evolutionChain = getJsonFromUrl(evolutionUrl);

                    System.out.println("\nCadeia Evolutiva:");
                    printEvolutionChain(evolutionChain.getAsJsonObject("chain"));
                }
            }

            // =============================
            // 7️⃣ Fraquezas e resistências
            // =============================
            System.out.println("\nFraquezas e Resistências:");
            Set<String> fraquezas = new HashSet<>();
            Set<String> resistencias = new HashSet<>();

            for (String tipo : tipos) {
                JsonObject tipoJson = getJsonFromUrl("https://pokeapi.co/api/v2/type/" + tipo);

                if (tipoJson != null) {
                    JsonObject damage = tipoJson.getAsJsonObject("damage_relations");

                    addTypeSet(damage.getAsJsonArray("double_damage_from"), fraquezas);
                    addTypeSet(damage.getAsJsonArray("half_damage_from"), resistencias);
                }
            }

            System.out.println("Fraquezas: " + formatSet(fraquezas));
            System.out.println("Resistências: " + formatSet(resistencias));

        } catch (IOException e) {
            System.out.println("Erro ao conectar à API: " + e.getMessage());
        }

        input.close();
    }

    // =============================
    // Funções auxiliares
    // =============================

    private static JsonObject getJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            return null;
        }

        Scanner scanner = new Scanner(conn.getInputStream());
        StringBuilder json = new StringBuilder();
        while (scanner.hasNext()) {
            json.append(scanner.nextLine());
        }
        scanner.close();

        return JsonParser.parseString(json.toString()).getAsJsonObject();
    }

    private static void printEvolutionChain(JsonObject chain) {
        String species = chain.getAsJsonObject("species").get("name").getAsString();
        System.out.print(capitalize(species));

        JsonArray evolutions = chain.getAsJsonArray("evolves_to");
        if (evolutions.size() > 0) {
            System.out.print(" → ");
            for (int i = 0; i < evolutions.size(); i++) {
                printEvolutionChain(evolutions.get(i).getAsJsonObject());
                if (i < evolutions.size() - 1) System.out.print(", ");
            }
        } else {
            System.out.println();
        }
    }

    private static void addTypeSet(JsonArray arr, Set<String> set) {
        for (JsonElement e : arr) {
            String type = e.getAsJsonObject().get("name").getAsString();
            set.add(capitalize(type));
        }
    }

    private static String formatSet(Set<String> set) {
        return set.isEmpty() ? "Nenhuma" : String.join(", ", set);
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
