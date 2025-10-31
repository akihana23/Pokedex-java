package pokemon;

public class App {
    public static void main(String[] args) {
        System.out.println("🎉 Bem-vindo ao Pokedex com CRUD!");
        System.out.println("💾 Sistema integrado com PokeAPI e banco de dados local");
        
        PokemonMenu menu = new PokemonMenu();
        menu.showMenu();
    }
}