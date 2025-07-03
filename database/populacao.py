import requests
import pymysql
from datetime import datetime
import time
import logging

# --- CONFIGURAÇÃO DO LOGGER ---
logging.basicConfig(
    filename='movie_importer.log',
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)
logger = logging.getLogger(__name__)
# -----------------------------

# 1) CONFIGURAÇÕES
API_KEY = 'b559711fc404210ec60568ee633a1729' # Sua chave da API TMDB
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '12345678', # Sua senha do DB
    'db': 'star_movie_final', # Seu banco de dados
    'charset': 'utf8mb4'
}
BASE_URL = 'https://api.themoviedb.org/3'
ADULT_GENRE_ID = None
TOTAL_FILMES_PROCESSADOS = 0
TOTAL_FILMES_INSERIDOS = 0

# --- IDIOMAS A SEREM EXCLUÍDOS (ISO 639-1 codes) ---
EXCLUDED_LANGUAGES = ['zh', 'ko', 'ja'] # Chinês, Coreano, Japonês
# ----------------------------------------------------

# 2) FUNÇÃO PARA CHAMADAS GET À API TMDB
def tmdb_get(path, **params):
    params.update({'api_key': API_KEY, 'language': 'pt-BR', 'include_adult': 'false'})
    try:
        r = requests.get(f"{BASE_URL}{path}", params=params)
        r.raise_for_status()
        return r.json()
    except requests.exceptions.HTTPError as http_err:
        message = f"Erro HTTP: {http_err} - Path: {path}, Params: {params}"
        logger.error(message)
        print(message)
        if r.status_code == 429:
            logger.warning("Limite de requisições atingido. Aguardando 10 segundos...")
            print("Limite de requisições atingido. Aguardando 10 segundos...")
            time.sleep(10)
            return tmdb_get(path, **params)
        elif r.status_code == 401:
            logger.critical("Erro 401: Chave da API inválida ou problema de autenticação.")
            raise
        elif r.status_code == 404:
            logger.warning(f"Erro 404: Recurso não encontrado - Path: {path}")
            return None
    except requests.exceptions.RequestException as req_err:
        message = f"Erro na requisição: {req_err} - Path: {path}, Params: {params}"
        logger.error(message)
        print(message)
    except Exception as e:
        message = f"Erro inesperado em tmdb_get: {e} - Path: {path}, Params: {params}"
        logger.exception(message)
        print(message)
    return None

# 3) CONEXÃO COM O BANCO DE DADOS
try:
    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor()
    logger.info("Conexão com o banco de dados estabelecida.")
except pymysql.Error as db_conn_err:
    logger.critical(f"Erro ao conectar ao banco de dados: {db_conn_err}")
    print(f"Erro CRÍTICO ao conectar ao banco de dados: {db_conn_err}. Verifique o arquivo de log.")
    exit()

# 4) POPULANDO GÊNEROS
def popular_generos():
    global ADULT_GENRE_ID
    print("Populando gêneros...")
    logger.info("Iniciando população de gêneros.")
    data = tmdb_get('/genre/movie/list')
    if data and 'genres' in data:
        for g in data['genres']:
            try:
                cur.execute("INSERT IGNORE INTO genero (id, nome) VALUES (%s, %s)", (g['id'], g['name']))
                if g['name'].lower() in ['adulto', 'adult']:
                     ADULT_GENRE_ID = g['id']
                     logger.info(f"ID do gênero 'Adulto' identificado: {ADULT_GENRE_ID}")
            except pymysql.Error as e:
                logger.error(f"Erro ao inserir gênero {g.get('name', 'N/A')}: {e}")
        conn.commit()
        logger.info("Gêneros populados.")
        print("Gêneros populados.")
    else:
        logger.warning("Falha ao buscar gêneros da API TMDB.")
        print("Falha ao buscar gêneros da API TMDB.")

