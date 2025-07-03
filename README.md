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

---

## 🚀 Como Executar o Projeto

### 🔧 Requisitos

* Java JDK 17 ou superior
* Apache Maven 3.6+
* MySQL Server 8.0+
* Python 3.x (para popular o banco de dados)
* Git

### 🧪 Passos para Configuração

**1. Clone o Repositório**
```bash
# Clone este repositório para a sua máquina local
git clone [https://github.com/seu-usuario/StarMovie.git](https://github.com/seu-usuario/StarMovie.git)
cd StarMovie
```

**2. Configure o Banco de Dados MySQL**

O projeto precisa de um banco de dados MySQL para funcionar. Os scripts para criar e popular o banco estão na pasta `/database`.

```bash
# Navegue até a pasta de scripts do banco de dados
cd database

# Execute o script de criação da estrutura (tabelas e usuário)
# Você precisará da sua senha de root do MySQL.
mysql -u root -p < setup.sql

# (Opcional, mas recomendado) Execute o script Python para popular o banco com filmes.
# Pode ser necessário instalar as dependências do script primeiro (ex: pip install requests mysql-connector-python)
python popular_db_starmovie.py
```

**3. Configure as Variáveis de Ambiente da Aplicação**

A aplicação Spring Boot precisa saber como se conectar ao banco de dados que você acabou de criar.

* Abra o arquivo: `src/main/resources/application.properties`
* Verifique se as credenciais correspondem ao que foi definido no `setup.sql` ou à sua configuração local.

```properties
# Exemplo de configuração no application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/star_movie_final
spring.datasource.username=star_platinum
spring.datasource.password=20103050
spring.jpa.hibernate.ddl-auto=update
```

**4. Execute a Aplicação**

Com o banco de dados pronto e configurado, você pode iniciar a aplicação Spring Boot.

```bash
# Volte para a raiz do projeto
cd ..

# Use o Maven para compilar e executar o projeto
mvn spring-boot:run
```

**5. Acesse a Aplicação**

Pronto! Abra seu navegador e acesse: [http://localhost:8080](http://localhost:8080)

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
