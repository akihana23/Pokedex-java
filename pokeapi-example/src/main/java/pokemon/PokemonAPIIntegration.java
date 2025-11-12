package pokemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PokemonAPIIntegration {
    
    public static PokemonData getPokemonFromAPI(String searchTerm) {
        try {
            // Primeira chamada: dados básicos do Pokémon
            String pokemonUrl = "https://pokeapi.co/api/v2/pokemon/" + searchTerm.toLowerCase();
            JsonObject pokemonJson = fetchJSONFromAPI(pokemonUrl);
            
            if (pokemonJson == null) {
                return null;
            }
            
            int id = pokemonJson.get("id").getAsInt();
            String name = pokemonJson.get("name").getAsString();
            
            // CONVERSÃO PARA METROS E KG
            int heightDecimeters = pokemonJson.get("height").getAsInt();
            int weightHectograms = pokemonJson.get("weight").getAsInt();
            double heightMeters = heightDecimeters / 10.0; // 1 dm = 0.1 m
            double weightKg = weightHectograms / 10.0;     // 1 hg = 0.1 kg
            
            String spriteUrl = "";
            if (!pokemonJson.getAsJsonObject("sprites").get("front_default").isJsonNull()) {
                spriteUrl = pokemonJson.getAsJsonObject("sprites").get("front_default").getAsString();
            }
            
            // Processar tipos
            JsonArray typesArray = pokemonJson.getAsJsonArray("types");
            StringBuilder types = new StringBuilder();
            List<String> typeList = new ArrayList<>();
            for (int i = 0; i < typesArray.size(); i++) {
                JsonObject typeObj = typesArray.get(i).getAsJsonObject();
                String typeName = typeObj.getAsJsonObject("type").get("name").getAsString();
                typeList.add(typeName);
                if (i > 0) types.append(", ");
                types.append(typeName);
            }
            
            // Processar habilidades
            JsonArray abilitiesArray = pokemonJson.getAsJsonArray("abilities");
            StringBuilder abilities = new StringBuilder();
            for (int i = 0; i < abilitiesArray.size(); i++) {
                JsonObject abilityObj = abilitiesArray.get(i).getAsJsonObject();
                String abilityName = abilityObj.getAsJsonObject("ability").get("name").getAsString();
                if (i > 0) abilities.append(", ");
                abilities.append(abilityName);
            }
            
            // Processar stats
            JsonArray statsArray = pokemonJson.getAsJsonArray("stats");
            StringBuilder stats = new StringBuilder();
            for (int i = 0; i < statsArray.size(); i++) {
                JsonObject statObj = statsArray.get(i).getAsJsonObject();
                String statName = statObj.getAsJsonObject("stat").get("name").getAsString();
                int baseStat = statObj.get("base_stat").getAsInt();
                if (i > 0) stats.append(" | ");
                stats.append(statName).append(": ").append(baseStat);
            }
            
            // Segunda chamada: espécie para descrição, habitat, etc.
            String speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/" + id;
            JsonObject speciesJson = fetchJSONFromAPI(speciesUrl);
            
            String description = getEnglishDescription(speciesJson);
            String habitat = getHabitat(speciesJson);
            List<String> evolutionaryChain = getEvolutionaryChain(speciesJson);
            
            // Fraquezas e resistências baseadas nos tipos
            List<String> weaknesses = getTypeWeaknesses(typeList);
            List<String> resistances = getTypeResistances(typeList);
            
            // Capitalizar nome
            if (name != null && !name.isEmpty()) {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
            
            // Criar PokemonData com unidades convertidas
            PokemonData pokemonData = new PokemonData(
                id,
                name, 
                types.toString(), 
                heightMeters,  // Em metros
                weightKg,      // Em kg
                abilities.toString(),
                stats.toString(),
                description,
                spriteUrl,
                habitat,
                weaknesses,
                resistances,
                evolutionaryChain
            );
            return pokemonData;
            
        } catch (Exception e) {
            System.out.println("❌ Erro ao buscar Pokémon na API: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static JsonObject fetchJSONFromAPI(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();
                
                return JsonParser.parseString(content.toString()).getAsJsonObject();
            } else {
                System.out.println("❌ Erro na API. Código: " + responseCode + " para URL: " + urlString);
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Erro na conexão com API: " + e.getMessage());
            return null;
        }
    }
    
    private static String getEnglishDescription(JsonObject speciesJson) {
        if (speciesJson == null) return "Descrição não disponível";
        
        try {
            JsonArray flavorTextEntries = speciesJson.getAsJsonArray("flavor_text_entries");
            for (int i = 0; i < flavorTextEntries.size(); i++) {
                JsonObject entry = flavorTextEntries.get(i).getAsJsonObject();
                String language = entry.getAsJsonObject("language").get("name").getAsString();
                if (language.equals("en")) {
                    return entry.get("flavor_text").getAsString()
                               .replace("\n", " ")
                               .replace("\f", " ");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao obter descrição: " + e.getMessage());
        }
        return "Descrição em inglês não encontrada";
    }
    
    private static String getHabitat(JsonObject speciesJson) {
        if (speciesJson == null) return "Desconhecido";
        
        try {
            if (speciesJson.has("habitat") && !speciesJson.get("habitat").isJsonNull()) {
                return speciesJson.getAsJsonObject("habitat").get("name").getAsString();
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao obter habitat: " + e.getMessage());
        }
        return "Desconhecido";
    }
    
    private static List<String> getEvolutionaryChain(JsonObject speciesJson) {
        List<String> chain = new ArrayList<>();
        if (speciesJson == null) return chain;
        
        try {
            String evolutionChainUrl = speciesJson.getAsJsonObject("evolution_chain").get("url").getAsString();
            JsonObject evolutionChainJson = fetchJSONFromAPI(evolutionChainUrl);
            
            if (evolutionChainJson != null) {
                extractEvolutionNames(evolutionChainJson.getAsJsonObject("chain"), chain);
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao obter cadeia evolutiva: " + e.getMessage());
        }
        return chain;
    }
    
    private static void extractEvolutionNames(JsonObject chain, List<String> names) {
        try {
            String pokemonName = chain.getAsJsonObject("species").get("name").getAsString();
            // Capitalizar nome
            pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1);
            names.add(pokemonName);
            
            if (chain.has("evolves_to") && chain.getAsJsonArray("evolves_to").size() > 0) {
                JsonArray evolvesTo = chain.getAsJsonArray("evolves_to");
                for (int i = 0; i < evolvesTo.size(); i++) {
                    extractEvolutionNames(evolvesTo.get(i).getAsJsonObject(), names);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao extrair evoluções: " + e.getMessage());
        }
    }
    
    public static void addIfNotExists(List<String> list, String... items) {
        for (String item : items) {
            if (!list.contains(item)) {
                list.add(item);
            }
        }
    }
    
    public static List<String> getTypeWeaknesses(List<String> types) {
        List<String> weaknesses = new ArrayList<>();
        // Mapeamento simplificado de fraquezas
        for (String type : types) {
            switch (type.toLowerCase()) {
                case "fire":
                    addIfNotExists(weaknesses, "Water", "Ground", "Rock");
                    break;
                case "water":
                    addIfNotExists(weaknesses, "Electric", "Grass");
                    break;
                case "grass":
                    addIfNotExists(weaknesses, "Fire", "Ice", "Poison", "Flying", "Bug");
                    break;
                case "electric":
                    addIfNotExists(weaknesses, "Ground");
                    break;
                case "ice":
                    addIfNotExists(weaknesses, "Fire", "Fighting", "Rock", "Steel");
                    break;
                case "fighting":
                    addIfNotExists(weaknesses, "Flying", "Psychic", "Fairy");
                    break;
                case "poison":
                    addIfNotExists(weaknesses, "Ground", "Psychic");
                    break;
                case "ground":
                    addIfNotExists(weaknesses, "Water", "Grass", "Ice");
                    break;
                case "flying":
                    addIfNotExists(weaknesses, "Electric", "Ice", "Rock");
                    break;
                case "psychic":
                    addIfNotExists(weaknesses, "Bug", "Ghost", "Dark");
                    break;
                case "bug":
                    addIfNotExists(weaknesses, "Fire", "Flying", "Rock");
                    break;
                case "rock":
                    addIfNotExists(weaknesses, "Water", "Grass", "Fighting", "Ground", "Steel");
                    break;
                case "ghost":
                    addIfNotExists(weaknesses, "Ghost", "Dark");
                    break;
                case "dragon":
                    addIfNotExists(weaknesses, "Ice", "Dragon", "Fairy");
                    break;
                case "dark":
                    addIfNotExists(weaknesses, "Fighting", "Bug", "Fairy");
                    break;
                case "steel":
                    addIfNotExists(weaknesses, "Fire", "Fighting", "Ground");
                    break;
                case "fairy":
                    addIfNotExists(weaknesses, "Poison", "Steel");
                    break;
            }
        }
        return weaknesses;
    }
    
    public static List<String> getTypeResistances(List<String> types) {
        List<String> resistances = new ArrayList<>();
        // Mapeamento simplificado de resistências
        for (String type : types) {
            switch (type.toLowerCase()) {
                case "fire":
                    addIfNotExists(resistances, "Fire", "Grass", "Ice", "Bug", "Steel", "Fairy");
                    break;
                case "water":
                    addIfNotExists(resistances, "Fire", "Water", "Ice", "Steel");
                    break;
                case "grass":
                    addIfNotExists(resistances, "Water", "Electric", "Grass", "Ground");
                    break;
                case "electric":
                    addIfNotExists(resistances, "Electric", "Flying", "Steel");
                    break;
                case "ice":
                    addIfNotExists(resistances, "Ice");
                    break;
                case "fighting":
                    addIfNotExists(resistances, "Bug", "Rock", "Dark");
                    break;
                case "poison":
                    addIfNotExists(resistances, "Grass", "Fighting", "Poison", "Bug", "Fairy");
                    break;
                case "ground":
                    addIfNotExists(resistances, "Poison", "Rock");
                    break;
                case "flying":
                    addIfNotExists(resistances, "Grass", "Fighting", "Bug");
                    break;
                case "psychic":
                    addIfNotExists(resistances, "Fighting", "Psychic");
                    break;
                case "bug":
                    addIfNotExists(resistances, "Grass", "Fighting", "Ground");
                    break;
                case "rock":
                    addIfNotExists(resistances, "Normal", "Fire", "Poison", "Flying");
                    break;
                case "ghost":
                    addIfNotExists(resistances, "Poison", "Bug");
                    break;
                case "dragon":
                    addIfNotExists(resistances, "Fire", "Water", "Electric", "Grass");
                    break;
                case "dark":
                    addIfNotExists(resistances, "Ghost", "Dark");
                    break;
                case "steel":
                    addIfNotExists(resistances, "Normal", "Grass", "Ice", "Flying", "Psychic", 
                                 "Bug", "Rock", "Dragon", "Steel", "Fairy");
                    break;
                case "fairy":
                    addIfNotExists(resistances, "Fighting", "Bug", "Dark");
                    break;
            }
        }
        return resistances;
    }
    
    public static void displayPokemonDetails(PokemonData pokemon) {
        if (pokemon != null) {
            System.out.println(pokemon.getDetailedInfo());
        }
    }
    
    public static void displayPokemonSummary(PokemonData pokemon) {
        if (pokemon != null) {
            System.out.println("\n=== 📋 RESUMO DO POKÉMON ===");
            System.out.println("ID: " + pokemon.getId());
            System.out.println("Nome: " + pokemon.getName());
            System.out.println("Tipo: " + pokemon.getType());
            System.out.println("Altura: " + pokemon.getHeight() + " m | Peso: " + pokemon.getWeight() + " kg");
            System.out.println("Habitat: " + (pokemon.getHabitat() != null ? pokemon.getHabitat() : "Desconhecido"));
            System.out.println("Descrição: " + 
                (pokemon.getDescription().length() > 100 ? 
                 pokemon.getDescription().substring(0, 100) + "..." : 
                 pokemon.getDescription()));
            System.out.println("Sprite: " + pokemon.getSpriteUrl());
            System.out.println("Fraquezas: " + String.join(", ", pokemon.getWeaknesses()));
            System.out.println("Resistências: " + String.join(", ", pokemon.getResistances()));
            System.out.println("Cadeia Evolutiva: " + String.join(" → ", pokemon.getEvolutionaryChain()));
        }
    }
}