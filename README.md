# 🌟 StarMovie

**StarMovie** é um sistema fullstack para exibição e gerenciamento de filmes, inspirado em plataformas como IMDb. Ele oferece uma interface moderna desenvolvida em **SpringBoot**, integrando funcionalidades de **favoritos**, **filmes populares** e **busca** com um **carrossel interativo de imagens**.

## 🧩 Funcionalidades

- 🎬 Exibição de filmes com poster, título, descrição e avaliação
- ⭐ Marcar filmes como favoritos
- 🔥 Listar filmes populares da semana
- 🔎 Barra de pesquisa para buscar títulos
- 🎠 Carrossel de imagens para navegação dinâmica
- 👤 Perfil do usuário e Watchlist
- 🌐 Integração com back-end e banco de dados

---

## 🛠️ Tecnologias Utilizadas

| Camada        | Tecnologia                        |
|---------------|-----------------------------------|
| Front-end     | SpringBoot-WEB                    |
| Back-end      | Java                              |
| Banco de Dados| MySQL                             |
| Versionamento | Git + GitHub                      |

> \* O back-end e banco ainda podem ser ajustados conforme o andamento do projeto.

---

## 🚀 Como executar o projeto

### 🔧 Requisitos

- Java 17 ou superior
- Maven ou Gradle
- Git
- PostgreSQL ou MySQL (dependendo da implementação)

### 🧪 Passos iniciais

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/StarMovie.git
cd StarMovie

# Compile o projeto (se estiver usando Maven)
mvn clean install

# Execute a aplicação JavaFX
mvn javafx:run
```

> Configure as variáveis de ambiente e banco de dados conforme necessário no `application.properties` ou `.env`.

---

## 📁 Estrutura do Projeto

```
StarMovie/
│
├── frontend/           # SpringBoot interface
├── backend/            # API de filmes
├── database/           # Scripts SQL / modelo ER
├── README.md
└── .gitignore
```

---



## 👨‍💻 Autor

**Leonardo Freitas dos Santos**  
📚 Eng. da Computação - UTFPR  
📬 [linkedin.com/in/leonardofs](https://linkedin.com/in/leonardofs)

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
