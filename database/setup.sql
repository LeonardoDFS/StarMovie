-- =====================================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS E TABELAS PARA O PROJETO STARMOVIE
-- =====================================================================

-- Parte 1: Criação do Banco de Dados e Usuário
-- ---------------------------------------------------------------------

-- Apaga o banco de dados se ele já existir, para permitir uma recriação limpa.
DROP DATABASE IF EXISTS star_movie_final;

-- Cria o novo banco de dados com o conjunto de caracteres utf8mb4 para suportar emojis e caracteres especiais.
CREATE DATABASE star_movie_final CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Seleciona o banco de dados recém-criado para executar os comandos seguintes.
USE star_movie_final;

-- Cria um usuário para a aplicação. Substitua 'senha_forte_aqui' por uma senha segura.
-- Usamos 'star_platinum' e '20103050' como no seu exemplo de configuração de produção.
CREATE USER IF NOT EXISTS 'star_platinum'@'localhost' IDENTIFIED BY '20103050';

-- Concede todos os privilégios no banco 'star_movie_final' para o novo usuário.
GRANT ALL PRIVILEGES ON star_movie_final.* TO 'star-platinum'@'localhost';

-- Aplica as alterações de privilégios.
FLUSH PRIVILEGES;


-- Parte 2: Criação das Tabelas
-- ---------------------------------------------------------------------

-- Tabela de Usuários
CREATE TABLE IF NOT EXISTS `usuario` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `senha` VARCHAR(255) NOT NULL,
  `roles` VARCHAR(100) NOT NULL DEFAULT 'ROLE_USER',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- Tabela de Gêneros
-- O ID virá da API do TMDB, por isso não é AUTO_INCREMENT.
CREATE TABLE IF NOT EXISTS `genero` (
  `id` INT NOT NULL,
  `nome` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_genero_nome` (`nome`)
) ENGINE=InnoDB;

-- Tabela de Filmes
-- O ID virá da API do TMDB, por isso não é AUTO_INCREMENT.
CREATE TABLE IF NOT EXISTS `filme` (
  `id` INT NOT NULL,
  `nome` VARCHAR(255) NOT NULL,
  `sinopse` TEXT,
  `ano` INT,
  `data_lancamento` DATE,
  `poster_path` VARCHAR(255),
  `caminho_fundo` VARCHAR(255),
  `popularidade` DECIMAL(10, 4),
  `is_adult` BOOLEAN DEFAULT FALSE,
  `duracao` INT,
  `classificacao_indicativa` VARCHAR(10),
  `elenco_principal` TEXT,
  `diretor` VARCHAR(100),
  `idioma_original` VARCHAR(10),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- Tabela de Listas de Filmes (Ex: Favoritos)
CREATE TABLE IF NOT EXISTS `lista` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `usuario_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_lista_usuario_idx` (`usuario_id`),
  CONSTRAINT `fk_lista_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabela de Junção para Filme e Gênero (Muitos-para-Muitos)
CREATE TABLE IF NOT EXISTS `filmegenero` (
  `filme_id` INT NOT NULL,
  `genero_id` INT NOT NULL,
  PRIMARY KEY (`filme_id`, `genero_id`),
  KEY `fk_filmegenero_genero_idx` (`genero_id`),
  CONSTRAINT `fk_filmegenero_filme` FOREIGN KEY (`filme_id`) REFERENCES `filme` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_filmegenero_genero` FOREIGN KEY (`genero_id`) REFERENCES `genero` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabela de Junção para Lista e Filme (Muitos-para-Muitos)
CREATE TABLE IF NOT EXISTS `listafilme` (
  `lista_id` INT NOT NULL,
  `filme_id` INT NOT NULL,
  PRIMARY KEY (`lista_id`, `filme_id`),
  KEY `fk_listafilme_filme_idx` (`filme_id`),
  CONSTRAINT `fk_listafilme_lista` FOREIGN KEY (`lista_id`) REFERENCES `lista` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_listafilme_filme` FOREIGN KEY (`filme_id`) REFERENCES `filme` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabela para Notas e Comentários (Chave Primária Composta)
CREATE TABLE IF NOT EXISTS `notafil` (
  `usuario_id` INT NOT NULL,
  `filme_id` INT NOT NULL,
  `nota` INT,
  `comentario` TEXT,
  PRIMARY KEY (`usuario_id`, `filme_id`),
  KEY `fk_notafil_filme_idx` (`filme_id`),
  CONSTRAINT `fk_notafil_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notafil_filme` FOREIGN KEY (`filme_id`) REFERENCES `filme` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;