# FUNÇÃO AUXILIAR PARA OBTER DETALHES COMPLETOS DO FILME
def get_movie_details(movie_id):
    logger.debug(f"Buscando detalhes completos para o filme ID: {movie_id}...")
    details = tmdb_get(f'/movie/{movie_id}', append_to_response='credits,keywords')
    time.sleep(0.3) # Ajuste o delay conforme necessário para respeitar o rate limit
    return details

# 5) POPULANDO FILMES (ESTRATÉGIA MISTA) E RELAÇÃO GÊNERO
def popular_filmes_estrategia(num_pages_popular=30, num_pages_underground=30, num_pages_critics=10,
                               start_page_popular_override=None,
                               start_page_underground_override=None,
                               start_page_critics_override=None):
    global TOTAL_FILMES_PROCESSADOS, TOTAL_FILMES_INSERIDOS
    endpoints = {
        'popular': { 'path': '/movie/popular', 'params': {}, 'pages_to_fetch': num_pages_popular, 'id_controle': 1, 'start_override': start_page_popular_override },
        'discover_underground': { 'path': '/discover/movie', 'params': {'sort_by': 'popularity.asc', 'vote_count.lte': 150, 'vote_average.gte': 4}, 'pages_to_fetch': num_pages_underground, 'id_controle': 2, 'start_override': start_page_underground_override },
        'discover_critics_darling': { 'path': '/discover/movie', 'params': {'sort_by': 'vote_average.desc', 'vote_count.gte': 100, 'vote_count.lte': 1000, 'primary_release_date.lte': datetime.now().strftime('%Y-%m-%d')}, 'pages_to_fetch': num_pages_critics, 'id_controle': 3, 'start_override': start_page_critics_override }
    }

    for key, config in endpoints.items():
        msg_inicio_estrategia = f"--- Iniciando população para estratégia: {key} ---"
        print(f"\n{msg_inicio_estrategia}")
        logger.info(msg_inicio_estrategia)
        
        start_page = 0
        if config['start_override'] is not None:
            start_page = config['start_override'] - 1
            logger.info(f"Override da página inicial para {key}: {config['start_override']}")
        else:
            try:
                cur.execute("SELECT ultima_pagina FROM controle_paginacao WHERE endpoint_tipo = %s", (key,))
                result = cur.fetchone()
                if result: start_page = result[0]
                else:
                    cur.execute("INSERT IGNORE INTO controle_paginacao (id, endpoint_tipo, ultima_pagina) VALUES (%s, %s, 0)", (config['id_controle'], key))
                    conn.commit()
                    logger.info(f"Entrada de controle criada para {key} com página inicial 0.")
                    start_page = 0
            except pymysql.Error as e:
                logger.error(f"Erro ao acessar controle_paginacao para {key}: {e}. Iniciando da página 0.")
                start_page = 0

        print(f"Estratégia '{key}': Começando da página API {start_page + 1}.")
        logger.info(f"Estratégia '{key}': Começando da página API {start_page + 1}.")

        for page_offset in range(1, config['pages_to_fetch'] + 1):
            current_api_page = start_page + page_offset
            msg_pagina_api = f"Buscando {key} - Página API: {current_api_page}"
            print(msg_pagina_api)
            logger.info(msg_pagina_api)
            
            page_data = tmdb_get(config['path'], page=current_api_page, **config['params'])
            
            if not page_data or not page_data.get('results'):
                logger.warning(f"Sem resultados ou erro ao buscar página {current_api_page} para {key}.")
                if page_data and page_data.get('total_pages', 0) < current_api_page :
                    logger.info(f"Atingido o total de páginas disponíveis ({page_data.get('total_pages', 0)}) para {key}.")
                break 

            movies_inserted_in_page = 0
            for m_summary in page_data['results']:
                TOTAL_FILMES_PROCESSADOS += 1
                movie_id = m_summary['id']
                movie_title_log = m_summary.get('title', 'Título Desconhecido')

                # --- VERIFICAÇÃO DE IDIOMA ORIGINAL ---
                original_lang = m_summary.get('original_language')
                if original_lang in EXCLUDED_LANGUAGES:
                    logger.info(f"Filme ID {movie_id} ('{movie_title_log}') pulado: idioma original '{original_lang}' está na lista de exclusão.")
                    continue # Pula para o próximo filme
                # ---------------------------------------

                if m_summary.get('adult', False):
                    logger.info(f"Filme ID {movie_id} ('{movie_title_log}') pulado: marcado como adulto pela API.")
                    continue
                release_date_str = m_summary.get('release_date')
                if not release_date_str:
                    logger.info(f"Filme ID {movie_id} ('{movie_title_log}') pulado: data de lançamento ausente.")
                    continue
                try:
                    year = datetime.strptime(release_date_str, '%Y-%m-%d').year
                except ValueError:
                    logger.warning(f"Filme ID {movie_id} ('{movie_title_log}') pulado: data de lançamento inválida ('{release_date_str}').")
                    continue
                if ADULT_GENRE_ID and ADULT_GENRE_ID in m_summary.get('genre_ids', []):
                    logger.info(f"Filme ID {movie_id} ('{movie_title_log}') pulado: pertence ao gênero adulto ID {ADULT_GENRE_ID}.")
                    continue
                
                m_details = get_movie_details(movie_id)
                if not m_details:
                    logger.warning(f"Não foi possível obter detalhes para o filme ID {movie_id} ('{movie_title_log}'). Pulando.")
                    continue
                
                # Se a língua original não estiver no summary, checar nos detalhes (redundante se já checado antes, mas garante)
                # No entanto, original_language costuma vir no summary. Esta checagem adicional pode ser opcional.
                # original_lang_detail = m_details.get('original_language')
                # if original_lang_detail in EXCLUDED_LANGUAGES:
                # logger.info(f"Filme ID {movie_id} ('{movie_title_log}') pulado (verificado nos detalhes): idioma original '{original_lang_detail}' está na lista de exclusão.")
                # continue

                movie_title_detail = m_details.get('title', 'Título Desconhecido')
                diretor_nome = None
                if m_details.get('credits') and m_details['credits'].get('crew'):
                    for crew_member in m_details['credits']['crew']:
                        if crew_member.get('job', '').lower() == 'director':
                            diretor_nome = crew_member.get('name')
                            break
                try:
                    cur.execute("""
                        INSERT IGNORE INTO filme
                        (id, nome, sinopse, poster_path, caminho_fundo, ano, popularidade, data_lancamento,
                         diretor, duracao, tagline, status, idioma_original, budget, revenue, imdb_id)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    """, (
                        m_details['id'], movie_title_detail, m_details.get('overview'),
                        m_details.get('poster_path'), m_details.get('backdrop_path'),
                        year, m_details.get('popularity'), release_date_str,
                        diretor_nome,
                        m_details.get('runtime'), # API TMDB usa 'runtime'
                        m_details.get('tagline'),
                        m_details.get('status'),
                        m_details.get('original_language'), # API TMDB usa 'original_language'
                        m_details.get('budget'), m_details.get('revenue'), m_details.get('imdb_id')
                    ))
                    
                    if cur.rowcount > 0:
                        TOTAL_FILMES_INSERIDOS += 1
                        logger.info(f"Filme '{movie_title_detail}' (ID: {movie_id}) inserido.")
                        movies_inserted_in_page +=1
                        for g_detail in m_details.get('genres', []):
                            cur.execute("INSERT IGNORE INTO filmegenero (filme_id, genero_id) VALUES (%s, %s)", (m_details['id'], g_detail['id']))
                    else:
                        logger.info(f"Filme '{movie_title_detail}' (ID: {movie_id}) já existe no banco.")

                except pymysql.Error as db_err:
                    logger.error(f"Erro de BD ao inserir filme ID {movie_id} ('{movie_title_detail}'): {db_err}")
                    conn.rollback()
                except Exception as e_proc:
                    logger.exception(f"Erro inesperado ao processar filme ID {movie_id} ('{movie_title_detail}'): {e_proc}")
                    conn.rollback()
                
                if TOTAL_FILMES_PROCESSADOS % 100 == 0:
                    print(f"Progresso: {TOTAL_FILMES_PROCESSADOS} filmes verificados, {TOTAL_FILMES_INSERIDOS} novos filmes inseridos no total.")
                    logger.info(f"Progresso: {TOTAL_FILMES_PROCESSADOS} filmes verificados, {TOTAL_FILMES_INSERIDOS} novos filmes inseridos no total.")

            conn.commit()
            if movies_inserted_in_page > 0 or (page_data and not page_data.get('results') and current_api_page >= page_data.get('total_pages',0)):
                try:
                    cur.execute("UPDATE controle_paginacao SET ultima_pagina = %s WHERE endpoint_tipo = %s", (current_api_page, key))
                    conn.commit()
                    logger.info(f"Controle de paginação para '{key}' atualizado para página {current_api_page}.")
                except pymysql.Error as e:
                    logger.error(f"Erro ao atualizar controle_paginacao para {key}: {e}")
            else:
                 logger.info(f"Nenhum filme novo inserido da página {current_api_page} para {key}. Controle de paginação não atualizado para esta página.")
                 if not page_data or not page_data.get('results'):
                     logger.info(f"Encerrando busca para '{key}' pois não houve resultados na página {current_api_page}.")
                     break
            if page_offset < config['pages_to_fetch']:
                logger.debug(f"Pausa de 2 segundos antes da próxima página de {key}...")
                time.sleep(2)

        msg_fim_estrategia = f"--- População para estratégia '{key}' concluída ---"
        print(msg_fim_estrategia)
        logger.info(msg_fim_estrategia)
        if key != list(endpoints.keys())[-1]: time.sleep(5)

