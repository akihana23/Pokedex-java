# Pokedex Java - CRUD Mobile e Desktop

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-ED8B00?style=for-the-badge&logo=java&logoColor=white)

Um aplicativo de Pokedex desenvolvido em Java para demonstrar operações CRUD (Create, Read, Update, Delete) em interfaces mobile e desktop, como projeto para a disciplina de Comunicação Humano Computador.

## 📋 Descrição do Projeto

Este projeto consiste em uma Pokedex digital que permite aos usuários:
- Visualizar informações sobre Pokémon
- Adicionar novos Pokémon
- Editar informações existentes
- Excluir Pokémon
- Pesquisar e filtrar Pokémon

O sistema é desenvolvido para funcionar tanto em dispositivos móveis (Android) quanto em desktop, demonstrando princípios de usabilidade e experiência do usuário.

## 🚀 Funcionalidades

### CRUD Completo
- **Create**: Adicionar novos Pokémon à Pokedex
- **Read**: Visualizar lista e detalhes dos Pokémon
- **Update**: Editar informações dos Pokémon existentes
- **Delete**: Remover Pokémon da Pokedex

### Interfaces
- **Desktop**: Interface gráfica usando Java Swing
- **Mobile**: Aplicativo Android com interface touch-friendly

### Funcionalidades Extras
- Pesquisa por nome e tipo
- Filtros por geração e região
- Sistema de favoritos
- Histórico de visualizações

## 🛠️ Tecnologias Utilizadas

- **Java 8+**
- **Swing** (Interface Desktop)
- **Android SDK** (Interface Mobile)
- **SQLite** (Banco de dados local)
- **Gradle** (Gerenciamento de dependências)

## 📁 Estrutura do Projeto
Pokedex-java/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ ├── model/ # Modelos de dados (Pokemon, etc.)
│ │ │ ├── dao/ # Data Access Objects
│ │ │ ├── service/ # Lógica de negócio
│ │ │ ├── desktop/ # Interface desktop (Swing)
│ │ │ └── mobile/ # Interface mobile (Android)
│ │ └── resources/ # Imagens, ícones, etc.
│ └── test/ # Testes unitários
├── docs/ # Documentação
├── README.md
└── build.gradle

text

## 🎯 Status do Projeto

### ✅ Concluído
- [x] Estrutura básica do projeto
- [x] Modelos de dados
- [ ] Implementação do CRUD
- [ ] Interface desktop (Swing)
- [ ] Interface mobile (Android)
- [ ] Integração com banco de dados
- [ ] Testes unitários

### 🚧 Em Desenvolvimento
- Interface visual
- Versão mobile

## 📱 Capturas de Tela

*(Adicione screenshots aqui quando o projeto estiver mais avançado)*

## 🏃‍♂️ Como Executar

### Pré-requisitos
- Java JDK 8 ou superior
- Android Studio (para versão mobile)
- Gradle

### Executando a versão Desktop
```bash
git clone https://github.com/akihana23/Pokedex-java.git
cd Pokedex-java
./gradlew run
Executando a versão Mobile
bash
# Conecte um dispositivo Android ou use um emulador
./gradlew installDebug
📚 Documentação
Princípios de CHC Aplicados
Usabilidade: Interface intuitiva e de fácil aprendizado

Acessibilidade: Design que considera diferentes usuários

Experiência do Usuário: Fluxos bem definidos e feedback visual

Consistência: Padrões visuais mantidos entre mobile e desktop

Diagramas
(Adicione diagramas UML, fluxos de usuário, etc.)

🤝 Contribuindo
Contribuições são bem-vindas! Para contribuir:

Fork o projeto

Crie uma branch para sua feature (git checkout -b feature/AmazingFeature)

Commit suas mudanças (git commit -m 'Add some AmazingFeature')

Push para a branch (git push origin feature/AmazingFeature)

Abra um Pull Request

📄 Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

👥 Autores
Seu Nome - akihana23

🙏 Agradecimentos
Professor(a) e colegas da disciplina de Comunicação Humano Computador

Comunidade Pokémon

Contribuidores do projeto
