package com.StarMovie.star.Filme;

import com.StarMovie.star.Lista.Lista;
import jakarta.persistence.*; // Use javax.persistence.* se estiver no Spring Boot 2.x
import java.time.LocalDate; // Para o tipo DATE do SQL
import java.util.HashSet;
import java.util.Set;

@Entity // Marca esta classe como uma entidade JPA
@Table(name = "filme") // Mapeia para a tabela "filme" no banco de dados

public class Filme {
    @Id // Marca este campo como a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assume que o ID é auto-incrementado pelo banco
    private Integer id;

    @Column(name = "duracao")
    private Integer duracao;

    @Column(name = "classificacao_indicativa", length = 10)
    private String classificacaoIndicativa;

    @Column(name = "elenco_principal", columnDefinition = "TEXT")
    private String elencoPrincipal;

    @Column(name = "is_adult") // Mapeia para a nova coluna snake_case
    private Boolean isAdult; // Campo Java

    @Column(name = "tmdb_id") // Mapeia para a coluna tmdb_id (opcional se a estratégia de nomes converter camelCase para snake_case)
    private Integer tmdbId; // Nome do campo em Java (camelCase)

    @Column(length = 100) // Define o tamanho máximo (VARCHAR(100))
    private String nome;

    @Column(name = "ano")
    private Integer ano;

    @Column(columnDefinition = "TEXT") // Ajuda a garantir o mapeamento para TEXT
    private String sinopse;

    @Column(length = 100)
    private String diretor;

    private Float popularidade; // Mapeia para FLOAT

    // O nome do campo posterPath é igual ao da coluna no diagrama, @Column não seria estritamente necessário
    // a menos que a estratégia de nomes do Hibernate fizesse alguma conversão inesperada.
    // @Column(name = "posterPath", length = 255)
    @Column(length = 255, name = "posterPath")
    private String posterPath;

    @Column(name = "caminho_fundo", length = 255) // Mapeia o campo Java para a coluna do DB
    private String caminhoFundo; // Nome do campo em Java (camelCase)

    @Column(name = "idioma_original", length = 10)
    private String idiomaOriginal;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento; // Usa LocalDate para o tipo DATE do SQL (API moderna)
    // Se preferir usar a API antiga:
    // @Temporal(TemporalType.DATE)
    // private java.util.Date dataLancamento;

    @ManyToMany(fetch = FetchType.EAGER) //EAGER para carregar junto com o Filme (simplifica)
    @JoinTable(
            name = "filmegenero", // Nome da tabela de junção no DB
            joinColumns = @JoinColumn(name = "filme_id"), // Coluna nesta tabela que referencia Filme
            inverseJoinColumns = @JoinColumn(name = "genero_id") // Coluna nesta tabela que referencia Genero
    )

    private Set<Genero> generos = new HashSet<>(); // Campo Java para os gêneros



    // --- Construtores ---
    // Construtor vazio (OBRIGATÓRIO para JPA)
    public Filme() {
    }

    // Construtor com campos (opcional, para conveniência)
    public Filme(Integer tmdbId, String nome, Integer ano, String sinopse, String diretor, Float popularidade, String posterPath, String caminhoFundo, String idiomaOriginal, LocalDate dataLancamento) {
        this.tmdbId = tmdbId;
        this.nome = nome;
        this.ano = ano;
        this.sinopse = sinopse;
        this.diretor = diretor;
        this.popularidade = popularidade;
        this.posterPath = posterPath;
        this.caminhoFundo = caminhoFundo;
        this.idiomaOriginal = idiomaOriginal;
        this.dataLancamento = dataLancamento;
        this.isAdult = isAdult;
        this.duracao = duracao;
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.elencoPrincipal = elencoPrincipal;
    }

    // --- Getters e Setters ---
    // (Gerados para todos os campos)
    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void setClassificacaoIndicativa(String classificacaoIndicativa) {
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public String getElencoPrincipal() {
        return elencoPrincipal;
    }

    public void setElencoPrincipal(String elencoPrincipal) {
        this.elencoPrincipal = elencoPrincipal;
    }

    public Set<Genero> getGeneros(){
        return generos;
    }

    public void setGeneros(Set<Genero> generos) {
        this.generos = generos;
    }

    public Boolean getAdult() {
        return isAdult;
    }

    public void setAdult(Boolean adult) {
        isAdult = adult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getDiretor() {
        return diretor;
    }

    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public Float getPopularidade() {
        return popularidade;
    }

    public void setPopularidade(Float popularidade) {
        this.popularidade = popularidade;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getCaminhoFundo() {
        return caminhoFundo;
    }

    public void setCaminhoFundo(String caminhoFundo) {
        this.caminhoFundo = caminhoFundo;
    }

    public String getIdiomaOriginal() {
        return idiomaOriginal;
    }

    public void setIdiomaOriginal(String idiomaOriginal) {
        this.idiomaOriginal = idiomaOriginal;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    // Você pode adicionar @ToString, @EqualsAndHashCode se desejar (requer cuidado com relacionamentos)
}