# 6) POPULANDO/ATUALIZANDO INFORMAÇÕES ADICIONAIS
def popular_info_adicional_filmes():
    # Esta função permanece a mesma, pois a lógica de exclusão de idioma é no momento da inserção inicial.
    # Se você quisesse remover filmes já existentes no BD com esses idiomas,
    # precisaria de uma lógica de DELETE aqui, baseada na coluna 'idioma_original' do seu BD.
    global TOTAL_FILMES_PROCESSADOS 
    print("\nVerificando/Atualizando diretores para filmes sem essa informação...")
    logger.info("Iniciando atualização de diretores para filmes sem essa informação.")
    try:
        cur.execute("SELECT id, nome FROM filme WHERE diretor IS NULL OR diretor = ''")
        filmes_sem_diretor = cur.fetchall()
    except pymysql.Error as e:
        logger.error(f"Erro ao buscar filmes sem diretor: {e}")
        print(f"Erro ao buscar filmes sem diretor: {e}")
        return

    if not filmes_sem_diretor:
        print("Todos os filmes já possuem informação de diretor ou não há filmes para processar.")
        logger.info("Nenhum filme encontrado sem diretor para atualizar.")
        return

    print(f"Encontrados {len(filmes_sem_diretor)} filmes sem diretor. Tentando atualizar...")
    logger.info(f"Encontrados {len(filmes_sem_diretor)} filmes sem diretor para atualizar.")
    
    filmes_atualizados_count = 0
    for i, (mid, mnome) in enumerate(filmes_sem_diretor):
        logger.debug(f"Processando créditos para (atualização diretor): '{mnome}' (ID: {mid}) - {i+1}/{len(filmes_sem_diretor)}")
        
        movie_details_with_credits = get_movie_details(mid)

        if movie_details_with_credits and 'credits' in movie_details_with_credits:
            diretor_nome_update = None
            for c_crew in movie_details_with_credits['credits'].get('crew', []):
                if c_crew.get('job', '').lower() == 'director':
                    diretor_nome_update = c_crew.get('name')
                    break 
            
            if diretor_nome_update:
                try:
                    cur.execute("UPDATE filme SET diretor=%s WHERE id=%s", (diretor_nome_update, mid))
                    conn.commit()
                    logger.info(f"Diretor '{diretor_nome_update}' atualizado para o filme ID {mid} ('{mnome}').")
                    filmes_atualizados_count += 1
                except pymysql.Error as db_err:
                    logger.error(f"Erro de BD ao atualizar diretor para filme ID {mid} ('{mnome}'): {db_err}")
                    conn.rollback()
            else:
                logger.warning(f"Nenhum diretor encontrado nos créditos para o filme ID {mid} ('{mnome}') durante a atualização.")
        else:
            logger.warning(f"Não foi possível obter créditos para o filme ID {mid} ('{mnome}') para atualizar diretor.")
        
        if (i + 1) % 10 == 0:
            print(f"Progresso atualização diretores: {i+1}/{len(filmes_sem_diretor)} verificados.")
            if i + 1 < len(filmes_sem_diretor):
                 logger.debug("Pausa de 3 segundos no processamento de diretores...")
                 time.sleep(3)
            
    print(f"Atualização de diretores concluída. {filmes_atualizados_count} filmes atualizados.")
    logger.info(f"Atualização de diretores concluída. {filmes_atualizados_count} filmes atualizados de {len(filmes_sem_diretor)} verificados.")


