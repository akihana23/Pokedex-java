package pokemon;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class PokemonMenu {
    private PokemonCRUD crud;
    private Scanner scanner;

    public PokemonMenu() {
        this.crud = new PokemonCRUD();
        this.scanner = new Scanner(System.in);
    }

    private void viewCompleteDetails() {
        crud.listAllPokemon();
        if (crud.getPokemonList().isEmpty()) return;

        try {
            System.out.print("\n🔍 Digite o número do Pokémon para ver detalhes completos: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine(); // CORREÇÃO: nextLine() em vez de nextInt()

            if (index >= 0 && index < crud.getPokemonList().size()) {
                PokemonData pokemon = crud.getPokemonList().get(index);
                System.out.println(pokemon.getDetailedInfo());
            } else {
                System.out.println("❌ Índice inválido!");
            }
        } catch (Exception e) {
            System.out.println("❌ Entrada inválida!");
            scanner.nextLine();
        }
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== 🎮 MENU POKÉDEX CRUD ===");
            System.out.println("=== 📊 Pokémon Salvos: " + crud.getTotalPokemon() + " ===");
            System.out.println("=".repeat(50));
            System.out.println("1. 🔍 Buscar Pokémon na API");
            System.out.println("2. 💾 Salvar Pokémon da API no Banco");
            System.out.println("3. 📝 Adicionar Pokémon Manualmente");
            System.out.println("4. 📋 Listar todos os Pokémon Salvos");
            System.out.println("5. 🔎 Buscar Pokémon por Nome");
            System.out.println("6. ✏️ Atualizar Pokémon");
            System.out.println("7. 🗑️ Remover Pokémon");
            System.out.println("8. 📊 Estatísticas do Banco");
            System.out.println("9. 📖 Ver Detalhes Completos de um Pokémon");
            System.out.println("0. 🚪 Sair");
            System.out.print("Escolha uma opção: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); // Limpar buffer

                switch (option) {
                    case 1:
                        searchPokemonAPI();
                        break;
                    case 2:
                        savePokemonFromAPI();
                        break;
                    case 3:
                        addPokemonManual();
                        break;
                    case 4:
                        crud.listAllPokemon();
                        break;
                    case 5:
                        searchPokemonByName();
                        break;
                    case 6:
                        updatePokemon();
                        break;
                    case 7:
                        deletePokemon();
                        break;
                    case 8:
                        showStatistics();
                        break;
                    case 9:
                        viewCompleteDetails();
                        break;    
                    case 0:
                        System.out.println("👋 Saindo... Até mais!");
                        return;
                    default:
                        System.out.println("❌ Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("❌ Entrada inválida! Digite um número.");
                scanner.nextLine(); // Limpar buffer inválido
            }
        }
    }

    private void searchPokemonAPI() {
        System.out.print("\n🔍 Digite o nome ou ID do Pokémon: ");
        String searchTerm = scanner.nextLine();
        
        System.out.println("🌐 Buscando na PokeAPI...");
        PokemonData pokemon = PokemonAPIIntegration.getPokemonFromAPI(searchTerm);
        
        if (pokemon != null) {
            PokemonAPIIntegration.displayPokemonDetails(pokemon);
            
            System.out.print("\n💾 Deseja salvar este Pokémon no banco? (s/n): ");
            String save = scanner.nextLine();
            if (save.equalsIgnoreCase("s")) {
                crud.addPokemon(pokemon);
            }
        }
    }

    private void savePokemonFromAPI() {
        System.out.print("\n💾 Digite o nome ou ID do Pokémon para salvar: ");
        String searchTerm = scanner.nextLine();
        
        System.out.println("🌐 Buscando e salvando...");
        PokemonData pokemon = PokemonAPIIntegration.getPokemonFromAPI(searchTerm);
        
        if (pokemon != null) {
            crud.addPokemon(pokemon);
        }
    }

    private void addPokemonManual() {
        System.out.println("\n📝 === ADICIONAR POKÉMON MANUAL ===");
        
        try {
            System.out.print("ID: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Nome: ");
            String name = scanner.nextLine();
            
            System.out.print("Tipo (ex: Fire, Water): ");
            String type = scanner.nextLine();
            
            System.out.print("Altura: ");
            int height = scanner.nextInt();
            
            System.out.print("Peso: ");
            int weight = scanner.nextInt();
            scanner.nextLine();
            
            System.out.print("Habilidades (separadas por vírgula): ");
            String abilities = scanner.nextLine();
            
            System.out.print("Stats (ex: hp: 45, attack: 49): ");
            String stats = scanner.nextLine();

            System.out.print("Descrição: ");
            String description = scanner.nextLine();
            
            System.out.print("Sprite URL: ");
            String spriteUrl = scanner.nextLine();
            
            System.out.print("Habitat: ");
            String habitat = scanner.nextLine();

            // Criar listas vazias para os novos campos
            List<String> weaknesses = new ArrayList<>();
            List<String> resistances = new ArrayList<>();
            List<String> evolutionaryChain = new ArrayList<>();

            // Usar o construtor COMPLETO
            PokemonData pokemon = new PokemonData(id, name, type, height, weight, 
                                                abilities, stats, description, spriteUrl,
                                                habitat, weaknesses, resistances, evolutionaryChain);
            crud.addPokemon(pokemon);
        } catch (Exception e) {
            System.out.println("❌ Erro nos dados informados!");
            scanner.nextLine();
        }
    }

    private void searchPokemonByName() {
        System.out.print("\n🔎 Digite o nome do Pokémon: ");
        String name = scanner.nextLine();
        crud.findPokemonByName(name);
    }

    private void updatePokemon() {
        crud.listAllPokemon();
        if (crud.getPokemonList().isEmpty()) return;

        try {
            System.out.print("\n✏️ Digite o número do Pokémon para atualizar: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            if (index >= 0 && index < crud.getPokemonList().size()) {
                PokemonData current = crud.getPokemonList().get(index);
                
                System.out.println("Atualizando: " + current);
                System.out.println("Deixe em branco para manter o valor atual:");

                System.out.print("Nome [" + current.getName() + "]: ");
                String name = scanner.nextLine();
                if (name.isEmpty()) name = current.getName();

                System.out.print("Tipo [" + current.getType() + "]: ");
                String type = scanner.nextLine();
                if (type.isEmpty()) type = current.getType();

                System.out.print("Altura [" + current.getHeight() + "]: ");
                String heightStr = scanner.nextLine();
                int height = heightStr.isEmpty() ? current.getHeight() : Integer.parseInt(heightStr);

                System.out.print("Peso [" + current.getWeight() + "]: ");
                String weightStr = scanner.nextLine();
                int weight = weightStr.isEmpty() ? current.getWeight() : Integer.parseInt(weightStr);

                System.out.print("Habilidades [" + current.getAbilities() + "]: ");
                String abilities = scanner.nextLine();
                if (abilities.isEmpty()) abilities = current.getAbilities();

                System.out.print("Stats [" + current.getStats() + "]: ");
                String stats = scanner.nextLine();
                if (stats.isEmpty()) stats = current.getStats();

                System.out.print("Descrição [" + current.getDescription() + "]: ");
                String description = scanner.nextLine();
                if (description.isEmpty()) description = current.getDescription();

                System.out.print("Sprite URL [" + current.getSpriteUrl() + "]: ");
                String spriteUrl = scanner.nextLine();
                if (spriteUrl.isEmpty()) spriteUrl = current.getSpriteUrl();

                System.out.print("Habitat [" + current.getHabitat() + "]: ");
                String habitat = scanner.nextLine();
                if (habitat.isEmpty()) habitat = current.getHabitat();

                // Usar o construtor COMPLETO
                PokemonData updated = new PokemonData(current.getId(), name, type, height, weight,
                                                    abilities, stats, description, spriteUrl,
                                                    habitat, current.getWeaknesses(), 
                                                    current.getResistances(), current.getEvolutionaryChain());
                crud.updatePokemon(index, updated);
            } else {
                System.out.println("❌ Índice inválido!");
            }
        } catch (Exception e) {
            System.out.println("❌ Erro na entrada de dados!");
            scanner.nextLine();
        }
    }

    private void deletePokemon() {
        crud.listAllPokemon();
        if (crud.getPokemonList().isEmpty()) return;

        try {
            System.out.print("\n🗑️ Digite o número do Pokémon para remover: ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine();

            System.out.print("⚠️ Tem certeza que deseja remover? (s/n): ");
            String confirm = scanner.nextLine();
            
            if (confirm.equalsIgnoreCase("s")) {
                crud.deletePokemon(index);
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (Exception e) {
            System.out.println("❌ Índice inválido!");
            scanner.nextLine();
        }
    }

    private void showStatistics() {
        System.out.println("\n📊 === ESTATÍSTICAS DO BANCO ===");
        System.out.println("Total de Pokémon: " + crud.getTotalPokemon());
        
        if (!crud.getPokemonList().isEmpty()) {
            System.out.println("\nÚltimos Pokémon adicionados:");
            int count = Math.min(5, crud.getTotalPokemon());
            for (int i = 0; i < count; i++) {
                PokemonData pokemon = crud.getPokemonList().get(crud.getTotalPokemon() - 1 - i);
                System.out.println("  • " + pokemon.toShortString());
            }
        }
    }
}