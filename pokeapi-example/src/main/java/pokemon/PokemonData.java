package pokemon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PokemonData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String type;
    private int height;
    private int weight;
    private String abilities;
    private String stats;
    private String description;
    private String spriteUrl;
    private String habitat;
    private List<String> weaknesses;
    private List<String> resistances;
    private List<String> evolutionaryChain;
    private String dateAdded;

    // CONSTRUTOR SEM ARGUMENTOS (já existe)
    public PokemonData() {
        this.weaknesses = new ArrayList<>();
        this.resistances = new ArrayList<>();
        this.evolutionaryChain = new ArrayList<>();
    }

    // CONSTRUTOR COMPLETO - ADICIONE ESTE CONSTRUTOR
    public PokemonData(int id, String name, String type, int height, int weight,
                      String abilities, String stats, String description, String spriteUrl,
                      String habitat, List<String> weaknesses, List<String> resistances,
                      List<String> evolutionaryChain) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.height = height;
        this.weight = weight;
        this.abilities = abilities;
        this.stats = stats;
        this.description = description;
        this.spriteUrl = spriteUrl;
        this.habitat = habitat;
        this.weaknesses = weaknesses != null ? weaknesses : new ArrayList<>();
        this.resistances = resistances != null ? resistances : new ArrayList<>();
        this.evolutionaryChain = evolutionaryChain != null ? evolutionaryChain : new ArrayList<>();
        this.dateAdded = java.time.LocalDateTime.now().toString();
    }

    // Getters e Setters (mantenha os que você já tem)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public String getAbilities() { return abilities; }
    public void setAbilities(String abilities) { this.abilities = abilities; }

    public String getStats() { return stats; }
    public void setStats(String stats) { this.stats = stats; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpriteUrl() { return spriteUrl; }
    public void setSpriteUrl(String spriteUrl) { this.spriteUrl = spriteUrl; }

    public String getHabitat() { return habitat; }
    public void setHabitat(String habitat) { this.habitat = habitat; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getResistances() { return resistances; }
    public void setResistances(List<String> resistances) { this.resistances = resistances; }

    public List<String> getEvolutionaryChain() { return evolutionaryChain; }
    public void setEvolutionaryChain(List<String> evolutionaryChain) { this.evolutionaryChain = evolutionaryChain; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    @Override
    public String toString() {
        return String.format("Pokémon #%d: %s | Tipo: %s | Altura: %d | Peso: %d", 
                id, name, type, height, weight);
    }

    public String toShortString() {
        return String.format("#%d - %s (%s)", id, name, type);
    }

    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== 📊 DETALHES COMPLETOS - %s ===\n", name.toUpperCase()));
        sb.append(String.format("ID: %d\n", id));
        sb.append(String.format("Nome: %s\n", name));
        sb.append(String.format("Tipo: %s\n", type));
        sb.append(String.format("Altura: %d dm | Peso: %d hg\n", height, weight));
        sb.append(String.format("Habitat: %s\n", habitat != null ? habitat : "Desconhecido"));
        sb.append(String.format("Habilidades: %s\n", abilities));
        sb.append(String.format("Stats: %s\n", stats));
        sb.append(String.format("Descrição: %s\n", description != null ? description : "N/A"));
        sb.append(String.format("Sprite: %s\n", spriteUrl));
        sb.append(String.format("Fraquezas: %s\n", String.join(", ", weaknesses)));
        sb.append(String.format("Resistências: %s\n", String.join(", ", resistances)));
        sb.append(String.format("Cadeia Evolutiva: %s\n", String.join(" → ", evolutionaryChain)));
        sb.append(String.format("Data de Adição: %s", dateAdded));
        
        return sb.toString();
    }
}