# 7) EXECUÇÃO SEQUENCIAL PRINCIPAL
if __name__ == '__main__':
    try:
        start_time_script = datetime.now()
        msg_inicio_total = f"Iniciando script de população em: {start_time_script.strftime('%Y-%m-%d %H:%M:%S')}"
        print(msg_inicio_total)
        logger.info(msg_inicio_total)

        popular_generos()

        popular_filmes_estrategia(
            num_pages_popular=200, # Ajuste conforme necessário
            num_pages_underground=200, # Ajuste conforme necessário
            num_pages_critics=50, # Ajuste conforme necessário
        )
        
        # popular_info_adicional_filmes()

        end_time_script = datetime.now()
        msg_fim_total = f"Script concluído com sucesso em: {end_time_script.strftime('%Y-%m-%d %H:%M:%S')}"
        msg_duracao = f"Tempo total de execução: {end_time_script - start_time_script}"
        msg_stats = f"Total de filmes verificados durante a busca principal: {TOTAL_FILMES_PROCESSADOS}. Total de novos filmes inseridos: {TOTAL_FILMES_INSERIDOS}."

        print(f"\n{msg_fim_total}")
        print(msg_duracao)
        print(msg_stats)
        logger.info(msg_fim_total)
        logger.info(msg_duracao)
        logger.info(msg_stats)

    except requests.exceptions.HTTPError as http_err_main:
        if hasattr(http_err_main, 'response') and http_err_main.response is not None and http_err_main.response.status_code == 401:
            err_msg = "\nERRO CRÍTICO DE AUTENTICAÇÃO COM A API TMDB..."
            print(err_msg)
            logger.critical(err_msg)
        else:
            err_msg = f"\nErro HTTP não tratado durante a execução principal: {http_err_main}"
            print(err_msg)
            logger.critical(err_msg)
    except pymysql.Error as db_err_main:
        err_msg = f"\nErro de Banco de Dados não tratado durante a execução principal: {db_err_main}"
        print(err_msg)
        logger.critical(err_msg)
    except Exception as e_main:
        err_msg = f"\nErro inesperado durante a execução do script: {e_main}"
        print(err_msg)
        logger.exception(err_msg)
    finally:
        if 'conn' in locals() and conn and conn.open:
            cur.close()
            conn.close()
            logger.info("Conexão com o banco de dados fechada.")
            print("Conexão com o banco de dados fechada.")