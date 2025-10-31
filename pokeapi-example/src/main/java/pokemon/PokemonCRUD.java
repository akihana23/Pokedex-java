package pokemon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PokemonCRUD {
    private static final String FILE_NAME = "pokemon_database.dat";
    private List<PokemonData> pokemonList;
    private Scanner scanner;

    public PokemonCRUD() {
        this.pokemonList = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        loadFromFile();
    }

    // CREATE - Adicionar novo Pokémon
    public void addPokemon(PokemonData pokemon) {
        // Verificar se já existe um Pokémon com mesmo ID
        for (PokemonData existing : pokemonList) {
            if (existing.getId() == pokemon.getId()) {
                System.out.print("Já existe um Pokémon com ID " + pokemon.getId() + ". Deseja substituir? (s/n): ");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("s")) {
                    pokemonList.remove(existing);
                    break;
                } else {
                    System.out.println("Operação cancelada.");
                    return;
                }
            }
        }
        
        pokemonList.add(pokemon);
        saveToFile();
        System.out.println("✅ Pokémon adicionado com sucesso!");
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

    // Salvar em arquivo
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(pokemonList);
        } catch (IOException e) {
            System.out.println("❌ Erro ao salvar dados: " + e.getMessage());
        }
    }

    // Carregar do arquivo
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            pokemonList = (List<PokemonData>) ois.readObject();
            System.out.println("📂 " + pokemonList.size() + " Pokémon(s) carregado(s) do banco de dados.");
        } catch (FileNotFoundException e) {
            System.out.println("📂 Arquivo não encontrado. Criando novo banco de dados...");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Erro ao carregar dados: " + e.getMessage());
        }
    }

    public List<PokemonData> getPokemonList() {
        return pokemonList;
    }

    public int getTotalPokemon() {
        return pokemonList.size();
    }
}