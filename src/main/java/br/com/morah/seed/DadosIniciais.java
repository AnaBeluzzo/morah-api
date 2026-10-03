package br.com.morah.seed;

import br.com.morah.model.*;
import br.com.morah.model.enums.*;
import br.com.morah.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Popula o banco com dados de teste ao subir a aplicação, SÓ no perfil "dev".
 * CommandLineRunner = o Spring executa o método run() uma vez, logo após a inicialização.
 * É idempotente: se já existe condomínio, não faz nada (pode reiniciar à vontade).
 * Os CPFs abaixo são FICTÍCIOS.
 */
@Component
@Profile("dev")
public class DadosIniciais implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosIniciais.class);

    private final CondominioRepository condominioRepository;
    private final BlocoRepository blocoRepository;
    private final UnidadeRepository unidadeRepository;
    private final PerfilRepository perfilRepository;
    private final PessoaRepository pessoaRepository;
    private final PessoaPerfilRepository pessoaPerfilRepository;
    private final VinculoUnidadeRepository vinculoRepository;
    private final ComunicadoRepository comunicadoRepository;
    private final VisitanteRepository visitanteRepository;
    private final AutorizacaoVisitaRepository autorizacaoRepository;

    public DadosIniciais(CondominioRepository condominioRepository, BlocoRepository blocoRepository,
                         UnidadeRepository unidadeRepository, PerfilRepository perfilRepository,
                         PessoaRepository pessoaRepository, PessoaPerfilRepository pessoaPerfilRepository,
                         VinculoUnidadeRepository vinculoRepository, ComunicadoRepository comunicadoRepository,
                         VisitanteRepository visitanteRepository, AutorizacaoVisitaRepository autorizacaoRepository) {
        this.condominioRepository = condominioRepository;
        this.blocoRepository = blocoRepository;
        this.unidadeRepository = unidadeRepository;
        this.perfilRepository = perfilRepository;
        this.pessoaRepository = pessoaRepository;
        this.pessoaPerfilRepository = pessoaPerfilRepository;
        this.vinculoRepository = vinculoRepository;
        this.comunicadoRepository = comunicadoRepository;
        this.visitanteRepository = visitanteRepository;
        this.autorizacaoRepository = autorizacaoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (condominioRepository.count() > 0) {
            return;
        }

        Condominio condominio = new Condominio();
        condominio.setNome("Residencial Morah");
        condominio.setCnpj("12.345.678/0001-90");
        condominio.setEndereco("Rua das Flores, 100");
        condominio.setCidade("Curitiba");
        condominio.setEstado("PR");
        condominio.setQtdUnidades(3);
        condominio.setStatus(StatusCadastro.ATIVO);
        condominioRepository.save(condominio);

        Bloco bloco = new Bloco();
        bloco.setCondominio(condominio);
        bloco.setNome("Bloco A");
        bloco.setQtdAndares(3);
        blocoRepository.save(bloco);

        Unidade u101 = unidade(bloco, "Apto 101");
        Unidade u102 = unidade(bloco, "Apto 102");
        Unidade u201 = unidade(bloco, "Apto 201");

        List<Perfil> perfis = List.of(
                perfil("morador", "Morador de uma unidade"),
                perfil("proprietario", "Proprietário (cumulativo ao morador)"),
                perfil("sindico", "Síndico do condomínio"),
                perfil("portaria", "Porteiro / funcionário da portaria"));

        Pessoa sindico = pessoa("Carlos Síndico", "111.111.111-11", "carlos@morah.test", perfis.get(2), condominio);
        Pessoa morador = pessoa("Maria Moradora", "222.222.222-22", "maria@morah.test", perfis.get(0), condominio);
        Pessoa proprietario = pessoa("João Proprietário", "333.333.333-33", "joao@morah.test", perfis.get(1), condominio);
        Pessoa porteiro = pessoa("Pedro Porteiro", "444.444.444-44", "pedro@morah.test", perfis.get(3), condominio);

        vinculo(morador, u101, TipoVinculo.INQUILINO);
        vinculo(proprietario, u102, TipoVinculo.PROPRIETARIO);
        vinculo(sindico, u201, TipoVinculo.PROPRIETARIO);

        aviso(condominio, sindico, "Manutenção do elevador", "O elevador ficará parado na quinta, das 9h às 12h.",
                PrioridadeAviso.NORMAL, "manutencao", TipoPublicoAlvo.CONDOMINIO, null, null, null);
        aviso(condominio, sindico, "Vazamento no Bloco A", "Fechamento do registro geral às 14h. Armazenem água.",
                PrioridadeAviso.URGENTE, "urgente", TipoPublicoAlvo.BLOCO, bloco, null, null);
        aviso(condominio, sindico, "Recado para o Apto 101", "Sua encomenda grande está na portaria.",
                PrioridadeAviso.NORMAL, "geral", TipoPublicoAlvo.UNIDADE, null, u101, null);
        aviso(condominio, sindico, "Festa de fim de ano (EXPIRADO)", "Aviso antigo, já fora de vigência.",
                PrioridadeAviso.NORMAL, "geral", TipoPublicoAlvo.CONDOMINIO, null, null, LocalDateTime.now().minusDays(1));

        Visitante visitante = new Visitante();
        visitante.setNome("Ana Visitante");
        visitante.setDocumento("99999999999");
        visitanteRepository.save(visitante);
        autorizacao(visitante, u101, StatusAutorizacaoVisita.PENDENTE, "Entrega de móveis", LocalDateTime.now());
        autorizacao(visitante, u101, StatusAutorizacaoVisita.AUTORIZADA, "Visita familiar", LocalDateTime.now().minusHours(2));

        log.info("""

                ==== Dados de teste criados (use nos headers X-*) ====
                condominio={}  bloco={}  unidades: 101={} 102={} 201={}
                sindico:      X-Pessoa-Id={} X-Perfil=sindico
                morador:      X-Pessoa-Id={} X-Perfil=morador       X-Unidade-Id={}
                proprietario: X-Pessoa-Id={} X-Perfil=proprietario  X-Unidade-Id={}
                portaria:     X-Pessoa-Id={} X-Perfil=portaria
                =======================================================""",
                condominio.getId(), bloco.getId(), u101.getId(), u102.getId(), u201.getId(),
                sindico.getId(), morador.getId(), u101.getId(), proprietario.getId(), u102.getId(), porteiro.getId());
    }

    private void autorizacao(Visitante visitante, Unidade unidade, StatusAutorizacaoVisita status, String motivo, LocalDateTime solicitadoEm) {
        AutorizacaoVisita a = new AutorizacaoVisita();
        a.setVisitante(visitante);
        a.setUnidade(unidade);
        a.setStatus(status);
        a.setMotivo(motivo);
        a.setSolicitadoEm(solicitadoEm);
        autorizacaoRepository.save(a);
    }

    private Unidade unidade(Bloco bloco, String identificacao) {
        Unidade u = new Unidade();
        u.setBloco(bloco);
        u.setIdentificacao(identificacao);
        u.setTipo("apartamento");
        u.setAreaM2(new java.math.BigDecimal("72.50"));
        u.setFracaoIdeal(new java.math.BigDecimal("0.333333"));
        u.setStatus("ocupada");
        return unidadeRepository.save(u);
    }

    private Perfil perfil(String nome, String descricao) {
        Perfil p = new Perfil();
        p.setNome(nome);
        p.setDescricao(descricao);
        return perfilRepository.save(p);
    }

    private Pessoa pessoa(String nome, String cpf, String email, Perfil perfil, Condominio condominio) {
        Pessoa p = new Pessoa();
        p.setNome(nome);
        p.setCpf(cpf);
        p.setEmail(email);
        p.setTelefone("(41) 99999-0000");
        p.setStatus(StatusCadastro.ATIVO); // senhaHash fica null: login é do Keycloak
        pessoaRepository.save(p);

        PessoaPerfil pp = new PessoaPerfil();
        pp.setPessoa(p);
        pp.setPerfil(perfil);
        pp.setCondominio(condominio);
        pp.setStatus(StatusCadastro.ATIVO);
        pessoaPerfilRepository.save(pp);
        return p;
    }

    private void vinculo(Pessoa pessoa, Unidade unidade, TipoVinculo tipo) {
        VinculoUnidade v = new VinculoUnidade();
        v.setPessoa(pessoa);
        v.setUnidade(unidade);
        v.setTipoVinculo(tipo);
        v.setPrincipal(true);
        v.setInicio(LocalDate.now().minusMonths(6));
        vinculoRepository.save(v);
    }

    private void aviso(Condominio condominio, Pessoa autor, String titulo, String conteudo, PrioridadeAviso prioridade,
                       String categoria, TipoPublicoAlvo alvo, Bloco bloco, Unidade unidade, LocalDateTime expiraEm) {
        Comunicado c = new Comunicado();
        c.setCondominio(condominio);
        c.setAutor(autor);
        c.setTitulo(titulo);
        c.setConteudo(conteudo);
        c.setPrioridade(prioridade);
        c.setCategoria(categoria);
        c.setPublicoAlvoTipo(alvo);
        c.setBloco(bloco);
        c.setUnidade(unidade);
        c.setPublicadoEm(LocalDateTime.now());
        c.setExpiraEm(expiraEm);
        comunicadoRepository.save(c);
    }
}
