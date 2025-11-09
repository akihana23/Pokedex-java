package pokemon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PokemonGUI extends JFrame {
    private PokemonCRUD crud;
    private JTable pokemonTable;
    private DefaultTableModel tableModel;
    private JLabel imageLabel;

    public PokemonGUI() {
        crud = new PokemonCRUD();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Pokedex CRUD - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Abas
        tabbedPane.addTab("Buscar na API", createSearchAPIPanel());
        tabbedPane.addTab("Pokémon Salvos", createSavedPokemonPanel());
        tabbedPane.addTab("Adicionar Manual", createManualAddPanel());
        tabbedPane.addTab("Editar Pokémon", createEditPanel());

        add(tabbedPane);
    }

    private JPanel createSearchAPIPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Buscar");
        JTextArea resultArea = new JTextArea(15, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));

        topPanel.add(new JLabel("Nome ou ID:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        resultPanel.add(imageLabel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText().trim();
                if (searchTerm.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Digite um nome ou ID", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PokemonData pokemon = PokemonAPIIntegration.getPokemonFromAPI(searchTerm);
                if (pokemon != null) {
                    resultArea.setText(pokemon.getDetailedInfo());
                    loadPokemonImage(pokemon.getSpriteUrl());

                    int option = JOptionPane.showConfirmDialog(panel, "Deseja salvar este Pokémon?", "Salvar", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        crud.addPokemon(pokemon);
                        JOptionPane.showMessageDialog(panel, "Pokémon salvo com sucesso!");
                    }
                } else {
                    resultArea.setText("");
                    imageLabel.setIcon(null);
                    imageLabel.setText("Imagem não disponível");
                    JOptionPane.showMessageDialog(panel, "Pokémon não encontrado", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createSavedPokemonPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Nome", "Tipo", "Altura (m)", "Peso (kg)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        pokemonTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(pokemonTable);

        JPanel detailPanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 200));
        JTextArea detailArea = new JTextArea(10, 30);
        detailArea.setEditable(false);
        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        detailPanel.add(imageLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Atualizar Lista");
        JButton deleteButton = new JButton("Remover Selecionado");
        JButton viewDetailsButton = new JButton("Ver Detalhes");

        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewDetailsButton);

        panel.add(tableScroll, BorderLayout.NORTH);
        panel.add(detailPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshTable());

        deleteButton.addActionListener(e -> {
            int selectedRow = pokemonTable.getSelectedRow();
            if (selectedRow >= 0) {
                int option = JOptionPane.showConfirmDialog(panel, "Tem certeza que deseja remover?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    int index = -1;
                    List<PokemonData> list = crud.getPokemonList();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId() == id) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        crud.deletePokemon(index);
                        refreshTable();
                        detailArea.setText("");
                        imageLabel.setIcon(null);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione um Pokémon para remover", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = pokemonTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                PokemonData pokemon = crud.findPokemonById(id);
                if (pokemon != null) {
                    detailArea.setText(pokemon.getDetailedInfo());
                    loadPokemonImage(pokemon.getSpriteUrl());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione um Pokémon para ver os detalhes", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        refreshTable();
        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabela para selecionar Pokémon para editar
        String[] columnNames = {"ID", "Nome", "Tipo", "Altura (m)", "Peso (kg)"};
        DefaultTableModel editTableModel = new DefaultTableModel(columnNames, 0);
        JTable editTable = new JTable(editTableModel);
        JScrollPane tableScroll = new JScrollPane(editTable);

        // Formulário de edição
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField abilitiesField = new JTextField();
        JTextField statsField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField spriteUrlField = new JTextField();
        JTextField habitatField = new JTextField();

        idField.setEditable(false);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Nome:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Tipo:"));
        formPanel.add(typeField);
        formPanel.add(new JLabel("Altura (m):"));
        formPanel.add(heightField);
        formPanel.add(new JLabel("Peso (kg):"));
        formPanel.add(weightField);
        formPanel.add(new JLabel("Habilidades:"));
        formPanel.add(abilitiesField);
        formPanel.add(new JLabel("Stats:"));
        formPanel.add(statsField);
        formPanel.add(new JLabel("Descrição:"));
        formPanel.add(descriptionField);
        formPanel.add(new JLabel("Sprite URL:"));
        formPanel.add(spriteUrlField);
        formPanel.add(new JLabel("Habitat:"));
        formPanel.add(habitatField);

        JButton loadButton = new JButton("Carregar para Editar");
        JButton saveEditButton = new JButton("Salvar Alterações");

        formPanel.add(loadButton);
        formPanel.add(saveEditButton);

        JPanel buttonPanel = new JPanel();
        JButton refreshEditButton = new JButton("Atualizar Lista");
        buttonPanel.add(refreshEditButton);

        panel.add(tableScroll, BorderLayout.NORTH);
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Atualizar tabela de edição
        Runnable refreshEditTable = () -> {
            editTableModel.setRowCount(0);
            List<PokemonData> pokemonList = crud.getPokemonList();
            for (PokemonData p : pokemonList) {
                Object[] row = {p.getId(), p.getName(), p.getType(), p.getHeight(), p.getWeight()};
                editTableModel.addRow(row);
            }
        };

        // Carregar Pokémon para edição
        loadButton.addActionListener(e -> {
            int selectedRow = editTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) editTableModel.getValueAt(selectedRow, 0);
                PokemonData pokemon = crud.findPokemonById(id);
                if (pokemon != null) {
                    idField.setText(String.valueOf(pokemon.getId()));
                    nameField.setText(pokemon.getName());
                    typeField.setText(pokemon.getType());
                    heightField.setText(String.format("%.2f", pokemon.getHeight()));
                    weightField.setText(String.format("%.2f", pokemon.getWeight()));
                    abilitiesField.setText(pokemon.getAbilities());
                    statsField.setText(pokemon.getStats());
                    descriptionField.setText(pokemon.getDescription());
                    spriteUrlField.setText(pokemon.getSpriteUrl());
                    habitatField.setText(pokemon.getHabitat());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione um Pokémon para editar", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Salvar edições
        saveEditButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String type = typeField.getText();
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                String abilities = abilitiesField.getText();
                String stats = statsField.getText();
                String description = descriptionField.getText();
                String spriteUrl = spriteUrlField.getText();
                String habitat = habitatField.getText();

                // Encontrar o índice do Pokémon
                int index = -1;
                List<PokemonData> list = crud.getPokemonList();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == id) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    // Manter as listas originais
                    PokemonData original = list.get(index);
                    List<String> weaknesses = original.getWeaknesses();
                    List<String> resistances = original.getResistances();
                    List<String> evolutionaryChain = original.getEvolutionaryChain();
                    String dateAdded = original.getDateAdded();

                    PokemonData updatedPokemon = new PokemonData(id, name, type, height, weight, 
                        abilities, stats, description, spriteUrl, habitat, 
                        weaknesses, resistances, evolutionaryChain);
                    
                    // Manter a data original
                    updatedPokemon.setDateAdded(dateAdded);
                    
                    crud.updatePokemon(index, updatedPokemon);
                    refreshEditTable.run();
                    JOptionPane.showMessageDialog(panel, "Pokémon atualizado com sucesso!");
                    
                    // Limpar campos
                    idField.setText("");
                    nameField.setText("");
                    typeField.setText("");
                    heightField.setText("");
                    weightField.setText("");
                    abilitiesField.setText("");
                    statsField.setText("");
                    descriptionField.setText("");
                    spriteUrlField.setText("");
                    habitatField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Erro nos dados informados! Verifique Altura e Peso.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshEditButton.addActionListener(e -> refreshEditTable.run());
        refreshEditTable.run();

        return panel;
    }

    private JPanel createManualAddPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField abilitiesField = new JTextField();
        JTextField statsField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField spriteUrlField = new JTextField();
        JTextField habitatField = new JTextField();

        panel.add(new JLabel("ID:"));
        panel.add(idField);
        panel.add(new JLabel("Nome:"));
        panel.add(nameField);
        panel.add(new JLabel("Tipo:"));
        panel.add(typeField);
        panel.add(new JLabel("Altura (m):"));
        panel.add(heightField);
        panel.add(new JLabel("Peso (kg):"));
        panel.add(weightField);
        panel.add(new JLabel("Habilidades:"));
        panel.add(abilitiesField);
        panel.add(new JLabel("Stats:"));
        panel.add(statsField);
        panel.add(new JLabel("Descrição:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Sprite URL:"));
        panel.add(spriteUrlField);
        panel.add(new JLabel("Habitat:"));
        panel.add(habitatField);

        JButton saveButton = new JButton("Salvar");
        panel.add(saveButton);
        panel.add(new JLabel());

        saveButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String type = typeField.getText();
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                String abilities = abilitiesField.getText();
                String stats = statsField.getText();
                String description = descriptionField.getText();
                String spriteUrl = spriteUrlField.getText();
                String habitat = habitatField.getText();

                List<String> weaknesses = new ArrayList<>();
                List<String> resistances = new ArrayList<>();
                List<String> evolutionaryChain = new ArrayList<>();

                PokemonData pokemon = new PokemonData(id, name, type, height, weight, abilities, stats, description, spriteUrl, habitat, weaknesses, resistances, evolutionaryChain);
                crud.addPokemon(pokemon);

                JOptionPane.showMessageDialog(panel, "Pokémon salvo com sucesso!");

                // Limpar campos
                idField.setText("");
                nameField.setText("");
                typeField.setText("");
                heightField.setText("");
                weightField.setText("");
                abilitiesField.setText("");
                statsField.setText("");
                descriptionField.setText("");
                spriteUrlField.setText("");
                habitatField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Erro nos dados informados! Verifique ID, Altura e Peso.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private void loadPokemonImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
            imageLabel.setText("");
        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Imagem não disponível");
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<PokemonData> pokemonList = crud.getPokemonList();
        for (PokemonData p : pokemonList) {
            Object[] row = {p.getId(), p.getName(), p.getType(), p.getHeight(), p.getWeight()};
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PokemonGUI().setVisible(true);
            }
        });
    }
}