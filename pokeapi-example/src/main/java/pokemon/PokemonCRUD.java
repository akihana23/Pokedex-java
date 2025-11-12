package pokemon;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class PokemonCRUD {
    private static final String FILE_NAME = "pokemon_database.csv";
    private List<PokemonData> pokemonList;
    private Scanner scanner;

    public PokemonCRUD() {
        this.pokemonList = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadFromFile();
    }

    // CREATE - Adicionar novo Pokémon (versão para GUI)
    public boolean addPokemon(PokemonData pokemon, Component parentComponent) {
        // Verificar se já existe um Pokémon com mesmo ID
        for (PokemonData existing : pokemonList) {
            if (existing.getId() == pokemon.getId()) {
                int option = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "Já existe um Pokémon com ID " + pokemon.getId() + ". Deseja substituir?",
                    "Confirmação de Substituição",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    pokemonList.remove(existing);
                    break;
                } else {
                    JOptionPane.showMessageDialog(
                        parentComponent,
                        "Operação cancelada.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    return false;
                }
            }
        }
        
        pokemonList.add(pokemon);
        saveToFile();
        return true;
    }

    // Sobrecarga do método para manter compatibilidade
    public void addPokemon(PokemonData pokemon) {
        addPokemon(pokemon, null);
    }

    // READ - Listar todos os Pokémon
    public void listAllPokemon() {
        if (pokemonList.isEmpty()) {
            System.out.println("📭 Nenhum Pokémon cadastrado.");
            return;
        }

        System.out.println("\n=== 🗃️ LISTA DE POKÉMON SALVOS (" + pokemonList.size() + ") ===");
        for (int i = 0; i < pokemonList.size(); i++) {
            PokemonData p = pokemonList.get(i);
            System.out.printf("%d. %s | Habitat: %s | Fraquezas: %s%n",
                (i + 1), p.toShortString(), 
                p.getHabitat() != null ? p.getHabitat() : "N/A",
                String.join(", ", p.getWeaknesses()));
        }
    }

    // READ - Buscar Pokémon por nome
    public void findPokemonByName(String name) {
        List<PokemonData> found = new ArrayList<>();
        for (PokemonData pokemon : pokemonList) {
            if (pokemon.getName().toLowerCase().contains(name.toLowerCase())) {
                found.add(pokemon);
            }
        }

        if (found.isEmpty()) {
            System.out.println("❌ Nenhum Pokémon encontrado com o nome: " + name);
        } else {
            System.out.println("\n=== 🔍 POKÉMON ENCONTRADOS (" + found.size() + ") ===");
            for (PokemonData pokemon : found) {
                System.out.println(pokemon);
            }
        }
    }

    // READ - Buscar Pokémon por ID
    public PokemonData findPokemonById(int id) {
        for (PokemonData pokemon : pokemonList) {
            if (pokemon.getId() == id) {
                return pokemon;
            }
        }
        return null;
    }

    // Buscar Pokémon por índice na lista
    public PokemonData findPokemonByIndex(int index) {
        if (index >= 0 && index < pokemonList.size()) {
            return pokemonList.get(index);
        }
        return null;
    }

    // UPDATE - Atualizar Pokémon
    public void updatePokemon(int index, PokemonData updatedPokemon) {
        if (index >= 0 && index < pokemonList.size()) {
            pokemonList.set(index, updatedPokemon);
            saveToFile();
            System.out.println("✅ Pokémon atualizado com sucesso!");
        } else {
            System.out.println("❌ Índice inválido!");
        }
    }

    // DELETE - Remover Pokémon
    public void deletePokemon(int index) {
        if (index >= 0 && index < pokemonList.size()) {
            PokemonData removed = pokemonList.remove(index);
            saveToFile();
            System.out.println("🗑️ Pokémon removido: " + removed.getName());
        } else {
            System.out.println("❌ Índice inválido!");
        }
    }

    // Salvar em arquivo CSV
    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            // Escrever cabeçalho
            writer.println("id,name,type,height,weight,abilities,stats,description,spriteUrl,habitat,weaknesses,resistances,evolutionaryChain,dateAdded");
            
            // Escrever dados
            for (PokemonData pokemon : pokemonList) {
                writer.printf("%d,%s,%s,%.2f,%.2f,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    pokemon.getId(),
                    escapeCsv(pokemon.getName()),
                    escapeCsv(pokemon.getType()),
                    pokemon.getHeight(),
                    pokemon.getWeight(),
                    escapeCsv(pokemon.getAbilities()),
                    escapeCsv(pokemon.getStats()),
                    escapeCsv(pokemon.getDescription()),
                    escapeCsv(pokemon.getSpriteUrl()),
                    escapeCsv(pokemon.getHabitat()),
                    escapeCsv(String.join(";", pokemon.getWeaknesses())),
                    escapeCsv(String.join(";", pokemon.getResistances())),
                    escapeCsv(String.join(";", pokemon.getEvolutionaryChain())),
                    escapeCsv(pokemon.getDateAdded())
                );
            }
        } catch (IOException e) {
            System.out.println("❌ Erro ao salvar dados: " + e.getMessage());
        }
    }

    // Carregar do arquivo CSV
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("📂 Arquivo não encontrado. Criando novo banco de dados...");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            pokemonList.clear();
            String line = reader.readLine(); // Pular cabeçalho
            
            while ((line = reader.readLine()) != null) {
                String[] data = parseCsvLine(line);
                if (data.length >= 14) {
                    PokemonData pokemon = new PokemonData();
                    pokemon.setId(Integer.parseInt(data[0]));
                    pokemon.setName(unescapeCsv(data[1]));
                    pokemon.setType(unescapeCsv(data[2]));
                    pokemon.setHeight(Double.parseDouble(data[3]));
                    pokemon.setWeight(Double.parseDouble(data[4]));
                    pokemon.setAbilities(unescapeCsv(data[5]));
                    pokemon.setStats(unescapeCsv(data[6]));
                    pokemon.setDescription(unescapeCsv(data[7]));
                    pokemon.setSpriteUrl(unescapeCsv(data[8]));
                    pokemon.setHabitat(unescapeCsv(data[9]));
                    
                    // Processar listas
                    if (!data[10].isEmpty()) {
                        String[] weaknesses = data[10].split(";");
                        for (String weakness : weaknesses) {
                            pokemon.getWeaknesses().add(unescapeCsv(weakness));
                        }
                    }
                    
                    if (!data[11].isEmpty()) {
                        String[] resistances = data[11].split(";");
                        for (String resistance : resistances) {
                            pokemon.getResistances().add(unescapeCsv(resistance));
                        }
                    }
                    
                    if (!data[12].isEmpty()) {
                        String[] evolutions = data[12].split(";");
                        for (String evolution : evolutions) {
                            pokemon.getEvolutionaryChain().add(unescapeCsv(evolution));
                        }
                    }
                    
                    pokemon.setDateAdded(unescapeCsv(data[13]));
                    pokemonList.add(pokemon);
                }
            }
            System.out.println("📂 " + pokemonList.size() + " Pokémon(s) carregado(s) do banco de dados.");
        } catch (IOException e) {
            System.out.println("❌ Erro ao carregar dados: " + e.getMessage());
        }
    }

    // Métodos auxiliares para CSV
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String unescapeCsv(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString());
        return result.toArray(new String[0]);
    }

    public List<PokemonData> getPokemonList() {
        return pokemonList;
    }

    public int getTotalPokemon() {
        return pokemonList.size();
    }
}